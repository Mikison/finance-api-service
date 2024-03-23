package pl.sonmiike.financeapiservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.sonmiike.financeapiservice.exceptions.custom.EmailAlreadyTakenException;
import pl.sonmiike.financeapiservice.exceptions.custom.IdNotMatchingException;
import pl.sonmiike.financeapiservice.exceptions.custom.ResourceNotFound;

@ControllerAdvice
public class ResponseExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFound.class)
    public String handleResourceNotFound(ResourceNotFound e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({EmailAlreadyTakenException.class, IdNotMatchingException.class})
    public String handleConflict(RuntimeException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public String handleBadCredentials(BadCredentialsException e) {
        return e.getMessage();
    }
}
