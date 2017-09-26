package paasta.delivery.pipeline.api.job.template;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import paasta.delivery.pipeline.api.cf.info.CfInfoService;
import paasta.delivery.pipeline.api.common.CfInfo;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.job.CustomJob;
import paasta.delivery.pipeline.api.job.config.JobConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.jobTemplate
 *
 * @author REX
 * @version 1.0
 * @since 6 /27/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobTemplateServiceTest {

    private static final String REPOSITORY_URL = "test-repository-url";
    private static final String REPOSITORY_ACCOUNT_ID = "test-repository-account-id";
    private static final String REPOSITORY_BRUNCH = "test-repository-account-brunch";
    private static final String INSPECTION_PROJECT_NAME = "test-inspection-project-name";
    private static final String INSPECTION_PROJECT_KEY = "test-inspection-project-key";
    private static final String CF_ID = "test-cf-id";
    private static final String CF_PASSWORD = "test-cf-password";
    private static final String CF_API_URL = "test-cf-api-url";


    @Mock
    private CfInfoService cfInfoService;

    @InjectMocks
    private JobTemplateService jobTemplateService;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
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
     * Gets build template valid param java return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getBuildTemplate_ValidParamJava_ReturnString() throws Exception {
        CustomJob testModel = new CustomJob();
        testModel.setBuilderType(String.valueOf(JobConfig.BuilderType.GRADLE));
        testModel.setRepositoryUrl(REPOSITORY_URL);
        testModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID);
        testModel.setRepositoryBranch(REPOSITORY_BRUNCH);


        // TEST
        String resultString = jobTemplateService.getBuildJobTemplate(testModel);

        assertThat(resultString).isNotNull();
    }

    /**
     * Gets build template valid param go return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getBuildTemplate_ValidParamGo_ReturnString() throws Exception {
        CustomJob testModel = new CustomJob();
        testModel.setRepositoryUrl(REPOSITORY_URL);
        testModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID);
        testModel.setRepositoryBranch(REPOSITORY_BRUNCH);


        // TEST
        String resultString = jobTemplateService.getBuildJobTemplate(testModel);

        assertThat(resultString).isNotNull();
    }


    @Test
    public void getTestTemplate_ValidParamJava_ReturnString() throws Exception {
        CustomJob testModel = new CustomJob();
        testModel.setBuilderType(String.valueOf(JobConfig.BuilderType.GRADLE));
        testModel.setRepositoryUrl(REPOSITORY_URL);
        testModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID);
        testModel.setRepositoryBranch(REPOSITORY_BRUNCH);
        testModel.setInspectionProjectName(INSPECTION_PROJECT_NAME);
        testModel.setInspectionProjectKey(INSPECTION_PROJECT_KEY);


        // TEST
        String resultString = jobTemplateService.getTestJobTemplate(testModel);

        assertThat(resultString).isNotNull();
    }


    /**
     * Gets deploy template valid param java return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getDeployTemplate_ValidParamJava_ReturnString() throws Exception {
        CustomJob testModel = new CustomJob();
        CfInfo cfInfo = new CfInfo();

        long cfInfoId = 1L;

        testModel.setManifestUseYn(Constants.USE_YN_N);
        testModel.setCfInfoId(cfInfoId);

        cfInfo.setCfId(CF_ID);
        cfInfo.setCfPassword(CF_PASSWORD);
        cfInfo.setCfApiUrl(CF_API_URL);


        // GET CF INFO DETAIL
        when(cfInfoService.getCfInfo(testModel)).thenReturn(cfInfo);


        // TEST
        String resultString = jobTemplateService.getDeployJobTemplate(testModel);

        assertThat(resultString).isNotNull();
    }

}
