package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

@Data
public class MedicalVo {
	private String eventName;
    private String admissionId;
	private boolean haveOrder;
	private boolean haveProgress;
	private boolean haveOperation;
	private String date;


	public MedicalVo() {

	}

	public MedicalVo(String eventName, String addmissionId) {
		this.eventName = eventName;
        this.admissionId = addmissionId;
	}
}
