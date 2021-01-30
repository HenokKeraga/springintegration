package com.example.httpclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;

import java.util.Scanner;

@SpringBootApplication
@IntegrationComponentScan
public class HttpClientApplication {

    public static void main(String[] args) {
       ConfigurableApplicationContext context= SpringApplication.run(HttpClientApplication.class, args);
        Gateway gateway= context.getBean(Gateway.class);
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()){
            String line = scanner.nextLine();
            System.out.println(gateway.exchange(line));
        }
        scanner.close();
    }


    @MessagingGateway(defaultRequestChannel = "httpOut.input")
    public interface Gateway{
        String exchange(String out);
    }

    @Bean
    public IntegrationFlow  httpOut(){
        return f->f.handle(Http.outboundGateway("http://localhost:8080/receive")
                 .expectedResponseType(String.class));
    }


}
