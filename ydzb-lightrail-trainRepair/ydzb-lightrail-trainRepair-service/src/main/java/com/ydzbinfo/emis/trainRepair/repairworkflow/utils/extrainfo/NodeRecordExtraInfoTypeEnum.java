package com.ydzbinfo.emis.trainRepair.repairworkflow.utils.extrainfo;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.NodeRecordInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecordExtraInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.FlowUtil;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfoWithPayload;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节点记录额外信息类型枚举
 *
 * @author 张天可
 * @since 2021/7/6
 */
@Getter
public enum NodeRecordExtraInfoTypeEnum {
    PICTURE_URL("PICTURE_URL", "刷卡图片地址", PictureUrl.class),
    VIDEO_URL("VIDEO_URL", "刷卡视频地址", VideoUrl.class),
    SKIP("SKIP", "标记刷卡记录为跳过类型的记录", Skip.class),
    DISPOSE_AFTER_SKIP("DISPOSE_AFTER_SKIP", "标记刷卡记录为补卡类型的记录", DisposeAfterSkip.class);

    private final String name;
    private final String description;
    private final INodeRecordExtraInfoConvertor<?> convertor;

    NodeRecordExtraInfoTypeEnum(String name, String description, Class<? extends INodeRecordExtraInfoConvertor<?>> convertorClass) {
        this.name = name;
        this.description = description;
        try {
            this.convertor = convertorClass.getDeclaredConstructor(NodeRecordExtraInfoTypeEnum.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw RestRequestException.serviceInnerFatalFail("NodeExtraInfoTypeEnum初始化失败", e);
        }
    }

    public abstract static class NodeRecordExtraInfoConvertorBase<T> extends ExtraInfoConvertorBase<T, NodeRecordInfo, NodeRecordExtraInfo> implements INodeRecordExtraInfoConvertor<T> {
        // 加载类时需要初始化依赖的枚举类，防止出现空指针异常
        static {
            EnumUtils.staticInitializeEnum(NodeRecordExtraInfoTypeEnum.class);
        }

        @Override
        protected NodeRecordExtraInfo newExtraInfoModel() {
            return new NodeRecordExtraInfo();
        }
    }

    public static class VideoUrl extends NodeRecordExtraInfoConvertorBase<List<UploadedFileInfoWithPayload<UploadFileType>>> {

        private static NodeRecordExtraInfoTypeEnum enumValue;

        VideoUrl(NodeRecordExtraInfoTypeEnum enumValue) {
            VideoUrl.enumValue = enumValue;
        }

        private static NodeRecordExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static VideoUrl getInstance() {
            return (VideoUrl) getEnum().getConvertor();
        }

        @Override
        public List<UploadedFileInfoWithPayload<UploadFileType>> transformToValue(List<NodeRecordExtraInfo> nodeRecordExtraInfos) {
            List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoWithPayloads = transToValueByTypeName(
                nodeRecordExtraInfos,
                getEnum().getName(),
                values -> Arrays.stream(values).map(
                    value -> {
                        UploadedFileInfoWithPayload<UploadFileType> roleBase = new UploadedFileInfoWithPayload<UploadFileType>();
                        String[] role = value.split("[,]");
                        roleBase.setRelativeUrl(role[0]);
                        roleBase.setRelativePath(role[1]);
                        roleBase.setPayload(UploadFileType.VIDEO);
                        if (role.length > 3) {
                            roleBase.setIndexOfList(Integer.parseInt(role[3]));
                        }
                        return roleBase;
                    }
                ).collect(Collectors.toList())
            );
            if (uploadedFileInfoWithPayloads.stream().noneMatch(v -> v.getIndexOfList() == null)) {
                uploadedFileInfoWithPayloads.sort(Comparator.comparing(UploadedFileInfoWithPayload<UploadFileType>::getIndexOfList));
            }
            return uploadedFileInfoWithPayloads;
        }

        @Override
        @Deprecated
        public List<NodeRecordExtraInfo> transformToExtraInfoList(List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoList) {
            List<String> values = getValues(uploadedFileInfoList);
            return transToExtraInfoListByTypeName(getEnum().getName(), values.toArray(new String[0]));
        }

        public List<NodeRecordExtraInfo> getExtraInfoListFromUploadInfo(List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoList) {
            int sort = 0;
            for (UploadedFileInfoWithPayload<UploadFileType> uploadFileTypeUploadedFileInfoWithPayload : uploadedFileInfoList) {
                uploadFileTypeUploadedFileInfoWithPayload.setIndexOfList(sort);
                sort++;
            }
            return transToExtraInfoListByTypeName(
                getEnum().getName(),
                uploadedFileInfoList.stream().filter(
                    v -> v.getPayload().equals(UploadFileType.VIDEO)
                ).map(
                    v -> v.getRelativeUrl() + "," + v.getRelativePath() + "," + UploadFileType.VIDEO + "," + v.getIndexOfList()
                ).toArray(String[]::new)
            );
        }

        @Override
        public List<UploadedFileInfoWithPayload<UploadFileType>> getValue(NodeRecordInfo nodeInfo) {
            return nodeInfo.getVideoUrls();
        }

        @Override
        public void setValue(NodeRecordInfo nodeInfo, List<UploadedFileInfoWithPayload<UploadFileType>> value) {
            nodeInfo.setVideoUrls(value);
        }
    }

    public static class PictureUrl extends NodeRecordExtraInfoConvertorBase<List<UploadedFileInfoWithPayload<UploadFileType>>> {

        private static NodeRecordExtraInfoTypeEnum enumValue;

        PictureUrl(NodeRecordExtraInfoTypeEnum enumValue) {
            PictureUrl.enumValue = enumValue;
        }

        private static NodeRecordExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static PictureUrl getInstance() {
            return (PictureUrl) getEnum().getConvertor();
        }

        @Override
        public List<UploadedFileInfoWithPayload<UploadFileType>> transformToValue(List<NodeRecordExtraInfo> nodeRecordExtraInfos) {
            List<UploadedFileInfoWithPayload<UploadFileType>> uploadFiles = transToValueByTypeName(
                nodeRecordExtraInfos,
                getEnum().getName(),
                values -> Arrays.stream(values).map(
                    value -> {
                        UploadedFileInfoWithPayload<UploadFileType> roleBase = new UploadedFileInfoWithPayload<>();
                        String[] role = value.split("[,]");
                        roleBase.setRelativeUrl(role[0]);
                        roleBase.setRelativePath(role[1]);
                        roleBase.setPayload(UploadFileType.IMAGE);
                        if (role.length > 3) {
                            roleBase.setIndexOfList(Integer.parseInt(role[3]));
                        } else {
                            roleBase.setIndexOfList(-1);
                        }
                        return roleBase;
                    }
                ).collect(Collectors.toList())
            );
            uploadFiles.sort(Comparator.comparing(UploadedFileInfoWithPayload<UploadFileType>::getIndexOfList));
            return uploadFiles;
        }

        @Override
        @Deprecated
        public List<NodeRecordExtraInfo> transformToExtraInfoList(List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoList) {
            List<String> values = getValues(uploadedFileInfoList);
            return transToExtraInfoListByTypeName(getEnum().getName(), values.toArray(new String[0]));
        }


        public List<NodeRecordExtraInfo> getExtraInfoListFromUploadInfo(List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoList) {
            int sort = 0;
            for (UploadedFileInfoWithPayload<UploadFileType> uploadFileTypeUploadedFileInfoWithPayload : uploadedFileInfoList) {
                uploadFileTypeUploadedFileInfoWithPayload.setIndexOfList(sort);
                sort++;
            }
            return transToExtraInfoListByTypeName(
                getEnum().getName(),
                uploadedFileInfoList.stream().filter(
                    v -> v.getPayload().equals(UploadFileType.IMAGE)
                ).map(
                    v -> v.getRelativeUrl() + "," + v.getRelativePath() + "," + UploadFileType.IMAGE + "," + v.getIndexOfList()
                ).toArray(String[]::new)
            );
        }

        @Override
        public List<UploadedFileInfoWithPayload<UploadFileType>> getValue(NodeRecordInfo nodeInfo) {
            return nodeInfo.getPictureUrls();
        }

        @Override
        public void setValue(NodeRecordInfo nodeInfo, List<UploadedFileInfoWithPayload<UploadFileType>> value) {
            nodeInfo.setPictureUrls(value);
        }
    }

    /**
     * 上传文件信息转换为数据库存储值列表
     */
    private static List<String> getValues(List<UploadedFileInfoWithPayload<UploadFileType>> uploadedFileInfoList) {
        List<String> values = new ArrayList<>();
        if (uploadedFileInfoList != null) {
            for (int i = 0; i < uploadedFileInfoList.size(); i++) {
                UploadedFileInfoWithPayload<UploadFileType> uploadedFileInfo = uploadedFileInfoList.get(i);
                values.add(uploadedFileInfo.getRelativeUrl() + "," + uploadedFileInfo.getRelativePath() + "," + i);
            }
        }
        return values;
    }

    public static class Skip extends NodeRecordExtraInfoConvertorBase<Boolean> {

        private static NodeRecordExtraInfoTypeEnum enumValue;

        Skip(NodeRecordExtraInfoTypeEnum enumValue) {
            Skip.enumValue = enumValue;
        }

        private static NodeRecordExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static Skip getInstance() {
            return (Skip) getEnum().getConvertor();
        }

        @Override
        public Boolean transformToValue(List<NodeRecordExtraInfo> nodeRecordExtraInfos) {
            return transToValueByTypeName(
                nodeRecordExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToBoolean(v[0], false)
            );
        }

        @Override
        public List<NodeRecordExtraInfo> transformToExtraInfoList(Boolean value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.booleanToString(value));
        }

        @Override
        public Boolean getValue(NodeRecordInfo nodeInfo) {
            return nodeInfo.getSkip();
        }

        @Override
        public void setValue(NodeRecordInfo nodeInfo, Boolean value) {
            nodeInfo.setSkip(value);
        }
    }

