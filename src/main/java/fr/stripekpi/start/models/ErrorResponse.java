package fr.stripekpi.start.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String code;

    public ErrorResponse(String message) {
        this.message = message;
        this.code = "INTERNAL_ERROR";
    }
}