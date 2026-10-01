package com.paymentEngine.service;

import com.paymentEngine.dto.AiFraudResult;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

@Service
public class AiFraudAnalysis {
    private  final ChatClient chatClient;
    public AiFraudAnalysis(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public  AiFraudResult analyzeTransaction(
            String transactionId,
            String transactionDetails) {

        String prompt = """
                You are an AI-powered payment fraud analyst.
                
                                Analyze the following payment transaction and determine
                                whether it appears legitimate or potentially fraudulent.
                
                                Transaction ID:
                                %s
                
                                Transaction Details:
                                %s
                
                                Analyze the transaction using ONLY the information provided.
                
                                Consider:
                                - Transaction amount
                                - Currency
                                - Unusual transaction characteristics
                                - Any explicitly provided fraud indicators
                                - Overall risk level
                
                                Important rules:
                                - Do not invent customer history, location, device information,
                                  previous transactions, transaction velocity, or other facts
                                  that are not provided.
                                - Do not say "insufficient data" for a normal transaction when
                                  the available transaction information is sufficient to make
                                  a basic risk assessment.
                                - If the transaction appears legitimate, clearly explain why
                                  it appears to be a normal and low-risk payment.
                                - If the transaction appears suspicious, clearly identify the
                                  characteristics that increase the risk.
                                - Keep the explanation professional and suitable for a
                                  real-time payment monitoring system.
                                - Do not make absolute claims that a transaction is guaranteed
                                  to be safe or fraudulent. Use "appears legitimate",
                                  "appears suspicious", or similar evidence-based wording.
                
                                Return EXACTLY this format:
                
                                Risk Level: LOW/MEDIUM/HIGH
                                Reason: <2-3 clear professional sentences>
                """.formatted(
                transactionId,
                transactionDetails
        );

        try {
            String response = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            String riskLevel = extractRiskLevel(response);
            String reason = extractReason(response);

            return new AiFraudResult(
                    riskLevel,
                    reason
            );
        } catch (Exception e) {

            System.out.println(
                    "Mistral AI unavailable: " + e.getMessage()
            );

            return new AiFraudResult(
                    "UNKNOWN",
                    "AI service temporarily unavailable"
            );
        }
    }
    private String extractRiskLevel(String response) {

        for (String line : response.split("\n")) {

            if (line.toLowerCase()
                    .startsWith("risk level:")) {

                return line
                        .substring("risk level:".length())
                        .trim()
                        .toUpperCase();
            }
        }

        return "UNKNOWN";


    }

    private String extractReason(String response) {
        for (String line : response.split("\n")) {

            if (line.toLowerCase()
                    .startsWith("reason:")) {

                return line
                        .substring("reason:".length())
                        .trim();
            }
        }

        return "No AI reason available";

    }

    public int convertRiskLevelToScore(String riskLevel) {

        return switch (riskLevel.toUpperCase()) {

            case "HIGH" -> 50;

            case "MEDIUM" -> 25;

            case "LOW" -> 10;

            default -> 0;
        };
    }
}
