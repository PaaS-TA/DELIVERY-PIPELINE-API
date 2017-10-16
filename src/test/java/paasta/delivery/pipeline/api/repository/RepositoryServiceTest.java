package paasta.delivery.pipeline.api.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import paasta.delivery.pipeline.api.common.Constants;
import paasta.delivery.pipeline.api.common.RestTemplateService;
import paasta.delivery.pipeline.api.job.CustomJob;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.repository
 *
 * @author REX
 * @version 1.0
 * @since 8 /17/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepositoryServiceTest {

    private static final String SCM_API_URL = "/api/rest/repositories";
    private static final String GIT_HUB_API_URL = "https://api.github.com/repos";
    private static final String SEARCH_SCM_GIT_STRING = "/git/";
    private static final String SEARCH_SCM_SVN_STRING = "/svn/";
    private static final String SEARCH_GIT_HUB_STRING = "https://github.com";
    private static final String SEARCH_BRANCHES_STRING = "/branches";
    private static final String SEARCH_COMMIT_REVISION_STRING = "/changesets?limit=1&branch=";
    private static final Long JOB_ID = 10L;
    private static final String TEST_REPOSITORY_ID = "test-repository-id";
    private static final String TEST_REPOSITORY_ACCOUNT_ID = "test-id";
    private static final String TEST_REPOSITORY_BRANCH_NAME = "test-branch-name";

    private static CustomJob gTestResultJobModel = null;


    @Mock
    private RestTemplateService restTemplateService;

    @InjectMocks
    private RepositoryService repositoryService;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        gTestResultJobModel = new CustomJob();

        gTestResultJobModel.setRepositoryId(TEST_REPOSITORY_ID);
        gTestResultJobModel.setRepositoryAccountId(TEST_REPOSITORY_ACCOUNT_ID);
        gTestResultJobModel.setRepositoryBranch(TEST_REPOSITORY_BRANCH_NAME);
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////// MethodName_StateUnderTest_ExpectedBehavior
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Gets repository info scm git valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getRepositoryInfo_SCM_GIT_ValidModel_ReturnModel() throws Exception {
        LinkedHashMap testResultMap = new LinkedHashMap();
        ArrayList<LinkedHashMap> testTempList = new ArrayList<>();
        LinkedHashMap testTempMap = new LinkedHashMap();

        String testRepositoryUrl = "http://123.123.123.123:8080/scm/git/test-repository";

        gTestResultJobModel.setRepositoryType(String.valueOf(RepositoryService.RepositoryType.SCM_GIT));
        gTestResultJobModel.setRepositoryUrl(testRepositoryUrl);

        testTempMap.put("id", TEST_REPOSITORY_ID);
        testTempList.add(testTempMap);
        testResultMap.put("changesets", testTempList);

        String repositoryUrl = testRepositoryUrl.substring(0, testRepositoryUrl.indexOf(SEARCH_SCM_GIT_STRING));
        String reqRepositoryCommitRevisionUrl = repositoryUrl + SCM_API_URL + "/" + TEST_REPOSITORY_ID + SEARCH_COMMIT_REVISION_STRING;


        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/jobs/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // CUSTOM REST SEND :: GET BRANCH LIST
        when(restTemplateService.customSend(reqRepositoryCommitRevisionUrl + gTestResultJobModel.getRepositoryBranch(), HttpMethod.GET, null, LinkedHashMap.class, gTestResultJobModel)).thenReturn(testResultMap);


        // TEST
        CustomJob resultModel = repositoryService.getRepositoryInfo(String.valueOf(JOB_ID));

        assertThat(resultModel).isNotNull();
        assertEquals(String.valueOf(RepositoryService.RepositoryType.SCM_GIT), resultModel.getRepositoryType());
        assertEquals(testRepositoryUrl, resultModel.getRepositoryUrl());
        assertEquals(TEST_REPOSITORY_ACCOUNT_ID, resultModel.getRepositoryAccountId());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Gets repository info scm svn valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getRepositoryInfo_SCM_SVN_ValidModel_ReturnModel() throws Exception {
        LinkedHashMap testResultMap = new LinkedHashMap();
        ArrayList<LinkedHashMap> testTempList = new ArrayList<>();
        LinkedHashMap testTempMap = new LinkedHashMap();

        String testRepositoryUrl = "http://123.123.123.123:8080/scm/svn/test-repository";

        gTestResultJobModel.setRepositoryType(String.valueOf(RepositoryService.RepositoryType.SCM_SVN));
        gTestResultJobModel.setRepositoryUrl(testRepositoryUrl);

        testTempMap.put("id", TEST_REPOSITORY_ID);
        testTempList.add(testTempMap);
        testResultMap.put("changesets", testTempList);

        String repositoryUrl = testRepositoryUrl.substring(0, testRepositoryUrl.indexOf(SEARCH_SCM_SVN_STRING));
        String reqRepositoryCommitRevisionUrl = repositoryUrl + SCM_API_URL + "/" + TEST_REPOSITORY_ID + SEARCH_COMMIT_REVISION_STRING;


        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/jobs/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // CUSTOM REST SEND :: GET BRANCH LIST
        when(restTemplateService.customSend(reqRepositoryCommitRevisionUrl + gTestResultJobModel.getRepositoryBranch(), HttpMethod.GET, null, LinkedHashMap.class, gTestResultJobModel)).thenReturn(testResultMap);


        // TEST
        CustomJob resultModel = repositoryService.getRepositoryInfo(String.valueOf(JOB_ID));

        assertThat(resultModel).isNotNull();
        assertEquals(String.valueOf(RepositoryService.RepositoryType.SCM_SVN), resultModel.getRepositoryType());
        assertEquals(testRepositoryUrl, resultModel.getRepositoryUrl());
        assertEquals(TEST_REPOSITORY_ACCOUNT_ID, resultModel.getRepositoryAccountId());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Gets repository info git hub valid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getRepositoryInfo_GIT_HUB_ValidModel_ReturnModel() throws Exception {
        LinkedHashMap testResultMap = new LinkedHashMap();
        LinkedHashMap testTempMap = new LinkedHashMap();

        String testRepositoryUrl = "https://github.com/test/test.git";

        gTestResultJobModel.setRepositoryType(String.valueOf(RepositoryService.RepositoryType.GIT_HUB));
        gTestResultJobModel.setRepositoryUrl(testRepositoryUrl);

        testTempMap.put("sha", TEST_REPOSITORY_ID);
        testResultMap.put("commit", testTempMap);

        String repositoryUrl = testRepositoryUrl.substring(SEARCH_GIT_HUB_STRING.length(), testRepositoryUrl.length() - 4);
        String reqRepositoryCommitRevisionUrl = GIT_HUB_API_URL + repositoryUrl + SEARCH_BRANCHES_STRING;


        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/jobs/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // CUSTOM REST SEND :: GET BRANCH LIST
        when(restTemplateService.customSend(reqRepositoryCommitRevisionUrl + "/" + gTestResultJobModel.getRepositoryBranch(), HttpMethod.GET, null, LinkedHashMap.class, gTestResultJobModel)).thenReturn(testResultMap);


        // TEST
        CustomJob resultModel = repositoryService.getRepositoryInfo(String.valueOf(JOB_ID));

        assertThat(resultModel).isNotNull();
        assertEquals(String.valueOf(RepositoryService.RepositoryType.GIT_HUB), resultModel.getRepositoryType());
        assertEquals(testRepositoryUrl, resultModel.getRepositoryUrl());
        assertEquals(TEST_REPOSITORY_ACCOUNT_ID, resultModel.getRepositoryAccountId());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Gets repository info git hub invalid model return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getRepositoryInfo_GIT_HUB_InvalidModel_ReturnModel() throws Exception {
        LinkedHashMap testResultMap = new LinkedHashMap();
        LinkedHashMap testTempMap = new LinkedHashMap();

        String testRepositoryUrl = "https://test.com/test/test.git";

        gTestResultJobModel.setRepositoryType(String.valueOf(RepositoryService.RepositoryType.GIT_HUB));
        gTestResultJobModel.setRepositoryUrl(testRepositoryUrl);

        testTempMap.put("sha", TEST_REPOSITORY_ID);
        testResultMap.put("commit", testTempMap);

        String repositoryUrl = testRepositoryUrl.substring(SEARCH_GIT_HUB_STRING.length(), testRepositoryUrl.length() - 4);
        String reqRepositoryCommitRevisionUrl = GIT_HUB_API_URL + repositoryUrl + SEARCH_BRANCHES_STRING;


        // GET JOB DETAIL FROM DATABASE
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/jobs/" + JOB_ID, HttpMethod.GET, null, CustomJob.class)).thenReturn(gTestResultJobModel);
        // CUSTOM REST SEND :: GET BRANCH LIST
        when(restTemplateService.customSend(reqRepositoryCommitRevisionUrl + "/" + gTestResultJobModel.getRepositoryBranch(), HttpMethod.GET, null, LinkedHashMap.class, gTestResultJobModel)).thenReturn(testResultMap);


        // TEST
        CustomJob resultModel = repositoryService.getRepositoryInfo(String.valueOf(JOB_ID));

        assertThat(resultModel).isNotNull();
        assertEquals(String.valueOf(RepositoryService.RepositoryType.GIT_HUB), resultModel.getRepositoryType());
        assertEquals(testRepositoryUrl, resultModel.getRepositoryUrl());
        assertEquals(TEST_REPOSITORY_ACCOUNT_ID, resultModel.getRepositoryAccountId());
        assertEquals(Constants.RESULT_STATUS_FAIL, resultModel.getResultStatus());
    }

}