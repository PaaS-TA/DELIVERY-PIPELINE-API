package paasta.delivery.pipeline.api.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import paasta.delivery.pipeline.api.job.CustomJob;

/**
 * The type Credentials controller.
 */
@RestController
@RequestMapping(value = "/credentials")
public class CredentialsController {

    private final CredentialsService credentialsService;


    /**
     * Instantiates a new Credentials controller.
     *
     * @param credentialsService the credentials service
     */
    @Autowired
    public CredentialsController(CredentialsService credentialsService) {this.credentialsService = credentialsService;}


    /**
     * Create credentials custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     */
    @PostMapping
    public CustomJob createCredentials(@RequestBody CustomJob customJob) {
        return credentialsService.createCredentials(customJob);
    }


    /**
     * Update credentials custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     */
    @PutMapping
    public CustomJob updateCredentials(@RequestBody CustomJob customJob) {
        return credentialsService.updateCredentials(customJob);
    }

}
