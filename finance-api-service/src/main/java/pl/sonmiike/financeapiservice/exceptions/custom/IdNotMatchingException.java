package pl.sonmiike.financeapiservice.exceptions.custom;

public class IdNotMatchingException extends RuntimeException {

    public IdNotMatchingException(String message) {
        super(message);
    }
}
