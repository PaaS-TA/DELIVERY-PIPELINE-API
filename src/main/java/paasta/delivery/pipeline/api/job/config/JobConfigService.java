package paasta.delivery.pipeline.api.job.config;

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

    /**
     * Gets config type list.
     *
     * @param configType the config type
     * @return the config type list
     * @throws NoSuchMethodException     the no such method exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     */
    List<ConfigType> getConfigTypeList(String configType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<ConfigType> resultList = new ArrayList<>();

        // JOB TYPE
        if (String.valueOf(JobConfig.ConfigType.JOB).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.JobType.values());
        }

        // LANGUAGE TYPE
        if (String.valueOf(JobConfig.ConfigType.LANGUAGE).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.LanguageType.values());
        }

        //LANGUAGE TYPE VERSION
        if (String.valueOf(JobConfig.ConfigType.LANGUAGE_VERSION).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.LanguageTypeVersion.values());
        }

        // BUILDER TYPE
        if (String.valueOf(JobConfig.ConfigType.BUILDER).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.BuilderType.values());
        }

        //BUILDER TYPE VERSION
        if (String.valueOf(JobConfig.ConfigType.BUILDER_VERSION).equals(configType)) {
            resultList = procConfigTypeList(JobConfig.BuilderTypeVersion.values());
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


    private List<ConfigType> procConfigTypeList(Object[] enumArray) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<ConfigType> resultList = new ArrayList<>();
        ConfigType tempModel;

        for (Object anEnumArray : enumArray) {
            tempModel = new ConfigType();
            tempModel.setTypeName(String.valueOf(anEnumArray));

            Method methodGetActualValue = anEnumArray.getClass().getMethod("getActualValue");
            methodGetActualValue.invoke(anEnumArray);
            tempModel.setTypeValue((String) methodGetActualValue.invoke(anEnumArray));

            resultList.add(tempModel);
        }

        return resultList;
    }

}
