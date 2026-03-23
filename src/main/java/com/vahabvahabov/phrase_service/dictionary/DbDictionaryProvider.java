package com.vahabvahabov.phrase_service.dictionary;

import com.vahabvahabov.phrase_service.repository.PhraseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component @RequiredArgsConstructor
public class DbDictionaryProvider implements DictionaryProvider {

    private final PhraseRepository phraseRepository;

    @Override
    public Optional<String> findDefinition(String word) {
        if (word == null || word.isBlank()) return Optional.empty();

        return phraseRepository.findByPhrase(word.trim())
                .flatMap(w -> (w.getDefinitions() == null || w.getDefinitions().isEmpty())
                        ? Optional.empty()
                        : Optional.ofNullable(w.getDefinitions().get(0)));
    }

    @Override
    public String name() {
        return "db";
    }
}
