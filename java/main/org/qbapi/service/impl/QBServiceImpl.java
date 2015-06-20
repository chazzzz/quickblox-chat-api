package org.qbapi.service.impl;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.qbapi.bean.QBApiUser;
import org.qbapi.bean.QBDialog;
import org.qbapi.bean.QBResponse;
import org.qbapi.bean.QBSession;
import org.qbapi.conf.QBConfig;
import org.qbapi.error.QBException;
import org.qbapi.service.QBService;
import org.qbapi.util.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chazz on 6/9/2015.
 */
public class QBServiceImpl implements QBService {

	private final static String API_ENDPOINT = "https://api.quickblox.com/";

	private final static int DIALOG_TYPE_PRIVATE = 3;

	public QBSession createSession(QBApiUser apiUser) throws QBException {

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
			signatureBody.append("&user[login]=").append(apiUser.getLogin());
			params.put("user[login]", apiUser.getLogin());

			if (!StringUtil.isEmpty(apiUser.getPassword())) {
				signatureBody.append("&user[password]=").append(apiUser.getPassword());
				params.put("user[password]", apiUser.getPassword());
			}
		}

		try {
			String signature = EncryptionUtil.encryptHmac(signatureBody.toString(), QBConfig.AUTH_SECRET);
			params.put("signature", signature);

			HttpResponse response = HttpUtil.post(getUrl("session"), params);

			QBResponse qbResponse = QBResponse.parse(HttpUtil.getContent(response));
			if (qbResponse.hasErrors()) {
				qbResponse.throwError();
			}

			return qbResponse.toSession();

		} catch (IOException e) {
			throw new QBException(QBException.ERROR_IO, e);

		} catch (JSONException e) {
			throw new QBException(QBException.ERROR_JSON, e);

		} catch (Exception e) {
			throw new QBException(QBException.ERROR_ENCRYPTION, e);
		}
	}

	@Override
	public QBSession createUnauthenticatedSession() throws QBException {
		return createSession(null);
	}

	@Override
	public QBApiUser getApiUserByLogin(String login) throws QBException {
		QBSession session = createUnauthenticatedSession();

		final Map<String, String> header = new HashMap<>();
		header.put("QB-Token", session.getToken());

		try {
			HttpResponse httpResponse = HttpUtil.get(getUrl("users/by_login?login=" + login), null, header);

			QBResponse qbResponse = QBResponse.parse(httpResponse.getStatusLine().getStatusCode(), HttpUtil.getContent(httpResponse));
			if (qbResponse.hasErrors()) {
				qbResponse.throwError();
			}

			return qbResponse.toApiUser();

		} catch (IOException e) {
			throw new QBException(QBException.ERROR_IO, e);

		} catch (JSONException e) {
			throw new QBException(QBException.ERROR_JSON, e);
		}

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

	@Override
	public QBDialog createDialog(QBApiUser owner, QBApiUser recipient, String className, String direction) throws QBException {

		// retrieve the session token for a created session
		String sessionToken = createSession(owner).getToken();

		try {
			// prepare the payload for the call
			JSONObject dialogJson = new JSONObject();
			dialogJson.put("type", DIALOG_TYPE_PRIVATE);
			dialogJson.put("occupants_ids", recipient.getId());

			if (!StringUtil.isEmpty(className)) {
				dialogJson.put("data[class_name]", className);
			}

			if (!StringUtil.isEmpty(direction)) {
				dialogJson.put("data[direction]", direction);
			}

			final Map<String, String> header = new HashMap<>();
			header.put("QB-Token", sessionToken);

			// execute dialog creation request
			final String responseTxt = HttpUtil.postJson(getUrl("chat/Dialog"), dialogJson.toString(), header);
			QBResponse qbResponse = QBResponse.parse(responseTxt);
			if (qbResponse.hasErrors()) {
				qbResponse.throwError();
			}

			return qbResponse.toDialog();

		} catch (JSONException e) {
			throw new QBException(QBException.ERROR_JSON, e);

		} catch (IOException e) {
			throw new QBException(QBException.ERROR_IO, e);
		}
	}

	@Override
	public QBDialog createDialog(QBApiUser owner, QBApiUser recipient) throws QBException {
		return createDialog(owner, recipient, null, null);
	}

	public QBApiUser registerApiUser(QBApiUser apiUser) throws QBException {

		QBSession session = createUnauthenticatedSession();

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
			QBResponse qbResponse = QBResponse.parse(responseTxt);
			if (qbResponse.hasErrors()) {
				qbResponse.throwError();
			}

			QBApiUser registeredUser = qbResponse.toApiUser();
			registeredUser.setPassword(apiUser.getPassword());

			return registeredUser;

		} catch (JSONException e) {
			throw new QBException(QBException.ERROR_JSON, e);

		} catch (IOException e) {
			throw new QBException(QBException.ERROR_IO, e);
		}
	}

	@Override
	public QBApiUser deleteApiUser(QBApiUser apiUser) throws QBException {

		if (apiUser.getId() != null) {
			QBSession session = createSession(apiUser);
			try {

				Map<String, String> headers = new HashMap<>();
				headers.put("QB-Token", session.getToken());

				String responseTxt = HttpUtil.delete(getUrl("users/" + apiUser.getId()), headers);

				QBResponse qbResponse = QBResponse.parse(responseTxt);
				if (qbResponse.hasErrors()) {
					qbResponse.throwError();
				}

				apiUser.setId(null);
				return apiUser;

			} catch (IOException e) {
				throw new QBException(QBException.ERROR_IO, e);

			} catch (JSONException e) {
				throw new QBException(QBException.ERROR_JSON, e);
			}

		}

		return null;
	}
}
