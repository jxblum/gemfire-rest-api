package org.gopivotal.app.domain;

import java.util.Date;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.gemstone.gemfire.internal.lang.ObjectUtils;

import org.gopivotal.app.domain.support.ResourceSupport;
import org.gopivotal.app.util.DateTimeUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

/**
 * The Person class is an abstraction modeling a person.
 * <p/>
 * @author John Blum
 * @see org.gopivotal.app.domain.DomainObject
 * @see org.gopivotal.app.domain.support.ResourceSupport
 * @since 7.5
 */
@Region("People")
@XmlRootElement(name = "person")
@XmlType(name = "org.gopivotal.app.domain.Person", propOrder = { "firstName", "middleName", "lastName", "birthDate", "gender" })
@SuppressWarnings("unused")
public class Person extends ResourceSupport implements DomainObject<Long> {

  private static final long serialVersionUID = 42108163264l;

  protected static final String DOB_FORMAT_PATTERN = "MM/dd/yyyy";

  private @Id Long id;

  private Date birthDate;

  private Gender gender;

  private String firstName;
  private String middleName;
  private String lastName;

  public Person() {
  }

  public Person(final Long id) {
    this.id = id;
  }

  public Person(final String firstName, final String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @XmlAttribute(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @XmlElement(name = "firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  @XmlElement(name = "lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  @XmlElement(name = "middleName")
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(final String middleName) {
    this.middleName = middleName;
  }

  @XmlElement(name = "birthDate")
  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(final Date birthDate) {
    this.birthDate = birthDate;
  }

  @XmlElement(name = "gender")
  public Gender getGender() {
    return gender;
  }

  public void setGender(final Gender gender) {
    this.gender = gender;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Person)) {
      return false;
    }

    final Person that = (Person) obj;

    return (ObjectUtils.equals(this.getId(), that.getId())
      || (ObjectUtils.equals(this.getBirthDate(), that.getBirthDate())
      && ObjectUtils.equals(this.getLastName(), that.getLastName())
      && ObjectUtils.equals(this.getFirstName(), that.getFirstName())));
  }

  @Override
  public int hashCode() {
    int hashValue = 17;
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getId());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getBirthDate());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getLastName());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getFirstName());
    return hashValue;
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder("{ type = ");
    buffer.append(getClass().getName());
    buffer.append(", id = ").append(getId());
    buffer.append(", firstName = ").append(getFirstName());
    buffer.append(", middleName = ").append(getMiddleName());
    buffer.append(", lastName = ").append(getLastName());
    buffer.append(", birthDate = ").append(DateTimeUtils.format(getBirthDate(), DOB_FORMAT_PATTERN));
    buffer.append(", gender = ").append(getGender());
    buffer.append(" }");
    return buffer.toString();
  }

}
