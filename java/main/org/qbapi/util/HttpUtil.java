package org.qbapi.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by chazz on 6/10/2015.
 */
public class HttpUtil {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_PUT = "PUT";

	/**
	 * Requests to a URL using the METHOD GET
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String get(String url, Map<String, String> params) throws IOException {
		return HttpUtil.sendRequest(METHOD_GET, url, params, null);
	}

	/**
	 * Requests to a URL using the METHOD GET
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String get(String url, Map<String, String> params, Map<String, String> header) throws IOException {
		return HttpUtil.sendRequest(METHOD_GET, url, params, header);
	}

	/**
	 * Requests to a URL using the METHOD POST
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String post (String url, Map<String, String> params) throws IOException {
		return HttpUtil.sendRequest(METHOD_POST, url, params, null);
	}

	/**
	 * Requests to a URL using the METHOD POST
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String post(String url, Map<String, String> headers, Map<String, String> params) throws ClientProtocolException, IOException {
		return HttpUtil.sendRequest(METHOD_POST, url, params, headers);
	}

	/**
	 * The core method of the utility function.
	 *
	 * @param method
	 * @param requestUrl
	 * @param params
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	private static String sendRequest(String method, String requestUrl, Map<String, String> params, Map<String, String> headers) throws IOException {

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpUriRequest request = null;

        // if the method is GET, the supplied parameters will be constructed and appended to the URL
        if (method.equals(METHOD_GET)) {
            if (params != null && params.size() > 0) {
                final StringBuilder rawUrl = new StringBuilder(requestUrl).append("?");
                for (String paramKey : params.keySet()) {
                    rawUrl.append(paramKey)
                            .append("=")
                            .append(params.get(paramKey))
                            .append("&");
                }
                requestUrl = rawUrl.substring(0, rawUrl.length() - 1);
            }
            request = new HttpGet(requestUrl);

        // if the method is POST, the parameters will be added using the NameValuePair of Apache
        } else if (method.equals(METHOD_POST)) {
            request = new HttpPost(requestUrl);
            if (params != null && params.size() > 0) {
                final List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                for (String paramKey : params.keySet()) {
                    nameValuePair.add(new BasicNameValuePair(paramKey, params.get(paramKey)));
                }

                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            }

        } else if (method.equals(METHOD_DELETE)) {
            request = new HttpDelete(requestUrl);

        } else if (method.equals(METHOD_PUT)) {
            request = new HttpPut(requestUrl);
            if (params != null && params.size() > 0) {
                final ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                for (String paramKey : params.keySet()) {
                    nameValuePair.add(new BasicNameValuePair(paramKey, params.get(paramKey)));
                }

                ((HttpPut) request).setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            }

        } else {
            throw new IllegalArgumentException("No request method is specified.");
        }

		// add the headers supplied
		if(headers != null) {
			for(String header: headers.keySet()) {
				request.addHeader(header, headers.get(header));
			}
		}

		final HttpResponse response = httpClient.execute(request);
		final InputStream inStream = response.getEntity().getContent();

		final String responseTxt = convertStreamToString(inStream);

		inStream.close();

		return responseTxt;
	}

	private static String convertStreamToString(InputStream inputStream) {
		try {
			return new Scanner(inputStream).useDelimiter("\\A").next();
		} catch (NoSuchElementException e) {
			return "";
		}
	}

	public static final String postJson(String url, String jsonData, Map<String, String> headers) throws IOException {
		final HttpPost httpPost = new HttpPost(url);
		final StringEntity entity = new StringEntity(jsonData, "UTF-8");
		entity.setContentType("application/json");

		httpPost.setEntity(entity);

		for(String key : headers.keySet()) {
			httpPost.addHeader(new BasicHeader(key, headers.get(key)));
		}

		final CloseableHttpClient client = HttpClients.createDefault();

		final InputStream inStream = client.execute(httpPost).getEntity().getContent();
		final String responseTxt = convertStreamToString(inStream);

		inStream.close();

		return responseTxt;
	}

    public static void delete(String url, Map<String, String> headers) throws IOException {
        sendRequest(METHOD_DELETE, url, null, headers);
    }
}
