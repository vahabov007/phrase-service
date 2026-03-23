package com.vahabvahabov.phrase_service.service;

import com.vahabvahabov.phrase_service.model.Phrase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @RequiredArgsConstructor
public class PhraseImportService {

    private final PhraseParsingService phraseParsingService;

    public ParseResult parseLines(List<String> lines) {
        List<Phrase> words = new ArrayList<>();
        Phrase current = null;

        for (String raw : lines) {
            String text = raw == null ? "" : raw.trim();
            if (text.isEmpty()) continue;

            // New word header
            if (phraseParsingService.isPhraseHeaderLine(text)) {
                if (current != null) {
                    words.add(current);
                }
                current = new Phrase();
                current.setPhrase(phraseParsingService.extractPhraseFromHeader(text).orElseThrow());
                initDefaults(current);
                continue;
            }
            if (current == null) continue;

            // Synonyms
            String synonyms = phraseParsingService.tryParseSynonyms(text);
            if (synonyms != null) {
                current.setSynonyms(synonyms.isBlank() ? null : synonyms);
                continue;
            }
            // Example
            String exampleText = phraseParsingService.tryParseExample(text);
            if (exampleText != null) {
                current.getExamples().add(exampleText);
                continue;
            }
            // Definition line
            current.getDefinitions().add(text);
        }
        if (current != null) {
            words.add(current);
        }
        return new ParseResult(words);
    }

    private void initDefaults(Phrase vocabularyWord) {
        vocabularyWord.setUsageFrequency("Undefined");
        vocabularyWord.setSynonyms(null);
    }

    public record ParseResult(@Getter List<Phrase> phrases) {

    }

}
