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
    public static final String TARGET_INSPECTION_API = "inspectionApi";
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

}
