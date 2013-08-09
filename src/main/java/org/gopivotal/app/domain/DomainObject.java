package org.gopivotal.app.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The DomainObject interface is a marker interface for application domain objects.
 * <p/>
 * @author John Blum
 * @see java.io.Serializable
 * @see java.lang.Comparable
 * @see org.gopivotal.app.domain.Identifiable
 * @since 7.5
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@SuppressWarnings("unused")
public interface DomainObject<T extends Comparable<T>> extends Identifiable<T>, Serializable {

}
