package org.jclouds.elb;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.jclouds.collect.PaginatedIterable;
import org.jclouds.collect.PaginatedIterables;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.features.LoadBalancerClient;
import org.jclouds.elb.options.ListLoadBalancersOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code ELB}.
 *
 * @author Adrian Cole
 */
@Test(testName = "ELBTest")
public class ELBTest {


   @Test
   public void testSinglePageResult() throws Exception {
      LoadBalancerClient loadBalancerClient = createMock(LoadBalancerClient.class);
      ListLoadBalancersOptions options = new ListLoadBalancersOptions();
      PaginatedIterable<LoadBalancer> response = PaginatedIterables.forward(ImmutableSet.of(createMock(LoadBalancer.class)));
      
      expect(loadBalancerClient.list(options))
            .andReturn(response)
            .once();

      EasyMock.replay(loadBalancerClient);

      Assert.assertEquals(1, Iterables.size(ELB.listLoadBalancers(loadBalancerClient, options)));
   }


   @Test
   public void testMultiPageResult() throws Exception {
      LoadBalancerClient loadBalancerClient = createMock(LoadBalancerClient.class);
      ListLoadBalancersOptions options = new ListLoadBalancersOptions();
      PaginatedIterable<LoadBalancer> response1 = PaginatedIterables.forwardWithMarker(ImmutableSet.of(createMock(LoadBalancer.class)), "NEXTTOKEN");
      PaginatedIterable<LoadBalancer> response2 = PaginatedIterables.forward(ImmutableSet.of(createMock(LoadBalancer.class)));

      expect(loadBalancerClient.list(anyObject(ListLoadBalancersOptions.class)))
            .andReturn(response1)
            .once();
      expect(loadBalancerClient.list(anyObject(ListLoadBalancersOptions.class)))
            .andReturn(response2)
            .once();

      EasyMock.replay(loadBalancerClient);

      Assert.assertEquals(2, Iterables.size(ELB.listLoadBalancers(loadBalancerClient, options)));
   }

}
