package paasta.delivery.pipeline.api.inspection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.common.RestTemplateService;
import paasta.delivery.pipeline.api.job.CustomJob;

/**
 * The type Inspection project service.
 */
@Service
public class InspectionProjectService {

    private static final String REQ_URL = "/projects";
    private final RestTemplateService restTemplateService;


    /**
     * Instantiates a new Inspection project service.
     *
     * @param restTemplateService the rest template service
     */
    @Autowired
    public InspectionProjectService(RestTemplateService restTemplateService) {this.restTemplateService = restTemplateService;}


    /**
     * Create project inspection project.
     *
     * @param customJob the custom job
     * @return the inspection project
     */
    public InspectionProject createProject(CustomJob customJob) {
        InspectionProject inspectionProject = new InspectionProject();

        // SET PARAM :: CREATE INSPECTION PROJECT TO INSPECTION API
        inspectionProject.setServiceInstancesId(customJob.getServiceInstancesId());
        inspectionProject.setPipelineId((int) customJob.getPipelineId());
        inspectionProject.setJobId(0);
        inspectionProject.setQualityProfileKey(customJob.getInspectionProfileKey());
        inspectionProject.setQualityGateId(customJob.getInspectionGateId());

        // CREATE INSPECTION PROJECT TO INSPECTION API
        return restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, inspectionProject, InspectionProject.class);
    }


    /**
     * Update project inspection project.
     *
     * @param customJob the custom job
     * @return the inspection project
     */
    public InspectionProject updateProject(CustomJob customJob) {
        InspectionProject inspectionProject = new InspectionProject();

        // SET PARAM : UPDATE INSPECTION PROJECT TO INSPECTION API
        inspectionProject.setId(customJob.getInspectionProjectId());
        inspectionProject.setJobId(customJob.getId());
        inspectionProject.setQualityProfileKey(customJob.getInspectionProfileKey());
        inspectionProject.setQualityGateId(customJob.getInspectionGateId());

        return restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsUpdate", HttpMethod.POST, inspectionProject, InspectionProject.class);
    }


    /**
     * Delete project inspection project.
     *
     * @param customJob the custom job
     * @return the inspection project
     */
    public InspectionProject deleteProject(CustomJob customJob) {
        InspectionProject inspectionProject = new InspectionProject();

        // SET PARAM : DELETE INSPECTION PROJECT TO INSPECTION API
        inspectionProject.setId(customJob.getInspectionProjectId());
        inspectionProject.setProjectKey(customJob.getInspectionProjectKey());

        return restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsDelete", HttpMethod.POST, inspectionProject, InspectionProject.class);
    }

}