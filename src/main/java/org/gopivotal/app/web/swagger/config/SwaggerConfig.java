package org.gopivotal.app.web.swagger.config;

import static com.google.common.collect.Lists.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mangofactory.swagger.configuration.JacksonScalaSupport;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.configuration.SpringSwaggerModelConfig;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.DefaultSwaggerPathProvider;
import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.mangofactory.swagger.core.SwaggerPathProvider;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.AuthorizationScope;
import com.wordnik.swagger.model.AuthorizationType;
import com.wordnik.swagger.model.GrantType;
import com.wordnik.swagger.model.ImplicitGrant;
import com.wordnik.swagger.model.LoginEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.mangofactory.swagger")
@SuppressWarnings("unused")
public class SwaggerConfig {

  public static final List<String> DEFAULT_INCLUDE_PATTERNS = Arrays.asList("/.*");

  public static final String SWAGGER_GROUP = "gemfireApi";

  @Autowired
  private SpringSwaggerConfig springSwaggerConfig;

  @Autowired
  private SpringSwaggerModelConfig springSwaggerModelConfig;

  @Value("${app.docs}")
  private String docsLocation;

  /**
   * Adds the Jackson Scala module to the MappingJackson2HttpMessageConverter registered with Spring.
   * Swagger core models are Scala so we need to be able to convert to JSON.
   * Also registers some custom serializers needed to transform Swagger models to Swagger-UI required JSON format.
   */
  @Bean
  public JacksonScalaSupport jacksonScalaSupport() {
    JacksonScalaSupport jacksonScalaSupport = new JacksonScalaSupport();
    // set to false to disable
    jacksonScalaSupport.setRegisterScalaModule(true);
    return jacksonScalaSupport;
  }


  /**
   * Global Swagger configuration settings
   */
  @Bean
  public SwaggerGlobalSettings swaggerGlobalSettings() {
    SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
    swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages());
    swaggerGlobalSettings.setIgnorableParameterTypes(springSwaggerConfig.defaultIgnorableParameterTypes());
    swaggerGlobalSettings.setParameterDataTypes(springSwaggerModelConfig.defaultParameterDataTypes());
    return swaggerGlobalSettings;
  }

  /**
   * API Info as it appears on the Swagger-UI page
   */
  private ApiInfo apiInfo() {
    return new ApiInfo(
      "GemFire Developer REST API",
      "Developer REST API and interface to GemFire's distributed, in-memory data grid and cache.",
      "http://pubs.vmware.com/vfabric53/index.jsp?topic=/com.vmware.vfabric.gemfire.7.0/about_gemfire.html",
      "support@gopivotal.com",
      "Pivotal GemFire 7.5 Production License",
      "http://pubs.vmware.com/vfabric53/topic/com.vmware.vfabric.gemfire.7.0/deploying/licensing/licensing.html"
    );
  }

  /**
   * Configure a SwaggerApiResourceListing for each swagger instance within your app. e.g. 1. private  2. external apis
   * Required to be a spring bean as spring will call the postConstruct method to bootstrap swagger scanning.
   */
  @Bean
  public SwaggerApiResourceListing swaggerApiResourceListing() {
    // The group name is important and should match the group set on ApiListingReferenceScanner
    // Note that swaggerCache() is by DefaultSwaggerController to serve the swagger json
    SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(
      springSwaggerConfig.swaggerCache(), SWAGGER_GROUP);

    // set the required Swagger settings
    swaggerApiResourceListing.setSwaggerGlobalSettings(swaggerGlobalSettings());

    // use a custom path provider or springSwaggerConfig.defaultSwaggerPathProvider()
    swaggerApiResourceListing.setSwaggerPathProvider(apiPathProvider());

    // supply the API Info as it should appear on Swagger-UI web page
    swaggerApiResourceListing.setApiInfo(apiInfo());

    // global authorization - see the Swagger documentation
    swaggerApiResourceListing.setAuthorizationTypes(authorizationTypes());

    // every SwaggerApiResourceListing needs an ApiListingReferenceScanner to scan the Spring RequestMappings
    swaggerApiResourceListing.setApiListingReferenceScanner(apiListingReferenceScanner());

    return swaggerApiResourceListing;
  }

  @Bean
  /**
   * The ApiListingReferenceScanner does most of the work.  It scans the appropriate Spring RequestMappingHandlerMappings,
   * applies the correct absolute paths to the generated Swagger resources, and so on.
   */
  public ApiListingReferenceScanner apiListingReferenceScanner() {
    ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner();

    //Picks up all of the registered spring RequestMappingHandlerMappings for scanning
    apiListingReferenceScanner.setRequestMappingHandlerMapping(springSwaggerConfig.swaggerRequestMappingHandlerMappings());

    //Excludes any controllers with the supplied annotations
    apiListingReferenceScanner.setExcludeAnnotations(springSwaggerConfig.defaultExcludeAnnotations());

    //
    apiListingReferenceScanner.setResourceGroupingStrategy(springSwaggerConfig.defaultResourceGroupingStrategy());

    //Path provider used to generate the appropriate uri's
    apiListingReferenceScanner.setSwaggerPathProvider(apiPathProvider());

    //Must match the swagger group set on the SwaggerApiResourceListing
    apiListingReferenceScanner.setSwaggerGroup(SWAGGER_GROUP);

    //Only include paths that match the supplied regular expressions
    apiListingReferenceScanner.setIncludePatterns(DEFAULT_INCLUDE_PATTERNS);

    return apiListingReferenceScanner;
  }

  /**
   * Example of a custom path provider
   */
  @Bean
  public ApiPathProvider apiPathProvider() {
    ApiPathProvider apiPathProvider = new ApiPathProvider(docsLocation);
    apiPathProvider.setDefaultPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
    return apiPathProvider;
  }


  private List<AuthorizationType> authorizationTypes() {
    ArrayList<AuthorizationType> authorizationTypes = new ArrayList<AuthorizationType>();

    List<AuthorizationScope> authorizationScopeList = newArrayList();

    authorizationScopeList.add(new AuthorizationScope("global", "access all"));

    List<GrantType> grantTypes = newArrayList();

    grantTypes.add(new ImplicitGrant(new LoginEndpoint(apiPathProvider().getAppBasePath() + "/user/authenticate"), "access_token"));

    return authorizationTypes;
  }

  @Bean
  public SwaggerPathProvider relativeSwaggerPathProvider() {
    return new ApiRelativeSwaggerPathProvider();
  }

  private class ApiRelativeSwaggerPathProvider extends DefaultSwaggerPathProvider {

    @Override
    public String getAppBasePath() {
      return "/";
    }

    @Override
    public String getSwaggerDocumentationBasePath() {
      return "/api-docs";
    }
  }

}
