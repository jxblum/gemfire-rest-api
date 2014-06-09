package org.gopivotal.app.web.controllers;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.query.QueryService;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.gemstone.gemfire.distributed.DistributedSystem;
import com.gemstone.gemfire.pdx.JSONFormatter;
import com.gemstone.gemfire.pdx.PdxInstance;

import org.gopivotal.app.domain.support.ResourceSupport;
import org.gopivotal.app.util.ArrayUtils;
import org.gopivotal.app.util.IdentifiableUtils;
import org.gopivotal.app.util.NumberUtils;
import org.gopivotal.app.util.UriUtils;
import org.gopivotal.app.util.ValidationUtils;
import org.gopivotal.app.web.controllers.util.RegionNotFoundException;
import org.gopivotal.app.web.controllers.util.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * The AbstractBaseController class is a base class encapsulating common functionality for all web service/REST API
 * Controller implementations with interactions with GemFire.
 * <p/>
 * @author John Blum
 * @see com.fasterxml.jackson.databind.ObjectMapper
 * @see com.gemstone.gemfire.cache.Cache
 * @see com.gemstone.gemfire.cache.Region
 * @see com.gemstone.gemfire.cache.query.QueryService
 * @see com.gemstone.gemfire.distributed.DistributedMember
 * @see com.gemstone.gemfire.distributed.DistributedSystem
 * @see com.gemstone.gemfire.pdx.JSONFormatter
 * @see com.gemstone.gemfire.pdx.PdxInstance
 * @see org.gopivotal.app.domain.support.ResourceSupport
 * @see org.springframework.stereotype.Controller
 * @see org.springframework.web.servlet.support.ServletUriComponentsBuilder
 * @since 7.5
 * @version 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractBaseController {

  protected static final String NEW_META_DATA_PROPERTY = "@new";
  protected static final String OLD_META_DATA_PROPERTY = "@old";
  protected static final String TYPE_META_DATA_PROPERTY = "@type";

  private static final AtomicLong idSequence = new AtomicLong(0l);

  @Autowired
  private Cache cache;

  protected final Logger logger = Logger.getLogger(getClass().getName());

  @Autowired
  private ObjectMapper objectMapper;

  protected String convert(final PdxInstance pdxObj) {
    return (pdxObj != null ? JSONFormatter.toJSON(pdxObj) : null);
  }

  protected String convert(final Iterable<PdxInstance> pdxObjs) {
    final StringBuilder buffer = new StringBuilder("[");
    int count = 0;

    for (final PdxInstance pdxObj : pdxObjs) {
      final String json = convert(pdxObj);

      if (StringUtils.hasText(json)) {
        buffer.append(count++ > 0 ? ", " : "").append(json);
      }
    }

    buffer.append("]");

    return buffer.toString();
  }

  protected PdxInstance convert(final String json) {
    return (StringUtils.hasText(json) ? JSONFormatter.fromJSON(json) : null);
  }

  protected String decode(final String value) {
    return UriUtils.decode(value);
  }

  protected String decode(String value, final String encoding) {
    return UriUtils.decode(value, encoding);
  }

  protected String encode(final String value) {
    return UriUtils.encode(value);
  }

  protected String encode(final String value, final String encoding) {
    return UriUtils.encode(value, encoding);
  }

  protected String generateKey(final String existingKey) {
    return generateKey(existingKey, null);
  }

  protected String generateKey(final String existingKey, final Object domainObject) {
    Object domainObjectId = IdentifiableUtils.getId(domainObject);
    String newKey;

    if (StringUtils.hasText(existingKey)) {
      newKey = existingKey;
      if (NumberUtils.isNumeric(newKey) && domainObjectId == null) {
        final Long newId = IdentifiableUtils.createId(NumberUtils.parseLong(newKey));
        if (newKey.equals(newId.toString())) {
          IdentifiableUtils.setId(domainObject, newId);
        }
      }
    }
    else if (domainObjectId != null) {
      final Long domainObjectIdAsLong = NumberUtils.longValue(domainObjectId);
      if (domainObjectIdAsLong != null) {
        final Long newId = IdentifiableUtils.createId(domainObjectIdAsLong);
        if (!domainObjectIdAsLong.equals(newId)) {
          IdentifiableUtils.setId(domainObject, newId);
        }
        newKey = String.valueOf(newId);
      }
      else {
        newKey = String.valueOf(domainObjectId);
      }
    }
    else {
      domainObjectId = IdentifiableUtils.createId();
      newKey = String.valueOf(domainObjectId);
      IdentifiableUtils.setId(domainObject, domainObjectId);
    }

    return newKey;
  }

  // TODO this is unreliable at best as certain domain object properties may require special conversion of JSON content values.
  @SuppressWarnings("unchecked")
  protected <T> T introspectAndConvert(final T value) {
    //logger.info(String.format("Value is (%1$s)", value));

    if (value instanceof Map) {
      final Map rawDataBinding = (Map) value;

      if (isForm(rawDataBinding)) {
        rawDataBinding.put(OLD_META_DATA_PROPERTY, introspectAndConvert(rawDataBinding.get(OLD_META_DATA_PROPERTY)));
        rawDataBinding.put(NEW_META_DATA_PROPERTY, introspectAndConvert(rawDataBinding.get(NEW_META_DATA_PROPERTY)));

        return (T) rawDataBinding;
      }
      else {
        final Object typeValue = rawDataBinding.get(TYPE_META_DATA_PROPERTY);

        Assert.state(typeValue != null, "The class type of the object to persist in GemFire must be specified in JSON/XML content using the '@type' property!");

        Assert.state(ClassUtils.isPresent(String.valueOf(typeValue), Thread.currentThread().getContextClassLoader()),
          String.format("Class (%1$s) could not be found!", typeValue));

        return (T) objectMapper.convertValue(rawDataBinding, ClassUtils.resolveClassName(String.valueOf(typeValue),
          Thread.currentThread().getContextClassLoader()));
      }
    }

    return value;
  }

  private boolean isForm(final Map<Object, Object> rawDataBinding) {
    return (!rawDataBinding.containsKey(TYPE_META_DATA_PROPERTY)
      && rawDataBinding.containsKey(OLD_META_DATA_PROPERTY)
      && rawDataBinding.containsKey(NEW_META_DATA_PROPERTY));
  }

  protected <T> T[] linkify(final T[] domainObjs, final String regionNamePath) {
    for (T domainObj : domainObjs) {
      linkify(domainObj, regionNamePath);
    }

    return domainObjs;
  }

  protected <T> Collection<T> linkify(final Collection<T> domainObjs, final String regionNamePath) {
    for (T domainObj : domainObjs) {
      linkify(domainObj, regionNamePath);
    }

    return domainObjs;
  }

  protected <T> T linkify(final T domainObj, final String regionNamePath) {
    final Object id = IdentifiableUtils.getId(domainObj);

    if (id != null) {
      final Link selfLink = new Link(toUri(regionNamePath, String.valueOf(id)).toString(), Link.REL_SELF);

      if (domainObj instanceof ResourceSupport) {
        ((ResourceSupport) domainObj).removeAll();
        ((ResourceSupport) domainObj).add(selfLink);
      }
      else if (domainObj instanceof org.springframework.hateoas.ResourceSupport) {
        //((org.springframework.hateoas.ResourceSupport) domainObj).removeLinks();
        ((org.springframework.hateoas.ResourceSupport) domainObj).add(selfLink);
      }
      else if (ClassUtils.hasMethod(domainObj.getClass(), "add", new Class[] { Link.class })) {
        final MethodInvoker method = new MethodInvoker();

        method.setTargetObject(domainObj);
        method.setTargetMethod("add");
        method.setArguments(new Object[] { selfLink });

        try {
          method.invoke();
        }
        catch (Exception ignore) {
          ignore.printStackTrace();
        }
      }
    }

    return domainObj;
  }

  protected String toJson(final String arrayProperty, final Object... array) {
    final StringBuilder json = new StringBuilder();
    final boolean arrayPropertyProvided = StringUtils.hasText(arrayProperty);
    int count = 0;

    if (arrayPropertyProvided) {
      json.append("{\n\t\"").append(arrayProperty).append("\" : ");
    }

    json.append("[");

    if (array != null) {
      for (final Object element : array) {
        json.append(count++ > 0 ? ", \"" : "\"").append(String.valueOf(element)).append("\"");
      }
    }

    json.append("]");

    if (arrayPropertyProvided) {
      json.append("\n}");
    }

    return json.toString();
  }

  protected URI toUri(final String... pathSegments) {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(getRestApiVersion()).pathSegment(pathSegments)
      .build().toUri();
  }

  // base CRUD operations support

  protected abstract String getRestApiVersion();

  protected Cache getCache() {
    Assert.state(this.cache != null, "The GemFire Cache reference was not properly initialized!");
    return this.cache;
  }

  protected DistributedSystem getDistributedSystem() {
    return getCache().getDistributedSystem();
  }

  protected Set<DistributedMember> getMembers(final String... memberIdNames) {
    final Set<DistributedMember> members = new HashSet<DistributedMember>(ArrayUtils.length(memberIdNames));

    if (memberIdNames != null) {
      final List<String> memberIdNameList = Arrays.asList(memberIdNames);

      for (DistributedMember member : getCache().getMembers()) {
        if (memberIdNameList.contains(member.getId()) || memberIdNameList.contains(member.getName())) {
          members.add(member);
        }
      }
    }

    return members;
  }

  protected QueryService getQueryService() {
    return getCache().getQueryService();
  }

  @SuppressWarnings("unchecked")
  protected <T> Region<Object, T> getRegion(final String namePath) {
    return (Region<Object, T>) ValidationUtils.returnValueThrowOnNull(getCache().getRegion(namePath),
      new RegionNotFoundException(String.format("The Region identified by name/path (%1$s) could not be found!",
        namePath)));
  }

  protected void deleteValue(final String regionNamePath, final Object key) {
    getRegion(regionNamePath).remove(key);
  }

  protected void deleteValues(final String regionNamePath, final Object... keys) {
    for (final Object key : keys) {
      deleteValue(regionNamePath, key);
    }
  }

  protected void deleteValues(final String regionNamePath) {
    getRegion(regionNamePath).clear();
  }

  protected Object[] getKeys(final String regionNamePath, Object[] keys) {
    return (!(keys == null || keys.length == 0) ? keys : getRegion(regionNamePath).keySet().toArray());
  }

  @SuppressWarnings("unchecked")
  protected <T> T getValue(final String regionNamePath, final Object key) {
    Assert.notNull(key, "The Cache Region key to read the value for cannot be null!");
    return (T) linkify(getRegion(regionNamePath).get(key), regionNamePath);
  }

  @SuppressWarnings("unchecked")
  protected <T> Collection<T> getValues(final String regionNamePath, final Object... keys) {
    final Region<Object, T> region = getRegion(regionNamePath);
    final Map<Object, T> entries = region.getAll(Arrays.asList(getKeys(regionNamePath, keys)));
    return (entries == null ? Collections.<T>emptyList() : linkify(entries.values(), regionNamePath));
  }

  protected <T> Collection<T> getValues(final String regionNamePath) {
    return getValues(regionNamePath, getKeys(regionNamePath, null));
  }

  @SuppressWarnings("unchecked")
  protected <T> T postValue(final String regionNamePath, final Object key, final Object value) {
    return (T) getRegion(regionNamePath).putIfAbsent(key, value);
  }

  protected void putValue(final String regionNamePath, final Object key, final Object value) {
    getRegion(regionNamePath).put(key, value);
  }

  protected void putValues(final String regionNamePath, final Map<Object, Object> values) {
    getRegion(regionNamePath).putAll(values);
  }

  protected void replaceValue(final String regionNamePath, final Object key, final Object value) {
    final Region<Object, Object> region = getRegion(regionNamePath);

    // TODO this conditional block with operations containsKey/replace sets up a race condition given a possible stale observation!!!
    // NOTE 1. the Key might NOT exist when checked by the current Thread but then gets created by another Thread right after the check and before the current Thread throws an Exception.
    // NOTE 2. the Key might exist when checked by the current Thread but then is deleted by another Thread immediately before the current Thread performs the replace.
    // NOTE #2 is the more problematic since a ResourceNotFoundException will NOT be thrown in this case and any attempt to synchronize here is futile in distributed, multi-threaded computing.
    // In other words, this is best handled by GemFire.  Unfortunately GemFire does not throw an Exception for non-existing keys.  Worse yet, the "replace" operation returns null when the Key
    // does not exist or when it does exist and the Key is allowed to be mapped to null values (argh!), therefore you simply do not know and it would have been better if the "replace" op
    // returned a boolean value indicating success!
    // NOTE so, the actual implementation should handle this differently, but there really is no consequence when performing this action and the user is free to try again.
    if (region.containsKey(key)) {
      region.replace(key, value);
    }
    else {
      throw new ResourceNotFoundException(String.format("No resource at (%1$s) exists!", toUri(regionNamePath, String.valueOf(key))));
    }
  }

  protected Object casValue(final String regionNamePath, final Object key, final Object oldValue, final Object newValue) {
    final Region<Object, Object> region = getRegion(regionNamePath);
    return (region.replace(key, oldValue, newValue) ? null : region.get(key));
  }

}
