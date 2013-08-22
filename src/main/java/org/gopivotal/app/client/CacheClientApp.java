package org.gopivotal.app.client;

import java.util.Calendar;

import org.gopivotal.app.domain.Address;
import org.gopivotal.app.domain.Person;
import org.gopivotal.app.domain.State;
import org.gopivotal.app.util.DateTimeUtils;
import org.springframework.util.Assert;

/**
 * The CacheClientApp class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@SuppressWarnings("unused")
public class CacheClientApp extends AbstractClientApp {

  public static void main(final String... args) {
    //updatePerson("1");
    createAddress(1l, "100 Main St.", "Portland", State.OREGON, "97005");
  }

  private static Address createAddress(final Long id,
                                       final String street1,
                                       final String city,
                                       final State state,
                                       final String zipCode)
  {
    final Address address = new Address(street1, city, state, zipCode);

    address.setId(id);

    return getPeopleAddressRegionTemplate().put(id.toString(), address);
  }

  private static void updatePerson(final String key) {
    final Person person = getPeopleRegionTemplate().get(key);

    System.out.printf("Original Person is (%1$s)%n", person);

    Assert.isTrue("Jack".equals(person.getFirstName()));
    Assert.isTrue("Handy".equals(person.getLastName()));

    person.setFirstName("Hop");
    person.setMiddleName("Key");
    person.setLastName("Doe");
    person.setBirthDate(DateTimeUtils.createDate(1987, Calendar.APRIL, 1));

    System.out.printf("Modified Person is (%1$s)%n", person);

    getPeopleRegionTemplate().put(key, person);
  }

}
