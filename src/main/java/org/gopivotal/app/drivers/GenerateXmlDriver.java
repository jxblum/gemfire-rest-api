package org.gopivotal.app.drivers;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.gopivotal.app.domain.Gender;
import org.gopivotal.app.domain.Person;
import org.gopivotal.app.domain.Product;
import org.gopivotal.app.util.DateTimeUtils;
import org.gopivotal.app.web.controllers.support.RegionData;

/**
 * The GenerateXmlDriver class...
 * <p/>
 * @author John Blum
 * @see
 * @since 7.x
 */
@SuppressWarnings("unused")
public class GenerateXmlDriver extends AbstractBaseDriver {

  public static void main(final String... args) throws Exception {
    final Person person = createPerson("Jon", "T", "Doe", DateTimeUtils.createDate(1977, Calendar.JUNE, 12), Gender.MALE);
    final Product product = createProduct("X11235RU480T2", "iPhone", "Apple iPhone 5 64 GB", new BigDecimal("399.99"), 1250560);

    final RegionData<Person> peopleRegion = createRegion("People", person);
    final RegionData<Product> productRegion = createRegion("Products", product);

    final JAXBContext context = JAXBContext.newInstance("org.gopivotal.app.domain:org.gopivotal.app.web.controllers.support");

    final Marshaller marshaller = context.createMarshaller();

    StringWriter writer = new StringWriter();

    marshaller.marshal(person, writer);
    //marshaller.marshal(peopleRegion, writer);

    System.out.printf("XML document for Person is...%n%1$s", writer.toString());

    writer = new StringWriter();
    marshaller.marshal(product, writer);
    //marshaller.marshal(productRegion, writer);

    System.out.printf("%nXML document for Product is...%n%1$s", writer.toString());
  }

}
