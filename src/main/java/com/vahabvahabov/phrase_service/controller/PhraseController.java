package com.vahabvahabov.phrase_service.controller;

import com.vahabvahabov.phrase_service.dto.GradeRequest;
import com.vahabvahabov.phrase_service.dto.ParseResultDTO;
import com.vahabvahabov.phrase_service.dto.PhraseUploadDTO;
import com.vahabvahabov.phrase_service.exception.ApiResponse;
import com.vahabvahabov.phrase_service.model.Phrase;
import com.vahabvahabov.phrase_service.service.PhraseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/v1/phrases")
@RequiredArgsConstructor
@Tag(name = "Phrase Rehearsal API",
        description = "Endpoints for your phone to fetch phrases")
public class PhraseController {

    private final PhraseService phraseService;

    @PostMapping
    @Operation(summary = "Save phrase for the File Service and OneNote Service")
    public ResponseEntity<ApiResponse<Void>> saveStrict(@RequestBody PhraseUploadDTO phraseUploadDTO) {
        Phrase phrase = phraseService.extractWordDto(phraseUploadDTO);
        phraseService.saveStrict(phrase);
        return ResponseEntity.ok(ApiResponse.success("Phrase successfully created.",
                null, 201,
                "/api/v1/phrases"));
    }

    @PostMapping("/save-all")
    public ResponseEntity<Void> saveAllStrict(@RequestBody List<PhraseUploadDTO> dtos) {
        // Convert DTOs to Entities
        List<Phrase> phrases = dtos.stream()
                .map(phraseService::extractWordDto)
                .toList();

        phraseService.saveAllStrict(phrases);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/parse")
    @Operation(summary = "Parse lines for the File Service and OneNote Service")
    public ResponseEntity<ApiResponse<ParseResultDTO>> parseLines(@RequestBody List<String> lines) {
        ParseResultDTO parseResultDTO = phraseService.parseLines(lines);
        return ResponseEntity.ok(ApiResponse.success("Parsed lines successfully sent.",
                parseResultDTO, 200,
                "/api/v1/phrases/parse"));
    }

    @PostMapping("/{id}/grade")
    @Operation(summary = "Submit a rehearsal grade via JSON body")
    public ResponseEntity<ApiResponse<Void>> submitReview(@PathVariable Long id,
                                                          @Valid @RequestBody GradeRequest gradeRequest) {
        phraseService.processReviewResult(id, gradeRequest.getGrade());
        return ResponseEntity.ok(ApiResponse.success("Rehearsal grade submitted.", null,
                200, "/api/v1/phrases/{id}/grade"));
    }

    @GetMapping("/rehearse")
    public ResponseEntity<ApiResponse<Page<Phrase>>> getPhrasesForPractise(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        Page<Phrase> phrasesForPractice = phraseService.getWordsForPractice(page, size);
        return ResponseEntity.ok(ApiResponse.success("Phrases delivered.",
                phrasesForPractice, 200,
                "/api/v1/phrases/rehearse"));
    }

    @GetMapping("/count-due")
    @Operation(summary = "Get the number of words waiting for review")
    public ResponseEntity<ApiResponse<Long>> getDueCount() {
        long countPreparedWords = phraseService.getCountPreparedPhrases();
        return ResponseEntity.ok(ApiResponse.success("The number of phrases received.",
                countPreparedWords, 200,
                "/api/v1/phrases/count-due"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for specific phrases by text")
    public ResponseEntity<ApiResponse<List<Phrase>>> searchWords(@RequestParam String query) {
        List<Phrase> results = phraseService.findPhraseByText(query);
        return ResponseEntity.ok(ApiResponse.success("The phrase successfully found!",
                results, 200,
                "/api/v1/phrases/search"));
    }






}
