package paasta.delivery.pipeline.api.common;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.common
 *
 * @author REX
 * @version 1.0
 * @since 8 /3/2017
 */
class AES256Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(AES256Util.class);
    private static final String CHAR_SET_NAME = "UTF-8";
    private static final String AES256_KEY = "AES256-KEY-MORE-THAN-16-LETTERS";
    private String ivParameterSpec;
    private Key secretKeySpec;


    /**
     * Instantiates a new Aes 256 util.
     */
    AES256Util() {
        try {
            String key = AES256_KEY;
            this.ivParameterSpec = key.substring(0, 16);

            byte[] keyBytes = new byte[16];
            byte[] aes256KeyBytes = key.getBytes(CHAR_SET_NAME);
            int aes256KeyBytesLength = aes256KeyBytes.length;

            if (aes256KeyBytesLength > keyBytes.length) {
                aes256KeyBytesLength = keyBytes.length;
            }

            System.arraycopy(aes256KeyBytes, 0, keyBytes, 0, aes256KeyBytesLength);
            this.secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception :: AES256Util :: UnsupportedEncodingException :: {}", e);
        }
    }


    /**
     * Aes encode decode string.
     *
     * @param requestString       the request string
     * @param reqEncodeDecodeType the req encode decode type
     * @return the string
     */
    String aesEncodeDecode(String requestString, Enum reqEncodeDecodeType) {
        String resultString = "";

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // ENCODE
            if (Constants.AES256Type.ENCODE.equals(reqEncodeDecodeType)) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(ivParameterSpec.getBytes()));
                byte[] encrypted = cipher.doFinal(requestString.getBytes(CHAR_SET_NAME));

                resultString = new String(Base64.encodeBase64(encrypted));
            }

            // DECODE
            if (Constants.AES256Type.DECODE.equals(reqEncodeDecodeType)) {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivParameterSpec.getBytes(CHAR_SET_NAME)));
                byte[] decodeBase64Bytes = Base64.decodeBase64(requestString.getBytes());

                resultString = new String(cipher.doFinal(decodeBase64Bytes), CHAR_SET_NAME);
            }

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Exception :: NoSuchAlgorithmException :: {}", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("Exception :: NoSuchPaddingException :: {}", e);
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("Exception :: InvalidAlgorithmParameterException :: {}", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("Exception :: InvalidKeyException :: {}", e);
        } catch (BadPaddingException e) {
            LOGGER.error("Exception :: BadPaddingException :: {}", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception :: UnsupportedEncodingException :: {}", e);
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("Exception :: IllegalBlockSizeException :: {}", e);
        }

        return resultString;
    }

}

