package org.gopivotal.app.drivers;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.gopivotal.app.domain.Gender;
import org.gopivotal.app.domain.Person;
import org.gopivotal.app.domain.Product;
import org.gopivotal.app.web.controllers.support.RegionData;

/**
 * The AbstractBaseDriver class is a base class encapsulating common functionality to create domain objects, etc.
 * <p/>
 * @author John Blum
 * @see java.util.Calendar
 * @see java.util.Date
 * @see org.gopivotal.app.domain.Person
 * @see org.gopivotal.app.domain.Product
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class AbstractBaseDriver {

  protected static Long createIdentifier() {
    return Calendar.getInstance().getTimeInMillis();
  }

  protected static Person createPerson(final String firstName,
                                       final String middleName,
                                       final String lastName,
                                       final Date birthDate,
                                       final Gender gender)
  {
    final Person person = new Person(createIdentifier());
    person.setFirstName(firstName);
    person.setMiddleName(middleName);
    person.setLastName(lastName);
    person.setBirthDate(birthDate);
    person.setGender(gender);
    return person;
  }

  protected static Product createProduct(final String serialNumber,
                                         final String name,
                                         final String description,
                                         final BigDecimal price,
                                         final Integer units)
  {
    final Product product = new Product(serialNumber);
    product.setName(name);
    product.setDescription(description);
    product.setPrice(price);
    product.setUnits(units);
    return product;
  }

  protected static <T> RegionData<T> createRegion(final String regionNamePath, final T... data) {
    final RegionData<T> region = new RegionData<T>(regionNamePath);
    region.add(data);
    return region;
  }

}
