package com.cloudcredo.cloudfoundry.test;

import com.cloudcredo.cloudfoundry.test.annotation.RabbitMQCloudFoundryService;
import com.cloudcredo.cloudfoundry.test.annotation.RedisCloudFoundryService;
import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author: chris
 * @date: 10/05/2013
 */
@RunWith(CloudFoundryJUnitClassRunner.class)
@ContextConfiguration({
        "classpath:META-INF/cf-test/rabbit-context.xml",
        "classpath:META-INF/cf-test/redis-context.xml",
})
@RabbitMQCloudFoundryService
@RedisCloudFoundryService
public class MultiCloudFoundryServiceTest {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    private final String ROUTE = "RabbitMQCloudFoundryServiceTest.Queue";

    @Autowired
    private RedisTemplate<UUID, DomainObject> redisTemplate;

    @Before
    public void declareQueue() {
        Queue testQueue = new Queue(ROUTE);
        amqpAdmin.declareQueue(testQueue);
    }

    @After
    public void deleteQueue() {
        amqpAdmin.deleteQueue(ROUTE);
    }

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

    @Test
    public void exampleHandshake() throws InterruptedException, ExecutionException, TimeoutException {

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> received = executor.submit(new Callable<String>() {

            public String call() throws Exception {
                Message message = null;
                for (int i = 0; i < 5; i++) {
                    message = rabbitTemplate.receive(ROUTE);
                    if (message != null) {
                        break;
                    }
                    Thread.sleep(100L);
                }

                Assertions.assertThat(message).isNotNull();
                rabbitTemplate.send(message.getMessageProperties().getReplyTo(), message);
                return (String) rabbitTemplate.getMessageConverter().fromMessage(message);
            }

        });
        String result = (String) rabbitTemplate.convertSendAndReceive(ROUTE, "ping");
        Assertions.assertThat("ping").isEqualTo(received.get(1, TimeUnit.SECONDS));
        Assertions.assertThat("ping").isEqualTo(result);

        Object empty = (String) rabbitTemplate.receiveAndConvert(ROUTE);
        Assertions.assertThat(empty).isNull();
    }

}
