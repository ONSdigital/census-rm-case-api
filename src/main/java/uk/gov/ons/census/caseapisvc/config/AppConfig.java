package uk.gov.ons.census.caseapisvc.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.cloud.spring.pubsub.support.converter.SimplePubSubMessageConverter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.stackdriver.StackdriverConfig;
import io.micrometer.stackdriver.StackdriverMeterRegistry;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.caseapisvc.utility.ObjectMapperFactory;

@Configuration
public class AppConfig {
  @Value("${management.stackdriver.metrics.export.project-id}")
  private String stackdriverProjectId;

  @Value("${management.stackdriver.metrics.export.enabled}")
  private boolean stackdriverEnabled;

  @Value("${management.stackdriver.metrics.export.step}")
  private String stackdriverStep;

  @Value("${logging.profile}")
  private String loggingProfile;

  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @Bean
  public PubSubTemplate pubSubTemplate(
          PublisherFactory publisherFactory,
          SubscriberFactory subscriberFactory,
          SimplePubSubMessageConverter simplePubSubMessageConverter) {
    PubSubTemplate pubSubTemplate = new PubSubTemplate(publisherFactory, subscriberFactory);
    pubSubTemplate.setMessageConverter(simplePubSubMessageConverter);
    return pubSubTemplate;
  }

  @Bean
  public SimplePubSubMessageConverter messageConverter() {
    return new SimplePubSubMessageConverter();
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
  public MeterFilter meterFilter() {
    return new MeterFilter() {
      @Override
      public MeterFilterReply accept(Meter.Id id) {
        if (id.getName().startsWith("rabbitmq")) {
          return MeterFilterReply.DENY;
        }
        return MeterFilterReply.NEUTRAL;
      }
    };
  }

  @Bean
  StackdriverMeterRegistry meterRegistry(StackdriverConfig stackdriverConfig) {
    return StackdriverMeterRegistry.builder(stackdriverConfig).build();
  }
}



