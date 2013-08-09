package org.gopivotal.app.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.gopivotal.app.domain.DomainObject;
import org.gopivotal.app.domain.Person;
import org.junit.Test;

/**
 * The DomainObjectToFromJsonSerializationTest class...
 * <p/>
 * @author John Blum
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since 7.5
 */
public class DomainObjectToFromJsonSerializationTest {

  @Test
  public void testToFromJson() throws IOException {
    final InputStream jsonStream = getClass().getResourceAsStream("/etc/data/joeDoe.json");

    assertNotNull(jsonStream);

    final ObjectMapper mapper = new ObjectMapper();

    mapper.setDateFormat(new SimpleDateFormat("MM/dd/yyyy"));

    final Object value = mapper.readValue(jsonStream, Object.class);

    assertNotNull(value);
    //assertTrue(value instanceof Person);

    System.out.printf("Value of type (%1$s) is (%2$s)%n", value.getClass().getName(), value);

    final Person joeDoe = mapper.convertValue(value, Person.class);

    assertNotNull(joeDoe);

    System.out.printf("Converted Person is (%1$s)%n", joeDoe);

    joeDoe.setFirstName("Jack");
    joeDoe.setLastName("Handy");

    final String jsonContent = mapper.writeValueAsString(joeDoe);

    System.out.printf("JSON content (%1$s)%n", jsonContent);

    // NOTE results in a ClassCastException, damn it!
    //final Person anotherValue = (Person) mapper.readValue(jsonContent, Object.class);
    final Object anotherValue = mapper.readValue(jsonContent, Object.class);

    assertNotNull(anotherValue);

    System.out.printf("Value of type (%1$s) is (%2$s)%n", anotherValue.getClass().getName(), anotherValue);

    final Object jackHandy = mapper.readValue(jsonContent, DomainObject.class);

    assertNotNull(jackHandy);

    System.out.printf("Person is (%1$s) of type (%2$s)%n", jackHandy, jackHandy.getClass().getName());
  }

}
