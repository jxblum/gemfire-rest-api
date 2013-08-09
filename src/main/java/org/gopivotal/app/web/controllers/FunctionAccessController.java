package org.gopivotal.app.web.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gemstone.gemfire.cache.execute.Execution;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionService;
import com.gemstone.gemfire.cache.execute.ResultCollector;

import org.gopivotal.app.util.ArrayUtils;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The FunctionsController class...
 * <p/>
 * @author John Blum
 * @see org.springframework.stereotype.Controller
 * @since 7.5
 */
@Controller("functionController")
@RequestMapping("/functions")
@SuppressWarnings("unused")
public class FunctionAccessController extends AbstractBaseController {

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

  @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Link[] list() {
    logger.info("Listing all registered Functions in GemFire...");

    final Map<String, Function> registeredFunctions = FunctionService.getRegisteredFunctions();

    final List<Link> functionLinks = new ArrayList<Link>(registeredFunctions.size());

    for (String functionId : registeredFunctions.keySet()) {
      functionLinks.add(linkTo(getClass()).slash(functionId).withRel(functionId));
    }

    return functionLinks.toArray(new Link[functionLinks.size()]);
  }

  // TODO arguments are primitive, scalar values, probably Strings
  // TODO I did not add support here for extracting an (JSON) array of complex arguments types (like Objects)
  // TODO the return type is a Function dependent... may need to define some sort of convention restricting return types (like an Object[] of Objects convertible to JSON/XML)

  @RequestMapping(method = RequestMethod.POST, value = "/{functionId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public Object[] execute(@PathVariable("functionId") final String functionId,
                          @RequestParam("args") final Object[] arguments,
                          @RequestParam("onRegion") final String region,
                          @RequestParam("onMembers") final String[] members,
                          @RequestParam("onGroups") final String[] groups)
  {
    Execution function = null;

    if (StringUtils.hasText(region)) {
      logger.info(String.format("Executing Function (%1$s) with arguments (%2$s) on Region (%3$s)...", functionId,
        ArrayUtils.toString(arguments), region));
      function = FunctionService.onRegion(getRegion(region));
    }
    else if (ArrayUtils.isNotEmpty(members)) {
      logger.info(String.format("Executing Function (%1$s) with arguments (%2$s) on Member (%3$s)...", functionId,
        ArrayUtils.toString(arguments), ArrayUtils.toString(members)));
      function = FunctionService.onMembers(getMembers(members));
    }
    else if (ArrayUtils.isNotEmpty(groups)) {
      logger.info(String.format("Executing Function (%1$s) with arguments (%2$s) on Groups (%3$s)...", functionId,
        ArrayUtils.toString(arguments), ArrayUtils.toString(groups)));
      function = FunctionService.onMembers(groups);
    }
    else {
      logger.info(String.format("Executing Function (%1$s) with arguments (%2$s) on all Members...", functionId,
        ArrayUtils.toString(arguments)));
      function = FunctionService.onMembers(getCache().getMembers());
    }

    final ResultCollector<?, ?> results = function.withArgs(arguments).execute(functionId);
    final List<?> resultList = (List<?>) results.getResult();

    return resultList.toArray();
  }

}
