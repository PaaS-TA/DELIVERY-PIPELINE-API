package paasta.delivery.pipeline.api.job;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import paasta.delivery.pipeline.api.common.Constants;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.job
 *
 * @author REX
 * @version 1.0
 * @since 6 /28/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobBuiltFileServiceTest {

    @MockBean
    private JobBuiltFileService jobBuiltFileService;


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
     * Sets built file valid param return model.
     *
     * @throws Exception the exception
     */
    @Test
    public void setBuiltFile_ValidParam_ReturnModel() throws Exception {
        // TODO MODIFY
        CustomJob testModel = new CustomJob();
        CustomJob testResultModel = new CustomJob();

        testResultModel.setResultStatus(Constants.RESULT_STATUS_SUCCESS);

        when(jobBuiltFileService.setBuiltFile(testModel)).thenReturn(testResultModel);

        CustomJob resultModel = jobBuiltFileService.setBuiltFile(testModel);

        assertThat(resultModel).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultModel.getResultStatus());
    }


    /**
     * Delete workspace.
     *
     * @throws Exception the exception
     */
    @Test
    public void deleteWorkspace() throws Exception {
        // TODO MODIFY
        CustomJob testModel = new CustomJob();

        doNothing().when(jobBuiltFileService).deleteWorkspace(testModel);

        jobBuiltFileService.deleteWorkspace(testModel);
    }

}
