package paasta.delivery.pipeline.api.job.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import paasta.delivery.pipeline.api.common.ConfigType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * paastaDeliveryPipelineApi
 * paasta.delivery.pipeline.api.jobConfig
 *
 * @author REX
 * @version 1.0
 * @since 6 /27/2017
 */
@Service
public class JobConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobConfigService.class);


    /**
     * Gets config type list.
     *
     * @param configType the config type
     * @return the config type list
     */
    List<ConfigType> getConfigTypeList(String configType) {
        List<ConfigType> resultList = new ArrayList<>();

        // JOB TYPE
        if (String.valueOf(JobConfig.ConfigType.JOB).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.JobType.values());
        }


        // BUILDER TYPE
        if (String.valueOf(JobConfig.ConfigType.BUILDER).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.BuilderType.values());
        }


        // REPOSITORY TYPE
        if (String.valueOf(JobConfig.ConfigType.REPOSITORY).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.RepositoryType.values());
        }


        // DEPLOY TYPE
        if (String.valueOf(JobConfig.ConfigType.DEPLOY).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.DeployType.values());
        }


        // BUILD JOB TRIGGER TYPE
        if (String.valueOf(JobConfig.ConfigType.BUILD_JOB_TRIGGER_TYPE).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.BuildJobTriggerType.values());
        }


        // TEST JOB TRIGGER TYPE
        if (String.valueOf(JobConfig.ConfigType.TEST_JOB_TRIGGER_TYPE).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.TestJobTriggerType.values());
        }


        // DEPLOY JOB TRIGGER TYPE
        if (String.valueOf(JobConfig.ConfigType.DEPLOY_JOB_TRIGGER_TYPE).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.DeployJobTriggerType.values());
        }

        return resultList;
    }


    private List<ConfigType> procConfigTypeList(Object[] enumArray) {
        List<ConfigType> resultList = new ArrayList<>();
        ConfigType tempModel;

        for (Object anEnumArray : enumArray) {
            tempModel = new ConfigType();
            tempModel.setTypeName(String.valueOf(anEnumArray));

            try {
                Method methodGetActualValue = anEnumArray.getClass().getMethod("getActualValue");
                methodGetActualValue.invoke(anEnumArray);
                tempModel.setTypeValue((String) methodGetActualValue.invoke(anEnumArray));
            } catch (NoSuchMethodException e) {
                LOGGER.error("NoSuchMethodException :: {}", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("IllegalAccessException :: {}", e);
            } catch (InvocationTargetException e) {
                LOGGER.error("InvocationTargetException :: {}", e);
            }

            resultList.add(tempModel);
        }

        return resultList;
    }

}
