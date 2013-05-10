package com.cloudcredo.cloudfoundry.test;

import com.cloudcredo.cloudfoundry.test.annotation.RedisCloudFoundryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

/**
 * @author: chris
 * @date: 05/05/2013
 */
@RunWith(CloudFoundryJUnitClassRunner.class)
@ContextConfiguration("classpath:META-INF/amqp/redis-context.xml")
@RedisCloudFoundryService
public class RedisCloudFoundryServiceTest {

    @Autowired
    private RedisTemplate<UUID, DomainObject> redisTemplate;

    @Test
    public void shouldPersistAndRetrieveManyShoppingBasketsWithShoppingBasketId() {
        DomainObject do1 = new DomainObject("Chris", 1);
        DomainObject do2 = new DomainObject("Steve", 2);

        redisTemplate.opsForValue().getAndSet(do1.getId(), do1);
        redisTemplate.opsForValue().getAndSet(do2.getId(), do2);

        DomainObject actual = redisTemplate.opsForValue().get(do1.getId());
        Assert.assertEquals(do1, actual);

        DomainObject actual2 = redisTemplate.opsForValue().get(do2.getId());
        Assert.assertEquals(do2, actual2);
    }
}
