package com.jyt.terminal.util;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

//import org.apache.http.ssl.SSLContexts;
/*
 * 利用HttpClient进行post请求的工具类
 */
public class HttpClientUtil {
//	protected static Logger logger = LoggerFactory
//			.getLogger(HttpClientUtil.class);

	public static String doPost(String url, Map<String, String> params) throws KeyManagementException, NoSuchAlgorithmException {
		// Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
//                .loadTrustMaterial(new File("my.keystore"), "nopassword".toCharArray(),
//                        new TrustSelfSignedStrategy())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1.2" },
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
//		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;

		HttpPost post = postForm(url, params);

		body = invoke(httpclient, post);

		httpclient.getConnectionManager().shutdown();

		return body;
	}

	private static String invoke(CloseableHttpClient httpclient,
			HttpUriRequest httpost) {

		HttpResponse response = sendRequest(httpclient, httpost);
		String body = paseResponse(response);

		return body;
	}

	private static String paseResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();

		EntityUtils.getContentCharSet(entity);

		String body = null;
		try {
			body = EntityUtils.toString(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return body;
	}

	private static HttpResponse sendRequest(CloseableHttpClient httpclient,
			HttpUriRequest httpost) {
		HttpResponse response = null;

		try {
			response = httpclient.execute(httpost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return httpost;
	}

	public static String doHttpGet(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept-Version", "1.0");
//		conn.setRequestProperty("Content-type", "text/html");
//		conn.setRequestProperty("Accept-Charset", "gbk");
//		conn.setRequestProperty("contentType", "gbk");
		InputStream inStream = conn.getInputStream();
		InputStreamReader bis = new InputStreamReader(inStream, "gbk");
		String result = "";
		int c = 0;
		while ((c = bis.read()) != -1) {
			result = result + (char) c;
		}
		return result;
	}

	public static String doPostFormNotJson(String url,
			Map<String, String> paramsMap) {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			HttpPost handler = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Set<String> keySet = paramsMap.keySet();
			for (String key : keySet) {
				if (StringUtils.isNotBlank(paramsMap.get(key)))
				{
					params.add(new BasicNameValuePair(key, paramsMap.get(key)));
				}
			}
			handler.setEntity(new UrlEncodedFormEntity(params, "utf8"));

			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}
			};

			return httpclient.execute(handler, responseHandler);
		} catch (Exception e) {
			//logger.error("", e);
			return "-1";
		} finally {
			try {
				httpclient.close();
			} catch (Exception e) {
			}
		}

	}
}
