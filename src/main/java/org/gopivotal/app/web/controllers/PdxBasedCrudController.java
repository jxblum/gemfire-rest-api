package org.gopivotal.app.web.controllers;

import java.util.Collection;

import com.gemstone.gemfire.internal.util.ArrayUtils;
import com.gemstone.gemfire.pdx.PdxInstance;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The PdxBasedCrudController class is a Spring Web MVC Controller exposing web service endpoints for
 * basic CRUD operations based on PDX in GemFire's REST interface.
 * <p/>
 * @author John Blum
 * @see org.gopivotal.app.web.controllers.CommonCrudController
 * @see org.springframework.stereotype.Controller
 * @since 7.5
 */
@Controller("pdxCrudController")
@RequestMapping(PdxBasedCrudController.REST_API_VERSION)
@SuppressWarnings("unused")
public class PdxBasedCrudController extends CommonCrudController {

  // TODO support Content Negotiation using HTTP ACCEPT and CONTENT-TYPE headers.

  // TODO consider creating a PdxInstance to/from JSON HttpMessageConverter implementation. (However, first look into
  // Spring Data GemFire to determine if HttpMessageConverters are already provided in combination with Spring Data REST.
  // I suspect Spring Data GemFire is storing raw objects and not PdxInstances!)

  // Constant String value indicating the version of the REST API.
  protected static final String REST_API_VERSION = "/v1";

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
    //params = "key",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
  public ResponseEntity<String> create(@PathVariable("region") final String region,
                                       @RequestParam(value = "key", required = false) String key,
                                       @RequestBody final String json)
  {
    key = generateKey(key);

    logger.info(String.format("Posting (creating/putIfAbsent) JSON document (%1$s) to Region (%2$s) with Key (%3$s)...",
      json, region, key));

    final PdxInstance pdxObj = convert(json);

    final PdxInstance existingPdxObj = postValue(region, key, pdxObj);

    final HttpHeaders headers = new HttpHeaders();

    headers.setLocation(toUri(region, key));

    if (existingPdxObj != null) {
      final String existingJson = convert(existingPdxObj);

      headers.setContentLength(existingJson.getBytes().length);
      headers.setContentType(MediaType.APPLICATION_JSON);

      return new ResponseEntity<String>(existingJson, headers, HttpStatus.CONFLICT);
    }
    else {
      return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{region}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> read(@PathVariable("region") final String region) {
    logger.info(String.format("Reading all data in Region (%1$s)...", region));

    final Collection<PdxInstance> pdxObjs = getValues(region);

    final String json = convert(pdxObjs);

    final String wrappedJson = "{ \"" + region + "\" : " + json + "}";

    logger.info(String.format("Writing JSON document (%1$s) to HTTP response...", json));

    return new ResponseEntity<String>(wrappedJson, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{region}/{keys}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> read(@PathVariable("region") final String region,
                                     @PathVariable("keys") final String[] keys)
  {
    logger.info(String.format("Reading data for Keys (%1$s) in Region (%2$s)", ArrayUtils.toString(keys), region));

    final Collection<PdxInstance> pdxObjs = getValues(region, keys);

    final String json = convert(pdxObjs);

    final HttpHeaders headers = new HttpHeaders();

    if (keys.length == 1) {
      headers.setLocation(toUri(region, keys[0]));
    }

    logger.info(String.format("Writing JSON document (%1$s) to HTTP response...", json));

    return new ResponseEntity<String>(json, headers, HttpStatus.OK);
  }

  // TODO the JSONFormatter class does not handle an array of PdxInstances serialized as JSON, therefore,
  // putAll is not possible!

  @RequestMapping(method = RequestMethod.PUT, value = "/{region}/{key}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> update(@PathVariable("region") final String region,
                                  @PathVariable("key") final String key,
                                  @RequestBody final String json)
  {
    logger.info(String.format("Updating (put) JSON document (%1$s) having Key (%2$s) in Region (%3$s)...",
      json, key, region));

    final PdxInstance pdxObj = convert(json);

    putValue(region, key, pdxObj);

    final HttpHeaders headers = new HttpHeaders();

    headers.setLocation(toUri(region, key));

    return new ResponseEntity<Object>(headers, HttpStatus.OK);
  }

}
