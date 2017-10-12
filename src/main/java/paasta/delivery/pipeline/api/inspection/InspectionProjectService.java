package paasta.delivery.pipeline.api.inspection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.common.RestTemplateService;

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
     * @param project the project
     * @return the inspection project
     */
    public InspectionProject createProject(InspectionProject project) {
        return restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsCreate", HttpMethod.POST, project, InspectionProject.class);
    }


    /**
     * Update project inspection project.
     *
     * @param project the project
     * @return the inspection project
     */
    public InspectionProject updateProject(InspectionProject project) {
        return restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsUpdate", HttpMethod.POST, project, InspectionProject.class);
    }


    /**
     * Delete project inspection project.
     *
     * @param project the project
     * @return the inspection project
     */
    public InspectionProject deleteProject(InspectionProject project) {
        return restTemplateService.send(Constants.TARGET_INSPECTION_API, REQ_URL + "/projectsDelete", HttpMethod.POST, project, InspectionProject.class);
    }

}