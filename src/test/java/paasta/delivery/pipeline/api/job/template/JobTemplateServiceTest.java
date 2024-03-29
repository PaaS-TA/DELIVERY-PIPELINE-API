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
import paasta.delivery.pipeline.api.cf.info.CfInfo;
import paasta.delivery.pipeline.api.cf.info.CfInfoService;
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
    private static final String REPOSITORY_ACCOUNT_PASSWORD = "test-repository-account-password";
    private static final String REPOSITORY_BRUNCH = "test-repository-account-brunch";
    private static final String INSPECTION_PROJECT_NAME = "test-inspection-project-name";
    private static final String INSPECTION_PROJECT_KEY = "test-inspection-project-key";
    private static final String CF_ID = "test-cf-id";
    private static final String CF_PASSWORD = "test-cf-password";
    private static final String CF_API_URL = "test-cf-api-url";
    private static final String MANIFEST_SCRIPT = "test-manifest-script\\n";
    private static final String LANGUAGE_TYPE_VERSION = "test-languageTypeVersion";
    private static final String BUILDER_TYPE_VERSION = "test-builderTypeVersion";

    private static CustomJob gTestJobModel = null;
    private static CfInfo gTestCfInfoModel = null;


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
        gTestJobModel = new CustomJob();
        gTestCfInfoModel = new CfInfo();

        gTestJobModel.setRepositoryUrl(REPOSITORY_URL);
        gTestJobModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID);
        gTestJobModel.setRepositoryAccountPassword(REPOSITORY_ACCOUNT_PASSWORD);
        gTestJobModel.setRepositoryBranch(REPOSITORY_BRUNCH);
        gTestJobModel.setInspectionProjectName(INSPECTION_PROJECT_NAME);
        gTestJobModel.setInspectionProjectKey(INSPECTION_PROJECT_KEY);
        gTestJobModel.setManifestUseYn(Constants.USE_YN_N);
        gTestJobModel.setCfInfoId(1L);
        gTestJobModel.setLanguageTypeVersion(LANGUAGE_TYPE_VERSION);
        gTestJobModel.setBuilderTypeVersion(BUILDER_TYPE_VERSION);

        gTestCfInfoModel.setCfId(CF_ID);
        gTestCfInfoModel.setCfPassword(CF_PASSWORD);
        gTestCfInfoModel.setCfApiUrl(CF_API_URL);
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
        gTestJobModel.setBuilderType(String.valueOf(JobConfig.BuilderType.GRADLE));
        gTestJobModel.setRepositoryType(String.valueOf(JobConfig.RepositoryType.SCM_GIT));

        // TEST
        String resultString = jobTemplateService.getBuildJobTemplate(gTestJobModel);

        assertThat(resultString).isNotNull();
    }


    /**
     * Gets build template valid param java github return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getBuildTemplate_ValidParamJava_GITHUB_ReturnString() throws Exception {
        gTestJobModel.setBuilderType(String.valueOf(JobConfig.BuilderType.GRADLE));
        gTestJobModel.setRepositoryType(String.valueOf(JobConfig.RepositoryType.GIT_HUB));

        // TEST
        String resultString = jobTemplateService.getBuildJobTemplate(gTestJobModel);

        assertThat(resultString).isNotNull();
    }


    /**
     * Gets build template valid param java maven scm svn return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getBuildTemplate_ValidParamJava_MAVEN_SCM_SVN_ReturnString() throws Exception {
        gTestJobModel.setBuilderType(String.valueOf(JobConfig.BuilderType.MAVEN));
        gTestJobModel.setRepositoryType(String.valueOf(JobConfig.RepositoryType.SCM_SVN));


        // TEST
        String resultString = jobTemplateService.getBuildJobTemplate(gTestJobModel);

        assertThat(resultString).isNotNull();
    }


    /**
     * Gets test template valid param java return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getTestTemplate_ValidParamJava_ReturnString() throws Exception {
        gTestJobModel.setJobType(String.valueOf(JobConfig.JobType.TEST));


        // TEST
        String resultString = jobTemplateService.getTestJobTemplate(gTestJobModel);

        assertThat(resultString).isNotNull();
    }


    /**
     * Gets deploy template valid param java return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getDeployTemplate_ValidParamJava_ReturnString() throws Exception {
        // GET CF INFO DETAIL
        when(cfInfoService.getCfInfo(gTestJobModel)).thenReturn(gTestCfInfoModel);


        // TEST
        String resultString = jobTemplateService.getDeployJobTemplate(gTestJobModel);

        assertThat(resultString).isNotNull();
    }


    /**
     * Gets deploy template valid param java set manifest use yn y return string.
     *
     * @throws Exception the exception
     */
    @Test
    public void getDeployTemplate_ValidParamJava_setManifestUseYn_Y_ReturnString() throws Exception {
        gTestJobModel.setManifestUseYn(Constants.USE_YN_Y);
        gTestJobModel.setManifestScript(MANIFEST_SCRIPT);


        // GET CF INFO DETAIL
        when(cfInfoService.getCfInfo(gTestJobModel)).thenReturn(gTestCfInfoModel);


        // TEST
        String resultString = jobTemplateService.getDeployJobTemplate(gTestJobModel);

        assertThat(resultString).isNotNull();
    }

}
