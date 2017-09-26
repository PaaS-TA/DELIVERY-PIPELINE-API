package paasta.delivery.pipeline.api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import paasta.delivery.pipeline.api.job.CustomJob;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.repository
 *
 * @author REX
 * @version 1.0
 * @since 8 /2/2017
 */
@RestController
public class RepositoryController {

    private final RepositoryService repositoryService;

    /**
     * Instantiates a new Repository controller.
     *
     * @param repositoryService the repository service
     */
    @Autowired
    public RepositoryController(RepositoryService repositoryService) {this.repositoryService = repositoryService;}


    /**
     * Gets repository info.
     *
     * @param jobId the job id
     * @return the repository info
     */
    @RequestMapping(value = {"/jobs/{jobId:.+}/repositories"}, method = RequestMethod.GET)
    @ResponseBody
    public CustomJob getRepositoryInfo(@PathVariable("jobId") String jobId) {
        return repositoryService.getRepositoryInfo(jobId);
    }

}
