package com.paymentEngine.controller;

import com.paymentEngine.dto.paymentRequest;
import com.paymentEngine.dto.paymentResponse;
import com.paymentEngine.dto.paymentStatusResponse;
import com.paymentEngine.entity.payment;
import com.paymentEngine.service.paymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
public class paymentController {

    private final paymentService paymentService;

    public paymentController(paymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<paymentResponse> createPayment(
            @Valid @RequestBody paymentRequest request) {

        paymentResponse response =
                paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<paymentStatusResponse> getPayment(
            @PathVariable String transactionId) {

        paymentStatusResponse payment =
                paymentService.getPaymentStatus(
                        transactionId
                );

        return ResponseEntity.ok(payment);
    }


}
