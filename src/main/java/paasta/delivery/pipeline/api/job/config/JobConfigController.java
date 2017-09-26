package paasta.delivery.pipeline.api.job.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import paasta.delivery.pipeline.api.common.ConfigType;

import java.util.List;

/**
 * The type Job config controller.
 */
@RestController
@RequestMapping(value = "/job-configs")
public class JobConfigController {

    private final JobConfigService jobConfigService;


    /**
     * Instantiates a new Job config controller.
     *
     * @param jobConfigService the job config service
     */
    @Autowired
    public JobConfigController(JobConfigService jobConfigService) {this.jobConfigService = jobConfigService;}


    /**
     * Gets config type list.
     *
     * @param configType the config type
     * @return the config type list
     */
    @RequestMapping(value = "/{configType:.+}", method = RequestMethod.GET)
    public List<ConfigType> getConfigTypeList(@PathVariable("configType") String configType) {
        return jobConfigService.getConfigTypeList(configType);
    }

}
