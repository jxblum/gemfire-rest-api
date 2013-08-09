package org.gopivotal.app.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

/**
 * The CustomRepositoryRestMvcConfiguration class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@Configuration
@Import({RepositoryRestMvcConfiguration.class})
@SuppressWarnings("unused")
public class RestExporterConfiguration {

}
