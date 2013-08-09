package org.gopivotal.app.domain;

/**
 * The Identifiable interface defines a contract for application domain objects to uniquely identify instances of a
 * particular class type.
 * <p/>
 * @author John Blum
 * @see java.lang.Comparable
 * @since 7.5
 */
@SuppressWarnings("unused")
public interface Identifiable<T extends Comparable<T>> {

  public T getId();

  public void setId(T id);

}
