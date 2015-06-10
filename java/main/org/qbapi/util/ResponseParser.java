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
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by chazz on 6/10/2015.
 */
public class ResponseParser {

	private final static String ATTR_APPLICATION_ID = "application_id";
	private final static String ATTR_CREATED_AT = "created_at";
	private final static String ATTR_UPDATED_AT = "updated_at";
	private final static String ATTR_ID = "_id";
	private final static String ATTR_TIMESTAMP = "ts";
	private final static String ATTR_TOKEN = "token";

	public static final Dialog toDialog(String jsonResponse) {

		return null;
	}

	public static final ApiUser toApiUser(String rawResponse) {
		ApiUser apiUser = new ApiUser();

		apiUser.setRawResponse(rawResponse);

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
				session.setCreateDate(parseDate(sessionJson.getString(ATTR_CREATED_AT)));
				session.setUpdateDate(parseDate(sessionJson.getString(ATTR_UPDATED_AT)));
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
