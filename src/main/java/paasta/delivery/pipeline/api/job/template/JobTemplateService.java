package paasta.delivery.pipeline.api.job.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paasta.delivery.pipeline.api.cf.info.CfInfoService;
import paasta.delivery.pipeline.api.cf.info.CfInfo;
import paasta.delivery.pipeline.api.job.CustomJob;

import java.io.IOException;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.jobTemplate
 *
 * @author REX
 * @version 1.0
 * @since 6 /27/2017
 */
@Service
public class JobTemplateService {

    private static final String CF_API_URL_SPLIT_STRING = "api.";
    private final CfInfoService cfInfoService;


    /**
     * Instantiates a new Job template service.
     *
     * @param cfInfoService the cf info service
     */
    @Autowired
    public JobTemplateService(CfInfoService cfInfoService) {
        this.cfInfoService = cfInfoService;
    }


    /**
     * Gets build template.
     *
     * @param customJob the custom job
     * @return the build template
     * @throws IOException the io exception
     */
    public String getBuildJobTemplate(CustomJob customJob) throws IOException {
        String loadedBuildTemplate = "";
//        String builderLanguage = customJob.getBuilderLanguage();

        // TODO FIX
//        // CHECK BUILDER LANGUAGE :: JAVA
//        if (String.valueOf(JobConfig.BuilderLanguage.JAVA).equals(builderLanguage)) {
//            loadedBuildTemplate = new BuildJobTemplate().getBuildJobTemplateForJava(customJob);
//        }
//
//        // CHECK BUILDER LANGUAGE :: GO
//        if (String.valueOf(JobConfig.BuilderLanguage.GO).equals(builderLanguage)) {
//            loadedBuildTemplate = new BuildJobTemplate().getBuildJobTemplateForGo();
//        }


        loadedBuildTemplate = new BuildJobTemplate().getBuildJobTemplateForJava(customJob);

        return loadedBuildTemplate;
    }


    /**
     * Gets test job template.
     *
     * @param customJob the custom job
     * @return the test job template
     * @throws IOException the io exception
     */
    public String getTestJobTemplate(CustomJob customJob) throws IOException {
        String loadedTestJobTemplate;

        // TODO FIX
//        String deployLanguage = customJob.getBuilderLanguage();
//        // CHECK BUILDER LANGUAGE :: JAVA
//        if (String.valueOf(JobConfig.BuilderLanguage.JAVA).equals(deployLanguage)) {
//            loadedDeployTemplate = new DeployJobTemplate(propertyService).getDeployJobTemplateForJava(customJob);
//        }
//
//        // CHECK BUILDER LANGUAGE :: GO
//        if (String.valueOf(JobConfig.BuilderLanguage.GO).equals(deployLanguage)) {
//            loadedDeployTemplate = new DeployJobTemplate(propertyService).getDeployJobTemplateForGo();
//        }

        loadedTestJobTemplate = new TestJobTemplate().getTestJobTemplateForJava(customJob);

        return loadedTestJobTemplate;
    }


    /**
     * Gets deploy template.
     *
     * @param customJob the custom job
     * @return the deploy template
     * @throws IOException the io exception
     */
    public String getDeployJobTemplate(CustomJob customJob) throws IOException {
        String loadedDeployJobTemplate;

        // TODO FIX
//        String deployLanguage = customJob.getBuilderLanguage();
//        // CHECK BUILDER LANGUAGE :: JAVA
//        if (String.valueOf(JobConfig.BuilderLanguage.JAVA).equals(deployLanguage)) {
//            loadedDeployTemplate = new DeployJobTemplate(propertyService).getDeployJobTemplateForJava(customJob);
//        }
//
//        // CHECK BUILDER LANGUAGE :: GO
//        if (String.valueOf(JobConfig.BuilderLanguage.GO).equals(deployLanguage)) {
//            loadedDeployTemplate = new DeployJobTemplate(propertyService).getDeployJobTemplateForGo();
//        }

        loadedDeployJobTemplate = new DeployJobTemplate().getDeployJobTemplateForJava(customJob);

        return procSetCfAccountInfo(loadedDeployJobTemplate, customJob);
    }


    private String procSetCfAccountInfo(String loadedDeployJobTemplate, CustomJob customJob) {
        String resultDeployJobTemplate;
        String cfApiUrl;

        // GET CF INFO DETAIL
        CfInfo cfInfo = cfInfoService.getCfInfo(customJob);
        cfApiUrl = cfInfo.getCfApiUrl();

        resultDeployJobTemplate = loadedDeployJobTemplate.replace("@CF_API_URL", cfApiUrl);
        resultDeployJobTemplate = resultDeployJobTemplate.replace("@CF_ACCOUNT_ID", cfInfo.getCfId());
        resultDeployJobTemplate = resultDeployJobTemplate.replace("@CF_ACCOUNT_PASSWORD", cfInfo.getCfPassword());
        resultDeployJobTemplate = resultDeployJobTemplate.replace("@DOMAIN_NAME", cfApiUrl.split(CF_API_URL_SPLIT_STRING)[1]);

        return resultDeployJobTemplate;
    }

}
