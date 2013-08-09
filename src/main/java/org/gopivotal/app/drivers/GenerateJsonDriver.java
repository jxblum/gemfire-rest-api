package org.gopivotal.app.drivers;

import java.math.BigDecimal;
import java.util.Calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.gopivotal.app.domain.Gender;
import org.gopivotal.app.domain.Person;
import org.gopivotal.app.domain.Product;
import org.gopivotal.app.util.DateTimeUtils;

/**
 * The GenerateJsonDriver class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@SuppressWarnings("unused")
public class GenerateJsonDriver extends AbstractBaseDriver {

  public static void main(final String... args) throws Exception {
    final Person person = createPerson("Jon", "T", "Doe", DateTimeUtils.createDate(1981, Calendar.JUNE, 12), Gender.MALE);
    final Product product = createProduct("X11235RU480T2", "iPhone", "Apple iPhone 5 64 GB", new BigDecimal("399.99"), 1250560);

    final ObjectMapper mapper = new ObjectMapper();

    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    String json = mapper.writeValueAsString(person);

    System.out.printf("JSON document for Person is...%n%1$s", json);

    json = mapper.writeValueAsString(product);

    System.out.printf("%nJSON document for Product is...%n%1$s", json);
  }

}
