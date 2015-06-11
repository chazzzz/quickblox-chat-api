package org.qbapi.util;

import org.json.JSONException;
import org.qbapi.bean.ApiUser;
import org.qbapi.bean.Dialog;
import org.qbapi.bean.Session;
import org.json.JSONObject;
import org.qbapi.error.QBException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chazz on 6/10/2015.
 */
public class ResponseParser {

	private final static String ATTR_APPLICATION_ID = "application_id";
	private final static String ATTR_CREATED_AT = "created_at";
	private final static String ATTR_UPDATED_AT = "updated_at";
	private final static String ATTR_ID = "id";
	private final static String ATTR_TIMESTAMP = "ts";
	private final static String ATTR_TOKEN = "token";
    private final static String ATTR_EMAIL = "email";
    private final static String ATTR_FULL_NAME = "full_name";
    private static final String ATTR_LOGIN = "login";


    public static final Dialog toDialog(String jsonResponse) {

		return null;
	}

	public static final ApiUser toApiUser(String rawResponse) throws QBException {
		ApiUser apiUser = new ApiUser();

		apiUser.setRawResponse(rawResponse);

        try {
            JSONObject jsonResponse = new JSONObject(rawResponse);
            if (jsonResponse.has("user")) {
                JSONObject userJson = jsonResponse.getJSONObject("user");

                apiUser.setEmail(userJson.getString(ATTR_EMAIL));
                apiUser.setId(userJson.getLong(ATTR_ID));
                apiUser.setFullName(userJson.getString(ATTR_FULL_NAME));
                apiUser.setLogin(userJson.getString(ATTR_LOGIN));
                apiUser.setCreatedAt(parseDate(userJson.getString(ATTR_CREATED_AT)));
                apiUser.setUpdatedAt(parseDate(userJson.getString(ATTR_UPDATED_AT)));
            }

        } catch (JSONException e) {
            return null;
        }

		return apiUser;
	}

	public static final Session toSession(String rawResponse) throws QBException {
		Session session = new Session();
		session.setRawResponse(rawResponse);

		try {
			JSONObject jsonResponse = new JSONObject(rawResponse);
			if (jsonResponse.has("session")) {

				JSONObject sessionJson = jsonResponse.getJSONObject("session");
				session.setApplicationId(sessionJson.getLong(ATTR_APPLICATION_ID));
				session.setId(sessionJson.getString(ATTR_ID));
				session.setTimestamp(sessionJson.getLong(ATTR_TIMESTAMP));
				session.setCreatedAt(parseDate(sessionJson.getString(ATTR_CREATED_AT)));
				session.setUpdatedAt(parseDate(sessionJson.getString(ATTR_UPDATED_AT)));
				session.setToken(sessionJson.getString(ATTR_TOKEN));

			} else {
				throw new QBException(QBException.UKNOWN_ERROR);
			}
		} catch (JSONException e) {
			return null;
		}

		return session;
	}

	private static Date parseDate(String dateStr) {
		DateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		try {
			return parser.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}
}
