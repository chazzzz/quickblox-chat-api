package org.qbapi.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qbapi.error.QBException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private String rawResponseTxt;

	private JSONObject jsonResponse;

	public static final QBResponse parse(String rawResponseTxt) throws JSONException {
		return new QBResponse(rawResponseTxt);
	}

	private QBResponse(String rawResponseTxt) throws JSONException {
		this.rawResponseTxt = rawResponseTxt;

		jsonResponse = new JSONObject(rawResponseTxt);
	}

	public QBDialog toDialog() throws JSONException {
	    QBDialog dialog = new QBDialog();
	    dialog.setRawInfo(rawResponseTxt);

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
		apiUser.setRawInfo(rawResponseTxt);

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
		session.setRawInfo(rawResponseTxt);

		JSONObject sessionJson = jsonResponse.getJSONObject(ATTR_SESSION);
		session.setApplicationId(sessionJson.getLong(ATTR_APPLICATION_ID));
		session.setId(sessionJson.getString(ATTR_ID));
		session.setTimestamp(sessionJson.getLong(ATTR_TIMESTAMP));
		session.setCreatedAt(parseDate(sessionJson.getString(ATTR_CREATED_AT)));
		session.setUpdatedAt(parseDate(sessionJson.getString(ATTR_UPDATED_AT)));
		session.setToken(sessionJson.getString(ATTR_TOKEN));

		return session;
	}

	public boolean hasErrors() {
		return jsonResponse.has("errors");
	}

	private Date parseDate(String dateStr) {
		DateFormat parser = new SimpleDateFormat(DATE_FORMAT);

		try {
			return parser.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	public String getRawResponseTxt() {
		return rawResponseTxt;
	}

	public void setRawResponseTxt(String rawResponseTxt) {
		this.rawResponseTxt = rawResponseTxt;
	}

	public void throwError() throws QBException {
		throw new QBException(QBException.ERROR_GENERIC);
	}
}
