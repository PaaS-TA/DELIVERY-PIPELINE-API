package paasta.delivery.pipeline.api.credential;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.common.PropertyService;
import paasta.delivery.pipeline.api.common.RestTemplateService;
import paasta.delivery.pipeline.api.common.ServiceInstances;
import paasta.delivery.pipeline.api.job.CustomJob;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.credential
 *
 * @author REX
 * @version 1.0
 * @since 5 /31/2017
 */
@Service
public class CredentialsService {

    private static final String REQ_URL = "/serviceInstance/";
    private final String ciServerApiCredentialsUrl;
    private final String credentialsScope;
    private final String requestClassName;

    private final RestTemplateService restTemplateService;


    /**
     * Instantiates a new Credentials service.
     *
     * @param propertyService     the property service
     * @param restTemplateService the rest template service
     */
    @Autowired
    public CredentialsService(PropertyService propertyService, RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;

        ciServerApiCredentialsUrl = propertyService.getCiServerCredentialsUrl();
        credentialsScope = propertyService.getCiServerCredentialsScope();
        requestClassName = propertyService.getCiServerCredentialsClassName();
    }


    /**
     * Create credentials custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     */
    public CustomJob createCredentials(CustomJob customJob) {
        CustomJob resultModel = new CustomJob();
        resultModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        String ciServerUrl = restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + customJob.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class).getCiServerUrl();

        String reqUrl = ciServerUrl + ciServerApiCredentialsUrl + "/createCredentials";
        String repositoryAccountId = customJob.getRepositoryAccountId();

        Map<String, String> credentialsMap = new LinkedHashMap<>();
        credentialsMap.put("scope", credentialsScope);
        credentialsMap.put("id", repositoryAccountId);
        credentialsMap.put("username", repositoryAccountId);
        credentialsMap.put("password", customJob.getRepositoryAccountPassword());
        credentialsMap.put("description", "");
        credentialsMap.put("$class", requestClassName);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("credentials", credentialsMap);

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("json", jsonObject.toString());

        // SEND FORM
        restTemplateService.sendForm(reqUrl, HttpMethod.POST, multiValueMap, null);

        resultModel.setRepositoryAccountId(repositoryAccountId);
        return resultModel;
    }


    /**
     * Update credentials custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     */
    public CustomJob updateCredentials(CustomJob customJob) {
        CustomJob resultModel = new CustomJob();
        resultModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        String ciServerUrl = restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + customJob.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class).getCiServerUrl();

        String repositoryAccountId = customJob.getRepositoryAccountId();
        String reqUrl = ciServerUrl + ciServerApiCredentialsUrl + "/credential/" + repositoryAccountId + "/updateSubmit";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("scope", credentialsScope);
        jsonObject.put("id", repositoryAccountId);
        jsonObject.put("username", repositoryAccountId);
        jsonObject.put("password", customJob.getRepositoryAccountPassword());
        jsonObject.put("description", "");
        jsonObject.put("stapler-class", requestClassName);

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("json", jsonObject.toString());

        // SEND FORM
        restTemplateService.sendForm(reqUrl, HttpMethod.POST, multiValueMap, null);

        resultModel.setRepositoryAccountId(repositoryAccountId);
        return resultModel;
    }

}
