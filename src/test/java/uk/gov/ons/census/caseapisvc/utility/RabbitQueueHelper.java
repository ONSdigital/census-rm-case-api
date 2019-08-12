package uk.gov.ons.census.caseapisvc.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
@EnableRetry
public class RabbitQueueHelper {
  @Autowired private ConnectionFactory connectionFactory;

  @Autowired private RabbitTemplate rabbitTemplate;

  @Autowired private AmqpAdmin amqpAdmin;

  public BlockingQueue<String> listen(String queueName) {
    BlockingQueue<String> transfer = new ArrayBlockingQueue(50);

    org.springframework.amqp.core.MessageListener messageListener =
        message -> {
          String msgStr = new String(message.getBody());
          transfer.add(msgStr);
        };
    SimpleMessageListenerContainer container =
        new SimpleMessageListenerContainer(connectionFactory);
    container.setMessageListener(messageListener);
    container.setQueueNames(queueName);
    container.start();

    return transfer;
  }

  @Retryable(
      value = {java.io.IOException.class},
      maxAttempts = 10,
      backoff = @Backoff(delay = 5000))
  public void purgeQueue(String queueName) {
    amqpAdmin.purgeQueue(queueName);
  }

  public String checkExpectedMessageReceived(BlockingQueue<String> queue)
      throws IOException, InterruptedException {
    String message = queue.poll(20, TimeUnit.SECONDS);
    assertNotNull("Did not receive message before timeout", message);
    return message;
  }
}
