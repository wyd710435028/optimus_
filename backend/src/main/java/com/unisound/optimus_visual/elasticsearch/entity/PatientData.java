package com.unisound.optimus_visual.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "data_mapper_test")
public class PatientData {
	@Id
	private String id;
	@Field(type = FieldType.Object)
	private Object data;
	@Field(type = FieldType.Text)
	private String msg;
	@Field(type = FieldType.Text)
	private String processStatus;

}
