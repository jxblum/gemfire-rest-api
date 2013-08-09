package org.gopivotal.app.client;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The AbstractClientApp class...
 * <p/>
 * @author John Blum
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @since 7.x
 */
@SuppressWarnings("unused")
public abstract class AbstractClientApp {

  // GemFire Cache Client configuration
  protected static final String APPLICATION_CONFIG_LOCATION = "META-INF/gemfire/client-cache-config.xml";
  protected static final String GEMFIRE_TEMPLATE_BEAN_ID = "gemfireTemplate";

  // GemFire REST API Web Service Interface configuration
  protected static final String BASE_URL = "http://localhost:8080";
  protected static final String GEMFIRE_REST_API_CONTEXT = "/gemfire-api";
  protected static final String GEMFIRE_REST_API_VERSION = "/v2";

  protected static final URI GEMFIRE_REST_API_WEB_SERVICE_URL = URI.create(BASE_URL + GEMFIRE_REST_API_CONTEXT
    + GEMFIRE_REST_API_VERSION);

  private static ApplicationContext context;

  private static GemfireTemplate gemfireTemplate;

  private static RestTemplate restTemplate;

  protected static ApplicationContext getApplicationContext() {
    if (context == null) {
      context = new ClassPathXmlApplicationContext(APPLICATION_CONFIG_LOCATION);
    }
    return context;
  }

  protected static GemfireTemplate getGemFireTemplate() {
    if (gemfireTemplate == null) {
      gemfireTemplate = getApplicationContext().getBean(GEMFIRE_TEMPLATE_BEAN_ID, GemfireTemplate.class);
    }
    return gemfireTemplate;
  }

  protected static RestTemplate getRestTemplate() {
    if (restTemplate == null) {
      restTemplate = new RestTemplate();

      //final List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(
      //  restTemplate.getMessageConverters());
      final List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

      messageConverters.add(new ByteArrayHttpMessageConverter());
      messageConverters.add(new ResourceHttpMessageConverter());
      messageConverters.add(new StringHttpMessageConverter());
      messageConverters.add(createMappingJackson2HttpMessageConverter());
      messageConverters.add(createMarshallingHttpMessageConverter());

      restTemplate.setMessageConverters(messageConverters);
    }
    return restTemplate;
  }

  protected static HttpMessageConverter<Object> createMappingJackson2HttpMessageConverter() {
    final Jackson2ObjectMapperFactoryBean objectMapperFactoryBean = new Jackson2ObjectMapperFactoryBean();

    objectMapperFactoryBean.setFailOnEmptyBeans(true);
    objectMapperFactoryBean.setIndentOutput(true);
    objectMapperFactoryBean.setDateFormat(new SimpleDateFormat("MM/dd/yyyy"));
    objectMapperFactoryBean.setFeaturesToDisable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapperFactoryBean.setFeaturesToEnable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS,
      com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES,
      com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    objectMapperFactoryBean.afterPropertiesSet();

    final MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();

    httpMessageConverter.setObjectMapper(objectMapperFactoryBean.getObject());

    return httpMessageConverter;
  }

  protected static HttpMessageConverter<Object> createMarshallingHttpMessageConverter() {
    final Jaxb2Marshaller jaxbMarshaller = new Jaxb2Marshaller();

    jaxbMarshaller.setContextPaths("org.gopivotal.app.domain", "org.gopivotal.app.web.controllers.support");
    jaxbMarshaller.setMarshallerProperties(Collections.singletonMap("jaxb.formatted.output", Boolean.TRUE));

    return new MarshallingHttpMessageConverter(jaxbMarshaller);
  }

  protected static URI toUri(final String... pathSegments) {
    return toUri(GEMFIRE_REST_API_WEB_SERVICE_URL, pathSegments);
  }

  protected static URI toUri(final URI baseUrl, final String... pathSegments) {
    return UriComponentsBuilder.fromUri(baseUrl).pathSegment(pathSegments).build().toUri();
  }

}
