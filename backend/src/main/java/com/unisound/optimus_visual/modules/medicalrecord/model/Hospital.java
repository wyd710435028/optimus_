package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

/**
 * @author zhaofulu <zhaofulu@unisound.com>
 * @date 2021-08-19 18:29
 */
@Data
public class Hospital {

    private String hospitalId;

    private String hospitalName;

	private String scene;

    public Hospital() {
    }

    public Hospital(String hospitalId, String hospitalName) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
    }
}
