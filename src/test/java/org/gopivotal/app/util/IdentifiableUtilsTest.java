package org.gopivotal.app.util;

import static org.junit.Assert.*;

import org.gopivotal.app.domain.Identifiable;
import org.junit.Test;

/**
 * The IdentifiableUtilsTest class is a test suite with test cases testing the contract and functionality of the
 * IdentifiableUtils class.
 * <p/>
 * @author John Blum
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since 7.5
 */
@SuppressWarnings("unused")
public class IdentifiableUtilsTest {

  @Test
  public void testGetId() {
    final IdentifiableObject identifiableObject = new IdentifiableObject(1l);
    assertEquals(1l, IdentifiableUtils.getId(identifiableObject));
  }

  @Test
  public void testSetId() {
    final IdentifiableObject identifiableObject = new IdentifiableObject();
    IdentifiableUtils.setId(identifiableObject, 2l);
    //System.out.printf("%1$s%n", identifiableObject);
    assertEquals(new Long(2l), identifiableObject.getId());
  }

  @Test
  public void testSetIdWithIdObject() {
    final IdObject idObject = new IdObject();

    IdentifiableUtils.setId(idObject, "key");

    assertNull(idObject.getId());

    IdentifiableUtils.setId(idObject, 3);

    assertNull(idObject.getId());

    IdentifiableUtils.setId(idObject, 4l);

    assertEquals(new Long(4l), idObject.getId());
    //System.out.printf("%1$s%n", idObject);
  }

  protected static final class IdObject {

    private Long id;

    public IdObject() {
    }

    public IdObject(final Long id) {
      this.id = id;
    }

    public Long getId() {
      return id;
    }

    public void setId(final Long id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return String.format("%1$s(%2$s)", getClass().getName(), getId());
    }
  }

  protected static final class IdentifiableObject implements Identifiable<Long> {

    private Long id;

    protected IdentifiableObject() {
    }

    protected IdentifiableObject(final Long id) {
      setId(id);
    }

    public Long getId() {
      return id;
    }

    public void setId(final Long id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return String.format("%1$s(%2$s)", getClass().getName(), getId());
    }
  }

}
