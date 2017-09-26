package paasta.delivery.pipeline.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import paasta.delivery.pipeline.api.job.CustomJob;

import java.nio.charset.StandardCharsets;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.common
 *
 * @author REX
 * @version 1.0
 * @since 6 /22/2017
 */
@Service
public class RestTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateService.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private final String ciServerAdminUserName;
    private final String ciServerAdminPassword;
    private final String commonApiUrl;
    private final String commonApiAuthorizationId;
    private final String commonApiAuthorizationPassword;
    private final String binaryStorageApiUrl;
    private final String binaryStorageApiAuthorizationId;
    private final String binaryStorageApiAuthorizationPassword;
    private final RestTemplate restTemplate;
    private String base64Authorization;
    private String baseUrl;


    /**
     * Instantiates a new Rest template service.
     *
     * @param restTemplate    the rest template
     * @param propertyService the property service
     */
    @Autowired
    public RestTemplateService(RestTemplate restTemplate, PropertyService propertyService) {
        this.restTemplate = restTemplate;

        ciServerAdminUserName = propertyService.getCiServerAdminUserName();
        ciServerAdminPassword = propertyService.getCiServerAdminPassword();
        commonApiUrl = propertyService.getCommonApiUrl();
        commonApiAuthorizationId = propertyService.getCommonApiAuthorizationId();
        commonApiAuthorizationPassword = propertyService.getCommonApiAuthorizationPassword();
        binaryStorageApiUrl = propertyService.getBinaryStorageApiUrl();
        binaryStorageApiAuthorizationId = propertyService.getBinaryStorageApiAuthorizationId();
        binaryStorageApiAuthorizationPassword = propertyService.getBinaryStorageApiAuthorizationPassword();
    }


    /**
     * Send t.
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T send(String reqApi, String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        procSetApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);

        LOGGER.info("<T> T send :: Request : {} {baseUrl} : {}, Content-Type: {}", httpMethod, reqUrl, reqHeaders.get(CONTENT_TYPE));
        ResponseEntity<T> resEntity = restTemplate.exchange(baseUrl + reqUrl, httpMethod, reqEntity, responseType);
        if (reqEntity.getBody() != null) {
            LOGGER.info("Response Type: {}", resEntity.getBody().getClass());
        }

        return resEntity.getBody();
    }


    /**
     * Custom send t.
     *
     * @param <T>          the type parameter
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @param customJob    the custom job
     * @return the t
     */
    public <T> T customSend(String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType, CustomJob customJob) {

        String authorization = customJob.getRepositoryAccountId() + ":" + customJob.getRepositoryAccountPassword();

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, "Basic " + Base64Utils.encodeToString(authorization.getBytes(StandardCharsets.UTF_8)));
        reqHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);

        LOGGER.info("<T> T send :: Request : {} {baseUrl} : {}, Content-Type: {}", httpMethod, reqUrl, reqHeaders.get(CONTENT_TYPE));
        ResponseEntity<T> resEntity = restTemplate.exchange(reqUrl, httpMethod, reqEntity, responseType);
        if (resEntity.getBody() != null) {
            LOGGER.info("Response Type: {}", resEntity.getBody().getClass());
        } else {
            LOGGER.info("Response Type: {}", "response body is null");
        }

        return resEntity.getBody();
    }


    /**
     * Send form t.
     *
     * @param <T>          the type parameter
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param bodyObject   the body object
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendForm(String reqUrl, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        String authorization = ciServerAdminUserName + ":" + ciServerAdminPassword;

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, "Basic " + Base64Utils.encodeToString(authorization.getBytes(StandardCharsets.UTF_8)));
        reqHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);

        LOGGER.info("<T> T send :: Request : {} {baseUrl} : {}, Content-Type: {}", httpMethod, reqUrl, reqHeaders.get(CONTENT_TYPE));
        ResponseEntity<T> resEntity = restTemplate.exchange(reqUrl, httpMethod, reqEntity, responseType);
        LOGGER.info("Response Status Code: {}", resEntity.getStatusCode());

        return resEntity.getBody();
    }


    /**
     * Send multipart t.
     *
     * @param <T>          the type parameter
     * @param reqApi       the req api
     * @param reqUrl       the req url
     * @param resource     the resource
     * @param responseType the response type
     * @return the t
     */
    public <T> T sendMultipart(String reqApi, String reqUrl, ByteArrayResource resource, Class<T> responseType) {
        procSetApiUrlAuthorization(reqApi);

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        reqHeaders.add(CONTENT_TYPE, "multipart/form-data");

        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(data, reqHeaders);

        LOGGER.info("POST >> Request: {} {baseUrl} : {}, Content-Type: {}", HttpMethod.POST, reqUrl, reqHeaders.get(CONTENT_TYPE));
        ResponseEntity<T> resEntity = restTemplate.exchange(baseUrl + reqUrl, HttpMethod.POST, requestEntity, responseType);
        LOGGER.info("Map sendMultipart :: Response Type: {}", reqUrl, resEntity.getBody().getClass());

        return resEntity.getBody();
    }


    private void procSetApiUrlAuthorization(String reqApi) {
        String apiUrl = "";
        String authorization = "";

        // COMMON API
        if (Constants.TARGET_COMMON_API.equals(reqApi)) {
            apiUrl = commonApiUrl;
            authorization = commonApiAuthorizationId + ":" + commonApiAuthorizationPassword;
        }

        //  BINARY STORAGE API
        if (Constants.TARGET_BINARY_STORAGE_API.equals(reqApi)) {
            apiUrl = binaryStorageApiUrl;
            authorization = binaryStorageApiAuthorizationId + ":" + binaryStorageApiAuthorizationPassword;
        }

        this.base64Authorization = "Basic " + Base64Utils.encodeToString(authorization.getBytes(StandardCharsets.UTF_8));
        this.baseUrl = apiUrl;
    }

}
