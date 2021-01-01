package com.jyt.bitcoinmaster.exchange.kraken;

import com.jyt.bitcoinmaster.exchange.kraken.utils.Base64Utils;
import com.jyt.bitcoinmaster.exchange.kraken.utils.ByteUtils;
import com.jyt.bitcoinmaster.exchange.kraken.utils.CryptoUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Http Json API client
 *
 * @author Stéphane Bouclier
 */
public class HttpJsonClient {

    public String executePublicQuery(String baseUrl, String urlMethod) throws IOException {
        return executePublicQuery(baseUrl, urlMethod, null);
    }

    public String executePublicQuery(String baseUrl, String urlMethod, Map<String, String> params) throws IOException {
        final StringBuilder url = new StringBuilder(baseUrl).append(urlMethod).append("?");

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                url.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        return getPublicJsonResponse(new URL(url.toString()));
    }

    public String getPublicJsonResponse(URL url) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            return getJsonResponse(connection);
        } finally {
            connection.disconnect();
        }
    }

    public String executePrivateQuery(String baseUrl, String urlMethod,KrakenParam krakenParam) throws IOException, KrakenApiException {
        return executePrivateQuery(baseUrl, urlMethod, null,krakenParam);
    }

    public String executePrivateQuery(String baseUrl, String urlMethod, Map<String, Object> params,KrakenParam krakenParam) throws IOException, KrakenApiException {
        if (krakenParam.getKrakenApiKey() == null || krakenParam.getKrakenSecret() == null) {
            throw new KrakenApiException("must provide API key and secret");
        }

        final String nonce = generateNonce();
        final String postData = buildPostData(params, nonce);
        final String signature = generateSignature(urlMethod, nonce, postData,krakenParam);

        return getPrivateJsonResponse(new URL(baseUrl + urlMethod), postData, signature,krakenParam);
    }

    public String getPrivateJsonResponse(URL url, String postData, String signature,KrakenParam krakenParam) throws IOException {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // https
            trustAllHosts();
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            connection.setRequestMethod("POST");
            connection.addRequestProperty("API-Key", krakenParam.getKrakenApiKey());
            connection.addRequestProperty("API-Sign", signature);

            if (postData != null && !postData.toString().isEmpty()) {
                connection.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(postData.toString());
                out.flush();
                out.close();
            }

            return getJsonResponse(connection);
        } finally {
            connection.disconnect();
        }
    }

    private String buildPostData(Map<String, Object> params, String nonce) {
        final StringBuilder postData = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                postData.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        postData.append("nonce=").append(nonce);
        return postData.toString();
    }

    public String generateNonce() {
        return String.valueOf(System.currentTimeMillis() * 1000);
    }

    /**
     * Generate signature
     *
     * @param path     URI path
     * @param nonce
     * @param postData POST data
     * @return generated signature
     * @throws KrakenApiException
     */
    private String generateSignature(String path, String nonce, String postData,KrakenParam krakenParam) throws KrakenApiException {
        // Algorithm: HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key

        String hmacDigest = null;

        try {
            byte[] bytePath = ByteUtils.stringToBytes(path);
            byte[] sha256 = CryptoUtils.sha256(nonce + postData);
            byte[] hmacMessage = ByteUtils.concatArrays(bytePath, sha256);

            byte[] hmacKey = Base64Utils.base64Decode(krakenParam.getKrakenSecret());

            hmacDigest = Base64Utils.base64Encode(CryptoUtils.hmacSha512(hmacKey, hmacMessage));
        } catch (Throwable ex) {
            throw new KrakenApiException("unable to generate signature");
        }
        return hmacDigest;
    }

    public String getJsonResponse(HttpsURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }


    private   void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
