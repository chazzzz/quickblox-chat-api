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
	public static final int ERROR_API = 6;

	private Exception rootException;

	private String rawResponse;

	private int errorCode;

	public QBException(int errorCode, Exception e) {
		super(e.getMessage());

		this.errorCode = errorCode;
		this.rootException = e;
	}

	public QBException(int errorCode, String rawResponse) {
		this.errorCode = errorCode;
		this.rawResponse = rawResponse;
	}

    public int getErrorCode() {
        return errorCode;
    }

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	public void setErrorCode(int errorCode) {


        this.errorCode = errorCode;
    }
}
