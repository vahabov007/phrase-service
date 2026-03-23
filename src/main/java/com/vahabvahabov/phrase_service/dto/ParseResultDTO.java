package com.vahabvahabov.phrase_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseResultDTO {
    private List<PhraseUploadDTO> lines;
}