package org.gopivotal.app.drivers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gopivotal.app.domain.Person;

/**
 * The ParseXmlDriver class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@SuppressWarnings("unused")
public class ParseXmlDriver extends AbstractBaseDriver {

  public static void main(final String... args) throws Exception {
    final JAXBContext context = JAXBContext.newInstance("org.gopivotal.app.domain");

    final Unmarshaller unmarshaller = context.createUnmarshaller();

    final Person jonDoe = (Person) unmarshaller.unmarshal(ParseXmlDriver.class.getResourceAsStream("/etc/data/jonDoe.xml"));

    System.out.printf("Person is (%1$s).", jonDoe);
  }

}
