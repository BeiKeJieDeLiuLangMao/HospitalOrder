package cy.service;

import cy.model.BaseResponse;
import cy.model.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@Service
@Slf4j
public class HttpClientService {

    private static final String BCH_API_BASE_URL = "https://bchapi.huaaiangel.com";
    private static final String CLIENT_ID = "c3d1030d-92f2-4773-8a15-c17d90e7ed25";
    private static final String CLIENT_ID_HEADER = "x-bchapi-clientid";
    private static final String SESSION_ID_HEADER = "x-bchapi-sessionid";
    private static final String SIGNATURE_HEADER = "x-bchapi-signature";
    private static final String TIMESTAMP_HEADER = "x-bchapi-timestamp";
    private static final String VERSION_HEADER = "x-bchapi-version";
    private static final String VERSION = "2.0";
    private static final String CLIENT_VERSION_HEADER = "x-bchapi-clientversion";
    private static final String CLIENT_VERSION = "android-3.1.1";

    private RestTemplate getTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        return new RestTemplate(factory);
    }

    @SuppressWarnings("unchecked")
    <T> BaseResponse<T> post2BchApiForObject(String path, String sessionId, Map<String, String> params,
                                             Class<T> clazz) {
        ResponseEntity<BaseResponse> responseEntity = post2BchApiForEntity("/bchapi/", path, sessionId, params);
        if (responseEntity.getBody() == null) {
            return new BaseResponse<>(ResponseCode.FAILED.getCode(), null, "Response entity body is empty");
        } else {
            return new BaseResponse<T>().transform(responseEntity.getBody(), clazz);
        }
    }

    @SuppressWarnings("unchecked")
    <T> BaseResponse<T> post2UmApiForObject(String path, String sessionId, Map<String, String> params,
        Class<T> clazz) {
        ResponseEntity<BaseResponse> responseEntity = post2BchApiForEntity("/umapi/", path, sessionId, params);
        if (responseEntity.getBody() == null) {
            return new BaseResponse<>(ResponseCode.FAILED.getCode(), null, "Response entity body is empty");
        } else {
            return new BaseResponse<T>().transform(responseEntity.getBody(), clazz);
        }
    }

    private ResponseEntity<BaseResponse> post2BchApiForEntity(String base, String path, String sessionId,
        Map<String, String> params) {
        RestTemplate restTemplate = getTemplate();
        String timestamp = getTimeStr();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(CLIENT_ID_HEADER, CLIENT_ID);
        headers.set(SESSION_ID_HEADER, sessionId);
        headers.set(SIGNATURE_HEADER, computeBchApiSignature(base, path, timestamp, params));
        headers.set(TIMESTAMP_HEADER, timestamp);
        headers.set(VERSION_HEADER, VERSION);
        headers.set(CLIENT_VERSION_HEADER, CLIENT_VERSION);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if (params != null) {
            params.forEach(multiValueMap::add);
        }
        return restTemplate.postForEntity(BCH_API_BASE_URL + base + path, new HttpEntity<>(multiValueMap, headers), BaseResponse.class);
    }

    @SuppressWarnings("unchecked")
    <T> BaseResponse<List<T>> post2BchApiForList(String path, String sessionId, Map<String, String> params,
                                                 Class<T> clazz) {
        ResponseEntity<BaseResponse> responseEntity = post2BchApiForEntity("/bchapi/", path, sessionId, params);
        if (responseEntity.getBody() == null) {
            return new BaseResponse<>(ResponseCode.FAILED.getCode(), new ArrayList<>(), "Response entity body is empty");
        } else {
            return new BaseResponse<List<T>>().transformList(responseEntity.getBody(), clazz);
        }
    }

    private String getTimeStr() {
        return String.valueOf(System.currentTimeMillis());
    }

    private String computeBchApiSignature(String base, String path, String timestamp, Map<String, String> params) {
        String fullPath = base + path;
        String paramsKey = paramsToString(params);
        return paraSignature(fullPath, paramsKey, timestamp);
    }

    @SuppressWarnings("unchecked")
    private String paramsToString(Map<String, String> params) {
        String var1 = "";
        String var2 = var1;
        if (params != null) {
            Iterator var3 = (new TreeMap(params)).entrySet().iterator();
            String var4 = var1;
            while (true) {
                var2 = var4;
                if (!var3.hasNext()) {
                    break;
                }
                Map.Entry var5 = (Map.Entry) var3.next();
                var4 = var4 + var5.getKey() + "=" + var5.getValue() + ";";
            }
        }
        return var2;
    }

    private String paraSignature(String fullPath, String paramsKey, String timestamp) {
        return getMd5(fullPath + "^" + paramsKey + "^" + timestamp + "^" + "1f11aa8a01552f57493cea0c1f89073b");
    }

    @SuppressWarnings("ConditionalBreakInInfiniteLoop")
    private String getMd5(String var0) {
        NoSuchAlgorithmException var10000;
        label48:
        {
            StringBuffer var10;
            byte[] var12;
            try {
                MessageDigest var1 = MessageDigest.getInstance("MD5");
                var1.update(var0.getBytes());
                var12 = var1.digest();
                var10 = new StringBuffer();
            } catch (NoSuchAlgorithmException var9) {
                var10000 = var9;
                break label48;
            }
            int var2 = 0;
            while (true) {
                if (var2 >= var12.length) {
                    break;
                }
                byte var3 = var12[var2];
                int var4 = var3;
                if (var3 < 0) {
                    var4 = var3 + 256;
                }
                if (var4 < 16) {
                    var10.append("0");
                }
                var10.append(Integer.toHexString(var4));
                ++var2;
            }
            var0 = var10.toString();
            return var0;
        }
        NoSuchAlgorithmException var11 = var10000;
        var11.printStackTrace();
        return null;
    }
}
