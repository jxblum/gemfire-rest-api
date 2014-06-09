package org.gopivotal.app.web.swagger.config;

import javax.servlet.ServletContext;

import com.mangofactory.swagger.core.SwaggerPathProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings("unused")
public class ApiPathProvider implements SwaggerPathProvider {

  protected static final String REST_API_VERSION = "/v2";

  @Autowired
  private ServletContext servletContext;

  private SwaggerPathProvider defaultPathProvider;

  private String docsLocation;

  public ApiPathProvider(String docsLocation) {
    this.docsLocation = docsLocation;
  }

  @Override
  public String getApiResourcePrefix() {
    return defaultPathProvider.getApiResourcePrefix();
  }

  public String getAppBasePath() {
    /*
    return UriComponentsBuilder.fromHttpUrl(docsLocation).path(servletContext.getContextPath()).path(REST_API_VERSION)
      .build().toString();
    */
    return UriComponentsBuilder.fromHttpUrl(docsLocation).path(servletContext.getContextPath()).build().toString();
  }

  @Override
  public String getSwaggerDocumentationBasePath() {
    return UriComponentsBuilder.fromHttpUrl(getAppBasePath()).pathSegment("api-docs/").build().toString();
  }

  @Override
  public String getRequestMappingEndpoint(String requestMappingPattern) {
    return defaultPathProvider.getRequestMappingEndpoint(requestMappingPattern);
  }

  public void setDefaultPathProvider(SwaggerPathProvider defaultSwaggerPathProvider) {
    this.defaultPathProvider = defaultSwaggerPathProvider;
  }

}
