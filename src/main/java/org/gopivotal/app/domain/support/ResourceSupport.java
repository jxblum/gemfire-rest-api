package org.gopivotal.app.domain.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.springframework.hateoas.Link;
import org.springframework.util.Assert;

/**
 * The ResourceSupport class is an abstract base class with support for links in HTTP REST resources, modeled after the
 * Spring HATEOAS project's ResourceSupport class, but without the type constraint on the ID property, which conflicts
 * with the ID property type of domain Objects in this prototype.
 * <p/>
 * @author John Blum
 * @see java.lang.Iterable
 * @see org.springframework.hateoas.Link
 * @since 7.5
 */
@SuppressWarnings("unused")
public abstract class ResourceSupport implements Iterable<Link> {

  private final List<Link> links = new ArrayList<Link>();

  public boolean add(final Link link) {
    Assert.notNull(link, "The Link to add cannot be null!");
    return this.links.add(link);
  }

  public boolean addAll(final Link[] links) {
    return addAll(Arrays.asList(links));
  }

  public boolean addAll(final Iterable<Link> links) {
    boolean modified = false;

    for (Link link : links) {
      modified |= add(link);
    }

    return modified;
  }

  public Link get(final String rel) {
    for (Link link : this) {
      if (link.getRel().equalsIgnoreCase(rel)) {
        return link;
      }
    }

    return null;
  }

  @XmlElementWrapper(name = "links")
  @XmlElement(name = "link", required = false)
  public List<Link> getLinks() {
    return Collections.unmodifiableList(this.links);
  }

  public Iterator<Link> iterator() {
    return getLinks().iterator();
  }

  public boolean remove(final Link link) {
    return this.links.remove(link);
  }

  public void removeAll() {
    this.links.clear();
  }

}
