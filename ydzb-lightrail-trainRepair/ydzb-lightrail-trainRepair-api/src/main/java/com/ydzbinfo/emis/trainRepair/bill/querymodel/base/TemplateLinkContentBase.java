package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-06-17
 */
@Data
public class TemplateLinkContentBase implements ITemplateLinkContentBase {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 父表(单据内容表)主键
     */
    @TableField("S_CONTENTID")
    private String contentId;

    /**
     * 关联内容对应的单据内容表主键
     */
    @TableField("S_LINKCONTENTID")
    private String linkContentId;

}
