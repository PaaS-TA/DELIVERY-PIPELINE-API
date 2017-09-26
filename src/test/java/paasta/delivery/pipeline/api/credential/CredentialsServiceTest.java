package paasta.delivery.pipeline.api.credential;

import net.sf.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import paasta.delivery.pipeline.api.common.ServiceInstances;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.common.PropertyService;
import paasta.delivery.pipeline.api.common.RestTemplateService;
import paasta.delivery.pipeline.api.job.CustomJob;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.credential
 *
 * @author REX
 * @version 1.0
 * @since 6 /2/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CredentialsServiceTest {

    private static final String REQ_URL = "/serviceInstance/";
    private static final String DELIVERY_API_CREDENTIALS_URL = "/credentials/store/system/domain/_";
    private static final String CREDENTIALS_SCOPE = "GLOBAL";
    private static final String REQUEST_CLASS_NAME = "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl";
    private static final String SERVICE_INSTANCES_ID = "test-service-instances-id";
    private static final String CI_SERVER_URL = "test-ci-server-url";
    private static final String REPOSITORY_ACCOUNT_ID = "test-repository-account-id";

    private static CustomJob gTestJobModel = null;
    private static ServiceInstances gTestServiceInstancesModel = null;


    @Mock
    private PropertyService propertyService;

    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private CredentialsService credentialsService;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        gTestJobModel = new CustomJob();
        gTestServiceInstancesModel = new ServiceInstances();

        gTestJobModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestJobModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID);

        gTestServiceInstancesModel.setCiServerUrl(CI_SERVER_URL);

        when(propertyService.getCiServerCredentialsUrl()).thenReturn("test-credential-url");
        when(propertyService.getCiServerCredentialsScope()).thenReturn("test-credential-scope");
        when(propertyService.getCiServerCredentialsClassName()).thenReturn("test-credential-class-name");
    }


    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////// MethodName_StateUnderTest_ExpectedBehavior
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Create credentials valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createCredentials_ValidModel_ReturnModel() throws Exception {
        String reqUrl = DELIVERY_API_CREDENTIALS_URL + "/createCredentials";
        Map<String, String> credentialsMap = new LinkedHashMap<>();
        credentialsMap.put("scope", CREDENTIALS_SCOPE);
        credentialsMap.put("id", REPOSITORY_ACCOUNT_ID);
        credentialsMap.put("username", REPOSITORY_ACCOUNT_ID);
        credentialsMap.put("description", "");
        credentialsMap.put("$class", REQUEST_CLASS_NAME);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("credentials", credentialsMap);

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("json", jsonObject.toString());


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // SEND FORM
        when(restTemplateService.sendForm(reqUrl, HttpMethod.POST, multiValueMap, null)).thenReturn(null);


        // TEST
        CustomJob resultModel = credentialsService.createCredentials(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(REPOSITORY_ACCOUNT_ID, resultModel.getRepositoryAccountId());
    }


    /**
     * Update credentials valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateCredentials_ValidModel_ReturnModel() throws Exception {
        String reqUrl = DELIVERY_API_CREDENTIALS_URL + "/credential/" + REPOSITORY_ACCOUNT_ID + "/updateSubmit";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("scope", CREDENTIALS_SCOPE);
        jsonObject.put("id", REPOSITORY_ACCOUNT_ID);
        jsonObject.put("username", REPOSITORY_ACCOUNT_ID);
        jsonObject.put("description", "");
        jsonObject.put("stapler-class", REQUEST_CLASS_NAME);

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("json", jsonObject.toString());


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // SEND FORM
        when(restTemplateService.sendForm(reqUrl, HttpMethod.POST, multiValueMap, null)).thenReturn(null);


        // TEST
        CustomJob resultModel = credentialsService.updateCredentials(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(REPOSITORY_ACCOUNT_ID, resultModel.getRepositoryAccountId());
    }

}
