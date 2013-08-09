package org.gopivotal.app.dao;

import java.util.Collection;

import org.gopivotal.app.domain.Person;
import org.springframework.data.repository.CrudRepository;

/**
 * The PersonRepository class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@SuppressWarnings("unused")
public interface PersonRepository extends CrudRepository<Person, Long> {

  public Collection<Person> findByFirstNameAndLastName(String firstName, String lastName);

}
