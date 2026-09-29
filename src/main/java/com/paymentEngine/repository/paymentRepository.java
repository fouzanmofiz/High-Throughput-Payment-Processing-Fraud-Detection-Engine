package com.paymentEngine.repository;
import com.paymentEngine.entity.payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface paymentRepository extends  JpaRepository<payment, Long> {

    Optional<payment> findByTransactionId(String transactionId);
    Optional<payment> findByIdempotencyKey(String idempotencyKey);
}
