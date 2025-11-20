package com.cheqi.sdk.receipt;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    private ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = Collections.unmodifiableList(Objects.requireNonNull(errors, "errors cannot be null"));
    }

    /**
     * Creates a successful validation result.
     *
     * @return ValidationResult indicating validation passed with no errors
     */
    public static ValidationResult valid() {
        return new ValidationResult(true, Collections.emptyList());
    }

    /**
     * Creates a failed validation result with a single error message.
     *
     * @param error Error message describing the validation failure
     * @return ValidationResult indicating validation failed
     * @throws NullPointerException if error is null
     */
    public static ValidationResult invalid(String error) {
        Objects.requireNonNull(error, "error message cannot be null");
        return new ValidationResult(false, Collections.singletonList(error));
    }

    /**
     * Creates a failed validation result with multiple error messages.
     *
     * @param errors List of error messages describing validation failures
     * @return ValidationResult indicating validation failed
     * @throws NullPointerException if errors list is null
     * @throws IllegalArgumentException if errors list is empty
     */
    public static ValidationResult invalid(List<String> errors) {
        Objects.requireNonNull(errors, "errors list cannot be null");
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("errors list cannot be empty for invalid result");
        }
        return new ValidationResult(false, errors);
    }

    /**
     * Checks if the validation passed.
     *
     * @return true if validation passed, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the list of validation error messages.
     *
     * @return Unmodifiable list of validation error messages (empty if valid)
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Gets all validation errors as a single comma-separated string.
     *
     * @return Comma-separated string of all error messages, or empty string if valid
     */
    public String getErrorMessage() {
        return String.join(", ", errors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return valid == that.valid && errors.equals(that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, errors);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errors=" + errors +
                '}';
    }
}
