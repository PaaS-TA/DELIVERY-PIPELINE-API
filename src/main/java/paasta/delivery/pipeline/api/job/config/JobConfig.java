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

    private static final String JOB_TRIGGER_PREVIOUS_JOB_SUCCESS_STRING = "이전 작업(Job)을 완료할 때 작업 실행";
    private static final String JOB_TRIGGER_MODIFIED_PUSH_STRING = "변경사항을 푸시할 때마다 작업 실행";
    private static final String JOB_TRIGGER_MANUAL_TRIGGER_STRING = "이 작업(Job)을 수동으로 실행할 때만 작업 실행";


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
        LANGUAGE,
        LANGUAGE_VERSION,
        /**
         * Builder ConfigType.
         */
        BUILDER,
        BUILDER_VERSION,
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
        BUILD("빌드 (Build)"),
        /**
         * Test job type.
         */
        TEST("테스트 (Test)"),
        /**
         * Deploy job type.
         */
        DEPLOY("배포 (Deploy)");

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
     * The enum Builder LanguageType
     */
    public enum LanguageType {

        JAVA("JAVA");

        /* //추후 확장될 빌드 언어
        RUBY("DOTNET"),
        DOTNET("DOTNET"),
        NODEJS("DOTNET"),
        GO("DOTNET"),
        PYTHON("DOTNET"),
        PHP("DOTNET");
        */
        private String actualValue;

        LanguageType(String actualValue) {
            this.actualValue = actualValue;
        }

        public String getActualValue() {
            return actualValue;
        }
    }

    /**
     * The enum Builder LanguageType Version
     */
    public enum LanguageTypeVersion {

        //jenkins Global Tool Configuration - JDK
        JAVA_8("java-1.8.152"),
        JAVA_11("jdk-11"),
        JAVA_17("jdk-17");

        private String actualValue;

        LanguageTypeVersion(String actualValue) {
            this.actualValue = actualValue;
        }

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
     * The enum Builder ConfigType Version
     */
    public enum BuilderTypeVersion {

        //jenkins Global Tool Configuration - Gradle & Maven
        GRADLE_2("gradle-2.14.1"),
        GRADLE_3("gradle-3.5.0"),
        GRADLE_4("gradle-4.10.3"),
        GRADLE_5("gradle-5.6.4"),
        GRADLE_6("gradle-6.7.1"),
        GRADLE_7("gradle-7.3.3"),

        MAVEN_3_5("maven-3.5.0"),
        MAVEN_3_6("maven-3.6.3"),
        MAVEN_3_8("maven-3.8.4");

        private String actualValue;

        BuilderTypeVersion(String actualValue) {
            this.actualValue = actualValue;
        }

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
        DEV("개발배포"),
        /**
         * Prd deploy type.
         */
        PRD("운영배포");

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
