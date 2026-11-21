package br.ufc.loading.requests.domain.model;

import br.ufc.loading.requests.domain.exception.DomainValidationException;

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
