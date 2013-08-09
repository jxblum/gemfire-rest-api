package org.gopivotal.app.config.postprocessors;

import com.gemstone.gemfire.cache.AttributesFactory;
import com.gemstone.gemfire.cache.DataPolicy;
import com.gemstone.gemfire.cache.RegionAttributes;
import com.gemstone.gemfire.cache.Scope;
import com.gemstone.gemfire.internal.cache.GemFireCacheImpl;
import com.gemstone.gemfire.internal.cache.InternalRegionArguments;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/**
 * The GemFireCachePostProcessor class is a BeanPostProcessor for creating the internal, hidden __ParameterizedQueries__
 * GemFire Cache Region.
 * <p/>
 * @author John Blum
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @since 7.5
 */
@SuppressWarnings("unused")
public class GemFireCachePostProcessor implements BeanPostProcessor, Ordered {

  protected static final String GEMFIRE_CACHE_BEAN_NAME = "gemfireCache";

  protected static final String PARAMETERIZED_QUERY_REGION_NAME = "__ParameterizedQueries__";

  public int getOrder() {
    return 0;
  }

  public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
    return bean;
  }

  @SuppressWarnings("unchecked")
  public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    if (GEMFIRE_CACHE_BEAN_NAME.equals(beanName) && bean instanceof GemFireCacheImpl) {
      try {
        final GemFireCacheImpl gemfireCache = (GemFireCacheImpl) bean;

        final InternalRegionArguments regionArguments = new InternalRegionArguments();

        regionArguments.setIsUsedForMetaRegion(true);

        final AttributesFactory attributesFactory = new AttributesFactory();

        attributesFactory.setConcurrencyChecksEnabled(false);
        attributesFactory.setDataPolicy(DataPolicy.REPLICATE);
        attributesFactory.setKeyConstraint(String.class);
        attributesFactory.setScope(Scope.DISTRIBUTED_NO_ACK);
        attributesFactory.setStatisticsEnabled(false);
        attributesFactory.setValueConstraint(String.class);

        final RegionAttributes regionAttributes = attributesFactory.create();

        gemfireCache.createVMRegion(PARAMETERIZED_QUERY_REGION_NAME, regionAttributes, regionArguments);
      }
      catch (Exception e) {
        throw new BeanInitializationException(String.format("Failed to create (%1$s) GemFire Cache Region!",
          PARAMETERIZED_QUERY_REGION_NAME), e);
      }
    }

    return bean;
  }

}
