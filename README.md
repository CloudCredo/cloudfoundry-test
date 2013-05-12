#Cloud Foundry Integration Test

The aim of this project is to provide test classes to make running integration tests against [Cloud Foundry][2] environments
simpler.

Currently running integration tests in any environment may entail manually setting up environments for tests
to run against. This project takes away that pain by allowing integration tests to create Cloud Foundry services,
automatically bind to them, execute the tests and (optionally) tear down the services.


##Usage

The tool requires a running instance of Cloud Foundry that is able to make it's services public. Micro Cloud
Foundry is perfect for this as it requires no extra configuration. It may also be possible to run this against your
private hosted instance of Cloud Foundry.

Two forms of usage are available; standalone and through the use of the Springframework [`<cloud>`][1] namespace.


###Configuration

The tool require that the folowing environment variables are set

1. `target`: This is the target URL of the instnace of Cloud Foundry to run tests against. Defaults to http://api.vcap.me
2. `mbus`: This is the URL of the Nats service. Defaults to nats://nats:nats@api.vcap.me:4222.
3. `cloud_foundry_password`: The password of the Cloud Foundry user.
4. `cloud_foundry_email`: The email address of the Cloud Foundry user.


###Standard Usage
Comng soon.

###Usage with Spring

If you are using the Spring cloud namespace then you can use the `CloudFoundryJUnitClassRunner` directly in your
unit tests just as you would with the regular SpringJunit4TestRunner, the service will be created in your target Cloud
Foundry and the Spring context will auto connect to the service for you.

Create a test annotated to RunWith use the CloudFoundryTestRunner.

```java
@RunWith(CloudFoundryJUnitClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/example-test.xml")
public class ExampleIntegrationTest {
   @Resource(name = "rabbitTemplate")
   private RabbitTemplate rabbitTemplate;
```

Provide the context(s) containing the `<cloud>` namespace for your service.

example-test.xml:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<beans:beans ...>
  <cloud:rabbit-connection-factory id="connectionFactory"/>
  <rabbit:admin connection-factory="connectionFactory" />
</beans>
```

Thats it, run your test!

[1]: http://blog.springsource.org/2011/11/09/using-cloud-foundry-services-with-spring-applications-part-3-the-cloud-namespace
[2]: http://www.cloudfoundry.org