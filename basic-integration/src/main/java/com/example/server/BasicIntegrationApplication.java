package com.example.server;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.inbound.AmqpInboundGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.dsl.Channels;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.splitter.DefaultMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;

@SpringBootApplication
@EnableIntegrationManagement
public class BasicIntegrationApplication {

    public static void main(String[] args) throws IOException {
       ConfigurableApplicationContext context= SpringApplication.run(BasicIntegrationApplication.class, args);
        System.out.println("enter to stop");

        System.in.read();

       context.close();
    }
    @Bean
    public Queue fooQueue(){
        return new Queue("foo",true);
    }

    @Bean
    public IntegrationFlow flow(RabbitTemplate template){
        return IntegrationFlows.from(Http.inboundGateway("/receive")
        .requestMapping(m->m.methods(HttpMethod.POST)).requestPayloadType(String.class))
//                .split(commaSplitter())
                .<String,String>transform(p-> "hi " +p+" what can i help u ?")
                .handle(Amqp.outboundGateway(template).routingKey("foo"))
                .get();
    }
    @Bean // return the upper cased payload
    public IntegrationFlow amqp(ConnectionFactory connectionFactory) {
        return IntegrationFlows.from(Amqp.inboundGateway(connectionFactory, "foo"))
//                .route("payload.substring(0,3)",r->r
//                        .resolutionRequired(false)
//                        .subFlowMapping("foo",s->s.<String,String>transform(String::toUpperCase))
//                        .subFlowMapping("bar",s->s.<String,String>transform(p->p+p)))
                        .get();
    }


    @Bean
    public DefaultMessageSplitter commaSplitter(){
        DefaultMessageSplitter splitter= new DefaultMessageSplitter();
        splitter.setDelimiters(",");
        return splitter;
    }

}
