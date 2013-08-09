package org.gopivotal.app.web.controllers.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The RegionData class is a container for data fetched from a GemFire Cache Region.
 * <p/>
 * @author John Blum
 * @see com.fasterxml.jackson.databind.JsonSerializable
 * @see java.lang.Iterable
 * @since 7.5
 */
@SuppressWarnings("unused")
@XmlRootElement(name = "region")
@XmlType(name = "org.gopivotal.app.web.controllers.support.RegionData")
public class RegionData<T> implements Iterable<T>, JsonSerializable {

  @JsonIgnore
  @XmlElement
  // TODO figure out how to use the XmlElementRef Annotation without forcing application domain object to extend a specific base Class type!
  //@XmlElementRef
  private final List<T> data = new ArrayList<T>();

  @JsonIgnore
  private String regionNamePath;

  public RegionData() {
  }

  public RegionData(final String regionNamePath) {
    setRegionNamePath(regionNamePath);
  }

  @XmlAttribute(name = "name")
  public String getRegionNamePath() {
    Assert.state(StringUtils.hasText(this.regionNamePath), "The Region name/path was not properly initialized!");
    return regionNamePath;
  }

  public final void setRegionNamePath(final String regionNamePath) {
    Assert.hasText(regionNamePath, "The name or path of the Region must be specified!");
    this.regionNamePath = regionNamePath;
  }

  public RegionData<T> add(final T data) {
    Assert.notNull(data, String.format("The data to add to Region (%1$s) cannot be null!", getRegionNamePath()));
    this.data.add(data);
    return this;
  }

  public RegionData<T> add(final T... data) {
    for (final T element : data) {
      if (element != null) {
        add(element);
      }
    }

    return this;
  }

  public RegionData<T> add(final Iterable<T> data) {
    for (final T element : data) {
      if (element != null) {
        add(element);
      }
    }

    return this;
  }

  public T get(final int index) {
    return list().get(index);
  }

  public boolean isEmpty() {
    return this.data.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return list().iterator();
  }

  public List<T> list() {
    return Collections.unmodifiableList(this.data);
  }

  public int size() {
    return this.data.size();
  }

  @Override
  public void serialize(final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
    throws IOException
  {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeArrayFieldStart(getRegionNamePath());

    for (T element : this) {
      jsonGenerator.writeObject(element);
    }

    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndObject();
  }

  @Override
  public void serializeWithType(final JsonGenerator jsonGenerator,
                                final SerializerProvider serializerProvider,
                                final TypeSerializer typeSerializer)
    throws IOException
  {
    // NOTE serializing "type" meta-data is not necessary in this case; just call serialize.
    serialize(jsonGenerator, serializerProvider);
  }

}
