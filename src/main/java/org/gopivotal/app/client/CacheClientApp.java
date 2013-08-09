package org.gopivotal.app.client;

import java.util.Calendar;

import org.gopivotal.app.domain.Person;
import org.gopivotal.app.util.DateTimeUtils;
import org.springframework.util.Assert;

/**
 * The CacheClientApp class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
public class CacheClientApp extends AbstractClientApp {

  private static final String KEY = "1";

  public static void main(final String... args) {
    final Person person = getGemFireTemplate().get(KEY);

    System.out.printf("Person is (%1$s)%n", person);

    Assert.isTrue("Jack".equals(person.getFirstName()));
    Assert.isTrue("Handy".equals(person.getLastName()));

    person.setFirstName("Hop");
    person.setMiddleName("Key");
    person.setLastName("Doe");
    person.setBirthDate(DateTimeUtils.createDate(1987, Calendar.APRIL, 1));

    System.out.printf("Modified Person is (%1$s)%n", person);

    getGemFireTemplate().put(KEY, person);
  }

}
