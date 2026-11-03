package uz.repay.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.enums.ResultState;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class Advice {


    @ExceptionHandler(value = {GatewayException.class, TimeOutException.class, GeneralErrorException.class})
    public ResponseEntity<?> thirdPartyErrors(RuntimeException e) {
        log.info("Advice catch Third party error: {}", e.getMessage());
        return ResponseEntity.ok(new ResultData<>(ResultState.ThirdPartyError, e.getMessage()));
    }

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<?> badRequestErrors(Exception e) {
        log.info("Web error: {}", e.getMessage());
        return ResponseEntity.ok(new ResultData<>(ResultState.WrongParams, "Bad request or fields wrong filled!"));
    }

    @ExceptionHandler(value = {
            InvalidDataException.class,
            CardException.class,
            BranchException.class,
            EntityNotFoundException.class,
            InvalidDataException.class,
            LoanCardException.class,
            LoanCardException.class,
            PermissionException.class,
            SQLException.class,
            MissingServletRequestPartException.class
    })
    public ResponseEntity<?> backErrors(Exception e) {
        log.info("Back end catch error: {}", e.getMessage());
        return ResponseEntity.ok(new ResultData<>(ResultState.InvalidRequest, e.getMessage()));
    }

    @ExceptionHandler(value = ServiceDisabledException.class)
    public ResponseEntity<?> serviceIsDisabled(ServiceDisabledException e) {
        log.info("Service is disabled exception: {}", e.getMessage());
        return ResponseEntity.ok(ResultData.error(ResultState.BlockOperation, e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequest(RuntimeException e) {
        log.info("Bad Request exception: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ResultData.error(ResultState.BlockOperation, e.getMessage()));
    }

}
