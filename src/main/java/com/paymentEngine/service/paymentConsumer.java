package com.paymentEngine.service;
import com.paymentEngine.dto.AiFraudResult;
import com.paymentEngine.dto.fraudResult;
import com.paymentEngine.dto.paymentEvent;
import com.paymentEngine.entity.payment;
import com.paymentEngine.entity.paymentStatus;
import com.paymentEngine.repository.paymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.paymentEngine.service.AiFraudAnalysis;

import java.math.BigDecimal;




@Service


public class paymentConsumer {
    private final fraudDetectionService fraudDetectionService;
    private final paymentRepository paymentRepository;
    private final redisService redisService;
    private final ObjectMapper objectMapper;
    private final AiFraudAnalysis aiFraudAnalysis;
    private final settlementService settlementService;

    public paymentConsumer(
            fraudDetectionService fraudDetectionService, paymentRepository paymentRepository,
            redisService redisService,ObjectMapper objectMapper, AiFraudAnalysis aiFraudAnalysis,settlementService settlementService) {

        this.fraudDetectionService = fraudDetectionService;
        this.paymentRepository = paymentRepository;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
        this.aiFraudAnalysis = aiFraudAnalysis;
        this.settlementService =settlementService;
    }

    @RetryableTopic(attempts = "4")

    @KafkaListener(
            topics = "payment-transactions",
            groupId = "payment-group"
    )
    @Transactional
    public void consumePaymentEvent(String event) {

        System.out.println(
                "Payment event received from Kafka: " + event

        );






        paymentEvent paymentEvent;





        ObjectMapper objectMapper = new ObjectMapper();



        try {

            paymentEvent =
                    objectMapper.readValue(
                            event,
                            paymentEvent.class
                    );

        } catch (JacksonException e) {

            throw new RuntimeException(
                    "Failed to deserialize payment event",
                    e
            );
        }

        String transactionId =
                paymentEvent.getTransactionId();

        BigDecimal amount =
                paymentEvent.getAmount();

        String currency =
                paymentEvent.getCurrency();

        fraudResult result =
                fraudDetectionService.analyze(
                        amount,
                        currency
                );


        String transactionDetails = String.format(
                """
                Amount: %s
                Currency: %s
                Transaction ID: %s
                """,
                amount,
                currency,
                transactionId
        );

        AiFraudResult aiAnalysis =
                aiFraudAnalysis.analyzeTransaction(
                        transactionId,
                        transactionDetails
                );
        int aiScore =
                aiFraudAnalysis
                        .convertRiskLevelToScore(
                                aiAnalysis.getRiskLevel()
                        );

        System.out.println("Mistral AI Analysis:");
       System.out.println(
                "Mistral AI Risk Level: "
                        + aiAnalysis.getRiskLevel()
       );

        System.out.println(
                "Mistral AI Reason: "
                        + aiAnalysis.getReason()
        );

        int finalRiskScore =
                result.getRiskScore() + aiScore;

        String finalDecision;

        if (finalRiskScore >= 70) {

            finalDecision = "BLOCKED";

        } else if (finalRiskScore >= 40) {

            finalDecision = "REVIEW";

        } else {

            finalDecision = "APPROVED";
        }

        payment payment = paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found: " + transactionId
                        )
                );
        if (payment.isFraudProcessed()) {

            System.out.println(
                    "Duplicate payment event detected. "
                            + "Skipping transaction: "
                            + transactionId
            );

            return;
        }

        payment.setRiskScore(finalRiskScore);

        payment.setFraudReason(
                "Rule: " + result.getReason()
                        + " | AI: " + aiAnalysis.getReason()
        );

        payment.setStatus(
                paymentStatus.valueOf(finalDecision)
        );



        payment.setFraudProcessed(true);
        paymentRepository.save(payment);


        redisService.saveFraudResult(
                transactionId,
                finalDecision
        );

        if (finalDecision.equals("APPROVED")) {

            settlementService.settlePayment(
                    transactionId,
                    amount,
                    currency
            );

        } else {

            System.out.println(
                    "Settlement skipped because payment decision is: "
                            + finalDecision
            );
        }
        System.out.println("=================================");
        System.out.println(
                "Transaction: " + transactionId
        );

        System.out.println("Rule Risk Score: " + result.getRiskScore());
        System.out.println("AI Risk Score: " + aiScore);
        System.out.println("Final Risk Score: " + finalRiskScore);
        System.out.println("Final Decision: " + finalDecision);
        System.out.println("Rule Reason: " + result.getReason());
        System.out.println("AI Reason: " + aiAnalysis.getReason());

        System.out.println("=================================");




    }
    @DltHandler
    public void handleDlt(String event) {

        System.out.println(
                "Payment event moved to DLT: " + event
        );
    }
}
