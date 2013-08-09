package org.gopivotal.app.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The Gender enum is a enumeration of genders (sexes).
 * <p/>
 * @author John Blum
 * @since 7.5
 */
@XmlEnum(String.class)
@XmlType
@SuppressWarnings("unused")
public enum Gender {
  FEMALE,
  MALE
}
