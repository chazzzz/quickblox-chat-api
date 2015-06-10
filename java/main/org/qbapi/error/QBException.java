package org.qbapi.error;

/**
 * Created by chazz on 6/10/2015.
 */
public class QBException extends Exception {

    public static final int UKNOWN_ERROR = 0;

    private int errorCode;

    public QBException(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
