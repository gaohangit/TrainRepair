package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

import lombok.Data;

@Data
public class TrainsetBaseInfo {
	/**
	 * 车型平台，例如：CRH1A
	 */
	private String platform;

	/**
	 * 车型，例如CRH1A
	 */
	private String traintype;

	/**
	 * 批次，例如CRH1A(1)
	 */
	private String traintempid;

	/**
	 * 车组ID，例如：CRH1001A
	 */
	private String trainsetid;

	/**
	 * 车组名称，例如：CRH1A-1001
	 */
	private String trainsetname;

}
