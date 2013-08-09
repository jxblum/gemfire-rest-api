package org.gopivotal.app.web.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.internal.GemFireVersion;
import com.gemstone.gemfire.internal.util.ArrayUtils;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The CommonCrudController class encapsulates common CRUD operations that can be used by all web service implementations.
 * <p/>
 * @author John Blum
 * @see org.gopivotal.app.web.controllers.AbstractBaseController
 * @see org.springframework.stereotype.Controller
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class CommonCrudController extends AbstractBaseController {

  @RequestMapping(method = RequestMethod.DELETE, value = "/{region}")
  public ResponseEntity<?> delete(@PathVariable("region") final String region) {
    logger.info(String.format("Deleting all data in Region (%1$s)...", region));
    deleteValues(region);
    return new ResponseEntity<Object>(HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/{region}/{keys}")
  public ResponseEntity<?> delete(@PathVariable("region") final String region,
                                  @PathVariable("keys") final String[] keys)
  {
    logger.info(String.format("Deleting data for Keys (%1$s) in Region (%2$s)...", ArrayUtils.toString(keys), region));
    deleteValues(region, keys);
    return new ResponseEntity<Object>(HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{region}/keys", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<Link[]> keys(@PathVariable("region") final String region) {
    logger.info(String.format("Reading all Keys in Region (%1$s)...", region));

    final Object[] keys = getKeys(region, null);

    final List<Link> keyLinks = new ArrayList<Link>(keys.length);

    //final String json = toJson("Keys", keys);

    for (Object key : keys) {
      final String rel = String.valueOf(key);
      keyLinks.add(linkTo(getClass()).slash(region).slash(rel).withRel(rel));
    }

    final HttpHeaders headers = new HttpHeaders();

    headers.setLocation(toUri(region, "keys"));

    return new ResponseEntity<Link[]>(keyLinks.toArray(new Link[keyLinks.size()]), headers, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<Link[]> regions() {
    logger.info(String.format("Listing all resources (Regions) in GemFire..."));

    final Set<Region<?, ?>> regions = getCache().rootRegions();

    final List<Link> regionLinks = new ArrayList<Link>(regions.size());

    for (final Region<?, ?> region : regions) {
      final String regionName = region.getName();
      regionLinks.add(linkTo(getClass()).slash(regionName).withRel(region.getName()));
    }

    final HttpHeaders headers = new HttpHeaders();

    headers.setLocation(toUri());

    return new ResponseEntity<Link[]>(regionLinks.toArray(new Link[regionLinks.size()]), headers, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/version")
  @ResponseBody
  public String version() {
    return GemFireVersion.getProductName().concat("/").concat(GemFireVersion.getGemFireVersion());
  }

}
