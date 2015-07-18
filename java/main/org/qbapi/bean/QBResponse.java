package org.qbapi.bean;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qbapi.error.QBException;
import org.qbapi.util.HttpUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by chazz on 6/10/2015.
 */
public class QBResponse {

	private final static String ATTR_APPLICATION_ID = "application_id";
	private final static String ATTR_CREATED_AT = "created_at";
	private final static String ATTR_UPDATED_AT = "updated_at";
	private final static String ATTR_ID = "id";
	private final static String ATTR_TIMESTAMP = "ts";
	private final static String ATTR_TOKEN = "token";
    private final static String ATTR_EMAIL = "email";
    private final static String ATTR_FULL_NAME = "full_name";
    private final static String ATTR_LOGIN = "login";
	private final static String ATTR_DIALOG_ID = "_id";
	private final static String ATTR_OCCUPANTS_IDS = "occupants_ids";
	private final static String ATTR_USER_ID = "user_id";
	private final static String ATTR_SESSION = "session";
	private final static String ATTR_USER = "user";
	private final static String ATTR_API_ENDPOINT = "api_endpoint";
	private final static String ATTR_ACCOUNT_ID = "account_id";
	private final static String ATTR_CHAT_ENDPOINT = "chat_endpoint";
	private final static String ATTR_TURNSERVER_ENDPOINT = "turnserver.quickblox.com";
	private final static String ATTR_S3_BUCKET = "s3_bucket_name";

	private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";


	private HttpResponse httpResponse;

	private String responseTxt;

	private JSONObject jsonResponse;

	public static final QBResponse parse(HttpResponse httpResponse) throws IOException, JSONException {
		return new QBResponse(httpResponse);
	}

	private QBResponse(HttpResponse httpResponse) throws IOException, JSONException {
		this.httpResponse = httpResponse;
		this.responseTxt = HttpUtil.getContent(httpResponse);

		jsonResponse = new JSONObject();
	}

	public QBDialog toDialog() throws JSONException {
	    QBDialog dialog = new QBDialog();
	    dialog.setRawInfo(responseTxt);

		dialog.setCreatedAt(parseDate(jsonResponse.getString(ATTR_CREATED_AT)));
	    dialog.setUpdatedAt(parseDate(jsonResponse.getString(ATTR_UPDATED_AT)));
	    dialog.setId(jsonResponse.getString(ATTR_DIALOG_ID));
	    dialog.setUserId(jsonResponse.getLong(ATTR_USER_ID));

	    JSONArray occupantsIds = jsonResponse.getJSONArray(ATTR_OCCUPANTS_IDS);
	    for (int i = 0; i < occupantsIds.length(); i++) {
			dialog.addOccupantsId(occupantsIds.getLong(i));
	    }

	    return dialog;
    }

	public QBApiUser toApiUser() throws JSONException {
		QBApiUser apiUser = new QBApiUser();
		apiUser.setRawInfo(this.responseTxt);

        JSONObject userJson = jsonResponse.getJSONObject(ATTR_USER);
        apiUser.setEmail(userJson.getString(ATTR_EMAIL));
        apiUser.setId(userJson.getLong(ATTR_ID));
        apiUser.setFullName(userJson.getString(ATTR_FULL_NAME));
        apiUser.setLogin(userJson.getString(ATTR_LOGIN));
        apiUser.setCreatedAt(parseDate(userJson.getString(ATTR_CREATED_AT)));
        apiUser.setUpdatedAt(parseDate(userJson.getString(ATTR_UPDATED_AT)));

		return apiUser;
	}

	public QBSession toSession() throws JSONException {
		QBSession session = new QBSession();
		session.setRawInfo(this.responseTxt);

		JSONObject sessionJson = jsonResponse.getJSONObject(ATTR_SESSION);
		session.setApplicationId(sessionJson.getLong(ATTR_APPLICATION_ID));
		session.setId(sessionJson.getString(ATTR_ID));
		session.setTimestamp(sessionJson.getLong(ATTR_TIMESTAMP));
		session.setCreatedAt(parseDate(sessionJson.getString(ATTR_CREATED_AT)));
		session.setUpdatedAt(parseDate(sessionJson.getString(ATTR_UPDATED_AT)));
		session.setToken(sessionJson.getString(ATTR_TOKEN));

		return session;
	}

	public QBAccountSettings toAccountSettings() throws JSONException {
		QBAccountSettings accountSettings = new QBAccountSettings();
		accountSettings.setAccountId(jsonResponse.getInt(ATTR_ACCOUNT_ID));
		accountSettings.setApiEndpoint(jsonResponse.getString(ATTR_API_ENDPOINT));
		accountSettings.setTurnServerEndpoint(jsonResponse.getString(ATTR_TURNSERVER_ENDPOINT));
		accountSettings.setChatEndpoint(jsonResponse.getString(ATTR_CHAT_ENDPOINT));

		return accountSettings;
	}

	public boolean hasErrors() {
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		return  statusCode == QBException.ERROR_UNAUTHORIZED ||
				statusCode == QBException.ERROR_UNPROCESSABLE_ENTRY ||
				statusCode == QBException.ERROR_NOT_FOUND ||
				statusCode == QBException.ERROR_BAD_REQUEST ||
				jsonResponse.has("errors");
	}

	private Date parseDate(String dateStr) {
		DateFormat parser = new SimpleDateFormat(DATE_FORMAT);

		try {
			return parser.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	public void throwError() throws QBException {
		QBException qbException = new QBException(QBException.ERROR_API, this.responseTxt);

		try {
			JSONObject errorsJson = jsonResponse.getJSONObject("errors");
			Iterator keys = errorsJson.keys();
			while (keys.hasNext()) {
				String key = keys.next() + "";
				QBError qbError = new QBError();
				qbError.setField(key);

				JSONArray messagesJson = errorsJson.getJSONArray(key);
				for (int i = 0; i < messagesJson.length(); i++) {
					qbError.addMessage(messagesJson.getString(i));
				}

				qbException.addError(qbError);
			}
			throw qbException;

		} catch (JSONException e) {
			throw qbException;
		}
	}

}
