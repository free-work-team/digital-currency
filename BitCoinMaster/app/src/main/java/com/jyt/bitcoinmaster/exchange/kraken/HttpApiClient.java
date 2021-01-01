package com.jyt.bitcoinmaster.exchange.kraken;

import com.alibaba.fastjson.JSONObject;
import com.jyt.bitcoinmaster.exchange.Entity.MyResponse;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http API client
 *
 * @author Stéphane Bouclier
 */
public class HttpApiClient {

    private HttpJsonClient client;
    private int apiVersion = 0;
    private static Logger log = Logger.getLogger("BitCoinMaster");

    public HttpApiClient() {
        client = new HttpJsonClient();
    }

    /**
     * Call public kraken method
     *
     * @param baseUrl kraken base url
     * @param method  kraken method
     * @return result
     */
    public MyResponse callPublic(String baseUrl, KrakenApiMethod method){
        MyResponse result = new MyResponse();
        try {
            final String responseString = this.client.executePublicQuery(baseUrl, method.getUrl(apiVersion));
            JSONObject jsonObject = JSONObject.parseObject(responseString);
            if (jsonObject.getJSONArray("error").size() > 0) {
                result.setMessage(jsonObject.getJSONArray("error").toJSONString());
                return result;
            }
            result.setMessage(jsonObject.getJSONObject("result").toJSONString());
            result.setSuccess(true);
        } catch (Exception ex) {
            log.error("callPublic", ex);
            result.setMessage(ex.getMessage());
        }
        return result;

    }

    /**
     * Call public kraken method
     *
     * @param baseUrl kraken base url
     * @param method  kraken method
     * @param params  method parameters
     * @return result
     */
    public MyResponse callPublic(String baseUrl, KrakenApiMethod method, Map<String, String> params) {
        MyResponse result = new MyResponse();
        try {
            final String responseString = this.client.executePublicQuery(baseUrl, method.getUrl(apiVersion), params);
            JSONObject jsonObject = JSONObject.parseObject(responseString);
            if (jsonObject.getJSONArray("error").size() > 0) {
                result.setMessage(jsonObject.getJSONArray("error").toJSONString());
                return result;
            }
            result.setMessage(jsonObject.getJSONObject("result").toJSONString());
            result.setSuccess(true);
        } catch (Exception ex) {
            log.error("callPublic", ex);
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    /**
     * Call public kraken method and extract last id
     *
     * @param baseUrl kraken base url
     * @param method  kraken method
     * @param params  method parameters
     * @return result
     */
    public MyResponse callPublicWithLastId(String baseUrl, KrakenApiMethod method, Map<String, String> params) {
        MyResponse result = new MyResponse();
        try {

            final String responseString = this.client.executePublicQuery(baseUrl, method.getUrl(apiVersion), params);
            LastIdExtractedResult extractedResult = extractLastId(responseString);
            JSONObject jsonObject = JSONObject.parseObject(responseString);
            if (jsonObject.getJSONArray("error").size() > 0) {
                result.setMessage(jsonObject.getJSONArray("error").toJSONString());
                return result;
            }
            JSONObject newObj = jsonObject.getJSONObject("result");
            newObj.put("lastId", extractedResult.lastId);
            result.setMessage(newObj.toJSONString());
            result.setSuccess(true);
        } catch (Exception ex) {
            log.error("callPublicWithLastId", ex);
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    /**
     * Call private kraken method
     *
     * @param baseUrl kraken base url
     * @param method  kraken method
     * @return result
     */
    public MyResponse callPrivate(String baseUrl, KrakenApiMethod method,KrakenParam krakenParam) {
        MyResponse result = new MyResponse();
        try {
            final String responseString = this.client.executePrivateQuery(baseUrl, method.getUrl(apiVersion),krakenParam);
            JSONObject jsonObject = JSONObject.parseObject(responseString);
            if (jsonObject.getJSONArray("error").size() > 0) {
                result.setMessage(jsonObject.getJSONArray("error").toJSONString());
                return result;
            }
            result.setMessage(jsonObject.getJSONObject("result").toJSONString());
            result.setSuccess(true);
        } catch (Exception ex) {
            log.error("callPrivate", ex);
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    /**
     * Call private kraken method
     *
     * @param baseUrl kraken base url
     * @param method  kraken method
     * @param params  method parameters
     * @return result
     */
    public MyResponse callPrivate(String baseUrl, KrakenApiMethod method, Map<String, Object> params,KrakenParam krakenParam) {
        MyResponse result = new MyResponse();
        try {
            final String responseString = this.client.executePrivateQuery(baseUrl, method.getUrl(apiVersion), params,krakenParam);
            JSONObject jsonObject = JSONObject.parseObject(responseString);
            if (jsonObject.getJSONArray("error").size() > 0) {
                result.setMessage(jsonObject.getJSONArray("error").toJSONString());
                return result;
            }
            result.setMessage(JSONObject.toJSONString(jsonObject.getObject("result",Object.class)));
            result.setSuccess(true);
        } catch (Exception ex) {
            log.error("callPrivate", ex);
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    /**
     * LastId extracted class result
     */
    private class LastIdExtractedResult {
        public String responseWithoutLastId;
        public Long lastId;

        public LastIdExtractedResult(String responseWithoutLastId, Long lastId) {
            this.responseWithoutLastId = responseWithoutLastId;
            this.lastId = lastId;
        }
    }

    /**
     * Extract last id from string and wrap it with response without it
     *
     * @param response
     * @return extracted result
     * @throws KrakenApiException
     */
    private LastIdExtractedResult extractLastId(String response) throws KrakenApiException {
        final String lastPattern = ",(\\s*)\"last\":\"{0,1}([0-9]+)\"{0,1}";

        Pattern pattern = Pattern.compile(lastPattern);
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            response = response.replaceAll(lastPattern, "");

            return new LastIdExtractedResult(response, Long.valueOf(matcher.group(2)));
        } else {
            throw new KrakenApiException("unable to extract last id");
        }
    }
}
