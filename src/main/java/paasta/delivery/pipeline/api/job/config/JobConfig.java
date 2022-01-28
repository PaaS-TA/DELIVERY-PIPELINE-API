package paasta.delivery.pipeline.api.job.config;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.jobConfig
 *
 * @author REX
 * @version 1.0
 * @since 6 /27/2017
 */
public class JobConfig {

    private static final String JOB_TRIGGER_PREVIOUS_JOB_SUCCESS_STRING = "Running a job when it completes the previous job";
    private static final String JOB_TRIGGER_MODIFIED_PUSH_STRING = "Run a job every time you push a change";
    private static final String JOB_TRIGGER_MANUAL_TRIGGER_STRING = "Run job only when running this job manually";


    /**
     * The enum ConfigType.
     */
    public enum ConfigType {
        /**
         * Repository ConfigType.
         */
        REPOSITORY,
        /**
         * Builder ConfigType.
         */
        BUILDER,
        /**
         * Job config type.
         */
        JOB,
        /**
         * Deploy config type.
         */
        DEPLOY,
        /**
         * Build job trigger type config type.
         */
        BUILD_JOB_TRIGGER_TYPE,
        /**
         * Test job trigger type config type.
         */
        TEST_JOB_TRIGGER_TYPE,
        /**
         * Deploy job trigger type config type.
         */
        DEPLOY_JOB_TRIGGER_TYPE
    }


    /**
     * The enum Job type.
     */
    public enum JobType {
        /**
         * Build job type.
         */
        BUILD("Build"),
        /**
         * Test job type.
         */
        TEST("Test"),
        /**
         * Deploy job type.
         */
        DEPLOY("Deploy");

        private String actualValue;

        JobType(String actualValue) {
            this.actualValue = actualValue;
        }

        /**
         * Gets actual value.
         *
         * @return the actual value
         */
        public String getActualValue() {
            return actualValue;
        }
    }


    /**
     * The enum Repository.
     */
    public enum RepositoryType {
        /**
         * Git hub repository type.
         */
        GIT_HUB("Github"),
        /**
         * Scm git repository type.
         */
        SCM_GIT("SCM (Git)"),
        /**
         * Scm svn repository type.
         */
        SCM_SVN("SCM (SVN)");

        private String actualValue;

        RepositoryType(String actualValue) {
            this.actualValue = actualValue;
        }

        /**
         * Gets actual value.
         *
         * @return the actual value
         */
        public String getActualValue() {
            return actualValue;
        }
    }


    /**
     * The enum Builder ConfigType.
     */
    public enum BuilderType {
        /**
         * Gradle builder ConfigType.
         */
        GRADLE("Gradle"),
        /**
         * Maven builder ConfigType.
         */
        MAVEN("Maven");

        private String actualValue;

        BuilderType(String actualValue) {
            this.actualValue = actualValue;
        }

        /**
         * Gets actual value.
         *
         * @return the actual value
         */
        public String getActualValue() {
            return actualValue;
        }
    }


    /**
     * The enum Deploy type.
     */
    public enum DeployType {
        /**
         * Dev deploy type.
         */
        DEV("Development"),
        /**
         * Prd deploy type.
         */
        PRD("Production");

        private String actualValue;

        DeployType(String actualValue) {
            this.actualValue = actualValue;
        }

        /**
         * Gets actual value.
         *
         * @return the actual value
         */
        public String getActualValue() {
            return actualValue;
        }
    }


    /**
     * The enum Blue green deploy status.
     */
    public enum BlueGreenDeployStatus {
        /**
         * Blue deploy blue green deploy status.
         */
        BLUE_DEPLOY,
        /**
         * Green deploy blue green deploy status.
         */
        GREEN_DEPLOY,
        /**
         * Green deploy revert blue green deploy status.
         */
        REVERT_GREEN_DEPLOY;
    }


    /**
     * The enum Build job trigger type.
     */
    public enum BuildJobTriggerType {
        /**
         * Previous job success build job trigger type.
         */
        PREVIOUS_JOB_SUCCESS(JOB_TRIGGER_PREVIOUS_JOB_SUCCESS_STRING),
        /**
         * Modified push build job trigger type.
         */
        MODIFIED_PUSH(JOB_TRIGGER_MODIFIED_PUSH_STRING),
        /**
         * Manual trigger build job trigger type.
         */
        MANUAL_TRIGGER(JOB_TRIGGER_MANUAL_TRIGGER_STRING);

        private String actualValue;

        BuildJobTriggerType(String actualValue) {
            this.actualValue = actualValue;
        }

        /**
         * Gets actual value.
         *
         * @return the actual value
         */
        public String getActualValue() {
            return actualValue;
        }
    }


    /**
     * The enum Test job trigger type.
     */
    public enum TestJobTriggerType {
        /**
         * Previous job success test job trigger type.
         */
        PREVIOUS_JOB_SUCCESS(JOB_TRIGGER_PREVIOUS_JOB_SUCCESS_STRING),
        /**
         * Manual trigger test job trigger type.
         */
        MANUAL_TRIGGER(JOB_TRIGGER_MANUAL_TRIGGER_STRING);

        private String actualValue;

        TestJobTriggerType(String actualValue) {
            this.actualValue = actualValue;
        }

        /**
         * Gets actual value.
         *
         * @return the actual value
         */
        public String getActualValue() {
            return actualValue;
        }
    }


    /**
     * The enum Deploy job trigger type.
     */
    public enum DeployJobTriggerType {
        /**
         * Previous job success deploy job trigger type.
         */
        PREVIOUS_JOB_SUCCESS(JOB_TRIGGER_PREVIOUS_JOB_SUCCESS_STRING),
        /**
         * Manual trigger deploy job trigger type.
         */
        MANUAL_TRIGGER(JOB_TRIGGER_MANUAL_TRIGGER_STRING);

        private String actualValue;

        DeployJobTriggerType(String actualValue) {
            this.actualValue = actualValue;
        }

        /**
         * Gets actual value.
         *
         * @return the actual value
         */
        public String getActualValue() {
            return actualValue;
        }
    }

}
