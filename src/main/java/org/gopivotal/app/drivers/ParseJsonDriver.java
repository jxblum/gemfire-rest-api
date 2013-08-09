package org.gopivotal.app.drivers;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.gopivotal.app.domain.Person;

/**
 * The ParseJsonDriver class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@SuppressWarnings("unused")
public class ParseJsonDriver extends AbstractBaseDriver {

  public static void main(final String... args) throws Exception {
    final ObjectMapper mapper = new ObjectMapper();

    mapper.setDateFormat(new SimpleDateFormat("MM/dd/yyyy"));

    final Person jonDoe = mapper.readValue(ParseJsonDriver.class.getResourceAsStream("/etc/data/jonDoe.json"), Person.class);

    System.out.printf("Person is (%1$s).", jonDoe);
  }

}
