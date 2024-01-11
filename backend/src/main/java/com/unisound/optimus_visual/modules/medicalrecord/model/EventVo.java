package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

/**
 * 返回时间参数实体
 * 
 * @author ZXC
 *
 */
@Data
public class EventVo {

	/**
	 * 事件名称
	 */
	private Long id;

	private String hospitalId;

	private String scene;

	private String eventName;

	private long totalNum;

	private double eventPercentage;

	private long eventNum;

	private long eventOrderNum;

	private double eventOrderPercentage;

	private long eventProgressNum;

	private double eventProgressPercentage;

	private long eventOperationNum;

	private double eventOperationPercentage;

	private int count;

    // 用来判断指标是否异常 true为异常
    private boolean eventFlag = false;
    private boolean orderFlag = false;
    private boolean progressFlag = false;
    private boolean operationFlag = false;

	public void percentCalculate() {
		if (eventNum != 0) {
			eventOrderPercentage = formatNum(eventOrderNum * 1.0 / eventNum);
			eventProgressPercentage = formatNum(eventProgressNum * 1.0 / eventNum);
			eventOperationPercentage = formatNum(eventOperationNum * 1.0 / eventNum);
			eventPercentage = formatNum(eventNum * 1.0 / totalNum);
		}
	}

	private double formatNum(double num) {
		double result = Math.round(num * 10000) * 1.0 / 10000;
		return result;
		// String str = String.format("%.5f", num);
		// double result = Double.parseDouble(str);
		// return result;
	}
}
