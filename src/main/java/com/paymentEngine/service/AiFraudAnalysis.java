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
                You are a payment fraud analysis assistant.

                Analyze this transaction for suspicious characteristics.

                Transaction ID:
                %s

                Transaction details:
                %s

                Return your response in exactly this format:

                Risk Level: LOW, MEDIUM, or HIGH
                Reason: <short explanation>

                Do not make the final payment decision.
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
