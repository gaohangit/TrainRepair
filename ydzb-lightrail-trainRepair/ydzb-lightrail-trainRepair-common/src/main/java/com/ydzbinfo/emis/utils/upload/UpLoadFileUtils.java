package com.ydzbinfo.emis.utils.upload;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.enums.UploadFileType;
import com.ydzbinfo.emis.utils.Md5CaculateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 上传文件工具类
 *
 * @author 张天可
 */
@Component
public class UpLoadFileUtils {

    private static UpLoadFileUtils instance = null;

    protected static final Logger logger = LoggerFactory.getLogger(UpLoadFileUtils.class);

    UpLoadFileUtils(UploadProperties uploadProperties) {
        if (UpLoadFileUtils.instance == null && uploadProperties.getUploadFilePath() != null) {
            File uploadFilePathFile = new File(uploadProperties.getUploadFilePath());
            if (!uploadFilePathFile.exists()) {
                if (!(uploadFilePathFile.mkdirs() && uploadFilePathFile.exists())) {
                    throw new RuntimeException("文件上传工具初始化失败，请检查yml配置: ydzb.upload-file-path");
                }
            }
            UpLoadFileUtils.uploadFilePath = uploadProperties.getUploadFilePath();
            UpLoadFileUtils.uploadFileHandlerPath = uploadProperties.getUploadFileHandlerPath();
            UpLoadFileUtils.instance = this;
            logger.debug("文件上传工具初始化完毕");
        }
    }

    private static String uploadFilePath;

    private static String uploadFileHandlerPath;

    public static String getUploadFileRealPath() {
        if (uploadFilePath == null) {
            throw new RuntimeException("未定义ydzb.upload-file-path(上传文件存储路径)配置");
        }
        return uploadFilePath;
    }

    public static List<UploadedFileInfo> uploadImages(HttpServletRequest request, String dir) {
        return uploadFiles(request, file -> dir, UpLoadFileUtils::isImage);
    }

    public static List<UploadedFileInfo> uploadImages(HttpServletRequest request,
                                                      Function<MultipartFile, String> setDirFunction) {
        return uploadFiles(request, setDirFunction, UpLoadFileUtils::isImage);
    }

    /**
     * 目前request必须从controller获取
     *
     * @param request
     * @param dir
     * @return
     * @date 2019年8月1日 上午10:38:17
     */
    public static List<UploadedFileInfo> uploadFiles(HttpServletRequest request, String dir) {
        return uploadFiles(request, file -> dir, file -> true);
    }

    public static List<UploadedFileInfo> uploadFilesInParams(HttpServletRequest request, String dir,
                                                             String[] paramNames) {
        return uploadFilesInParams(request, file -> dir, paramNames);
    }

    public static List<UploadedFileInfo> uploadFiles(HttpServletRequest request,
                                                     Function<MultipartFile, String> setDirFunction) {
        return uploadFiles(request, setDirFunction, file -> true);
    }

    /**
     * 目前request必须从controller获取
     *
     * @param request
     * @param setDirFunction
     * @date 2019年8月1日 上午10:37:25
     */
    public static List<UploadedFileInfo> uploadFilesInParams(HttpServletRequest request,
                                                             Function<MultipartFile, String> setDirFunction, String[] paramNames) {
        List<String> paramNameList = Arrays.asList(paramNames);
        return uploadFiles(request, setDirFunction, mFile -> {
            return paramNameList.contains(mFile.getName());
        });
    }

    public static List<UploadedFileInfo> uploadFiles(HttpServletRequest request, Function<MultipartFile, String> setDirFunction, Predicate<MultipartFile> filter) {
        return uploadFilesGeneral(v -> new UploadedFileInfo(), request, setDirFunction, filter);
    }

    public static <PAYLOAD> List<UploadedFileInfoWithPayload<PAYLOAD>> uploadFilesUsePayload(HttpServletRequest request, Function<MultipartFile, String> setDirFunction, Predicate<MultipartFile> filter, Function<MultipartFile, PAYLOAD> getPayload) {
        return uploadFilesGeneral(multipartFile -> {
            UploadedFileInfoWithPayload<PAYLOAD> uploadedFileInfoWithPayload = new UploadedFileInfoWithPayload<>();
            uploadedFileInfoWithPayload.setPayload(getPayload.apply(multipartFile));
            return uploadedFileInfoWithPayload;
        }, request, setDirFunction, filter);
    }

