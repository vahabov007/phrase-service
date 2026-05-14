package com.vahabvahabov.phrase_service.service;

import com.vahabvahabov.phrase_service.dto.ParseResultDTO;
import com.vahabvahabov.phrase_service.dto.PhraseUploadDTO;
import com.vahabvahabov.phrase_service.model.Phrase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class PhraseConsumerService {
    private final PhraseService phraseService;

    @KafkaListener(topics = "phrase-sync-topic", groupId = "phrase-group")
    public void consumerSyncRequest(List<String> lines) {
        log.info("Kafka: Received {} lines to sync phrases.", lines.size());

        try {
            // 1. Reuse your existing Parsing Logic
            ParseResultDTO parseResult = phraseService.parseLines(lines);
            List<PhraseUploadDTO> dtos = parseResult.getLines();

            if (dtos == null || dtos.isEmpty()) {
                log.warn("Kafka: Parsing resulted in zero phrases.");
                return;
            }

            // 2. Convert DTOs to Entities (Reusing your existing extractWordDto)
            List<Phrase> phrases = dtos.stream()
                    .map(phraseService::extractWordDto)
                    .toList();

            // 3. Save them strictly
            phraseService.saveAllStrict(phrases);

            log.info("Kafka: Successfully synced {} phrases in the background.", phrases.size());

        } catch (Exception e) {
            log.error("Kafka: Error during background phrase sync: {}", e.getMessage());
            // In a real system, you might send this to a 'Dead Letter Topic' for later review
        }
    }
}
