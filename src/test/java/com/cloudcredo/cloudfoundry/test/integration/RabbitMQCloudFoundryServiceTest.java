package com.cloudcredo.cloudfoundry.test.integration;

import com.cloudcredo.cloudfoundry.test.CloudFoundryJUnitClassRunner;
import com.cloudcredo.cloudfoundry.test.annotation.RabbitMQCloudFoundryService;
import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * Simple example test that binds to an instance of RabbitMQ running inside of Cloud Foundry, creates some queues and
 * sends some messages.
 *
 * @author: chris
 * @date: 05/04/2013
 */
@RunWith(CloudFoundryJUnitClassRunner.class)
@ContextConfiguration("classpath:META-INF/amqp/rabbit-context.xml")
@RabbitMQCloudFoundryService
public class RabbitMQCloudFoundryServiceTest {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    private final String ROUTE = "RabbitMQCloudFoundryServiceTest.Queue";

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
