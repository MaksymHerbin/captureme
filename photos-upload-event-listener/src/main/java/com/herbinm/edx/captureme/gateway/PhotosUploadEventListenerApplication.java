package com.herbinm.edx.captureme.gateway;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;

@SpringBootApplication
@EnableJms
public class PhotosUploadEventListenerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhotosUploadEventListenerApplication.class, args);
    }

    @Bean
    public SQSConnectionFactory connectionFactory() {
        return SQSConnectionFactory.builder()
                .withRegion(Region.getRegion(Regions.US_WEST_2))
                .withAWSCredentialsProvider(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(SQSConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate(SQSConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }

}
