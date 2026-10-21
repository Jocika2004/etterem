
package hu.ettermifoglalas;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStatusException(
            ResponseStatusException exception
    ) {
        String message = exception.getReason() != null
                ? exception.getReason()
                : "A kérés nem dolgozható fel.";

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(Map.of("message", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String field = error.getField();

                    return switch (field) {
                        case "guestName" -> "A vendég nevét meg kell adni.";
                        case "email" -> "Adj meg egy érvényes e-mail-címet.";
                        case "phone" -> "A telefonszám megadása kötelező.";
                        case "reservationDateTime" ->
                                "A foglalás dátumát és időpontját meg kell adni.";
                        case "guestCount" ->
                                "A vendégek száma legalább 1 legyen.";
                        case "tableId" -> "Válassz egy asztalt.";
                        case "tableNumber" ->
                                "Az asztalszám 1 és 100 közé essen.";
                        case "capacity" ->
                                "A férőhely 1 és 20 közé essen.";
                        default -> "Ellenőrizd a megadott adatokat.";
                    };
                })
                .findFirst()
                .orElse("Ellenőrizd a megadott adatokat.");

        return ResponseEntity
                .badRequest()
                .body(Map.of("message", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableRequest(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message",
                        "A kérés adatai hibásak. Ellenőrizd a dátumot és a megadott értékeket."
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedException(
            Exception exception
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message",
                        "Váratlan hiba történt. Próbáld újra később."
                ));
    }
}
