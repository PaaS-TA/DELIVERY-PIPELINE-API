package paasta.delivery.pipeline.api.job.config;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * deliveryPipelineApi
 * paasta.delivery.pipeline.api.job.config
 *
 * @author REX
 * @version 1.0
 * @since 11 /16/2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobConfigTest {
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////// MethodName_StateUnderTest_ExpectedBehavior
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Constructor valid model.
     *
     * @throws Exception the exception
     */
    @Test
    public void constructor_ValidModel() throws Exception {
        JobConfig jobConfig = new JobConfig();

        assertThat(jobConfig).isNotNull();
    }

}
