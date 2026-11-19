package com.marcelo721.desfio_loading_full__stack.domain.model;


import com.marcelo721.desfio_loading_full__stack.domain.exceptions.DomainValidationException;

final class TextRules {

    private TextRules() {
    }

    static String requireText(String value, String fieldLabel, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(fieldLabel + " é obrigatório(a).");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new DomainValidationException(
                    fieldLabel + " deve ter no máximo " + maxLength + " caracteres.");
        }
        return trimmed;
    }
}
