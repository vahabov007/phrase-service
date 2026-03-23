package com.vahabvahabov.phrase_service.service;

import com.vahabvahabov.phrase_service.dto.ParseResultDTO;
import com.vahabvahabov.phrase_service.dto.PhraseUploadDTO;
import com.vahabvahabov.phrase_service.model.Phrase;
import com.vahabvahabov.phrase_service.repository.PhraseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class PhraseService {

    private final PhraseImportService phraseImportService;
    private final PhraseNormalizationService phraseNormalizationService;
    private final FallbackDictionaryService fallbackDictionaryService;

    private final PhraseRepository phraseRepository;

    private static final int INITIAL_INTERVAL_FIRST_SUCCESS = 1;
    private static final int INITIAL_INTERVAL_SECOND_SUCCESS = 6;
    private static final int MIN_PASSING_GRADE = 3;

    public List<Phrase> findPhraseByText(String query) {
        return phraseRepository.findByPhraseContainingIgnoreCase(query);
    }

    @Transactional
    public void processReviewResult(Long phraseId, int grade) {
        phraseRepository.findById(phraseId).ifPresent(phrase -> {
            if (grade >= MIN_PASSING_GRADE) {
                applySuccessfulReview(phrase, grade);
            } else {
                applyFailedReview(phrase);
            }
            phrase.setNextReviewDate(LocalDate.now().plusDays(phrase.getIntervalDays()));
            phraseRepository.save(phrase);
        });
    }

    @Transactional
    public void saveStrict(Phrase phrase) {
        if (phrase == null || phrase.getPhrase() == null) return;
        phraseNormalizationService.normalizeContent(phrase);

        if (phrase.getDefinitions().isEmpty()) {
            String fallback = fallbackDictionaryService.requireDefinition(phrase.getPhrase());
            phrase.getDefinitions().add(fallback);
        }
        phraseRepository.findByPhrase(phrase.getPhrase())
                .ifPresent(existing -> {
                    existing.getDefinitions().clear();
                    existing.getDefinitions().addAll(phrase.getDefinitions());
                    existing.getExamples().clear();
                    existing.getExamples().addAll(phrase.getExamples());
                    existing.setSynonyms(normalizeUndefined(phrase.getSynonyms()));
                    existing.setUsageFrequency(normalizeUndefined(phrase.getUsageFrequency()));

                    phraseRepository.save(existing);
                });
    }


    public Phrase extractWordDto(PhraseUploadDTO phraseUploadDTO) {
        Phrase phrase = new Phrase();
        phrase.setPhrase(phraseUploadDTO.getPhrase());
        phrase.setSynonyms(phraseUploadDTO.getSynonyms());
        phrase.setDefinitions(phraseUploadDTO.getDefinitions());
        phrase.setExamples(phraseUploadDTO.getExamples());
        phrase.setUsageFrequency(phraseUploadDTO.getUsageFrequency());
        return phrase;
    }

    @Transactional
    public void saveAllStrict(List<Phrase> phrases) {
        if (phrases == null || phrases.isEmpty()) return;
        List<String> phraseTexts = new ArrayList<>();

        for (Phrase phrase : phrases) {
            phraseNormalizationService.normalizeContent(phrase);
            phraseTexts.add(phrase.getPhrase());
        }

        List<Phrase> dbPhrases = phraseRepository.findByPhraseIn(phraseTexts);

        Map<String, Phrase> phraseTextToPhrase = new HashMap<>();
        for (Phrase phrase : dbPhrases) {
            phraseTextToPhrase.put(phrase.getPhrase(), phrase);
        }
        List<Phrase> savedPhrases = new ArrayList<>();

        for (Phrase phrase : phrases) {
            Phrase target = phraseTextToPhrase.get(phrase.getPhrase());

            if (target != null) {
                target.getDefinitions().clear();
                target.getDefinitions().addAll(phrase.getDefinitions());

                target.getExamples().clear();
                target.getExamples().addAll(phrase.getExamples());

                target.setSynonyms(normalizeUndefined(phrase.getSynonyms()));
                target.setUsageFrequency(normalizeUndefined(phrase.getUsageFrequency()));
                savedPhrases.add(target);
            } else {
                if (phrase.getDefinitions().isEmpty()) {
                    String fallback = fallbackDictionaryService.requireDefinition(phrase.getPhrase());
                    phrase.getDefinitions().add(fallback);
                }
                savedPhrases.add(phrase);
            }
        }
        phraseRepository.saveAll(savedPhrases);
    }

    public ParseResultDTO parseLines(List<String> lines) {
        PhraseImportService.ParseResult parseResult = phraseImportService.parseLines(lines);
        ParseResultDTO parseResultDTO = new ParseResultDTO();
        List<PhraseUploadDTO> dtos = new ArrayList<>();
        for (Phrase phrase : parseResult.getPhrases()) {
            PhraseUploadDTO phraseUploadDTO = new PhraseUploadDTO();
            phraseUploadDTO.setPhrase(phrase.getPhrase());
            phraseUploadDTO.setExamples(phrase.getExamples());
            phraseUploadDTO.setDefinitions(phrase.getDefinitions());
            phraseUploadDTO.setUsageFrequency(phrase.getUsageFrequency());
            dtos.add(phraseUploadDTO);
        }
        parseResultDTO.setLines(dtos);
        return parseResultDTO;
    }

    public Page<Phrase> getWordsForPractice(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return phraseRepository.findDuePhrasesStrictly(pageRequest);

    }

    public long getCountPreparedPhrases() {
        return phraseRepository.countPreparedPhrases();
    }


    private Double calculateNewEaseFactor(Double easeFactor, int grade) {
        double newEase = easeFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02));
        return Math.max(1.3, newEase);
    }

    private void applySuccessfulReview(Phrase phrase, int grade) {
        int repetitions = phrase.getRepetitions();
        int interval = (repetitions == 0) ? INITIAL_INTERVAL_FIRST_SUCCESS :
                (repetitions == 1) ? INITIAL_INTERVAL_SECOND_SUCCESS :
                        (int) Math.round(phrase.getIntervalDays() * (phrase.getEaseFactor() / 100.0));
        phrase.setIntervalDays(interval);
        phrase.setRepetitions(repetitions + 1);
        phrase.setEaseFactor(calculateNewEaseFactor(phrase.getEaseFactor(), grade));
    }

    private void applyFailedReview(Phrase phrase) {
        phrase.setRepetitions(0);
        phrase.setIntervalDays(1);
    }

    private String normalizeUndefined(String value) {
        if (value == null) return null;
        String trimmedValue = value.trim();
        return trimmedValue.equalsIgnoreCase("Undefined") ? null : trimmedValue;
    }

}






