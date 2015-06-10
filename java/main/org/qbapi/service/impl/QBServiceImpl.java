package org.qbapi.service.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.UNKNOWN;
import org.qbapi.bean.ApiUser;
import org.qbapi.bean.Dialog;
import org.qbapi.bean.Session;
import org.qbapi.conf.QBConfig;
import org.qbapi.error.QBException;
import org.qbapi.service.QBService;
import org.qbapi.util.*;

import javax.xml.ws.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chazz on 6/9/2015.
 */
public class QBServiceImpl implements QBService {

	private final static String API_ENDPOINT = "https://api.quickblox.com/";

	private final static int DIALOG_TYPE_PRIVATE = 3;

	public Session createSession(ApiUser apiUser)  {

		long timestamp = TimeUtil.getUnixTime();
		double nonce = NumberUtil.randomNonce();

		StringBuilder signatureBody = new StringBuilder();
		signatureBody.append("application_id=").append(QBConfig.APP_ID)
				.append("&auth_key=").append(QBConfig.AUTH_KEY)
				.append("&nonce=").append((int) nonce)
				.append("&timestamp=").append(timestamp);

		Map<String, String> params = new HashMap<String, String>();
		params.put("application_id", QBConfig.APP_ID);
		params.put("auth_key", QBConfig.AUTH_KEY);
		params.put("timestamp", ""+timestamp);
		params.put("nonce", ""+(int)nonce);

		if (apiUser != null) {
			signatureBody
					.append("&user[login]=").append(apiUser.getLogin())
					.append("&user[password]=").append(apiUser.getPassword());

			params.put("user[login]", apiUser.getLogin());
			params.put("user[password]", apiUser.getPassword());
		}

		try {
			String signature = EncryptionUtil.encryptHmac(signatureBody.toString(), QBConfig.AUTH_SECRET);
			params.put("signature", signature);

			String responseTxt = HttpUtil.post(getUrl("session"), params);

			return ResponseParser.toSession(responseTxt);

		} catch (Exception e) {
			return null;
		}
	}

	public Session createUnauthenticatedSession() {
		return createSession(null);
	}

	private String getUrl(String resource) {
		final String[] urlResource = resource.split("\\?");
		String urlParams = "";

		if(urlResource.length > 1) {
			urlParams = "?" + urlResource[1];
			resource = urlResource[0];
		}

		final String url = API_ENDPOINT + resource + ".json" + urlParams;

		return url;
	}

	public Dialog createDialog() {
		return null;
	}

	public ApiUser registerApiUser(ApiUser apiUser) throws QBException {

        Session session = createUnauthenticatedSession();

        try {
            final JSONObject userJson = new JSONObject();
            userJson.put("login", apiUser.getLogin());
            userJson.put("password", apiUser.getPassword());
            userJson.put("email", apiUser.getEmail());
            userJson.put("full_name", apiUser.getFullName());

            final JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("user", userJson);

            final Map<String, String> headers = new HashMap<>();
            headers.put("QB-Token", session.getToken());

            // execute the request
            final String responseTxt = HttpUtil.postJson(getUrl("users"), jsonPayload.toString(), headers);

            return ResponseParser.toApiUser(responseTxt);

        } catch (JSONException e) {
            throw new QBException(QBException.UKNOWN_ERROR);

        } catch (IOException e) {
            throw new QBException(QBException.UKNOWN_ERROR);
        }

        /*
        final JsonNode responseJson = Json.parse(responseTxt);
        if (responseJson.has("errors")) {
            systemLogService.createLog(SystemLogType.DEBUG, "Response has errors...");

            final JsonNode errorsJson = responseJson.get("errors");
            if (errorsJson.has("login")) {
                final Iterator<JsonNode> loginErrors = errorsJson.get("login").iterator();
                while (loginErrors.hasNext()) {
                    final String loginError = loginErrors.next().textValue();
                    if (loginError.equals("has already been taken")) {
                        systemLogService.createLog(SystemLogType.DEBUG, "This user is already registered to QuickBlox. retrieving details...");
                        return getApiUserByLogin(user.getQuickBloxLoginName(), sessionToken);
                    }
                }
            }
        } else {
            systemLogService.createLog(SystemLogType.DEBUG, "Doesn't have errors...");
        }

        final JsonNode responseUserJson = responseJson.get("user");

        if (responseUserJson.has("id")) {
            systemLogService.createLog(SystemLogType.DEBUG, "Returning id = " + responseUserJson.get("id").textValue());

            return responseUserJson.get("id").longValue();
        }*/
    }

    @Override
    public void deleteApiUser(ApiUser apiUser) throws QBException {

        Session session = createUnauthenticatedSession();

        Map<String, String> headers = new HashMap<>();
        headers.put("QB-Token", session.getToken());

        try {
            HttpUtil.delete(getUrl("users/" + apiUser.getId()), headers);
        } catch (IOException e) {
            throw new QBException(QBException.UKNOWN_ERROR);
        }
    }
}
