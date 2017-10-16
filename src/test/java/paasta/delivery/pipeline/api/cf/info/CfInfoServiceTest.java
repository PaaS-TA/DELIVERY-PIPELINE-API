package paasta.delivery.pipeline.api.cf.info;

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
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.common.RestTemplateService;
import paasta.delivery.pipeline.api.job.CustomJob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.cf.info
 *
 * @author REX
 * @version 1.0
 * @since 9 /6/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CfInfoServiceTest {

    private static final long CF_INFO_ID = 1L;
    private static final String SERVICE_INSTANCES_ID = "test-service-instances-id";
    private static final String CF_NAME = "test-cf-name";
    private static final String CF_ID = "test-cf-id";
    private static final String CF_PASSWORD = "test-cf-password";
    private static final String CF_API_URL = "test-cf-api-url";
    private static final String TEST_DESCRIPTION = "test-description";
    private static final String USER_ID = "test-user-id";
    private static final String RESULT_MESSAGE = "test-result-message";
    private static final String TEST_CREATED = "test-created";
    private static final String TEST_LAST_MODIFIED = "test-last-modified";
    private static final String TEST_CREATED_STRING = "test-created-string";
    private static final String TEST_LAST_MODIFIED_STRING = "test-last-modified-string";

    private static CfInfo gTestResultCfInfo = null;
    private static CustomJob gTestJobModel = null;


    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private CfInfoService cfInfoService;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        gTestResultCfInfo = new CfInfo();
        gTestJobModel = new CustomJob();

        gTestJobModel.setCfInfoId(CF_INFO_ID);

        gTestResultCfInfo.setId(CF_INFO_ID);
        gTestResultCfInfo.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestResultCfInfo.setCfName(CF_NAME);
        gTestResultCfInfo.setCfId(CF_ID);
        gTestResultCfInfo.setCfPassword(CF_PASSWORD);
        gTestResultCfInfo.setCfApiUrl(CF_API_URL);
        gTestResultCfInfo.setDescription(TEST_DESCRIPTION);
        gTestResultCfInfo.setUserId(USER_ID);
        gTestResultCfInfo.setResultStatus(Constants.RESULT_STATUS_SUCCESS);
        gTestResultCfInfo.setResultMessage(RESULT_MESSAGE);
        gTestResultCfInfo.setCreated(TEST_CREATED);
        gTestResultCfInfo.setLastModified(TEST_LAST_MODIFIED);
        gTestResultCfInfo.setCreatedString(TEST_CREATED_STRING);
        gTestResultCfInfo.setLastModifiedString(TEST_LAST_MODIFIED_STRING);
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
     * Gets cf info valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getCfInfo_ValidModel_ReturnModel() throws Exception {
        // GET CF INFO DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/cf-info/" + gTestJobModel.getCfInfoId(), HttpMethod.GET, null, CfInfo.class)).thenReturn(gTestResultCfInfo);

        // TEST
        CfInfo resultModel = cfInfoService.getCfInfo(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(CF_INFO_ID, resultModel.getId());
        assertEquals(SERVICE_INSTANCES_ID, resultModel.getServiceInstancesId());
        assertEquals(CF_NAME, resultModel.getCfName());
        assertEquals(CF_ID, resultModel.getCfId());
        assertEquals(CF_PASSWORD, resultModel.getCfPassword());
        assertEquals(CF_API_URL, resultModel.getCfApiUrl());
        assertEquals(TEST_DESCRIPTION, resultModel.getDescription());
        assertEquals(USER_ID, resultModel.getUserId());
        assertEquals(RESULT_MESSAGE, resultModel.getResultMessage());
        assertEquals(TEST_CREATED, resultModel.getCreated());
        assertEquals(TEST_LAST_MODIFIED, resultModel.getLastModified());
        assertEquals(TEST_CREATED_STRING, resultModel.getCreatedString());
        assertEquals(TEST_LAST_MODIFIED_STRING, resultModel.getLastModifiedString());
    }

}