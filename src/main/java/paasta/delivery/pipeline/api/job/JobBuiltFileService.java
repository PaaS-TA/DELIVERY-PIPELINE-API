package paasta.delivery.pipeline.api.job;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import paasta.delivery.pipeline.api.common.*;
import paasta.delivery.pipeline.api.job.config.JobConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.job
 *
 * @author REX
 * @version 1.0
 * @since 6 /19/2017
 */
@Service
public class JobBuiltFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobBuiltFileService.class);
    private static final String REQ_URL = "/jobs";
    private static final String REQ_FILE_URL = "/file";
    private static final String REQ_SERVICE_INSTANCES_URL = "/serviceInstance/";

    private final String ciServerSshUserName;
    private final String ciServerSshPassword;
    private final String ciServerSshPort;
    private final String ciServerWorkspacePath;

    private final RestTemplateService restTemplateService;


    /**
     * Instantiates a new Job built file service.
     *
     * @param restTemplateService the rest template service
     * @param propertyService     the property service
     */
    public JobBuiltFileService(RestTemplateService restTemplateService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;

        ciServerSshUserName = propertyService.getCiServerSshUserName();
        ciServerSshPassword = propertyService.getCiServerSshPassword();
        ciServerSshPort = propertyService.getCiServerSshPort();
        ciServerWorkspacePath = propertyService.getCiServerWorkspacePath();
    }

    /**
     * Check ack int.
     *
     * @param in the in
     * @return the int
     * @throws IOException the io exception
     */
    private static int procCheckAck(InputStream in) throws IOException {
        int b = in.read();
        if (b == 0) {
            return b;
        }

        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
        }

        return b;
    }


    /**
     * Sets built file.
     *
     * @param customJob the custom job
     * @return the built file
     * @throws IOException   the io exception
     * @throws JSchException the j sch exception
     */
    CustomJob setBuiltFile(CustomJob customJob) throws IOException, JSchException {
        CustomJob resultModel = new CustomJob();
        FileInfo fileInfo = new FileInfo();
        FileInfo resultFileInfoModel = new FileInfo();

        resultModel.setResultStatus(Constants.RESULT_STATUS_FAIL);
        fileInfo.setResultStatus(Constants.RESULT_STATUS_FAIL);
        resultFileInfoModel.setId(0);

        ByteArrayResource byteArrayResource;

        // GET JOB DETAIL FROM DATABASE
        CustomJob tempResultModel = restTemplateService.send(Constants.TARGET_COMMON_API, REQ_URL + "/" + customJob.getId(), HttpMethod.GET, null, CustomJob.class);
        customJob.setBuilderType(tempResultModel.getBuilderType());
        byteArrayResource = procGetBuiltFileByteArrayResourceForJava(customJob);

        try {
            // SEND FILE TO BINARY STORAGE API
            fileInfo = restTemplateService.sendMultipart(Constants.TARGET_BINARY_STORAGE_API, REQ_FILE_URL + "/uploadFile", byteArrayResource, FileInfo.class);
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException :: {}", e);
        }

        if (Constants.RESULT_STATUS_SUCCESS.equals(fileInfo.getResultStatus())) {
            // INSERT FILE INFO TO DATABASE
            resultFileInfoModel = restTemplateService.send(Constants.TARGET_COMMON_API, REQ_FILE_URL + "/upload", HttpMethod.POST, fileInfo, FileInfo.class);
            resultModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);
        }

        resultModel.setFileId(resultFileInfoModel.getId());

        return resultModel;
    }


    private ByteArrayResource procGetBuiltFileByteArrayResourceForJava(CustomJob customJob) throws JSchException, IOException {
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        String ciServerUrl = restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + customJob.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class).getCiServerUrl();

        // CREATE JSch SESSION
        Session session = procCreateJSchSession(ciServerUrl);

        if (session == null) {
            throw new IOException("JSch SESSION ERROR");
        }

        String builderType = customJob.getBuilderType();
        String requestFile = ciServerWorkspacePath + customJob.getJobGuid();

        if (String.valueOf(JobConfig.BuilderType.GRADLE).equals(builderType)) {
            requestFile += "/build/libs/*.war";
        }

        if (String.valueOf(JobConfig.BuilderType.MAVEN).equals(builderType)) {
            requestFile += "/target/*.war";
        }

        String command = "scp -f " + requestFile;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        ByteArrayResource byteArrayResource = procGetByteArrayResource(in, out);
        session.disconnect();

        return byteArrayResource;
    }


    private Session procCreateJSchSession(String ciServerUrl) {
        String charSequence = ":";
        String host = ciServerUrl.replace("http://", "");
        host = (host.contains(charSequence)) ? host.split(charSequence)[0] : host;
        int port = Integer.parseInt(ciServerSshPort);

        JSch jsch = new JSch();
        Session session = null;

        try {
            session = jsch.getSession(ciServerSshUserName, host, port);
            session.setPassword(ciServerSshPassword);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

        } catch (JSchException e) {
            LOGGER.error("JSchException :: {}", e);
        }

        return session;
    }


    private ByteArrayResource procGetByteArrayResource(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];

        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        String fileName = "";
        long fileSize = 0L;
        byte[] resBytes = null;

        while (true) {
            int c = procCheckAck(in);
            if (c != 'C') {
                break;
            }

            in.read(buf, 0, 5);
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }

                if (buf[0] == ' ') {
                    break;
                }

                fileSize = fileSize * 10L + (long) (buf[0] - '0');
            }

            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    fileName = new String(buf, 0, i);
                    break;
                }
            }

            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int foo;
            while (true) {
                if (buf.length < fileSize) {
                    foo = buf.length;
                } else {
                    foo = (int) fileSize;
                }

                foo = in.read(buf, 0, foo);
                if (foo < 0) {
                    // ERROR
                    break;
                }
                bos.write(buf, 0, foo);

                fileSize -= foo;
                if (fileSize == 0L) {
                    break;
                }
            }
            resBytes = bos.toByteArray();
            bos.close();

            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
        }

        String finalFileName = fileName;
        return new ByteArrayResource(resBytes != null ? resBytes : new byte[0]) {
            @Override
            public String getFilename() {
                return finalFileName;
            }
        };
    }


    /**
     * Delete workspace.
     *
     * @param customJob the custom job
     */
    void deleteWorkspace(CustomJob customJob) {
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        String ciServerUrl = restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + customJob.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class).getCiServerUrl();

        // CREATE JSch SESSION
        Session session = procCreateJSchSession(ciServerUrl);

        if (session != null) {
            try {
                String requestPath = ciServerWorkspacePath + customJob.getJobGuid() + "*";

                String command = "rm -rf " + requestPath;
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);

                channel.connect();
            } catch (JSchException e) {
                LOGGER.error("Exception :: DELETE WORKSPACE :: {}", e);
            }
        }
    }

}
