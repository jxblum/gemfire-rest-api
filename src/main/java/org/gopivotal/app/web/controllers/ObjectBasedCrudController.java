package org.gopivotal.app.web.controllers;

import java.util.Collection;
import java.util.Map;

import com.gemstone.gemfire.internal.util.ArrayUtils;

import org.gopivotal.app.web.controllers.support.RegionData;
import org.gopivotal.app.web.controllers.support.UpdateOp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The ObjectBasedCrudController class is a Spring Web MVC Controller exposing web service endpoints for
 * basic CRUD operations accessing "raw", application domain objects in GemFire's REST interface.
 * <p/>
 * @author John Blum
 * @see org.gopivotal.app.web.controllers.CommonCrudController
 * @see org.springframework.stereotype.Controller
 * @since 7.5
 */
@Controller("objCrudController")
@RequestMapping(ObjectBasedCrudController.REST_API_VERSION)
@SuppressWarnings("unused")
public class ObjectBasedCrudController extends CommonCrudController {

  // Constant String value indicating the version of the REST API.
  protected static final String REST_API_VERSION = "/v2";

  /**
   * Gets the version of the REST API implemented by this @Controller.
   * <p/>
   * @return a String indicating the REST API version.
   */
  @Override
  protected String getRestApiVersion() {
    return REST_API_VERSION;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{region}",
    consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<Object> create(@PathVariable("region") String region,
                                       @RequestParam(value = "key", required = false) String key,
                                       @RequestBody Object domainObj)
  {
    region = decode(region);
    domainObj = introspectAndConvert(domainObj);
    key = generateKey(key, domainObj);

    logger.info(String.format("Posting (creating/putIfAbsent) data (%1$s) to Region (%2$s) with Key (%3$s)...",
      domainObj, region, key));

    final Object existingDomainObj = postValue(region, key, domainObj);

    final HttpHeaders headers = new HttpHeaders();

    headers.setLocation(toUri(region, key));

    return (existingDomainObj != null ? new ResponseEntity<Object>(existingDomainObj, headers, HttpStatus.CONFLICT)
      : new ResponseEntity<Object>(headers, HttpStatus.CREATED));
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{region}",
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<RegionData<?>> read(@PathVariable("region") String region) {
    region = decode(region);

    logger.info(String.format("Reading all data in Region (%1$s)...", region));

    final Collection<Object> values = getValues(region);

    final RegionData<Object> data = new RegionData<Object>(region);

    data.add(values);

    return new ResponseEntity<RegionData<?>>(data, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{region}/{keys}",
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  public ResponseEntity<?> read(@PathVariable("region") String region,
                                @PathVariable("keys") final String[] keys)
  {
    region = decode(region);

    logger.info(String.format("Reading data for Keys (%1$s) in Region (%2$s)...", ArrayUtils.toString(keys), region));

    final Collection<Object> values = getValues(region, keys);

    final RegionData<Object> data = new RegionData<Object>(region);

    if (!(values == null || values.isEmpty())) {
      data.add(values);
    }

    final HttpHeaders headers = new HttpHeaders();

    if (data.size() == 1) {
      headers.setLocation(toUri(region, keys[0]));
      return new ResponseEntity<Object>(data.get(0), headers, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<RegionData<?>>(data, headers, HttpStatus.OK);
    }
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/{region}/{key}",
    consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
  )
  @SuppressWarnings("unchecked")
  public ResponseEntity<?> update(@PathVariable("region") String region,
                                  @PathVariable("key") final String key,
                                  @RequestParam(value = "op", defaultValue = "PUT") final String opValue,
                                  @RequestBody Object domainObj)
  {
    region = decode(region);

    final UpdateOp op = UpdateOp.valueOf(opValue.toUpperCase());

    domainObj = introspectAndConvert(domainObj);

    logger.info(String.format("Updating (%1$s) data (%2$s) having Key (%3$s) in Region (%4$s)...",
      op, domainObj, key, region));

    Object existingValue = null;

    switch (op) {
      case CAS:
        Assert.isInstanceOf(Map.class, domainObj);
        final Map<String, Object> form = (Map<String, Object>) domainObj;
        existingValue = casValue(region, key, form.get("@old"), form.get("@new"));
        break;
      case REPLACE:
        replaceValue(region, key, domainObj);
        break;
      case PUT:
      default:
        putValue(region, key, domainObj);
    }

    final HttpHeaders headers = new HttpHeaders();

    headers.setLocation(toUri(region, key));

    return new ResponseEntity<Object>(existingValue, headers, (existingValue == null ? HttpStatus.OK : HttpStatus.CONFLICT));
  }

}
