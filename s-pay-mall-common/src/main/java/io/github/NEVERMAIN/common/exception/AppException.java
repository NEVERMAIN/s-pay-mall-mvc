package io.github.NEVERMAIN.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 8653090271840061986L;

    private String code;

    private String info;

    public AppException(String code) {
        this.code = code;
    }


    public AppException(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public AppException(String code, Throwable cause) {
        this.code = code;
        super.initCause(cause);
    }

    public AppException(String code, String message, Throwable cause) {
        this.code = code;
        this.info = message;
        super.initCause(cause);
    }

    @Override
    public String toString() {
        return "io.github.NEVERMAIN.common.exception.AppException{" +
                "code='" + code + '\'' +
                ", info='" + info + '\'' +
                '}';
    }


}