    public static class DisposeAfterSkip extends NodeRecordExtraInfoConvertorBase<Boolean> {

        private static NodeRecordExtraInfoTypeEnum enumValue;

        DisposeAfterSkip(NodeRecordExtraInfoTypeEnum enumValue) {
            DisposeAfterSkip.enumValue = enumValue;
        }

        private static NodeRecordExtraInfoTypeEnum getEnum() {
            return enumValue;
        }

        public static DisposeAfterSkip getInstance() {
            return (DisposeAfterSkip) getEnum().getConvertor();
        }

        @Override
        public Boolean transformToValue(List<NodeRecordExtraInfo> nodeRecordExtraInfos) {
            return transToValueByTypeName(
                nodeRecordExtraInfos,
                getEnum().getName(),
                v -> FlowUtil.stringToBoolean(v[0], false)
            );
        }

        @Override
        public List<NodeRecordExtraInfo> transformToExtraInfoList(Boolean value) {
            return transToExtraInfoListByTypeName(getEnum().getName(), FlowUtil.booleanToString(value));
        }

        @Override
        public Boolean getValue(NodeRecordInfo nodeInfo) {
            return nodeInfo.getDisposeAfterSkip();
        }

        @Override
        public void setValue(NodeRecordInfo nodeInfo, Boolean value) {
            nodeInfo.setDisposeAfterSkip(value);
        }
    }

}
