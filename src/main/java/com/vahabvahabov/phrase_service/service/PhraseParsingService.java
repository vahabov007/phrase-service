package com.vahabvahabov.phrase_service.service;

import com.vahabvahabov.phrase_service.repository.PhraseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service @RequiredArgsConstructor
public class PhraseParsingService {

    private static final Pattern PHRASE_HEADER =
            Pattern.compile("^([\\p{L}][\\p{L}\\p{M}\\s'\\-]*)\\s*:\\s*(\\[[^\\]]+\\])?\\s*$");

    private static final Pattern EXAMPLE =
            Pattern.compile("^Example\\s*\\d*\\s*:\\s*(.*)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern SYNONYMS =
            Pattern.compile("^Synonyms\\s*:\\s*(.*)$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private final PhraseRepository phraseRepository;

    public boolean isPhraseHeaderLine(String text) {
        if (text == null) return false;
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) return false;
        // Locale.ROOT => It avoids cultural differences.
        String normalizedText = trimmedText.toLowerCase(Locale.ROOT);

        if (!isItPhrase(normalizedText)) return false;
        return PHRASE_HEADER.matcher(trimmedText).matches();
    }

    public Optional<String> extractPhraseFromHeader(String text) {
        if (text == null) return Optional.empty();
        Matcher matcher = PHRASE_HEADER.matcher(text.trim());
        if (!matcher.matches()) return Optional.empty();

        /*
         Example : group(0) → "Concrete : [konkrit]"
                   group(1) → "Concrete"
                   group(2) → "[konkrit]"
        */

        String word = matcher.group(1);
        return word == null ? Optional.empty() : Optional.of(word.trim());
    }

    public String tryParseSynonyms(String text) {
        if(text == null) return null;
        // It gives us Synonym words
        return matchPayload(SYNONYMS, text);
    }

    public String tryParseExample(String text) {
        if(text == null) return null;
        // It gives us Examples
        return matchPayload(EXAMPLE, text);
    }

    private String matchPayload(Pattern pattern, String text) {
        if (text == null) return null;
        Matcher matcher = pattern.matcher(text.trim());
        if (!matcher.matches()) return null;

        String payload = matcher.group(1);
        return payload == null ? "" : payload.trim();
    }

    private boolean isItPhrase(String text)
    {
        if (text.startsWith("example")) return false;
        if (text.startsWith("synonyms")) return false;
        if (text.startsWith("antonyms")) return false;
        return true;
    }
}
