package fayi.utils;

/*
    异常
 */
public class SetAException extends Exception {

    private final Integer Code;
    private final String Message;

    public SetAException(Integer code, String message) {
        super(message);
        Message = message;
        Code = code;
    }

    public Integer getCode() {
        return Code;
    }

    @Override
    public String getMessage() {
        return Message;
    }
}