    public static <T extends UploadedFileInfo> List<T> uploadFilesGeneral(Function<MultipartFile, T> initUploadedFileInfo, HttpServletRequest request, Function<MultipartFile, String> setDirFunction, Predicate<MultipartFile> filter) {
        List<T> uploadFiles = new ArrayList<>();
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            // 获取文件名集合放入迭代器
            Iterator<String> fileParamNameIt = mRequest.getFileNames();
            while (fileParamNameIt.hasNext()) {
                String fileParamName = fileParamNameIt.next();
                List<MultipartFile> fileList = mRequest.getFiles(fileParamName);
                fileList.forEach((file) -> {
                    if (filter != null && !filter.test(file)) {
                        return;
                    }
                    T uploadedFileInfo = initUploadedFileInfo.apply(file);
                    uploadedFileInfo.setParamName(fileParamName);
                    if (fileList.size() > 1) {
                        uploadedFileInfo.setIsOfList(true);
                        uploadedFileInfo.setIndexOfList(fileList.indexOf(file));
                    } else {
                        uploadedFileInfo.setIsOfList(false);
                    }
                    String oldName = file.getOriginalFilename();
                    uploadedFileInfo.setOldName(oldName);
                    String dir = setDirFunction.apply(file);
                    String relativePath = uploadUniqueFile(file, dir);
                    uploadedFileInfo.setRelativeUrl((uploadFileHandlerPath + "/" + relativePath).replaceAll("[/]+", "/"));
                    uploadedFileInfo.setRelativePath(uploadFilePath.substring(0, uploadFilePath.length() - 1) + (relativePath).replaceAll("[/]+", "\\\\"));
                    uploadFiles.add(uploadedFileInfo);
                });
            }
        }
        return uploadFiles;
    }
    public static UploadedFileInfoWithPayload<UploadFileType> UploadMultipartFile(MultipartFile file, String dir){
        UploadedFileInfoWithPayload<UploadFileType> uploadedFileInfo = new UploadedFileInfoWithPayload<>();
        uploadedFileInfo.setIsOfList(false);
        String oldName = file.getOriginalFilename();
        uploadedFileInfo.setOldName(oldName);
        String relativePath = uploadUniqueFile(file, dir);
        uploadedFileInfo.setRelativeUrl((uploadFileHandlerPath + "/" + relativePath).replaceAll("[/]+", "/"));
        uploadedFileInfo.setRelativePath(uploadFilePath.substring(0, uploadFilePath.length() - 1) + (relativePath).replaceAll("[/]+", "\\\\"));
        return uploadedFileInfo;
    }
    /**
     * 上传文件（重复文件不会重复上传）
     *
     * @return relativePath 实际存储相对路径
     * @author 张天可
     * @date 2021/5/18 11:09
     */
    public static String uploadUniqueFile(MultipartFile mFile, String dir) {
        try {
            String realPath = getUploadFileRealPath();
            InputStream inputStream = mFile.getInputStream();
            String fileName = mFile.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String md5Name = Md5CaculateUtil.getMD5(inputStream);
            String relativePath = dir + "\\" + md5Name + suffix;
            String realFullPath = realPath + "\\" + relativePath;
            if (realFullPath.contains(",")) {
                throw new RuntimeException("上传文件出错！路径名中禁止出现半角逗号。");
            }
            // System.out.println(realFullPath);
            // System.out.println(mFile.getContentType());
            File fileObj = new File(realFullPath);
            File fileParentObj = fileObj.getParentFile();
            if (!fileObj.exists()) {
                if (!fileParentObj.exists()) {
                    fileParentObj.mkdirs();
                }
                mFile.transferTo(fileObj);
            }
            return relativePath.replaceAll("\\\\", "/").replaceAll("[/]+", "/");
        } catch (IOException e) {
            logger.error("上传文件出错！", e);
            throw new RuntimeException("上传文件出错！", e);
        }
    }

    public static boolean isImage(MultipartFile mFile) {
        return Objects.requireNonNull(mFile.getContentType()).contains("image") || isImageType(getFileType(mFile));
    }

    public static boolean isVideo(MultipartFile mFile) {
        return Objects.requireNonNull(mFile.getContentType()).contains("video") || isVideoType(getFileType(mFile));
    }

    private static final String[] videoTypes = {"mp4"};

    private static final String[] audioTypes = {"mp3"};

    public static boolean isAudioType(String fileType) {
        return Stream.of(audioTypes).anyMatch(imageTypeName -> {
            return imageTypeName.equalsIgnoreCase(fileType);
        });
    }

    public static boolean isVideoType(String fileType) {
        return Stream.of(videoTypes).anyMatch(imageTypeName -> {
            return imageTypeName.equalsIgnoreCase(fileType);
        });
    }

    private static final String[] imageTypes = {"gif", "png", "jpg", "jpeg", "bmp"};

    public static boolean isImageType(String typeName) {
        return Stream.of(imageTypes).anyMatch(imageTypeName -> {
            return imageTypeName.equalsIgnoreCase(typeName);
        });
    }

    public static String getFileType(MultipartFile mFile) {
        String type = null;
        String fileName = mFile.getOriginalFilename();
        int index = fileName.indexOf(".");
        if (index != -1) {
            type = fileName.substring(index + 1);
        }
        return type;
    }
}
