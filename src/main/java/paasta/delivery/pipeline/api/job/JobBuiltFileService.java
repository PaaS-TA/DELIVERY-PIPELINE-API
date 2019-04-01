package paasta.delivery.pipeline.api.job;

import com.jcraft.jsch.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import paasta.delivery.pipeline.api.common.*;
import paasta.delivery.pipeline.api.job.config.JobConfig;

import java.io.*;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
    private final String ciServerSshIdentity;
    private final String ciServerWorkspacePath;

    private final RestTemplateService restTemplateService;
    private final String ciServerAdminUserName;
    private final String ciServerAdminPassword;


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
        ciServerSshIdentity = propertyService.getCiServerSshIdentity();
        ciServerWorkspacePath = propertyService.getCiServerWorkspacePath();
        ciServerAdminUserName = propertyService.getCiServerAdminUserName();
        ciServerAdminPassword = propertyService.getCiServerAdminPassword();

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
            } while (c != '\n');
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


    private ByteArrayResource procGetBuiltFileByteArrayResourceForJava(CustomJob customJob) throws IOException {
        // GET SERVICE INSTANCES DETAIL FROM DATABASE
        String ciServerUrl = restTemplateService.send(Constants.TARGET_COMMON_API, REQ_SERVICE_INSTANCES_URL + customJob.getServiceInstancesId(), HttpMethod.GET, null, ServiceInstances.class).getCiServerUrl();

        // CREATE JSch SESSION  BOSH_1.0 SCP로 파일 다운로드 기능으로 개발된 코드 BOSH_2.0에서는 사용불가
//        Session session = procCreateJSchSession(ciServerUrl);
//
//        if (session == null) {
//            throw new IOException("JSch SESSION ERROR");
//        }
//
//        String builderType = customJob.getBuilderType();
//        String requestFile = ciServerWorkspacePath + customJob.getJobGuid();
//
//        if (String.valueOf(JobConfig.BuilderType.GRADLE).equals(builderType)) {
//            requestFile += "/build/libs/*.war";
//        }
//
//        if (String.valueOf(JobConfig.BuilderType.MAVEN).equals(builderType)) {
//            requestFile += "/target/*.war";
//        }
//
//        String command = "scp -f " + requestFile;
//        Channel channel = session.openChannel("exec");
//        ((ChannelExec) channel).setCommand(command);
//
//        OutputStream out = channel.getOutputStream();
//        InputStream in = channel.getInputStream();
//
//        channel.connect();
//        ByteArrayResource byteArrayResource = procGetByteArrayResource(in, out);
//        session.disconnect();

        String builderType = customJob.getBuilderType();
        String requestFile = ciServerUrl + "/job/" + customJob.getJobGuid();

        if (String.valueOf(JobConfig.BuilderType.GRADLE).equals(builderType)) {
            requestFile += "/ws/build/libs/";
        }

        if (String.valueOf(JobConfig.BuilderType.MAVEN).equals(builderType)) {
            requestFile += "/ws/target/";
        }


        Map<String, String> fileInfo = getFileUrl(requestFile);
        if (fileInfo.get("URL") != null && fileInfo.get("FILE_NAME") != null) {
            ByteArrayResource byteArrayResource = procGetByteArrayResource(fileInfo.get("FILE_NAME"), fileInfo.get("URL"));
            return byteArrayResource;
        } else {
            return null;
        }


    }


    private Session procCreateJSchSession(String ciServerUrl) {
        String charSequence = ":";
        String host = ciServerUrl.replace("http://", "");
        host = (host.contains(charSequence)) ? host.split(charSequence)[0] : host;
        int port = Integer.parseInt(ciServerSshPort);

        JSch jsch = new JSch();
        Session session = null;

        try {
            if (!(null == ciServerSshIdentity || "".equals(ciServerSshIdentity) || "null".equals(ciServerSshIdentity))) {
                jsch.addIdentity(ciServerSshIdentity);
            }

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

//  BOSH_1.0 SCP로 파일 다운로드 기능으로 개발된 코드 BOSH_2.0에서는 사용불가
//    private ByteArrayResource procGetByteArrayResource(InputStream in, OutputStream out) throws IOException {
//        byte[] buf = new byte[1024];
//
//        buf[0] = 0;
//        out.write(buf, 0, 1);
//        out.flush();
//
//        String fileName = "";
//        long fileSize = 0L;
//        byte[] resBytes = null;
//
//        while (true) {
//            int c = procCheckAck(in);
//            if (c != 'C') {
//                break;
//            }
//
//            in.read(buf, 0, 5);
//            while (true) {
//                if (in.read(buf, 0, 1) < 0) {
//                    // error
//                    break;
//                }
//
//                if (buf[0] == ' ') {
//                    break;
//                }
//
//                fileSize = fileSize * 10L + (long) (buf[0] - '0');
//            }
//
//            for (int i = 0; ; i++) {
//                in.read(buf, i, 1);
//                if (buf[i] == (byte) 0x0a) {
//                    fileName = new String(buf, 0, i);
//                    break;
//                }
//            }
//
//            buf[0] = 0;
//            out.write(buf, 0, 1);
//            out.flush();
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            int bufferSize;
//            while (true) {
//                if (buf.length < fileSize) {
//                    bufferSize = buf.length;
//                } else {
//                    bufferSize = (int) fileSize;
//                }
//
//                bufferSize = in.read(buf, 0, bufferSize);
//                if (bufferSize < 0) {
//                    // ERROR
//                    break;
//                }
//                bos.write(buf, 0, bufferSize);
//
//                fileSize -= bufferSize;
//                if (fileSize == 0L) {
//                    break;
//                }
//            }
//
//            resBytes = bos.toByteArray();
//            bos.close();
//
//            buf[0] = 0;
//            out.write(buf, 0, 1);
//            out.flush();
//        }
//
//        String finalFileName = fileName;
//        return new ByteArrayResource(resBytes != null ? resBytes : new byte[0]) {
//            @Override
//            public String getFilename() {
//                return finalFileName;
//            }
//        };
//    }


    private ByteArrayResource procGetByteArrayResource(String fileName, String url) throws IOException {
        try {

            HttpEntity entity = getEntity(url);
            if (entity != null) {
                byte[] resBytes = null;
                BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                int bufferSize;
                while ((bufferSize = bis.read()) != -1) {
                    bos.write(bufferSize);
                }

                resBytes = bos.toByteArray();
                bis.close();
                bis.close();

                String finalFileName = fileName;
                return new ByteArrayResource(resBytes != null ? resBytes : new byte[0]) {
                    @Override
                    public String getFilename() {
                        return finalFileName;
                    }
                };

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

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


    public Map getFileUrl(String url) {
        Map fileInfo = new HashMap();
        try {
            HttpEntity entity = getEntity(url);
            if (entity != null) {
                Document doc = Jsoup.parseBodyFragment(EntityUtils.toString(entity));
                Elements alinks = doc.select("a");
                for (String aText : alinks.eachText()) {
                    try {
                        String[] fileNames = aText.split("\\.");
                        if (fileNames[fileNames.length - 1].toUpperCase().equals("JAR") || fileNames[fileNames.length - 1].toUpperCase().equals("WAR")) {
                            if (fileNames.length <= 2) {
                                fileInfo.put("URL", url + aText);
                                fileInfo.put("FILE_NAME", aText);
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileInfo;
    }


    public HttpEntity getEntity(String url) throws Exception {
        URI uri = URI.create(url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((ciServerAdminUserName + ":" + ciServerAdminPassword).getBytes("UTF-8")));
        HttpResponse response = client.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            return entity;
        }
        ((CloseableHttpClient) client).close();
        return null;
    }

}
