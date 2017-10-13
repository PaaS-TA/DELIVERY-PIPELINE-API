package paasta.delivery.pipeline.api.inspection;

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

import static org.mockito.Mockito.when;

/**
 * deliveryPipelineApi
 * paasta.delivery.pipeline.api.inspection
 *
 * @author REX
 * @version 1.0
 * @since 10 /12/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InspectionProjectServiceTest {

    private static final String REQ_URL = "/projects";
    private static final String SERVICE_INSTANCES_ID = "test-service-instances-id";
    private static final long PIPELINE_ID = 1L;
    private static final String PIPELINE_NAME = "test-pipeline-name";
    private static final String JOB_NAME = "test-job-name";
    private static final String SONAR_KEY = "test-sonar-key";

    private static InspectionProject gTestResultInspectionProjectModel = null;
    private static CustomJob gTestJobModel = null;

    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private InspectionProjectService inspectionProjectService;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        InspectionProject gTestInspectionProjectModel = new InspectionProject();
        gTestResultInspectionProjectModel = new InspectionProject();
        gTestJobModel = new CustomJob();

        gTestInspectionProjectModel.setKey(SONAR_KEY);
        gTestInspectionProjectModel.setQualifier("");
        gTestInspectionProjectModel.setOrgName("");
        gTestInspectionProjectModel.setOrgGuid("");
        gTestInspectionProjectModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestInspectionProjectModel.setPipelineId((int) PIPELINE_ID);
        gTestInspectionProjectModel.setJobId(0L);
        gTestInspectionProjectModel.setName(PIPELINE_NAME + "_" + JOB_NAME);
        gTestInspectionProjectModel.setQualityProfileId(1);
        gTestInspectionProjectModel.setQualityGateId(1);
        gTestInspectionProjectModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        gTestInspectionProjectModel.setId(0);
        gTestInspectionProjectModel.setSonarKey("");
        gTestInspectionProjectModel.setUserName("");
        gTestInspectionProjectModel.setUserGuid("");
        gTestInspectionProjectModel.setResultMessage("");
        gTestInspectionProjectModel.setBranch("");
        gTestInspectionProjectModel.setLinked(false);
        gTestInspectionProjectModel.setGateId("");
        gTestInspectionProjectModel.setProjectId("");
        gTestInspectionProjectModel.setProfileKey("");
        gTestInspectionProjectModel.setProjectKey("");
        gTestInspectionProjectModel.setMeasures("");
        gTestInspectionProjectModel.setUuid("");
        gTestInspectionProjectModel.setProjectName("");
        gTestInspectionProjectModel.setResource("");
        gTestInspectionProjectModel.setMsr(null);
        gTestInspectionProjectModel.setMetrics("");
        gTestInspectionProjectModel.setBaseComponent("");
        gTestInspectionProjectModel.setBaseComponentKey("");
        gTestInspectionProjectModel.setComponents(null);
        gTestInspectionProjectModel.setSources(null);
        gTestInspectionProjectModel.setScm(null);
        gTestInspectionProjectModel.setIssues(null);

        gTestResultInspectionProjectModel.setKey(gTestInspectionProjectModel.getKey());
        gTestResultInspectionProjectModel.setQualifier(gTestInspectionProjectModel.getQualifier());
        gTestResultInspectionProjectModel.setOrgName(gTestInspectionProjectModel.getOrgName());
        gTestResultInspectionProjectModel.setOrgGuid(gTestInspectionProjectModel.getOrgGuid());
        gTestResultInspectionProjectModel.setServiceInstancesId(gTestInspectionProjectModel.getServiceInstancesId());
        gTestResultInspectionProjectModel.setPipelineId(gTestInspectionProjectModel.getPipelineId());
        gTestResultInspectionProjectModel.setJobId(gTestInspectionProjectModel.getJobId());
        gTestResultInspectionProjectModel.setName(gTestInspectionProjectModel.getName());
        gTestResultInspectionProjectModel.setQualityProfileId(gTestInspectionProjectModel.getQualityProfileId());
        gTestResultInspectionProjectModel.setQualityGateId(gTestInspectionProjectModel.getQualityGateId());
        gTestResultInspectionProjectModel.setResultStatus(gTestInspectionProjectModel.getResultStatus());

        gTestResultInspectionProjectModel.setId(gTestInspectionProjectModel.getId());
        gTestResultInspectionProjectModel.setSonarKey(gTestInspectionProjectModel.getSonarKey());
        gTestResultInspectionProjectModel.setUserName(gTestInspectionProjectModel.getUserName());
        gTestResultInspectionProjectModel.setUserGuid(gTestInspectionProjectModel.getUserGuid());
        gTestResultInspectionProjectModel.setResultMessage(gTestInspectionProjectModel.getResultMessage());
        gTestResultInspectionProjectModel.setBranch(gTestInspectionProjectModel.getBranch());
        gTestResultInspectionProjectModel.setLinked(gTestInspectionProjectModel.getLinked());
        gTestResultInspectionProjectModel.setGateId(gTestInspectionProjectModel.getGateId());
        gTestResultInspectionProjectModel.setProjectId(gTestInspectionProjectModel.getProjectId());
        gTestResultInspectionProjectModel.setProfileKey(gTestInspectionProjectModel.getProfileKey());
        gTestResultInspectionProjectModel.setProjectKey(gTestInspectionProjectModel.getProjectKey());
        gTestResultInspectionProjectModel.setMeasures(gTestInspectionProjectModel.getMeasures());
        gTestResultInspectionProjectModel.setUuid(gTestInspectionProjectModel.getUuid());
        gTestResultInspectionProjectModel.setProjectName(gTestInspectionProjectModel.getProjectName());
        gTestResultInspectionProjectModel.setResource(gTestInspectionProjectModel.getResource());
        gTestResultInspectionProjectModel.setMsr(gTestInspectionProjectModel.getMsr());
        gTestResultInspectionProjectModel.setMetrics(gTestInspectionProjectModel.getMetrics());
        gTestResultInspectionProjectModel.setBaseComponent(gTestInspectionProjectModel.getBaseComponent());
        gTestResultInspectionProjectModel.setBaseComponentKey(gTestInspectionProjectModel.getBaseComponentKey());
        gTestResultInspectionProjectModel.setComponents(gTestInspectionProjectModel.getComponents());
        gTestResultInspectionProjectModel.setSources(gTestInspectionProjectModel.getSources());
        gTestResultInspectionProjectModel.setScm(gTestInspectionProjectModel.getScm());
        gTestResultInspectionProjectModel.setIssues(gTestInspectionProjectModel.getIssues());

        gTestJobModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestJobModel.setPipelineId((int) PIPELINE_ID);
        gTestJobModel.setId(0L);
        gTestJobModel.setPipelineName(PIPELINE_NAME);
        gTestJobModel.setJobName(JOB_NAME);
        gTestJobModel.setInspectionProfileId(1);
        gTestJobModel.setInspectionGateId(1);
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
     * Create project valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void createProject_ValidModel_ReturnModel() throws Exception {
        // CREATE INSPECTION PROJECT TO INSPECTION API
        when(restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, gTestResultInspectionProjectModel, InspectionProject.class)).thenReturn(gTestResultInspectionProjectModel);


        // TEST
        inspectionProjectService.createProject(gTestJobModel);
    }


    /**
     * Update project.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateProject() throws Exception {
        // UPDATE INSPECTION PROJECT TO INSPECTION API
        when(restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, gTestResultInspectionProjectModel, InspectionProject.class)).thenReturn(gTestResultInspectionProjectModel);


        // TEST
        inspectionProjectService.updateProject(gTestJobModel);
    }


    /**
     * Delete project.
     *
     * @throws Exception the exception
     */
    @Test
    public void deleteProject() throws Exception {
        // DELETE INSPECTION PROJECT TO INSPECTION API
        when(restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, gTestResultInspectionProjectModel, InspectionProject.class)).thenReturn(gTestResultInspectionProjectModel);


        // TEST
        inspectionProjectService.deleteProject(gTestJobModel);
    }

}
