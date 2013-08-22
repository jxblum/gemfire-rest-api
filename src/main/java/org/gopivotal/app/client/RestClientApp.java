package org.gopivotal.app.client;

import java.net.URI;
import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.gopivotal.app.domain.Address;
import org.gopivotal.app.domain.Gender;
import org.gopivotal.app.domain.Person;
import org.gopivotal.app.domain.State;
import org.gopivotal.app.util.DateTimeUtils;
import org.gopivotal.app.web.controllers.support.UpdateOp;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The RestClientApp class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@SuppressWarnings("unused")
public class RestClientApp extends AbstractClientApp {

  private static final String OP_URL_QUERY_PARAMETER_NAME = "op";

  public static void main(final String... args) throws Exception {
    //final Person jonDoe = doGet("/People", "1", Person.class);
    //System.out.printf("Person is (%1$s)%n", jonDoe);

    //final String json = doGetAsJson("/People", "1");
    //System.out.printf("Person is (%1$s)%n", json);

    doPost("/People/Address", new Address("2021 Yamhill", "Portland", State.OREGON, "12345"));

    //final Address address = doGet("/People/Address", "4", Address.class);
    //System.out.printf("Address is (%1$s)%n", address);

    //doPutAsReplace("/People");
    //doPutAsCas("/People");
  }

  private static <T> T doGet(final String regionNamePath, final String key, final Class<T> type) {
    return getRestTemplate().getForObject(toUri(regionNamePath, key), type);
  }

  private static String doGetAsJson(final String regionNamePath, final String key) {
    return getRestTemplate().getForObject(toUri(regionNamePath, key), String.class);
  }

  private static <T> void doPost(final String regionNamePath, final T domainObject) {
    System.out.printf("Location is (%1$s)%n", getRestTemplate().postForLocation(toUri(regionNamePath), domainObject));
  }

  private static void doPutAsCas(final String regionNamePath) {
    try {
      //final Person pieDoe = new Person(3l);
      final Person pieDoe = new Person(4l);

      pieDoe.setFirstName("Pie");
      pieDoe.setMiddleName("R");
      pieDoe.setLastName("Doe");
      pieDoe.setBirthDate(DateTimeUtils.createDate(2012, Calendar.NOVEMBER, 2));
      pieDoe.setGender(Gender.FEMALE);

      final Person cookieDoe = new Person(pieDoe.getId());

      cookieDoe.setFirstName("Cookie");
      cookieDoe.setMiddleName("R");
      cookieDoe.setLastName("Doe");
      cookieDoe.setBirthDate(DateTimeUtils.createDate(2004, Calendar.APRIL, 4));
      cookieDoe.setGender(Gender.FEMALE);

      final URI putReplaceUri = UriComponentsBuilder.fromUri(GEMFIRE_REST_API_WEB_SERVICE_URL)
        //.pathSegment(regionNamePath, String.valueOf(pieDoe.getId()))
        .pathSegment(regionNamePath, "4")
        .queryParam(OP_URL_QUERY_PARAMETER_NAME, UpdateOp.CAS.name())
        .build()
        .toUri();

      getRestTemplate().put(putReplaceUri, new UpdateCasHolder(pieDoe, cookieDoe));

      System.out.println("PUT (CAS) passed.");
    }
    catch (RestClientException e) {
      System.out.printf("PUT (CAS) failed: %1$s%n", e.getMessage());
    }
  }

  private static void doPutAsReplace(final String regionNamePath) {
    try {
      final Person joeDoe = new Person(8l);

      joeDoe.setFirstName("Rowe");
      joeDoe.setMiddleName("T");
      joeDoe.setLastName("Doe");
      joeDoe.setBirthDate(DateTimeUtils.createDate(1977, Calendar.DECEMBER, 2));
      joeDoe.setGender(Gender.MALE);

      final URI putReplaceUri = UriComponentsBuilder.fromUri(GEMFIRE_REST_API_WEB_SERVICE_URL)
        .pathSegment(regionNamePath, String.valueOf(joeDoe))
        .queryParam(OP_URL_QUERY_PARAMETER_NAME, UpdateOp.REPLACE.name())
        .build()
        .toUri();

      getRestTemplate().put(putReplaceUri, joeDoe);

      System.out.println("PUT (REPLACE) passed.");
    }
    catch (RestClientException e) {
      System.out.printf("PUT (REPLACE) failed: %1$s%n", e.getMessage());
    }
  }

  protected static final class UpdateCasHolder {

    private final Person oldValue;
    private final Person newValue;

    protected UpdateCasHolder(final Person oldValue, final Person newValue) {
      this.oldValue = oldValue;
      this.newValue = newValue;
    }

    @JsonProperty("@old")
    public Person getOldValue() {
      return oldValue;
    }

    @JsonProperty("@new")
    public Person getNewValue() {
      return newValue;
    }
  }

  // TODO the following class definition of UpdateCasHolder does not work!
  // TODO Need to understand why Jackson cannot introspect the generically typed old and new value for Person objects
  // and include @type meta-data in the generated JSON
  /*
  protected static final class UpdateCasHolder<T> {

    private final T oldValue;
    private final T newValue;

    protected UpdateCasHolder(final T oldValue, final T newValue) {
      this.oldValue = oldValue;
      this.newValue = newValue;
    }

    @JsonProperty("@old")
    public T getOldValue() {
      return oldValue;
    }

    @JsonProperty("@new")
    public T getNewValue() {
      return newValue;
    }
  }
  */

}
