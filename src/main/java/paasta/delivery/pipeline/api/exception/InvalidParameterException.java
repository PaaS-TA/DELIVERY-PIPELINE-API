package paasta.delivery.pipeline.api.exception;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.exception
 *
 * @author REX
 * @version 1.0
 * @since 6 /2/2017
 */
public class InvalidParameterException extends RuntimeException {

    /**
     * Instantiates a new Invalid parameter exception.
     *
     * @param msg the msg
     */
    public InvalidParameterException(String msg) {
        super(msg);
    }


    /**
     * Instantiates a new Invalid parameter exception.
     *
     * @param msg the msg
     * @param t   the t
     */
    public InvalidParameterException(String msg, Throwable t) {
        super(msg, t);
    }

}
