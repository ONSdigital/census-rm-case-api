package uk.gov.ons.census.caseapisvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.stackdriver.StackdriverConfig;
import io.micrometer.stackdriver.StackdriverMeterRegistry;
import java.time.Duration;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Value("${management.metrics.export.stackdriver.project-id}")
  private String stackdriverProjectId;

  @Value("${management.metrics.export.stackdriver.enabled}")
  private boolean stackdriverEnabled;

  @Value("${management.metrics.export.stackdriver.step}")
  private String stackdriverStep;

  @Bean
  public MapperFacade mapperFacade() {
    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    return mapperFactory.getMapperFacade();
  }

  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    rabbitTemplate.setChannelTransacted(true);
    return rabbitTemplate;
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  @Bean
  StackdriverConfig stackdriverConfig() {
    return new StackdriverConfig() {
      @Override
      public Duration step() {
        return Duration.parse(stackdriverStep);
      }

      @Override
      public boolean enabled() {
        return stackdriverEnabled;
      }

      @Override
      public String projectId() {
        return stackdriverProjectId;
      }

      @Override
      public String get(String key) {
        return null;
      }
    };
  }

  @Bean
  StackdriverMeterRegistry meterRegistry(StackdriverConfig stackdriverConfig) {
    return StackdriverMeterRegistry.builder(stackdriverConfig).build();
  }
}
