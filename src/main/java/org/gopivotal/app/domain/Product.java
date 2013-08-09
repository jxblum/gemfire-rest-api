package org.gopivotal.app.domain;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gemstone.gemfire.internal.lang.ObjectUtils;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

/**
 * The Product class is an abstraction modeling a manufactured product.
 * <p/>
 * @author John Blum
 * @since 7.5
 */
@Region("Products")
@XmlRootElement(name = "product")
@XmlType(name = "org.gopivotal.app.domain.Product", propOrder = { "serialNumber", "name", "description", "price", "units" })
@SuppressWarnings("unused")
public class Product implements DomainObject<String> {

  private static final long serialVersionUID = 6909876123l;

  private BigDecimal price;

  private Integer units;

  private @Id String serialNumber;
  private String name;
  private String description;

  public Product() {
  }

  public Product(final String serialNumber) {
    this.serialNumber = serialNumber;
  }

  @JsonIgnore
  @XmlAttribute(name = "id")
  public String getId() {
    return getSerialNumber();
  }

  @JsonIgnore
  public void setId(final String id) {
    setSerialNumber(id);
  }

  @XmlElement(name = "serialNumber")
  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(final String serialNumber) {
    this.serialNumber = serialNumber;
  }

  @XmlElement(name = "name")
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @XmlElement(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @XmlElement(name = "price")
  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(final BigDecimal price) {
    this.price = price;
  }

  @XmlElement(name = "units")
  public Integer getUnits() {
    return units;
  }

  public void setUnits(final Integer units) {
    this.units = units;
  }

  @JsonIgnore
  public boolean isInStock() {
    final Integer units = getUnits();
    return (units != null && units > 0);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Product)) {
      return false;
    }

    final Product that = (Product) obj;

    return ObjectUtils.equals(this.getSerialNumber(), that.getSerialNumber())
      || ObjectUtils.equals(this.getName(), that.getName());
  }

  @Override
  public int hashCode() {
    int hashValue = 17;
    hashValue = 37 * hashValue + ObjectUtils.hashCode(this.getSerialNumber());
    hashValue = 37 * hashValue + ObjectUtils.hashCode(this.getName());
    return hashValue;
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder("{ type = ");
    buffer.append(getClass().getName());
    buffer.append(", serialNumber = ").append(getSerialNumber());
    buffer.append(", name = ").append(getName());
    buffer.append(", description = ").append(getDescription());
    buffer.append(", price = ").append(getPrice());
    buffer.append(", units = ").append(getUnits());
    buffer.append(" }");
    return buffer.toString();
  }

}
