package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

/**
 * @author 张天可
 * @since 2021/6/25
 */
public interface ITemplateLinkContentBase {
    String getId();

    String getContentId();

    String getLinkContentId();

    void setId(String id);

    void setContentId(String contentId);

    void setLinkContentId(String linkContentId);

}
