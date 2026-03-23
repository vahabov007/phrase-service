package com.vahabvahabov.phrase_service.service;

import com.vahabvahabov.phrase_service.model.Phrase;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class PhraseNormalizationService {

    public void normalizeContent(Phrase content) {
        if (content == null || content.getPhrase() == null) return;
        String target = content.getPhrase().trim().toLowerCase(Locale.ROOT);

        // Definitions
        LinkedHashSet<String> definitions = new LinkedHashSet<>();
        for (String definition : getSafeList(content.getDefinitions())) {
            String cleanedText = cleanText(definition);
            if (cleanedText.isBlank() || cleanedText.toLowerCase(Locale.ROOT).equals(target)) continue;
            definitions.add(cleanedText);
        }

        // Remove old definitions
        content.getDefinitions().clear();
        content.getDefinitions().addAll(definitions);

        // Examples
        LinkedHashSet<String> examples = new LinkedHashSet<>();
        for (String example : getSafeList(content.getExamples())) {
            String clean = cleanText(example);
            if (clean.isBlank()) continue;
            if (clean.toLowerCase(Locale.ROOT).equals(target)) continue;
            examples.add(clean);
        }

        content.getExamples().clear();
        content.getExamples().addAll(examples);
    }

    private List<String> getSafeList(List<String> list) {
        return list == null ? List.of() : list;
    }

    public String cleanText(String text) {
        if (text == null) return "";
        return text.trim()
                // "\\s+" -> Replaces any sequence of whitespace with a single space.
                .replaceAll("\\s+", " ")
                .replaceAll("^[\"'“”]+|[\"'“”]+$", "");
    }

}
