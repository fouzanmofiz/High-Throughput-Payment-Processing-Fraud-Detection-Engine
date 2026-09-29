package com.paymentEngine.repository;


import com.paymentEngine.entity.settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface settlementRepository extends JpaRepository<settlement, Long> {
    Optional<settlement> findByTransactionId(String transactionId);

}
