package com.paymentEngine.service;
import com.paymentEngine.entity.setSettlementStatus;
import com.paymentEngine.entity.settlement;
import com.paymentEngine.repository.settlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class settlementService {

    private final settlementRepository settlementRepository;

    public settlementService(
            settlementRepository settlementRepository) {

        this.settlementRepository = settlementRepository;
    }

    @Transactional
    public settlement settlePayment(
            String transactionId,
            java.math.BigDecimal amount,
            String currency) {

        // Idempotency check
        if (settlementRepository
                .findByTransactionId(transactionId)
                .isPresent()) {

            System.out.println(
                    "Settlement already exists for transaction: "
                            + transactionId
            );

            return settlementRepository
                    .findByTransactionId(transactionId)
                    .get();
        }

        settlement settlement = new settlement();

        settlement.setSettlementId(
                "SET-" + UUID.randomUUID()
        );

        settlement.setTransactionId(transactionId);
        settlement.setAmount(amount);
        settlement.setCurrency(currency);

        settlement.setStatus(
                setSettlementStatus.COMPLETED
        );

        settlement.setCreatedAt(
                LocalDateTime.now()
        );

        settlement.setCompletedAt(
                LocalDateTime.now()
        );

        settlement savedSettlement =
                settlementRepository.save(settlement);

        System.out.println(
                "Payment settled successfully: "
                        + transactionId
        );

        System.out.println(
                "Settlement ID: "
                        + savedSettlement.getSettlementId()
        );

        return savedSettlement;
    }










}
