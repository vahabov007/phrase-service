package com.vahabvahabov.phrase_service.service;

import com.vahabvahabov.phrase_service.dictionary.DictionaryProvider;
import com.vahabvahabov.phrase_service.exception.exceptions.PhraseDefinitionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class FallbackDictionaryService {

    private final PhraseNormalizationService phraseNormalizationService;

    private final List<DictionaryProvider> providers;

    public String requireDefinition(String phrase) {
        if (phrase == null || phrase.trim().isEmpty()) {
            throw new PhraseDefinitionNotFoundException("UNKNOWN");
        }

        for (DictionaryProvider provider : providers) {
            var defOpt = provider.findDefinition(phrase.trim());
            if (defOpt.isPresent()) {
                String clean = phraseNormalizationService.cleanText(defOpt.get());
                if (!clean.isBlank()) return clean;
            }
        }

        throw new PhraseDefinitionNotFoundException(phrase);
    }
}
