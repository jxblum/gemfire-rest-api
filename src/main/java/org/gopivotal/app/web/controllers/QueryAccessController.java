package org.gopivotal.app.web.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;

import org.gopivotal.app.util.ValidationUtils;
import org.gopivotal.app.web.controllers.util.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.query.Query;

/**
 * The QueryingController class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@Controller("queryController")
@RequestMapping(QueryAccessController.REST_API_VERSION + "/queries")
@SuppressWarnings("unused")
public class QueryAccessController extends AbstractBaseController {

  protected static final String PARAMETERIZED_QUERIES_REGION = "__ParameterizedQueries__";

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

  @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Link[] list() {
    logger.info("Listing all Parameterized Queries in GemFire...");

    final Region<Object, ?> parameterizedQueryRegion = getRegion(PARAMETERIZED_QUERIES_REGION);

    final List<Link> queryLinks = new ArrayList<Link>(parameterizedQueryRegion.size());

    for (Object key : parameterizedQueryRegion.keySet()) {
      final String rel = String.valueOf(key);
      queryLinks.add(linkTo(getClass()).slash(rel).withRel(rel));
    }

    return queryLinks.toArray(new Link[queryLinks.size()]);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> create(@RequestParam("id") final String queryId,
                                  @RequestParam(value = "q", required = false) final String oqlInUrl,
                                  @RequestBody(required = false) final String oqlInBody)
  {
    final String oqlStatement = (StringUtils.hasText(oqlInUrl) ? oqlInUrl : oqlInBody);

    logger.info(String.format("Creating a named, parameterized Query (%1$s) with ID (%2$s)...", oqlStatement, queryId));

    // store the OQL statement with 'queryId' as the Key into the hidden, ParameterizedQueries Region...
    final Object existingOql = postValue(PARAMETERIZED_QUERIES_REGION, queryId, oqlStatement);

    final HttpHeaders headers = new HttpHeaders();

    headers.setLocation(toUri(queryId));

    return new ResponseEntity<Object>(existingOql, headers, (existingOql == null ? HttpStatus.OK : HttpStatus.CONFLICT));
  }

  @RequestMapping(method = RequestMethod.GET, value = "/adhoc", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Object runAdhocQuery(@RequestParam("q") final String oql) throws Exception {
    logger.info(String.format("Running an adhoc Query (%1$s)...", oql));

    final Query query = getQueryService().newQuery(oql);

    // NOTE Query.execute throws many checked Exceptions; let the BaseControllerAdvice Exception handlers catch
    // and handle the Exceptions appropriately (500 Server Error)!
    return query.execute();
  }

  // TODO the runNamedQuery method currently only supports primitive, scalar arguments to the Query
  @RequestMapping(method = RequestMethod.GET, value = "/{query}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Object runNamedQuery(@PathVariable("query") final String queryId,
                              @RequestParam("args") final Object[] arguments)
    throws Exception
  {
    logger.info(String.format("Running named Query with ID (%1$s)...", queryId));

    final String oql = getValue(PARAMETERIZED_QUERIES_REGION, queryId);

    ValidationUtils.returnValueThrowOnNull(oql, new ResourceNotFoundException(
      String.format("No Query with ID (%1$s) was found!", queryId)));

    final Query query = getQueryService().newQuery(oql);

    // NOTE Query.execute throws many checked Exceptions; let the BaseControllerAdvice Exception handlers catch
    // and handle the Exceptions appropriately (500 Server Error)!
    return query.execute(arguments);
  }

}
