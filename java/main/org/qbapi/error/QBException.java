package org.qbapi.error;

import org.qbapi.bean.QBError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chazz on 6/10/2015.
 */
public class QBException extends Exception {

    public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_JSON = 1;
	public static final int ERROR_IO = 4;
	public static final int ERROR_ENCRYPTION = 5;
	public static final int ERROR_API = 6;
	public static final int ERROR_UNAUTHORIZED = 401;
	public static final int ERROR_UNPROCESSABLE_ENTRY = 422;
	public static final int ERROR_NOT_FOUND = 404;
	public static final int ERROR_BAD_REQUEST = 400;

	private Exception rootException;

	private String rawResponse;

	private List<QBError> errors = new ArrayList<>();

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

	public void addError(QBError error) {
		this.errors.add(error);
	}
}
