package paasta.delivery.pipeline.api.job.template;

import org.apache.commons.io.FileUtils;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.job.CustomJob;
import paasta.delivery.pipeline.api.job.config.JobConfig;

import java.io.File;
import java.io.IOException;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.job.template
 *
 * @author REX
 * @version 1.0
 * @since 8 /31/2017
 */
class JobCommonTemplate {

    private static final String JOB_TEMPLATE_URL_STRING = "job-template/{JOB_TYPE}-job-java-{BUILDER_TYPE}-{REPOSITORY_TYPE}.xml";


    /**
     * Gets common template for build test job for java.
     *
     * @param customJob the custom job
     * @return the common template for build test job for java
     * @throws IOException the io exception
     */
    String getCommonTemplateForBuildTestJobForJava(CustomJob customJob) throws IOException {
        String jobType = customJob.getJobType();
        String templateFilePath = JOB_TEMPLATE_URL_STRING.replace("{JOB_TYPE}", "build");
        String repositoryString = "git";
        String loadedJobTemplate = "";
        String builderType = customJob.getBuilderType();
        ClassLoader classLoader;
        File templateFile;

        // CHECK JOB TYPE :: TEST
        if (String.valueOf(JobConfig.JobType.TEST).equals(jobType)) {
            templateFilePath = JOB_TEMPLATE_URL_STRING.replace("{JOB_TYPE}", "test");
        }

        // CHECK REPOSITORY TYPE
        if (String.valueOf(JobConfig.RepositoryType.SCM_SVN).equals(customJob.getRepositoryType())) {
            repositoryString = "svn";
        }

        templateFilePath = templateFilePath.replace("{REPOSITORY_TYPE}", repositoryString);

        // CHECK BUILDER TYPE :: GRADLE
        if (String.valueOf(JobConfig.BuilderType.GRADLE).equals(builderType)) {
            templateFilePath = templateFilePath.replace("{BUILDER_TYPE}", "gradle");

            classLoader = getClass().getClassLoader();
            templateFile = new File(classLoader.getResource(templateFilePath).getFile());
            loadedJobTemplate = FileUtils.readFileToString(templateFile, "UTF-8");

            loadedJobTemplate = loadedJobTemplate.replace("@GRADLE_PLUGIN_VERSION", Constants.PluginConfig.GRADLE_PLUGIN_VERSION.getValue());
            loadedJobTemplate = loadedJobTemplate.replace("@GRADLE_NAME", Constants.PluginConfig.GRADLE_NAME.getValue());
        }

        // CHECK BUILDER TYPE :: MAVEN
        if (String.valueOf(JobConfig.BuilderType.MAVEN).equals(builderType)) {
            templateFilePath = templateFilePath.replace("{BUILDER_TYPE}", "maven");

            classLoader = getClass().getClassLoader();
            templateFile = new File(classLoader.getResource(templateFilePath).getFile());
            loadedJobTemplate = FileUtils.readFileToString(templateFile, "UTF-8");

            loadedJobTemplate = loadedJobTemplate.replace("@MAVEN_NAME", Constants.PluginConfig.MAVEN_NAME.getValue());
        }

        // CHECK REPOSITORY TYPE
        if (String.valueOf(JobConfig.RepositoryType.SCM_SVN).equals(customJob.getRepositoryType())) {
            loadedJobTemplate = loadedJobTemplate.replace("@SVN_SCM_PLUGIN_VERSION", Constants.PluginConfig.SVN_SCM_PLUGIN_VERSION.getValue());
        } else {
            loadedJobTemplate = loadedJobTemplate.replace("@SCM_GIT_PLUGIN_VERSION", Constants.PluginConfig.SCM_GIT_PLUGIN_VERSION.getValue());
            loadedJobTemplate = loadedJobTemplate.replace("@REPOSITORY_BRANCH", customJob.getRepositoryBranch());
        }

        loadedJobTemplate = loadedJobTemplate.replace("@REPOSITORY_URL", customJob.getRepositoryUrl());
        loadedJobTemplate = loadedJobTemplate.replace("@REPOSITORY_ACCOUNT_ID", customJob.getRepositoryAccountId());

        return loadedJobTemplate;
    }

}
