package org.qbapi.error;

/**
 * Created by chazz on 6/10/2015.
 */
public class QBException extends Exception {

    public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_JSON = 1;
	public static final int ERROR_GENERIC = 2;
	public static final int ERROR_IO = 4;
	public static final int ERROR_ENCRYPTION = 5;

	private Exception rootException;

	private int errorCode;

    public QBException(int errorCode) {
        this.errorCode = errorCode;
    }

	public QBException(int errorCode, Exception e) {
		this.errorCode = errorCode;
		this.rootException = e;
	}

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
