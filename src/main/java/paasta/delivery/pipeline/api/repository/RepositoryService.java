package paasta.delivery.pipeline.api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.common.RestTemplateService;
import paasta.delivery.pipeline.api.job.CustomJob;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.repository
 *
 * @author REX
 * @version 1.0
 * @since 8 /2/2017
 */
@Service
public class RepositoryService {

    private static final String SCM_API_URL = "/api/rest/repositories";
    private static final String GIT_HUB_API_URL = "https://api.github.com/repos";
    private static final String SEARCH_SCM_GIT_STRING = "/git/";
    private static final String SEARCH_SCM_SVN_STRING = "/svn/";
    private static final String SEARCH_GIT_HUB_STRING = "https://github.com";
    private static final String SEARCH_BRANCHES_STRING = "/branches";
    private static final String SEARCH_COMMIT_REVISION_STRING = "/changesets?limit=1&branch=";

    private final RestTemplateService restTemplateService;


    /**
     * Instantiates a new CustomJob service.
     *
     * @param restTemplateService the rest template service
     */
    @Autowired
    public RepositoryService(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }


    /**
     * Gets repository info.
     *
     * @param jobId the job id
     * @return the repository info
     */
    public CustomJob getRepositoryInfo(String jobId) {
        CustomJob resultModel = new CustomJob();
        resultModel.setResultStatus(Constants.RESULT_STATUS_FAIL);

        // GET JOB DETAIL FROM DATABASE
        CustomJob customJob = restTemplateService.send(Constants.TARGET_COMMON_API, "/jobs/" + jobId, HttpMethod.GET, null, CustomJob.class);
        String repositoryType = customJob.getRepositoryType();

        // CHECK REPOSITORY TYPE :: GIT_HUB / SCM_GIT / SCM_SVN
        if (String.valueOf(RepositoryType.GIT_HUB).equals(repositoryType)) {
            resultModel = procGetGitHubRepositoryInfo(customJob);
        } else {
            resultModel = procGetScmRepositoryInfo(customJob);
        }

        resultModel.setRepositoryType(repositoryType);
        resultModel.setRepositoryUrl(customJob.getRepositoryUrl());
        resultModel.setRepositoryAccountId(customJob.getRepositoryAccountId());

        return resultModel;
    }


    private CustomJob procGetGitHubRepositoryInfo(CustomJob customJob) {
        CustomJob resultModel = new CustomJob();
        resultModel.setResultStatus(Constants.RESULT_STATUS_FAIL);

        String reqRepositoryUrl = customJob.getRepositoryUrl();
        String repositoryUrl;
        String reqRepositoryBranchListUrl;

        if (reqRepositoryUrl.contains(SEARCH_GIT_HUB_STRING)) {
            repositoryUrl = reqRepositoryUrl.substring(SEARCH_GIT_HUB_STRING.length(), reqRepositoryUrl.length() - 4);
            reqRepositoryBranchListUrl = GIT_HUB_API_URL + repositoryUrl + SEARCH_BRANCHES_STRING;

            // CUSTOM REST SEND :: GET COMMIT REVISION FROM GIT HUB SERVER
            resultModel.setRepositoryCommitRevision(procGetGitHubRepositoryCommitRevision(customJob, reqRepositoryBranchListUrl));
            resultModel.setRepositoryId(SEARCH_GIT_HUB_STRING);
            resultModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);
        }

        return resultModel;
    }


    private String procGetGitHubRepositoryCommitRevision(CustomJob customJob, String reqRepositoryCommitRevisionUrl) {
        String resultString;

        // CUSTOM REST SEND :: GET BRANCH LIST
        LinkedHashMap tempMap = restTemplateService.customSend(reqRepositoryCommitRevisionUrl + "/" + customJob.getRepositoryBranch(), HttpMethod.GET, null, LinkedHashMap.class, customJob);
        Map tempSubMap = (Map) tempMap.get("commit");
        resultString = tempSubMap.get("sha").toString();

        return resultString;
    }


    private CustomJob procGetScmRepositoryInfo(CustomJob customJob) {
        CustomJob resultModel = new CustomJob();
        String repositoryId;

        // GET ID
        repositoryId = customJob.getRepositoryId();

        // CUSTOM REST SEND :: GET COMMIT REVISION
        resultModel.setRepositoryCommitRevision(procGetScmRepositoryCommitRevision(customJob, procGetScmRepositoryUrl(customJob)));
        resultModel.setRepositoryId(repositoryId);
        resultModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        return resultModel;
    }


    private String procGetScmRepositoryUrl(CustomJob customJob) {
        String resultString;
        String reqRepositoryUrl = customJob.getRepositoryUrl();
        String searchString = SEARCH_SCM_GIT_STRING;
        String repositoryId = customJob.getRepositoryId();

        if (String.valueOf(RepositoryType.SCM_SVN).equals(customJob.getRepositoryType())) {
            searchString = SEARCH_SCM_SVN_STRING;
        }

        // SEARCH
        int searchResult = reqRepositoryUrl.indexOf(searchString);
        String repositoryUrl = reqRepositoryUrl.substring(0, searchResult);

        resultString = repositoryUrl + SCM_API_URL + "/" + repositoryId + SEARCH_COMMIT_REVISION_STRING;

        return resultString;
    }


    private String procGetScmRepositoryCommitRevision(CustomJob customJob, String reqRepositoryCommitRevisionUrl) {
        String resultString;
        String repositoryBranch = customJob.getRepositoryBranch();

        // CUSTOM REST SEND :: GET REPOSITORY COMMIT REVISION
        LinkedHashMap tempResultMap = restTemplateService.customSend(reqRepositoryCommitRevisionUrl + repositoryBranch, HttpMethod.GET, null, LinkedHashMap.class, customJob);
        ArrayList<LinkedHashMap> tempList = (ArrayList<LinkedHashMap>) tempResultMap.get("changesets");
        resultString = (String) tempList.get(0).get("id");

        return resultString;
    }


    /**
     * The enum Repository Type.
     */
    enum RepositoryType {
        /**
         * Scm git repository type.
         */
        SCM_GIT,
        /**
         * Scm svn repository type.
         */
        SCM_SVN,
        /**
         * Git hub repository type.
         */
        GIT_HUB;
    }

}
