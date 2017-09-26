package paasta.delivery.pipeline.api.job.config;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import paasta.delivery.pipeline.api.common.ConfigType;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.job
 *
 * @author REX
 * @version 1.0
 * @since 5 /24/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobConfigServiceTest {

    @InjectMocks
    private JobConfigService jobConfigService;


    /**
     * Sets up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
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
     * Gets config ConfigType list valid param return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getConfigTypeList_ValidParam_JOB_ReturnModel() throws Exception {
        String configType = String.valueOf(JobConfig.ConfigType.JOB);

        List<ConfigType> resultList = jobConfigService.getConfigTypeList(configType);

        assertThat(resultList).isNotNull();
        assertEquals(resultList.get(0).getTypeName(), String.valueOf(JobConfig.JobType.BUILD));
        assertEquals(resultList.get(0).getTypeValue(), JobConfig.JobType.BUILD.getActualValue());
    }


    /**
     * Gets config type list valid param builder return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getConfigTypeList_ValidParam_BUILDER_ReturnModel() throws Exception {
        String configType = String.valueOf(JobConfig.ConfigType.BUILDER);

        List<ConfigType> resultList = jobConfigService.getConfigTypeList(configType);

        assertThat(resultList).isNotNull();
        assertEquals(resultList.get(0).getTypeName(), String.valueOf(JobConfig.BuilderType.GRADLE));
        assertEquals(resultList.get(0).getTypeValue(), JobConfig.BuilderType.GRADLE.getActualValue());
    }


    /**
     * Gets config type list valid param repository return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getConfigTypeList_ValidParam_REPOSITORY_ReturnModel() throws Exception {
        String configType = String.valueOf(JobConfig.ConfigType.REPOSITORY);

        List<ConfigType> resultList = jobConfigService.getConfigTypeList(configType);

        assertThat(resultList).isNotNull();
        assertEquals(resultList.get(0).getTypeName(), String.valueOf(JobConfig.RepositoryType.GIT_HUB));
        assertEquals(resultList.get(0).getTypeValue(), JobConfig.RepositoryType.GIT_HUB.getActualValue());
    }


    /**
     * Gets config type list valid param deploy return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getConfigTypeList_ValidParam_DEPLOY_ReturnModel() throws Exception {
        String configType = String.valueOf(JobConfig.ConfigType.DEPLOY);

        List<ConfigType> resultList = jobConfigService.getConfigTypeList(configType);

        assertThat(resultList).isNotNull();
        assertEquals(resultList.get(0).getTypeName(), String.valueOf(JobConfig.DeployType.DEV));
        assertEquals(resultList.get(0).getTypeValue(), JobConfig.DeployType.DEV.getActualValue());
    }


    /**
     * Gets config type list valid param build job trigger type return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getConfigTypeList_ValidParam_BUILD_JOB_TRIGGER_TYPE_ReturnModel() throws Exception {
        String configType = String.valueOf(JobConfig.ConfigType.BUILD_JOB_TRIGGER_TYPE);

        List<ConfigType> resultList = jobConfigService.getConfigTypeList(configType);

        assertThat(resultList).isNotNull();
        assertEquals(resultList.get(0).getTypeName(), String.valueOf(JobConfig.BuildJobTriggerType.PREVIOUS_JOB_SUCCESS));
        assertEquals(resultList.get(0).getTypeValue(), JobConfig.BuildJobTriggerType.PREVIOUS_JOB_SUCCESS.getActualValue());
    }


    /**
     * Gets config type list valid param test job trigger type return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getConfigTypeList_ValidParam_TEST_JOB_TRIGGER_TYPE_ReturnModel() throws Exception {
        String configType = String.valueOf(JobConfig.ConfigType.TEST_JOB_TRIGGER_TYPE);

        List<ConfigType> resultList = jobConfigService.getConfigTypeList(configType);

        assertThat(resultList).isNotNull();
        assertEquals(resultList.get(0).getTypeName(), String.valueOf(JobConfig.TestJobTriggerType.PREVIOUS_JOB_SUCCESS));
        assertEquals(resultList.get(0).getTypeValue(), JobConfig.TestJobTriggerType.PREVIOUS_JOB_SUCCESS.getActualValue());
    }


    /**
     * Gets config type list valid param deploy job trigger type return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void getConfigTypeList_ValidParam_DEPLOY_JOB_TRIGGER_TYPE_ReturnModel() throws Exception {
        String configType = String.valueOf(JobConfig.ConfigType.DEPLOY_JOB_TRIGGER_TYPE);

        List<ConfigType> resultList = jobConfigService.getConfigTypeList(configType);

        assertThat(resultList).isNotNull();
        assertEquals(resultList.get(0).getTypeName(), String.valueOf(JobConfig.TestJobTriggerType.PREVIOUS_JOB_SUCCESS));
        assertEquals(resultList.get(0).getTypeValue(), JobConfig.TestJobTriggerType.PREVIOUS_JOB_SUCCESS.getActualValue());
    }

}
