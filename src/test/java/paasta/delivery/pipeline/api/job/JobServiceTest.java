package paasta.delivery.pipeline.api.job;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.JenkinsTriggerHelper;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.*;
import com.offbytwo.jenkins.model.Queue;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import paasta.delivery.pipeline.api.cf.info.CfInfoService;
import paasta.delivery.pipeline.api.common.*;
import paasta.delivery.pipeline.api.credential.CredentialsService;
import paasta.delivery.pipeline.api.job.config.JobConfig;
import paasta.delivery.pipeline.api.job.template.JobTemplateService;
import paasta.delivery.pipeline.api.repository.RepositoryService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.doNothing;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.job
 *
 * @author REX
 * @version 1.0
 * @since 5 /8/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobServiceTest {

    private static final String REQ_URL = "/jobs";
    private static final String REQ_PIPELINES_URL = "/pipelines/";
    private static final String REQ_HISTORY_URL = "/histories";
    private static final String REQ_FILE_URL = "/file";
    private static final String REQ_JOB_HISTORY_URL = "/job-histories";
    private static final String REQ_SERVICE_INSTANCES_URL = "/serviceInstance/";
    private static final String SERVICE_INSTANCES_ID = "test-service-instances-id";
    private static final long PIPELINE_ID = 1L;
    private static final long JOB_ID = 2L;
    private static final int JOB_ID_IN_MAP = 3;
    private static final long FILE_ID = 4L;
    private static final long JOB_HISTORY_ID = 5L;
    private static final int JOB_NUMBER = 1;
    private static final String USER_ID = "test-user-id";
    private static final String JOB_GUID = "test-job-" + UUID.randomUUID().toString();
    private static final String JOB_NAME = "test-job-name";
    private static final String JOB_XML = "test-job-xml";
    private static final String REPOSITORY_ID = "test-repository-id";
    private static final String REPOSITORY_ACCOUNT_ID = "test-repository-account-id";
    private static final String REPOSITORY_ACCOUNT_PASSWORD = "test-repository-account-password";
    private static final String REPOSITORY_COMMIT_REVISION = "test-repository-commit-revision";
    private static final String REPOSITORY_COMMIT_REVISON = "test-repository-commit-revision";
    private static final String CHECK_EXISTED_JOB_NAME_URL = REQ_PIPELINES_URL + PIPELINE_ID + "/job-names/";
    private static final String CI_SERVER_URL = "test-ci-server-url";
    private static final String MANIFEST_SCRIPT = "test-manifest-script\\n";
    private static final String CF_API_URL = "http://api.123.456";

    private static CustomJob gTestJobModel = null;
    private static CustomJob gTestJobDetailModel = null;
    private static CustomJob gTestResultJobModel = null;
    private static JobHistory gTestJobHistoryModel = null;
    private static JobHistory gTestResultJobHistoryModel = null;
    private static ServiceInstances gTestServiceInstancesModel = null;
    private static List<Map<String, Object>> gTestResultList = null;


    @Mock
    private CommonService commonService;

    @Mock
    private JobTemplateService jobTemplateService;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private RestTemplateService restTemplateService;

    @Mock
    private JobBuiltFileService jobBuiltFileService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private CfInfoService cfInfoService;

    @MockBean
    private JenkinsServer ciServer;

    @MockBean
    private JenkinsTriggerHelper ciTriggerHelper;

    @MockBean
    private JobWithDetails jobWithDetails;

    @MockBean
    private Build build;

    @MockBean
    private BuildWithDetails buildWithDetails;

    @MockBean
    private Queue ciQueue;

    @MockBean
    private QueueItem queueItem;

    @MockBean
    private JenkinsHttpClient ciHttpClient;

    @InjectMocks
    private JobService jobService;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        Map<String, Object> gTestResultMap = new HashMap<>();

        gTestJobModel = new CustomJob();
        gTestJobDetailModel = new CustomJob();
        gTestResultJobModel = new CustomJob();
        gTestJobHistoryModel = new JobHistory();
        gTestResultJobHistoryModel = new JobHistory();
        gTestServiceInstancesModel = new ServiceInstances();
        gTestResultList = new ArrayList<>();

        gTestJobModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestJobModel.setPipelineId(PIPELINE_ID);

        gTestJobDetailModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestJobDetailModel.setPipelineId(PIPELINE_ID);
        gTestJobDetailModel.setId(JOB_ID);
        gTestJobDetailModel.setGroupOrder(1);
        gTestJobDetailModel.setJobOrder(1);
        gTestJobDetailModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID);
        gTestJobDetailModel.setRepositoryAccountPassword(REPOSITORY_ACCOUNT_PASSWORD);
        gTestJobDetailModel.setRepositoryCommitRevision(REPOSITORY_COMMIT_REVISION);
        gTestJobDetailModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        gTestResultJobModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestResultJobModel.setPipelineId(PIPELINE_ID);
        gTestResultJobModel.setId(JOB_ID);
        gTestResultJobModel.setJobNumber(1);
        gTestResultJobModel.setGroupOrder(1);
        gTestResultJobModel.setJobOrder(1);
        gTestResultJobModel.setRepositoryId(REPOSITORY_ID);
        gTestResultJobModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID);
        gTestResultJobModel.setRepositoryAccountPassword(REPOSITORY_ACCOUNT_PASSWORD);
        gTestResultJobModel.setRepositoryCommitRevision(REPOSITORY_COMMIT_REVISON);
        gTestResultJobModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        gTestResultJobModel.setJobTrigger(String.valueOf(JobService.JobTriggerType.PREVIOUS_JOB_SUCCESS));
        gTestResultJobModel.setDeployType(String.valueOf(JobConfig.DeployType.DEV));
        gTestResultJobModel.setBlueGreenDeployStatus(String.valueOf(JobConfig.BlueGreenDeployStatus.GREEN_DEPLOY));
        gTestResultJobModel.setManifestScript("");
        gTestResultJobModel.setInspectionProjectId("");
        gTestResultJobModel.setInspectionProfileId("");
        gTestResultJobModel.setInspectionGateId("");
        gTestResultJobModel.setCreated("");
        gTestResultJobModel.setLastModified("");
        gTestResultJobModel.setBuildJobName("");
        gTestResultJobModel.setLastJobModified("");
        gTestResultJobModel.setLastSuccessJobNumber(1);
        gTestResultJobModel.setLastGroupOrder(1);
        gTestResultJobModel.setLastJobNumber(1);
        gTestResultJobModel.setBuildFilePath("");
        gTestResultJobModel.setTriggerType(String.valueOf(JobService.JobTriggerType.PREVIOUS_JOB_SUCCESS));
        gTestResultJobModel.setPreviousJobNumber(1);
        gTestResultJobModel.setSchedulerModifiedPushYn(Constants.USE_YN_Y);
        gTestResultJobModel.setResultMessage("");

        gTestResultJobHistoryModel.setId(JOB_HISTORY_ID);
        gTestResultJobHistoryModel.setUserId(USER_ID);
        gTestResultJobHistoryModel.setJobId(JOB_ID);
        gTestResultJobHistoryModel.setJobNumber(0);
        gTestResultJobHistoryModel.setPreviousJobNumber(0);
        gTestResultJobHistoryModel.setStatus(Constants.RESULT_STATUS_SUCCESS);
        gTestResultJobHistoryModel.setDuration(1L);
        gTestResultJobHistoryModel.setFileId(FILE_ID);
        gTestResultJobHistoryModel.setTriggerType(String.valueOf(JobService.JobTriggerType.MANUAL_TRIGGER));
        gTestResultJobHistoryModel.setCreated(null);
        gTestResultJobHistoryModel.setLastModified(null);

        gTestServiceInstancesModel.setCiServerUrl(CI_SERVER_URL);

        gTestResultMap.put("id", JOB_ID_IN_MAP);
        gTestResultMap.put("serviceInstancesId", SERVICE_INSTANCES_ID);
        gTestResultMap.put("pipelineId", 1);
        gTestResultMap.put("groupOrder", 1);
        gTestResultMap.put("jobOrder", 1);
        gTestResultMap.put("jobTrigger", String.valueOf(JobService.JobTriggerType.PREVIOUS_JOB_SUCCESS));
        gTestResultMap.put("deployType", String.valueOf(JobConfig.DeployType.PRD));
        gTestResultMap.put("previousJobNumberCount", "1");

        gTestResultList.add(gTestResultMap);
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
     * Create job build valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build new work group yn y valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_newWorkGroupYn_Y_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);
        gTestJobModel.setNewWorkGroupYn(Constants.USE_YN_Y);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET JOB MAX GROUP ORDER FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_PIPELINES_URL + PIPELINE_ID + "/max-job-group-order", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME PHASE 1 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // SET JOB NAME PHASE 2 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-1", HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build set job name phase 1 valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_setJobName_phase_1_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME + "-1");
        gTestJobModel.setNewWorkGroupYn(Constants.USE_YN_Y);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET JOB MAX GROUP ORDER FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_PIPELINES_URL + PIPELINE_ID + "/max-job-group-order", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME PHASE 1 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-1", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // SET JOB NAME PHASE 2 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-2", HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build set job name phase 2 valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_setJobName_phase_2_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME + "-11");
        gTestJobModel.setNewWorkGroupYn(Constants.USE_YN_Y);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET JOB MAX GROUP ORDER FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_PIPELINES_URL + PIPELINE_ID + "/max-job-group-order", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME PHASE 1 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-11", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // SET JOB NAME PHASE 2 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-12", HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build set job name phase 3 valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_setJobName_phase_3_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME + "-111");
        gTestJobModel.setNewWorkGroupYn(Constants.USE_YN_Y);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET JOB MAX GROUP ORDER FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_PIPELINES_URL + PIPELINE_ID + "/max-job-group-order", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME PHASE 1 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-111", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // SET JOB NAME PHASE 2 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-112", HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build set job order phase 1 valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_setJobOrder_phase_1_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setGroupOrder(2);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build set job order phase 2 valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_setJobOrder_phase_2_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setJobOrder(2);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build set job order phase 3 valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_setJobOrder_phase_3_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setGroupOrder(2);
        gTestJobDetailModel.setJobOrder(2);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job build set job order phase 4 valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_BUILD_setJobOrder_phase_4_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setId(3L);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Update job build valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateJob_BUILD_ValidModel_ReturnModel() throws Exception {
        gTestJobModel.setId(JOB_ID);
        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setJobName(JOB_NAME);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + gTestJobModel.getId(), HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // UPDATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // UPDATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).updateJob(JOB_GUID, JOB_XML, true);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.updateJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Update job build check repository account id valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateJob_BUILD_checkRepositoryAccountId_ValidModel_ReturnModel() throws Exception {
        gTestJobModel.setId(JOB_ID);
        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setJobName(JOB_NAME);
        gTestJobDetailModel.setRepositoryAccountId(REPOSITORY_ACCOUNT_ID + "-1");

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + gTestJobModel.getId(), HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // UPDATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // UPDATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).updateJob(JOB_GUID, JOB_XML, true);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.updateJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Update job build check repository account id repository account password valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateJob_BUILD_checkRepositoryAccountId_RepositoryAccountPassword_ValidModel_ReturnModel() throws Exception {
        gTestJobModel.setId(JOB_ID);
        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setJobName(JOB_NAME);
        gTestJobDetailModel.setRepositoryAccountPassword(REPOSITORY_ACCOUNT_PASSWORD + "-1");

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + gTestJobModel.getId(), HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // UPDATE CREDENTIALS TO CI SERVER
        when(credentialsService.updateCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // UPDATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // UPDATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).updateJob(JOB_GUID, JOB_XML, true);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.updateJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Update job build set job name valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateJob_BUILD_setJobName_ValidModel_ReturnModel() throws Exception {
        gTestJobModel.setId(JOB_ID);
        gTestJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobModel.setJobName(JOB_NAME + "-1");

        gTestJobDetailModel.setJobName(JOB_NAME);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + gTestJobModel.getId(), HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // UPDATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // UPDATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).updateJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME PHASE 1 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-1", HttpMethod.GET, null, Integer.class)).thenReturn(1);
        // SET JOB NAME PHASE 2 :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME + "-2", HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.updateJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job test valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_TEST_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.TEST));
        gTestJobModel.setBuildJobId(JOB_ID);
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setBuilderType(String.valueOf(JobConfig.BuilderType.GRADLE));

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getTestJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE TEST JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE TEST JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT TEST JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE TEST JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);

        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Update job test valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateJob_TEST_ValidModel_ReturnModel() throws Exception {
        gTestJobModel.setId(JOB_ID);
        gTestJobModel.setJobType(String.valueOf(JobService.JobType.TEST));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setJobName(JOB_NAME);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + gTestJobModel.getId(), HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // UPDATE TEST JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // UPDATE TEST JOB TO CI SERVER
        doNothing().when(ciServer).updateJob(JOB_GUID, JOB_XML, true);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE TEST JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.updateJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job deploy valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_DEPLOY_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.DEPLOY));
        gTestJobModel.setJobName(JOB_NAME);

        CfInfo testCfInfoModel = new CfInfo();
        testCfInfoModel.setCfApiUrl(CF_API_URL);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE DEPLOY JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE DEPLOY JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // SET APP URL :: CF INFO DETAIL FROM DATABASE
        when(cfInfoService.getCfInfo(gTestJobModel)).thenReturn(testCfInfoModel);
        // INSERT DEPLOY JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);


        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Create job deploy manifest use yn y valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createJob_DEPLOY_ManifestUseYn_Y_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.DEPLOY));
        gTestJobModel.setJobName(JOB_NAME);
        gTestJobModel.setManifestUseYn(Constants.USE_YN_Y);
        gTestJobModel.setManifestScript(MANIFEST_SCRIPT);

        CfInfo testCfInfoModel = new CfInfo();
        testCfInfoModel.setCfApiUrl(CF_API_URL);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(gTestJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // CREATE DEPLOY JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE DEPLOY JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + JOB_NAME, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // SET APP URL :: CF INFO DETAIL FROM DATABASE
        when(cfInfoService.getCfInfo(gTestJobModel)).thenReturn(testCfInfoModel);
        // INSERT DEPLOY JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);


        // TEST
        CustomJob resultModel = jobService.createJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
    }


    /**
     * Update job deploy valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateJob_DEPLOY_ValidModel_ReturnModel() throws Exception {
        gTestJobModel.setJobType(String.valueOf(JobService.JobType.DEPLOY));
        gTestJobModel.setJobName(JOB_NAME);

        gTestJobDetailModel.setJobName(JOB_NAME);

        CfInfo testCfInfoModel = new CfInfo();
        testCfInfoModel.setCfApiUrl(CF_API_URL);

        gTestResultJobModel.setJobName(JOB_NAME);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + gTestJobModel.getId(), HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(gTestJobModel)).thenReturn(JOB_XML);
        // UPDATE DEPLOY JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // UPDATE DEPLOY JOB TO CI SERVER
        doNothing().when(ciServer).updateJob(JOB_GUID, JOB_XML, true);
        // SET APP URL :: CF INFO DETAIL FROM DATABASE
        when(cfInfoService.getCfInfo(gTestJobModel)).thenReturn(testCfInfoModel);
        // UPDATE DEPLOY JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.updateJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(JOB_NAME, resultModel.getJobName());
        assertEquals(gTestResultJobModel.getJobTrigger(), resultModel.getJobTrigger());
        assertEquals(gTestResultJobModel.getAppUrl(), resultModel.getAppUrl());
        assertEquals(gTestResultJobModel.getManifestScript(), resultModel.getManifestScript());
        assertEquals(gTestResultJobModel.getInspectionProjectId(), resultModel.getInspectionProjectId());
        assertEquals(gTestResultJobModel.getInspectionProfileId(), resultModel.getInspectionProfileId());
        assertEquals(gTestResultJobModel.getInspectionGateId(), resultModel.getInspectionGateId());
        assertEquals(gTestResultJobModel.getCreated(), resultModel.getCreated());
        assertEquals(gTestResultJobModel.getLastModified(), resultModel.getLastModified());
        assertEquals(gTestResultJobModel.getBuildJobName(), resultModel.getBuildJobName());
        assertEquals(gTestResultJobModel.getLastJobModified(), resultModel.getLastJobModified());
        assertEquals(gTestResultJobModel.getLastSuccessJobNumber(), resultModel.getLastSuccessJobNumber());
        assertEquals(gTestResultJobModel.getLastGroupOrder(), resultModel.getLastGroupOrder());
        assertEquals(gTestResultJobModel.getDuration(), resultModel.getDuration());
        assertEquals(gTestResultJobModel.getEstimatedDuration(), resultModel.getEstimatedDuration());
        assertEquals(gTestResultJobModel.getConsoleOutputHtml(), resultModel.getConsoleOutputHtml());
        assertEquals(gTestResultJobModel.getTimeStamp(), resultModel.getTimeStamp());
        assertEquals(gTestResultJobModel.getBuildFilePath(), resultModel.getBuildFilePath());
        assertEquals(gTestResultJobModel.getResultMessage(), resultModel.getResultMessage());
    }


    /**
     * Delete job valid job id return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void deleteJob_ValidJobId_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setJobType(String.valueOf(JobService.JobType.DEPLOY));


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // DELETE JOB IN CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // DELETE JOB IN CI SERVER
        doNothing().when(ciServer).deleteJob(JOB_GUID, true);
        // DELETE WORKSPACE IN CI SERVER
        doNothing().when(jobBuiltFileService).deleteWorkspace(gTestResultJobModel);
        // DELETE JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.DELETE, null, String.class)).thenReturn(null);
        // GET JOB HISTORY LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID + REQ_HISTORY_URL, HttpMethod.GET, null, List.class)).thenReturn(new ArrayList());
        // DELETE JOB HISTORY TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID + REQ_HISTORY_URL, HttpMethod.DELETE, null, String.class)).thenReturn(null);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);


        // TEST
        CustomJob resultModel = jobService.deleteJob(String.valueOf(JOB_ID));

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Trigger job build valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void triggerJob_BUILD_ValidModel_ReturnModel() throws Exception {
        gTestJobModel.setId(JOB_ID);

        gTestJobDetailModel.setCiServerUrl(CI_SERVER_URL);
        gTestJobDetailModel.setJobType(String.valueOf(JobService.JobType.BUILD));
        gTestJobDetailModel.setJobGuid(JOB_GUID);


        // PROCESS TRIGGER JOB :: GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // INSERT BUILD JOB HISTORY TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_JOB_HISTORY_URL, HttpMethod.POST, gTestJobHistoryModel, JobHistory.class)).thenReturn(gTestResultJobHistoryModel);
        // TRIGGER BUILD JOB TO CI SERVER
        when(commonService.getCiTriggerHelper(CI_SERVER_URL)).thenReturn(ciTriggerHelper);
        // TRIGGER BUILD JOB TO CI SERVER
        when(ciTriggerHelper.triggerJobAndWaitUntilFinished(JOB_GUID, true)).thenReturn(buildWithDetails);
        // TRIGGER BUILD JOB TO CI SERVER
        when(buildWithDetails.getResult()).thenReturn(BuildResult.SUCCESS);
        // TRIGGER BUILD JOB TO CI SERVER
        when(jobBuiltFileService.setBuiltFile(gTestJobDetailModel)).thenReturn(new CustomJob());
        // UPDATE DEPLOY JOB HISTORY TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_JOB_HISTORY_URL, HttpMethod.PUT, gTestResultJobHistoryModel, JobHistory.class)).thenReturn(null);
        // GET REPOSITORY COMMIT REVISION
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestResultJobModel);
        // UPDATE JOB DETAIL TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.triggerJob(gTestJobModel, gTestJobHistoryModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Trigger job test valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void triggerJob_TEST_ValidModel_ReturnModel() throws Exception {
        // TODO
    }


    /**
     * Trigger job deploy valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void triggerJob_DEPLOY_ValidModel_ReturnModel() throws Exception {
        CustomJob testDeployDetail = new CustomJob();
        FileInfo testFileInfo = new FileInfo();
        Map<String, String> testParamMap = new HashMap<>();

        long buildJobId = 10L;
        long deployDetailJobId = 11L;
        long jobHistoryId = 0L;
        String appName = "test-app-name";
        String orgName = "test-org-name";
        String spaceName = "test-space-name";
        String fileUrl = "test-file-url";
        String originalFileName = "test-original-file-name";
        String builderLanguage = "java_buildpack_offline";
        String reqUrl = REQ_URL + "/" + buildJobId + REQ_HISTORY_URL + "/status/" + Constants.EMPTY_VALUE + "/first";

        testDeployDetail.setAppName(appName);
        testDeployDetail.setDeployTargetOrg(orgName);
        testDeployDetail.setDeployTargetSpace(spaceName);

        testFileInfo.setFileUrl(fileUrl);
        testFileInfo.setOriginalFileName(originalFileName);

        testParamMap.put("APP_NAME", appName);
        testParamMap.put("ORG_NAME", orgName);
        testParamMap.put("SPACE_NAME", spaceName);
        testParamMap.put("BUILD_FILE_PATH", fileUrl);
        testParamMap.put("BUILD_FILE_NAME", originalFileName);
        testParamMap.put("BUILD_PACK_NAME", builderLanguage);

        // TODO :: FIX TEST CASE
        testParamMap.put("DEPLOY_TYPE", String.valueOf(JobConfig.DeployType.DEV));

        gTestJobModel.setId(JOB_ID);

        gTestJobDetailModel.setId(deployDetailJobId);
        gTestJobDetailModel.setJobType(String.valueOf(JobService.JobType.DEPLOY));
        gTestJobDetailModel.setJobGuid(JOB_GUID);
        gTestJobDetailModel.setBuildJobId(buildJobId);
        gTestJobDetailModel.setJobHistoryId(jobHistoryId);

        gTestJobHistoryModel.setJobId(deployDetailJobId);
        gTestJobHistoryModel.setFileId(FILE_ID);

        gTestResultJobHistoryModel.setId(deployDetailJobId);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // PROCESS TRIGGER JOB :: GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);
        // GET BUILD JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + buildJobId, HttpMethod.GET, null, CustomJob.class)).thenReturn(new CustomJob());
        // INSERT DEPLOY JOB HISTORY TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_JOB_HISTORY_URL, HttpMethod.POST, gTestJobHistoryModel, JobHistory.class)).thenReturn(gTestResultJobHistoryModel);
        // GET DEPLOY JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + deployDetailJobId, HttpMethod.GET, null, CustomJob.class)).thenReturn(testDeployDetail);
        // GET JOB HISTORY FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, JobHistory.class)).thenReturn(gTestJobHistoryModel);
        // GET FILE DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_FILE_URL + "/" + FILE_ID, HttpMethod.GET, null, FileInfo.class)).thenReturn(testFileInfo);
        // TRIGGER DEPLOY JOB TO CI SERVER
        when(commonService.getCiTriggerHelper(CI_SERVER_URL)).thenReturn(ciTriggerHelper);
        // TRIGGER DEPLOY JOB TO CI SERVER
        when(ciTriggerHelper.triggerJobAndWaitUntilFinished(JOB_GUID, testParamMap, true)).thenReturn(buildWithDetails);
        // TRIGGER DEPLOY JOB TO CI SERVER
        when(buildWithDetails.getResult()).thenReturn(BuildResult.SUCCESS);
        // TRIGGER DEPLOY JOB TO CI SERVER
        when(buildWithDetails.getNumber()).thenReturn(1);
        // UPDATE DEPLOY JOB HISTORY TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_JOB_HISTORY_URL, HttpMethod.PUT, gTestResultJobHistoryModel, JobHistory.class)).thenReturn(null);


        // TEST
        CustomJob resultModel = jobService.triggerJob(gTestJobModel, gTestJobHistoryModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(1, resultModel.getJobNumber());
        assertEquals(deployDetailJobId, gTestResultJobHistoryModel.getId());
        assertEquals(JOB_ID, gTestResultJobHistoryModel.getJobId());
        assertEquals(1, gTestResultJobHistoryModel.getJobNumber());
        assertEquals(0, gTestResultJobHistoryModel.getPreviousJobNumber());
        assertEquals(0, gTestResultJobHistoryModel.getDuration());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, gTestResultJobHistoryModel.getStatus());
        assertEquals(USER_ID, gTestResultJobHistoryModel.getUserId());
        assertEquals(null, gTestResultJobHistoryModel.getCreated());
        assertEquals(null, gTestResultJobHistoryModel.getLastModified());
    }


    /**
     * Trigger job deploy roll back valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void triggerJob_DEPLOY_rollBack_ValidModel_ReturnModel() throws Exception {
        // TODO

    }


    /**
     * Trigger post job valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void triggerPostJob_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setId(JOB_ID);

        gTestResultJobModel.setPostActionYn(Constants.USE_YN_N);
        gTestResultJobModel.setLastJobStatus(Constants.RESULT_STATUS_SUCCESS);

        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        jobService.triggerPostJob(gTestJobModel);
    }


    /**
     * Stop job valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void stopJob_ValidModel_ReturnModel() throws Exception {
        List<QueueItem> queueItemList = new ArrayList<>();
        QueueItem testQueueItem = new QueueItem();
        long queueId = 1L;

        testQueueItem.setId(JOB_ID);
        queueItemList.add(testQueueItem);

        gTestJobModel.setJobGuid(JOB_GUID);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // GET QUEUE FROM CI SERVER
        when(ciServer.getQueue()).thenReturn(ciQueue);
        // GET QUEUE ITEM LIST FROM CI SERVER
        when(ciQueue.getItems()).thenReturn(queueItemList);
        // GET JOB DETAIL FROM CI SERVER
        when(ciServer.getJob(JOB_GUID)).thenReturn(jobWithDetails);
        // GET QUEUE ITEM FROM CI SERVER
        when(jobWithDetails.getQueueItem()).thenReturn(queueItem);
        // GET QUEUE ITEM ID FROM CI SERVER
        when(queueItem.getId()).thenReturn(queueId);
        // CANCEL QUEUE ITEM TO CI SERVER
        when(commonService.procGetCiHttpClient(CI_SERVER_URL)).thenReturn(ciHttpClient);
        // CANCEL QUEUE ITEM TO CI SERVER
        doNothing().when(ciHttpClient).post("/queue/cancelItem?id=" + queueId, true);


        // TEST
        CustomJob resultModel = jobService.stopJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Gets job log valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getJobLog_ValidModel_ReturnModel() throws Exception {
        gTestResultJobModel.setJobGuid(JOB_GUID);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // GET JOB DETAIL FROM CI SERVER
        when(ciServer.getJob(JOB_GUID)).thenReturn(jobWithDetails);
        // GET BUILD FROM CI SERVER
        when(jobWithDetails.getBuildByNumber(JOB_NUMBER)).thenReturn(build);
        // GET BUILD DETAIL FROM CI SERVER
        when(build.details()).thenReturn(buildWithDetails);


        // TEST
        CustomJob resultModel = jobService.getJobLog(Math.toIntExact(JOB_ID), JOB_NUMBER);

        assertThat(resultModel).isNotNull();
        assertEquals(JOB_GUID, resultModel.getJobGuid());
        assertEquals(JOB_NUMBER, resultModel.getJobNumber());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Gets job status valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getJobStatus_ValidModel_ReturnModel() throws Exception {
        gTestResultJobModel.setJobGuid(JOB_GUID);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // GET JOB DETAIL FROM CI SERVER
        when(ciServer.getJob(JOB_GUID)).thenReturn(jobWithDetails);
        // GET BUILD FROM CI SERVER
        when(jobWithDetails.getBuildByNumber(JOB_NUMBER)).thenReturn(build);
        // GET BUILD DETAIL FROM CI SERVER
        when(build.details()).thenReturn(buildWithDetails);
        // GET BUILD STATUS FROM CI SERVER
        when(buildWithDetails.isBuilding()).thenReturn(true);


        // TEST
        CustomJob resultModel = jobService.getJobStatus(Math.toIntExact(JOB_ID), JOB_NUMBER);

        assertThat(resultModel).isNotNull();
        assertEquals(JOB_GUID, resultModel.getJobGuid());
        assertEquals(JOB_NUMBER, resultModel.getJobNumber());
        assertEquals("true", resultModel.getIsBuilding());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Gets job status invalid model return false.
     *
     * @throws Exception the exception
     */
    @Test
    public void getJobStatus_InvalidModel_ReturnFalse() throws Exception {
        gTestResultJobModel.setJobGuid(JOB_GUID);


        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + gTestJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // GET JOB DETAIL FROM CI SERVER
        when(ciServer.getJob(JOB_GUID)).thenReturn(jobWithDetails);
        // GET BUILD FROM CI SERVER
        when(jobWithDetails.getBuildByNumber(JOB_NUMBER)).thenReturn(build);
        // GET BUILD DETAIL FROM CI SERVER
        when(build.details()).thenReturn(buildWithDetails);
        // GET BUILD STATUS FROM CI SERVER
        when(buildWithDetails.isBuilding()).thenReturn(false);


        // TEST
        CustomJob resultModel = jobService.getJobStatus(Math.toIntExact(JOB_ID), JOB_NUMBER);

        assertThat(resultModel).isNotNull();
        assertEquals(JOB_GUID, resultModel.getJobGuid());
        assertEquals(JOB_NUMBER, resultModel.getJobNumber());
        assertEquals("false", resultModel.getIsBuilding());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Replicate job valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void replicateJob_ValidModel_ReturnModel() throws Exception {
        CustomJob testResultJobModel = new CustomJob();

        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;
        String replicatedJobName = JOB_NAME + "-COPY";

        testResultJobModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        testResultJobModel.setPipelineId(PIPELINE_ID);
        testResultJobModel.setJobName(JOB_NAME);
        testResultJobModel.setJobType(String.valueOf(JobService.JobType.BUILD));

        gTestJobModel.setId(JOB_ID);

        gTestResultJobModel.setJobName(replicatedJobName);


        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + gTestJobModel.getId(), HttpMethod.GET, null, CustomJob.class)).thenReturn(testResultJobModel);
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + testResultJobModel.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class)).thenReturn(gTestServiceInstancesModel);
        // CREATE CREDENTIALS TO CI SERVER
        when(credentialsService.createCredentials(testResultJobModel)).thenReturn(null);
        // GET JOB XML FROM TEMPLATE FILE
        when(jobTemplateService.getBuildJobTemplate(testResultJobModel)).thenReturn(JOB_XML);
        // CREATE BUILD JOB TO CI SERVER
        when(commonService.procGetCiServer(CI_SERVER_URL)).thenReturn(ciServer);
        // CREATE BUILD JOB TO CI SERVER
        doNothing().when(ciServer).createJob(JOB_GUID, JOB_XML, true);
        // SET JOB NAME :: CHECK FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, CHECK_EXISTED_JOB_NAME_URL + replicatedJobName, HttpMethod.GET, null, Integer.class)).thenReturn(0);
        // INSERT BUILD JOB TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.POST, testResultJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET REPOSITORY COMMIT REVISION FROM DATABASE
        when(repositoryService.getRepositoryInfo(String.valueOf(JOB_ID))).thenReturn(gTestJobDetailModel);
        // SET PARAM :: UPDATE JOB DETAIL
        when(commonService.setPasswordByAES256(Constants.AES256Type.DECODE, REPOSITORY_ACCOUNT_PASSWORD)).thenReturn("");
        // UPDATE JOB DETAIL TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestResultJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);
        // SET JOB ORDER IN PIPELINE
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestJobDetailModel);


        // TEST
        CustomJob resultModel = jobService.replicateJob(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
        assertEquals(replicatedJobName, resultModel.getJobName());
    }


    /**
     * Rearrange job order valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void rearrangeJobOrder_ValidModel_ReturnModel() throws Exception {
        String reqUrl = REQ_PIPELINES_URL + PIPELINE_ID + REQ_URL;

        gTestJobModel.setId(JOB_ID);
        gTestJobModel.setJobOrder(2);

        gTestResultJobModel.setPostActionYn(Constants.USE_YN_N);
        gTestResultJobModel.setLastJobStatus(Constants.RESULT_STATUS_SUCCESS);


        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // GET JOB LIST FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, reqUrl, HttpMethod.GET, null, List.class)).thenReturn(gTestResultList);
        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + JOB_ID_IN_MAP, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // UPDATE JOB DETAIL TO DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL, HttpMethod.PUT, gTestResultJobModel, CustomJob.class)).thenReturn(gTestResultJobModel);


        // TEST
        CustomJob resultModel = jobService.rearrangeJobOrder(gTestJobModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());

    }

}
