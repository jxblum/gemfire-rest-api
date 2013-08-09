package org.gopivotal.app.tests;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gopivotal.app.domain.Person;
import org.junit.Test;

/**
 * The DomainObjectToFromXmlSerializationTest class...
 * <p/>
 * @author John Blum
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since 7.5
 */
@SuppressWarnings("unused")
public class DomainObjectToFromXmlSerializationTest {

  @Test
  public void testToFromXml() throws Exception {
    final JAXBContext context = JAXBContext.newInstance(Person.class);

    final Unmarshaller unmarshaller = context.createUnmarshaller();

    final Object value = unmarshaller.unmarshal(getClass().getResourceAsStream("/etc/data/joeDoe.xml"));

    assertTrue(value instanceof Person);

    final Person joeDoe = (Person) value;

    assertEquals("Joe", joeDoe.getFirstName());
    assertEquals("Doe", joeDoe.getLastName());

    System.out.printf("Person is... %n%1$s", joeDoe);
  }

}
