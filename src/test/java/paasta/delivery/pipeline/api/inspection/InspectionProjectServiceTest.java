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
 * @since 10/12/2017
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

    private static CustomJob gTestJobModel = null;
    private static InspectionProject gTestResultInspectionProjectModel = null;

    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private InspectionProjectService inspectionProjectService;


    @Before
    public void setUp() throws Exception {
        gTestJobModel = new CustomJob();
        gTestResultInspectionProjectModel = new InspectionProject();

        gTestJobModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestJobModel.setPipelineId((int) PIPELINE_ID);
        gTestJobModel.setId(0L);
        gTestJobModel.setPipelineName(PIPELINE_NAME);
        gTestJobModel.setJobName(JOB_NAME);
        gTestJobModel.setInspectionProfileId(1);
        gTestJobModel.setInspectionGateId(1);

        gTestResultInspectionProjectModel.setServiceInstancesId(SERVICE_INSTANCES_ID);
        gTestResultInspectionProjectModel.setPipelineId((int) PIPELINE_ID);
        gTestResultInspectionProjectModel.setJobId(0L);
        gTestResultInspectionProjectModel.setName(PIPELINE_NAME + "_" + JOB_NAME);
        gTestResultInspectionProjectModel.setQualityProfileId(1);
        gTestResultInspectionProjectModel.setQualityGateId(1);
    }

    @After
    public void tearDown() throws Exception {
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////// MethodName_StateUnderTest_ExpectedBehavior
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    public void createProject_ValidModel_ReturnModel() throws Exception {
        // CREATE INSPECTION PROJECT TO INSPECTION API
        when(restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, gTestResultInspectionProjectModel, InspectionProject.class)).thenReturn(gTestResultInspectionProjectModel);


        // TEST
        inspectionProjectService.createProject(gTestJobModel);
    }


    @Test
    public void updateProject() throws Exception {
        // UPDATE INSPECTION PROJECT TO INSPECTION API
        when(restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, gTestResultInspectionProjectModel, InspectionProject.class)).thenReturn(gTestResultInspectionProjectModel);


        // TEST
        inspectionProjectService.updateProject(gTestJobModel);
    }


    @Test
    public void deleteProject() throws Exception {
        // DELETE INSPECTION PROJECT TO INSPECTION API
        when(restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, gTestResultInspectionProjectModel, InspectionProject.class)).thenReturn(gTestResultInspectionProjectModel);


        // TEST
        inspectionProjectService.deleteProject(gTestJobModel);
    }

}
