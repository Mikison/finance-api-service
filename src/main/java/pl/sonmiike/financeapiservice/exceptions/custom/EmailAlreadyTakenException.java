package pl.sonmiike.financeapiservice.exceptions.custom;

public class EmailAlreadyTakenException extends RuntimeException {

    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
