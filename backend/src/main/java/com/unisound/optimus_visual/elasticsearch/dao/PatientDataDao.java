package com.unisound.optimus_visual.elasticsearch.dao;

import com.unisound.optimus_visual.elasticsearch.entity.PatientData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientDataDao extends ElasticsearchRepository<PatientData, String> {

}
