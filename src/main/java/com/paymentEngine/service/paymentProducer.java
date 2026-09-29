package com.paymentEngine.service;

import com.paymentEngine.dto.paymentEvent;
import com.paymentEngine.entity.payment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;


@Service
public class paymentProducer {

    private static final String TOPIC = "payment-transactions";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public paymentProducer(KafkaTemplate<String, String> kafkaTemplate,ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendPaymentEvent(payment payment) {
        paymentEvent event = new paymentEvent(
                payment.getTransactionId(),
                payment.getAmount(),
                payment.getCurrency()
        );


        try {
            String jsonEvent =
                    new ObjectMapper().writeValueAsString(event);


            kafkaTemplate.send(
                    TOPIC,
                    payment.getTransactionId(),
                    jsonEvent
            );

            System.out.println(
                    "Payment event sent to Kafka: " + jsonEvent
            );

        } catch (JacksonException e) {
            throw new RuntimeException(
                    "Failed to serialize payment event", e
            );


        }


    }
}
