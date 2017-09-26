package paasta.delivery.pipeline.api.common;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.JenkinsTriggerHelper;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.common
 *
 * @author REX
 * @version 1.0
 * @since 5 /8/2017
 */
@Service
public class CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
    private final String ciServerAdminUserName;
    private final String ciServerAdminPassword;


    /**
     * Instantiates a new Common service.
     *
     * @param propertyService the property service
     */
    @Autowired
    public CommonService(PropertyService propertyService) {
        ciServerAdminUserName = propertyService.getCiServerAdminUserName();
        ciServerAdminPassword = propertyService.getCiServerAdminPassword();
    }


    /**
     * Gets ci server.
     *
     * @return the ci server
     */
    public JenkinsServer procGetCiServer(String ciServerUrl) {
        return this.procGetCiServer(procGetCiServerURI(ciServerUrl), ciServerAdminUserName, ciServerAdminPassword);
    }


    private JenkinsServer procGetCiServer(URI ciServerUri, String userName, String passwordOrToken) {
        return new JenkinsServer(ciServerUri, userName, passwordOrToken);
    }


    /**
     * Gets ci trigger helper.
     *
     * @return the ci trigger helper
     */
    public JenkinsTriggerHelper getCiTriggerHelper(String ciServerUrl) {
        return new JenkinsTriggerHelper(this.procGetCiServer(ciServerUrl));
    }


    /**
     * Gets ci http client.
     *
     * @return the ci http client
     */
    public JenkinsHttpClient procGetCiHttpClient(String ciServerUrl) {
        return procGetCiHttpClient(procGetCiServerURI(ciServerUrl), ciServerAdminUserName, ciServerAdminPassword);
    }


    private JenkinsHttpClient procGetCiHttpClient(URI ciServerUri, String userName, String passwordOrToken) {
        return new JenkinsHttpClient(ciServerUri, userName, passwordOrToken);
    }


    private URI procGetCiServerURI(String ciServerUrl) {
        URI uri = null;

        try {
            uri = new URI(ciServerUrl);
        } catch (Exception e) {
            LOGGER.info(String.valueOf(e));
        }

        return uri;
    }


    /**
     * Sets password by aes 256.
     *
     * @param reqProcess the req process
     * @param reqString  the req string
     * @return the password by aes 256
     */
    public String setPasswordByAES256(Enum reqProcess, String reqString) {
        String resultString = "";

        try {
            AES256Util aes256 = new AES256Util();
            URLCodec codec = new URLCodec();
            resultString = reqString;

            // ENCODE
            if (reqProcess.equals(Constants.AES256Type.ENCODE)) {
                resultString = codec.encode(aes256.aesEncodeDecode(reqString, Constants.AES256Type.ENCODE));
            }

            // DECODE
            if (reqProcess.equals(Constants.AES256Type.DECODE)) {
                resultString = aes256.aesEncodeDecode(codec.decode(reqString), Constants.AES256Type.DECODE);
            }

        } catch (EncoderException e) {
            LOGGER.error("Exception :: EncoderException :: {}", e);
        } catch (DecoderException e) {
            LOGGER.error("Exception :: DecoderException :: {}", e);
        }

        return resultString;
    }

}
