package nodir.paymo_add.exception;

import nodir.paymo_add.model.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorDto> handleMissingParams(MissingServletRequestParameterException ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(400);
        errorDto.setDetail(ex.getParameterName() + " - not found");
        errorDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put("error", "400");
            errors.put("description", "Required fields not exist");
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFound ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(400);
        errorDto.setDetail("Back End error");
        errorDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionFinished.class)
    public ResponseEntity<?> handleFinishedTransaction(TransactionFinished ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(400);
        errorDto.setDetail("Back End error");
        errorDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectionError.class)
    public ResponseEntity<?> handleConnectionError(ConnectionError ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(400);
        errorDto.setDetail("Back End error");
        errorDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParamNotFound.class)
    public ResponseEntity<?> paramNotFound(ParamNotFound ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(400);
        errorDto.setDetail("Back End error");
        errorDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardNotFound.class)
    public ResponseEntity<?> cardNotFound(CardNotFound ex) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(400);
        errorDto.setDetail("Back End error");
        errorDto.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
