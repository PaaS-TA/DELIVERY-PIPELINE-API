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
        CfInfo gTestCfInfo = new CfInfo();
        gTestResultCfInfo = new CfInfo();
        gTestJobModel = new CustomJob();

        gTestJobModel.setCfInfoId(CF_INFO_ID);

        gTestCfInfo.setId(CF_INFO_ID);
        gTestCfInfo.setServiceInstancesId("");
        gTestCfInfo.setCfName("");
        gTestCfInfo.setDescription("");
        gTestCfInfo.setUserId("");
        gTestCfInfo.setResultStatus(Constants.RESULT_STATUS_SUCCESS);
        gTestCfInfo.setResultMessage("");
        gTestCfInfo.setCreated("");
        gTestCfInfo.setLastModified("");
        gTestCfInfo.setCreatedString("");
        gTestCfInfo.setLastModifiedString("");

        gTestResultCfInfo.setId(gTestCfInfo.getId());
        gTestResultCfInfo.setServiceInstancesId(gTestCfInfo.getServiceInstancesId());
        gTestResultCfInfo.setCfName(gTestCfInfo.getCfName());
        gTestResultCfInfo.setDescription(gTestCfInfo.getDescription());
        gTestResultCfInfo.setUserId(gTestCfInfo.getUserId());
        gTestResultCfInfo.setResultStatus(gTestCfInfo.getResultStatus());
        gTestResultCfInfo.setResultMessage(gTestCfInfo.getResultMessage());
        gTestResultCfInfo.setCreated(gTestCfInfo.getCreated());
        gTestResultCfInfo.setLastModified(gTestCfInfo.getLastModified());
        gTestResultCfInfo.setCreatedString(gTestCfInfo.getCreatedString());
        gTestResultCfInfo.setLastModifiedString(gTestCfInfo.getLastModifiedString());
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
    }

}