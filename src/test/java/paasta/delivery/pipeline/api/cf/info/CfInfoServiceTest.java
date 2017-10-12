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
 * @since 9/6/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CfInfoServiceTest {

    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private CfInfoService cfInfoService;


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////// MethodName_StateUnderTest_ExpectedBehavior
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    public void getCfInfo_ValidModel_ReturnModel() throws Exception {
        CustomJob testJobModel = new CustomJob();
        testJobModel.setCfInfoId(1L);

        CfInfo testResultModel = new CfInfo();
        testResultModel.setId(1L);
        testResultModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        // GET CF INFO DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/cf-info/" + testJobModel.getCfInfoId(), HttpMethod.GET, null, CfInfo.class)).thenReturn(testResultModel);

        // TEST
        CfInfo resultModel = cfInfoService.getCfInfo(testJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }

}