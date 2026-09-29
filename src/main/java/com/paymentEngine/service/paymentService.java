package com.paymentEngine.service;

import com.paymentEngine.dto.paymentRequest;
import com.paymentEngine.dto.paymentResponse;
import com.paymentEngine.dto.paymentStatusResponse;
import com.paymentEngine.entity.payment;
import com.paymentEngine.entity.paymentStatus;
import com.paymentEngine.entity.settlement;
import com.paymentEngine.exception.resourceNotFoundException;
import com.paymentEngine.repository.paymentRepository;
import com.paymentEngine.repository.settlementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Service
public class paymentService {
    private final paymentRepository paymentRepository;
    private final paymentProducer paymentProducer;
    private final settlementRepository settlementRepository;

    public paymentService(paymentRepository paymentRepository, paymentProducer paymentProducer, settlementRepository settlementRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
        this.settlementRepository= settlementRepository;
    }
    @Transactional
    public paymentResponse createPayment(paymentRequest request) {

        if (request.getIdempotencyKey() != null) {

            Optional<payment> existingPayment =
                    paymentRepository.findByIdempotencyKey(
                            request.getIdempotencyKey()
                    );

            if (existingPayment.isPresent()) {

                payment existing = existingPayment.get();

                return new paymentResponse(
                        existing.getTransactionId(),
                        existing.getStatus().name(),
                        "Payment already processed"
                );
            }
        }

        payment payment = new payment();

        payment.setIdempotencyKey(
                request.getIdempotencyKey()
        );

        payment.setTransactionId(
                "TXN-" + UUID.randomUUID()
        );

        payment.setCustomerId(request.getCustomerId());
        payment.setMerchantId(request.getMerchantId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());

        payment.setStatus(paymentStatus.PROCESSING);
        payment.setCreatedAt(LocalDateTime.now());

        payment.setRiskScore(0);
        payment.setFraudReason("Pending fraud analysis");


        paymentRepository.save(payment);

        paymentProducer.sendPaymentEvent(payment);

        return new paymentResponse(
                payment.getTransactionId(),
                payment.getStatus().name(),
                "Payment received successfully"
        );
    }

    public payment getPaymentByTransactionId(String transactionId) {

        return paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found: " + transactionId
                        )
                );
    }

    public paymentStatusResponse getPaymentStatus(
            String transactionId) {

        payment payment = paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new resourceNotFoundException(
                                "Payment not found: " + transactionId
                        )
                );

        settlement settlement = settlementRepository
                .findByTransactionId(transactionId)
                .orElse(null);

        String settlementId = null;
        String settlementStatus = "NOT_SETTLED";

        if (settlement != null) {
            settlementId = settlement.getSettlementId();
            settlementStatus = settlement.getStatus().name();
        }

        return new paymentStatusResponse(
                payment.getTransactionId(),
                payment.getStatus().name(),
                payment.getRiskScore(),
                payment.isFraudProcessed(),
                payment.getFraudReason(),
                settlementId,
                settlementStatus
        );
    }


}
