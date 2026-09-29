package com.paymentEngine.controller;
import com.paymentEngine.entity.settlement;
import com.paymentEngine.repository.settlementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")

public class settlementController {
    private final settlementRepository settlementRepository;

    public settlementController(
            settlementRepository settlementRepository) {

        this.settlementRepository = settlementRepository;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<settlement> getSettlement(
            @PathVariable String transactionId) {

        return settlementRepository
                .findByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
