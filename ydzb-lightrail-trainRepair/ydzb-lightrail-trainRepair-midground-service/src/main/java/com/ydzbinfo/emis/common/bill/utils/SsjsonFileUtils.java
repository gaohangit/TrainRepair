package com.ydzbinfo.emis.common.bill.utils;

import com.ydzbinfo.emis.common.general.service.ICommonService;
import com.ydzbinfo.emis.configs.TrainRepairProperties;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData;
import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateForSave;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplate;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.ITemplateProcess;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.ITemplateSummary;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

/**
 * 记录单ssjson文件读写工具类
 */
@Component
public class SsjsonFileUtils {

    protected static final Logger logger = LoggerFactory.getLogger(SsjsonFileUtils.class);

    private static ICommonService commonService;

    private static SsjsonWriter ssjsonWriter;

    private static TrainRepairProperties trainRepairProperties;

    @Component
    public static class SsjsonWriter {

        @StreamCloudTransData(
            module = SpringCloudStreamModuleEnum.BILL_CONFIG,
            outputChannel = BillConfigMqSource.SSJSONFILE_OUTPUT,
            inputChannel = BillConfigMqSink.SSJSONFILE_INPUT
        )
        public void writeSsjson(String templatePath, String ssjsonContent) {
            BufferedWriter writer = null;
            try {
                File targetFile = new File(templatePath);
                //noinspection ResultOfMethodCallIgnored
                targetFile.getParentFile().mkdirs();
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile, false), BILL_TEMPLATE_SSJSON_FILE_CHARSET));
                writer.write(ssjsonContent);
            } catch (Exception ex) {
                throw new RuntimeException("保存ssjson失败", ex);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception ex) {
                    logger.error("保存ssjson时，关闭写入流失败", ex);
                }
            }
        }
    }

    SsjsonFileUtils(
        ICommonService commonService,
        SsjsonWriter ssjsonWriter,
        TrainRepairProperties trainRepairProperties
    ) {
        SsjsonFileUtils.commonService = commonService;
        SsjsonFileUtils.ssjsonWriter = ssjsonWriter;
        SsjsonFileUtils.trainRepairProperties = trainRepairProperties;
    }

    /**
     * 保存 文件对象 保存 过程的文件，后缀ssjson
     */
    public static String saveTemplateSsjsonFile(ITemplateForSave templateForSave) {
        return saveTemplateSsjsonFile(templateForSave, templateForSave.getSsjsonFile());
    }

    public static String saveTemplateSsjsonFile(ITemplateBase template, String ssjson) {
        // 获取数据库配置的根目
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("10");
        configParamsModel.setName("TemplatePath");
        List<XzyCConfig> list = commonService.getXzyCConfigs(configParamsModel);
        if (list.size() == 0) {
            throw new RuntimeException("系统未配置记录单存储路径");
        }
        String rootPath;
        if (StringUtils.isNotBlank(trainRepairProperties.getDevBillFilePath())) {
            rootPath = trainRepairProperties.getDevBillFilePath();
        } else {
            rootPath = list.get(0).getParamValue();
        }
        // 根目录
        String filePath;
        if (template instanceof ITemplateSummary) {
            // 目录格式【系统虚拟目录/CELL/TEMPLATE/模板类型/版本号/】；文件名生成规则【运用所_批次_车组_模板名称_项目名称_编组模式_版本号】；
            ITemplateSummary templateSummary = (ITemplateSummary) template;
            filePath = rootPath + "CELL" + File.separator + "TEMPLATE" + File.separator + templateSummary.getTemplateTypeCode() + File.separator + templateSummary.getVersion() + File.separator;
        } else if (template instanceof ITemplateProcess) {
            // 目录格式【系统虚拟目录/CELL/TEMPLATEPROC/模板类型/】；文件名生成规则【运用所_批次_车组_模板名称_项目名称_编组模式】；
            ITemplateProcess templateProcess = (ITemplateProcess) template;
            filePath = rootPath + "CELL" + File.separator + "TEMPLATEPROC" + File.separator + templateProcess.getTemplateTypeCode() + File.separator;
        } else if (template instanceof BaseTemplate) {
            // 目录格式【系统虚拟目录/CELL/BASETEMPLATE/模板类型/】；文件名生成规则【运用所_批次_车组_模板名称_项目名称_编组模式】；
            BaseTemplate baseTemplate = (BaseTemplate) template;
            filePath = rootPath + "CELL" + File.separator + "BASETEMPLATE" + File.separator + baseTemplate.getTemplateTypeCode() + File.separator;
        } else {
            throw new RuntimeException("意外的记录单对象类");
        }
        // 文件路径
        String fileName = BillUtil.generateTemplateName(template);
        File file = new File(filePath);
        File targetFile = new File(file, fileName + ".ssjson");
        String templatePath = targetFile.getPath();
        ssjsonWriter.writeSsjson(templatePath, ssjson);
        return templatePath;
    }

    public static final String BILL_TEMPLATE_SSJSON_FILE_CHARSET = "UTF-8";

    /**
     * 获取ssJsonFile 文件对象的内容
     */
    public static String getSsjsonFileContent(String templatePath) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(templatePath));
            return IOUtils.toString(inputStream, BILL_TEMPLATE_SSJSON_FILE_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException("读取ssjson文件失败", e);
        }
    }


}
