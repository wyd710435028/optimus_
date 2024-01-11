package com.unisound.optimus_visual.elasticsearch.dao;

import com.unisound.optimus_visual.modules.medicalrecord.model.Hospital;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexDataFetcher {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	public List<String> getHospitals() {
		GetIndexRequest getIndexRequest = new GetIndexRequest("optimus_data*");
		GetIndexResponse getIndexResponse;
		try {
			getIndexResponse = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
		} catch (Exception e1) {
			e1.printStackTrace();
			return Collections.emptyList();
		}
		// System.out.println(Arrays.toString(getIndexResponse.getIndices()));
		List<String> collect = Arrays.stream(getIndexResponse.getIndices()).map(e -> {
			return e.substring(13);
		}).collect(Collectors.toList());
		return collect;
	}

	public List<Hospital> getHospitalsEntity() {
		List<String> hospitals;
		try {
			hospitals = getHospitals();
		} catch (Exception e1) {
			e1.printStackTrace();
			return Collections.emptyList();
		}
		List<Hospital> collect = hospitals.stream().map(e -> {
			Hospital hospital = new Hospital();
			hospital.setHospitalId(e.substring(0, e.indexOf("_")));
			hospital.setScene(e.substring(e.indexOf("_") + 1));
			return hospital;
		}).collect(Collectors.toList());
		return collect;
	}
}
