package com.jyt.bitcoinmaster.wallet.bitgo;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * http请求工具类
 * 
 * @author sunshubin
 *
 */
public class HttpUtils {
    private final String CTYPE_FORM = "application/x-www-form-urlencoded;charset=utf-8";
    private final String CTYPE_JSON = "application/json; charset=utf-8";
    private final String charset = "utf-8";
    private static Logger log = Logger.getLogger("BitCoinMaster");

    private static HttpUtils instance = null;

    public static HttpUtils getInstance() {
        if (instance == null) {
            return new HttpUtils();
        }
        return instance;
    }

    private class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }

    /**
     * 以application/json; charset=utf-8方式传输
     * 
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String postJson(String url, String jsonContent,String token)  throws Exception{
    	Map<String, String> headerMap = new HashMap<>();
    	headerMap.put("Authorization","Bearer "+token);
        return doRequest("POST", url, jsonContent, 10000, 60000, CTYPE_JSON,
        		headerMap);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     * 
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String postForm(String url) throws Exception {
        return doRequest("POST", url, "", 10000, 10000, CTYPE_FORM, null);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     * 
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String postForm(String url, Map<String, String> params)
            throws Exception {
        return doRequest("POST", url, buildQuery(params), 10000, 10000,
                CTYPE_FORM, null);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     * 
     * @param url
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String getForm(String url,String token) throws Exception {
    	Map<String, String> headerMap = new HashMap<>();
    	headerMap.put("Authorization","Bearer "+token);
        return doRequest("GET", url, "", 10000, 10000, CTYPE_JSON, headerMap);
    }

    /**
     * POST 以application/x-www-form-urlencoded;charset=utf-8方式传输
     * 
     * @param url
     * @param params
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String getForm(String url, Map<String, String> params)
            throws Exception {
        return doRequest("GET", url, buildQuery(params), 10000, 10000,
                CTYPE_FORM, null);
    }

    /**
     * get
     * @param reqUrl
     * @param token
     * @return
     */
    public String get(String reqUrl, String token) throws Exception{

        HttpURLConnection connection = null;
        InputStream in = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(reqUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5 * 1000);
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-type", "application/json");
            connection.connect();
            in = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            return buffer.toString();
        } catch (Exception e) {
            log.error("get fail", e);
            throw e;
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    log.error("bitgo get bufferedReader close fail", e1);
                }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("bitgo get in close fail", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String doRequest(String method, String url, String requestContent,
            int connectTimeout, int readTimeout, String ctype,
            Map<String, String> headerMap) throws  Exception{
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            conn = getConnection(new URL(url), method, ctype, headerMap);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            
            if(requestContent!=null && requestContent!=""){
                out = conn.getOutputStream();
                out.write(requestContent.getBytes(charset));
            }
            int responseCode = conn.getResponseCode();
            rsp = getResponseAsString(conn);
        }catch (Exception e){
            log.error("get fail", e);
            throw e;
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("bitgo get doRequest close fail", e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
            conn = null;
        }
        return rsp;
    }

    private HttpURLConnection getConnection(URL url, String method,
            String ctype, Map<String, String> headerMap) throws IOException {
        HttpURLConnection conn;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0],
                        new TrustManager[] { new DefaultTrustManager() },
                        new SecureRandom());
            } catch (Exception e) {
                throw new IOException(e);
            }
            HttpsURLConnection connHttps = (HttpsURLConnection) url
                    .openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept",
                "text/xml,text/javascript,text/html,application/json");
        conn.setRequestProperty("Content-Type", ctype);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    private String getResponseAsString(HttpURLConnection conn)
            throws IOException {
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset, conn);
        } else {
            String msg = getStreamAsString(es, charset, conn);
           
            if ( msg==null || msg=="") {
                throw new IOException(conn.getResponseCode() + ":"
                        + conn.getResponseMessage());
            } else {
                return msg;
            }
        }
    }

    private String getStreamAsString(InputStream stream, String charset,
            HttpURLConnection conn) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);

            StringBuilder response = new StringBuilder();
            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private String buildQuery(Map<String, String> params) throws IOException {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Map.Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (hasParam) {
                query.append("&");
            } else {
                hasParam = true;
            }
            query.append(name).append("=")
                    .append(URLEncoder.encode(value, charset));
        }
        return query.toString();
    }
}
