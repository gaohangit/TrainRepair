package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillLinkCellInfoGeneral;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 单据回填内容关联内容表
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ChkDetailLinkContentForModule implements IBillLinkCellInfoGeneral {

    /**
     *
     */
    private String linkCellId;
}
