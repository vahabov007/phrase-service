package com.vahabvahabov.phrase_service.repository;

import com.vahabvahabov.phrase_service.model.Phrase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Long> {

    List<Phrase> findByPhraseContainingIgnoreCase(String phrase);

    Optional<Phrase> findByPhrase(String phrase);

    @Query(value = """
    SELECT * FROM word_service.vocabulary_phrase v
    WHERE v.next_review_date <= CURRENT_DATE
      AND v.is_ready = true
      AND EXISTS (
          SELECT 1
          FROM word_service.vocabulary_phrase_definitions d
          WHERE d.vocabulary_phrase_id = v.id
      )
      AND EXISTS (
          SELECT 1
          FROM word_service.vocabulary_phrase_examples e
          WHERE e.vocabulary_phrase_id = v.id
      )
    ORDER BY RANDOM()
    """,
            countQuery = """
    SELECT COUNT(*) FROM word_service.vocabulary_phrase v
    WHERE v.next_review_date <= CURRENT_DATE
      AND EXISTS (
          SELECT 1
          FROM word_service.vocabulary_phrase_definitions d
          WHERE d.vocabulary_phrase_id = v.id
      )
      AND EXISTS (
          SELECT 1
          FROM word_service.vocabulary_phrase_examples e
          WHERE e.vocabulary_phrase_id = v.id
      )
    """,
            nativeQuery = true)
    Page<Phrase> findDuePhrasesStrictly(Pageable pageable);

    @Query(value = """
    SELECT COUNT(*)
    FROM word_service.vocabulary_phrase v
    WHERE EXISTS (SELECT 1 FROM word_service.vocabulary_phrase_definitions d WHERE d.vocabulary_phrase_id = v.id)
      AND EXISTS (SELECT 1 FROM word_service.vocabulary_phrase_examples e WHERE e.vocabulary_phrase_id = v.id)
    """, nativeQuery = true)
    long countPreparedPhrases();

    List<Phrase> findByPhraseIn(List<String> phrases);






}
