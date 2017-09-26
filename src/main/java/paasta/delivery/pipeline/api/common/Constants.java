package paasta.delivery.pipeline.api.common;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.common
 *
 * @author REX
 * @version 1.0
 * @since 5 /15/2017
 */
public class Constants {

    public static final String RESULT_STATUS_SUCCESS = "SUCCESS";
    public static final String RESULT_STATUS_FAIL = "FAIL";

    public static final String TARGET_COMMON_API = "commonApi";
    public static final String TARGET_BINARY_STORAGE_API = "binaryStorageApi";

    public static final String EMPTY_VALUE = "EMPTY_VALUE";

    public static final String USE_YN_Y = "Y";
    public static final String USE_YN_N = "N";

    /**
     * Instantiates a new Constants.
     */
    Constants() {}

    /**
     * The enum Aes 256 type.
     */
    public enum AES256Type {
        /**
         * Encode aes 256 type.
         */
        ENCODE,
        /**
         * Decode aes 256 type.
         */
        DECODE;
    }


    /**
     * The enum Plugin config.
     */
    public enum PluginConfig {
        /**
         * Git scm plugin version plugin config.
         */
        SCM_GIT_PLUGIN_VERSION("git@3.3.0"),
        /**
         * Svn scm plugin version plugin config.
         */
        SVN_SCM_PLUGIN_VERSION("subversion@2.7.2"),
        /**
         * Gradle plugin version plugin config.
         */
        GRADLE_PLUGIN_VERSION("gradle@1.26"),
        /**
         * Gradle name plugin config.
         */
        GRADLE_NAME("gradle-3.5"),
        /**
         * Maven name plugin config.
         */
        MAVEN_NAME("maven-3.5"),
        /**
         * Sonar plugin version plugin config.
         */
        SONAR_PLUGIN_VERSION("sonar@2.6.1"),
        /**
         * Sonar name plugin config.
         */
        SONAR_NAME("sonarqube-5.3");

        private String value;

        PluginConfig(String value) {
            this.value = value;
        }

        /**
         * Gets value.
         *
         * @return the value
         */
        public String getValue() {
            return value;
        }

    }

}
