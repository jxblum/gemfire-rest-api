package org.gopivotal.app.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.gemstone.gemfire.internal.lang.ObjectUtils;

import org.gopivotal.app.domain.support.ResourceSupport;

/**
 * The Address class is abstraction modeling a street address.
 * <p/>
 * @author John Blum
 * @see org.gopivotal.app.domain.State
 * @see org.gopivotal.app.domain.support.ResourceSupport
 * @since 7.5
 */
@XmlRootElement(name = "address")
@XmlType(name = "org.gopivotal.app.domain.Address", propOrder = { "street1", "street2", "city", "state", "zipCode", "zipCodeExt" })
@SuppressWarnings("unused")
public class Address extends ResourceSupport implements DomainObject<Long> {

  private Long id;

  private State state;

  private String street1;
  private String street2;
  private String city;
  private String zipCode;
  private String zipCodeExt;

  public Address() {
  }

  public Address(final Long id) {
    this.id = id;
  }

  public Address(final String street1, final String city, final State state, final String zipCode) {
    setStreet1(street1);
    setCity(city);
    setState(state);
    setZipCode(zipCode);
  }

  public Address(final Address address) {
    setStreet1(address.getStreet1());
    setStreet2(address.getStreet2());
    setCity(address.getCity());
    setState(address.getState());
    setZipCode(address.getZipCode());
    setZipCodeExt(address.getZipCodeExt());
  }

  @XmlAttribute(name = "id")
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @XmlElement(name = "street1")
  public String getStreet1() {
    return street1;
  }

  public void setStreet1(final String street1) {
    this.street1 = street1;
  }

  @XmlElement(name = "street2")
  public String getStreet2() {
    return street2;
  }

  public void setStreet2(final String street2) {
    this.street2 = street2;
  }

  @XmlElement(name = "city")
  public String getCity() {
    return city;
  }

  public void setCity(final String city) {
    this.city = city;
  }

  @XmlElement(name = "state")
  public State getState() {
    return state;
  }

  public void setState(final State state) {
    this.state = state;
  }

  @XmlElement(name = "zipCode")
  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(final String zipCode) {
    this.zipCode = zipCode;
  }

  @XmlElement(name = "zipCodeExt")
  public String getZipCodeExt() {
    return zipCodeExt;
  }

  public void setZipCodeExt(final String zipCodeExt) {
    this.zipCodeExt = zipCodeExt;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Address)) {
      return false;
    }

    final Address that = (Address) obj;

    return ObjectUtils.equals(this.getId(), that.getId())
      || (ObjectUtils.equals(this.getStreet1(), that.getStreet2())
      && ObjectUtils.equalsIgnoreNull(this.getStreet2(), that.getStreet2())
      && ObjectUtils.equals(this.getCity(), that.getCity())
      && ObjectUtils.equals(this.getState(), that.getState())
      && ObjectUtils.equals(this.getZipCode(), that.getZipCode())
      && ObjectUtils.equalsIgnoreNull(this.getZipCodeExt(), that.getZipCodeExt()));
  }

  @Override
  public int hashCode() {
    int hashValue = 17;
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getId());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getStreet1());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getStreet2());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getCity());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getState());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getZipCode());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getZipCodeExt());
    return hashValue;
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder("{ type = ");
    buffer.append(getClass().getName());
    buffer.append(", street1 = ").append(getStreet1());
    buffer.append(", street2 = ").append(getStreet2());
    buffer.append(", city = ").append(getCity());
    buffer.append(", state = ").append(getState());
    buffer.append(", zipCode = ").append(getZipCode());
    buffer.append(", zipCodeExt = ").append(getZipCodeExt());
    return buffer.toString();
  }

}
