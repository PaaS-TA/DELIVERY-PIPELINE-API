package paasta.delivery.pipeline.api.job.template;

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
class BuildJobTemplate extends JobCommonTemplate {

    /**
     * Gets build template for java.
     *
     * @param customJob the custom job
     * @return the build template for java
     * @throws IOException the io exception
     */
    String getBuildJobTemplateForJava(CustomJob customJob) throws IOException {
        return getCommonTemplateForBuildTestJobForJava(customJob);
    }

}
