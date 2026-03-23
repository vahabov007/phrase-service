package com.vahabvahabov.phrase_service.dictionary;

import java.util.Optional;

public interface DictionaryProvider {
    Optional<String> findDefinition(String word);
    String name();
}
