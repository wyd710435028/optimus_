package com.unisound.optimus_visual.elasticsearch.dao;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unisound.optimus_visual.modules.medicalrecord.model.Hospital;
import com.unisound.optimus_visual.modules.medicalrecord.model.EventVo;
import com.unisound.optimus_visual.modules.medicalrecord.model.MatchMedicalRecordModel;
import com.unisound.optimus_visual.modules.medicalrecord.model.MedicalVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientDataFetcher {

	private static String path = "data.patient.inHospitalInfo.eventMap.";
	private static String index_pre = "optimus_data_";
	@Autowired
	private RestHighLevelClient restHighLevelClient;

	// private List<String> hospitals = Arrays.asList("10008", "10008_temp");

	public List<EventVo> getEventVOListbyEventName(List<String> eventNames, Hospital hospital) {
		List<EventVo> eventVos = new ArrayList<>();
		for (String eventName : eventNames) {
			EventVo eventVObyEventName = getEventVObyEventName(eventName, hospital);
			eventVos.add(eventVObyEventName);
		}
		return eventVos;
	}

    public int getEventVOListbyEventName(Hospital hospital, String formateDatestr) {
        String index = index_pre + hospital.getHospitalId() + "_" + hospital.getScene();

        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("@timestamp");
        rangeQuery.gt(formateDatestr);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(rangeQuery);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = null;

        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            int num = (int) (search.getHits().getTotalHits().value);
            return num;
        } catch (Exception e) {
            return 0;
        }
    }

	public List<EventVo> getEventVOListbyEventName(String event, List<Hospital> hospitals) {
		List<EventVo> eventVos = new ArrayList<>();
		for (Hospital hospital : hospitals) {
			EventVo eventVObyEventName = getEventVObyEventName(event, hospital);
			eventVos.add(eventVObyEventName);
		}
		return eventVos;
	}

	// public List<MedicalVo> getPatientDataList(String eventName, String
	// hospital, int page) {
	//
	// List<MedicalVo> eventData = getEventData(eventName, hospital,page);
	//
	// return eventData;
	//
	// }

	public EventVo getEventVObyEventName(String eventName, Hospital hospital) {
		String index = hospital.getHospitalId() + "_" + hospital.getScene();
		EventVo vo = new EventVo();
		vo.setHospitalId(hospital.getHospitalId());
		vo.setScene(hospital.getScene());
		vo.setTotalNum(getDocCount(eventName, index));
		vo.setEventName(eventName);
		vo.setEventNum(getEventNum(eventName, index));
		vo.setEventOrderNum(getEventOrderNum(eventName, index));
		vo.setEventProgressNum(getEventProgressNum(eventName, index));
		vo.setEventOperationNum(getEventOperationNum(eventName, index));
		vo.percentCalculate();
		return vo;
	}

	public long getDocCount(String eventName, String index) {
		ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery("data.sn");
		CountRequest countRequest = new CountRequest(index_pre + index);
		countRequest.query(existsQuery);
		CountResponse countResponse;
		try {
			countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			// e.printStackTrace();
			return 0;
		}
        return countResponse.getCount();
	}

    public long getEventNum(String eventName, String index, String order, String progress, String operation) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(path + eventName + ".id");
        boolQueryBuilder.must(existsQuery);
        if ("1".equals(order)) {
            boolQueryBuilder.must(QueryBuilders.existsQuery(path + eventName + ".orderMap"));
        } else if ("2".equals(order)) {
            boolQueryBuilder.mustNot(QueryBuilders.existsQuery(path + eventName + ".orderMap"));
        }

        if ("1".equals(progress)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedProgressNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedProgressNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.must(nestedQuery);
        } else if ("2".equals(progress)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedProgressNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedProgressNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.mustNot(nestedQuery);
        }

        if ("1".equals(operation)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedOperationNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedOperationNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.must(nestedQuery);
        } else if ("2".equals(operation)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedOperationNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedOperationNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.mustNot(nestedQuery);
        }

        NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName, boolQueryBuilder, ScoreMode.None);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.query(nestedQuery);
        searchSourceBuilder.size(0);
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
            e.printStackTrace();
            return -1;
		}
		return search.getHits().getTotalHits().value;
	}

    public long getEventNum(String eventName, String index) {
        ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(path + eventName + ".id");
        NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName, existsQuery, ScoreMode.None);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.query(nestedQuery);
        searchSourceBuilder.size(0);
        SearchRequest searchRequest = new SearchRequest(index_pre + index);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search;
        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return search.getHits().getTotalHits().value;
    }

	public List<String> getEventAdmissionIds(String eventName, String index) {
		ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(path + eventName + ".id");
		NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName, existsQuery, ScoreMode.None);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.query(nestedQuery);
		FetchSourceContext context = new FetchSourceContext(false);

		searchSourceBuilder.fetchSource("data.sn", "");
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		return null;
	}

    public List<MedicalVo> getEventData(String eventName, String index, int page, String order, String progress,
                                        String operation, int num) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(path + eventName + ".id");
        boolQueryBuilder.must(existsQuery);
        if ("1".equals(order)) {
            boolQueryBuilder.must(QueryBuilders.existsQuery(path + eventName + ".orderMap"));
        } else if ("2".equals(order)) {
            boolQueryBuilder.mustNot(QueryBuilders.existsQuery(path + eventName + ".orderMap"));
        }

        if ("1".equals(progress)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedProgressNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedProgressNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.must(nestedQuery);
        } else if ("2".equals(progress)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedProgressNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedProgressNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.mustNot(nestedQuery);
        }

        if ("1".equals(operation)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedOperationNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedOperationNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.must(nestedQuery);
        } else if ("2".equals(operation)) {
            ExistsQueryBuilder existsQuery2 = QueryBuilders
                    .existsQuery(path + eventName + ".mentionedOperationNodes.fileId");
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedOperationNodes",
                    existsQuery2, ScoreMode.None);
            boolQueryBuilder.mustNot(nestedQuery);
        }

        NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName, boolQueryBuilder, ScoreMode.None);

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.query(nestedQuery).from((page - 1) * num).size(num);
        if (num > 1000) {
            searchSourceBuilder.fetchSource("data.sn", "");
        }
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		List<SearchHit> asList = Arrays.asList(search.getHits().getHits());
		List<MedicalVo> list = new ArrayList<MedicalVo>();
		asList.stream().map(e -> e.getSourceAsString())
				.forEach(e->{
					JSONObject parseObject = JSON.parseObject(e);

					String admissionId = parseObject.getJSONObject("data").getString("sn");
					MedicalVo medicalVo = new MedicalVo(eventName,admissionId);
                    if (parseObject.getJSONObject("data").getJSONObject("patient") != null) {
                        JSONArray jsonArray = parseObject.getJSONObject("data").getJSONObject("patient")
                                .getJSONObject("inHospitalInfo").getJSONObject("eventMap").getJSONArray(eventName);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        boolean haveOrder = jsonObject.getJSONObject("orderMap") == null
                                || jsonObject.getJSONObject("orderMap").size() == 0 ? false : true;
                        boolean haveProgress = jsonObject.getJSONArray("mentionedProgressNodes") == null
                                || jsonObject.getJSONArray("mentionedProgressNodes").size() == 0 ? false : true;
                        boolean haveOperation = jsonObject.getJSONArray("mentionedOperationNodes") == null
                                || jsonObject.getJSONArray("mentionedOperationNodes").size() == 0 ? false : true;

                        medicalVo.setHaveOrder(haveOrder);
                        medicalVo.setHaveProgress(haveProgress);
                        medicalVo.setHaveOperation(haveOperation);

                        String time = parseObject.getString("@timestamp");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = simpleDateFormat.parse(time);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
                            String dateStr = simpleDateFormat2.format(calendar.getTime());
                            medicalVo.setDate(dateStr);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
					list.add(medicalVo);



				});
		return list;
	}

	public long getEventOrderNum(String eventName, String index) {
		ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(path + eventName + ".orderMap");
		NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName,
				existsQuery, ScoreMode.None);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.query(nestedQuery);
        searchSourceBuilder.size(0);
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
            e.printStackTrace();
			return 0;
		}
		return search.getHits().getTotalHits().value;
	}


	public long getEventOrderData(String eventName, String index) {
		ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(path + eventName + ".orderMap");
		NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName, existsQuery, ScoreMode.None);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.query(nestedQuery);
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			// e.printStackTrace();
			return 0;
		}
		return search.getHits().getTotalHits().value;
	}

	public long getEventProgressNum(String eventName, String index) {
		ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(path + eventName + ".mentionedProgressNodes.fileId");
		NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedProgressNodes",
				existsQuery, ScoreMode.None);
		NestedQueryBuilder nestedQuery2 = QueryBuilders.nestedQuery(path + eventName, nestedQuery, ScoreMode.None);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.query(nestedQuery2);
        searchSourceBuilder.size(0);
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
            e.printStackTrace();
			return 0;
		}
		return search.getHits().getTotalHits().value;
	}

	public long getEventOperationNum(String eventName, String index) {
		ExistsQueryBuilder existsQuery = QueryBuilders
				.existsQuery(path + eventName + ".mentionedOperationNodes.fileId");
		NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery(path + eventName + ".mentionedOperationNodes",
				existsQuery, ScoreMode.None);
		NestedQueryBuilder nestedQuery2 = QueryBuilders.nestedQuery(path + eventName, nestedQuery, ScoreMode.None);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.query(nestedQuery2);
        searchSourceBuilder.size(0);
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
            e.printStackTrace();
			return 0;
		}
		return search.getHits().getTotalHits().value;
	}

	/**
	 * 根据id查询doc
	 * 
	 * @throws Exception
	 */
	public String get(String hospitalId, String admissionId, String scene) {
		TermQueryBuilder termQuery = QueryBuilders.termQuery("_id", admissionId);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.query(termQuery);
		SearchRequest searchRequest = new SearchRequest(index_pre + hospitalId + "_" + scene);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
			return search.getHits().getHits()[0].getSourceAsString();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param hospitalId
	 * @param admissionId
	 * @param
	 * @return
	 */
	public String put(String hospitalId, String admissionId, String scene, String data) {
		try {
			String index = "optimus_data_" + hospitalId + "_" + scene;
			GetIndexRequest getIndexRequest = new GetIndexRequest(index);
			boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
			if (!exists) {
				CreateIndexRequest creatIndexRequest = new CreateIndexRequest(index);
				restHighLevelClient.indices().create(creatIndexRequest, RequestOptions.DEFAULT);
			}
			IndexRequest indexRequest = new IndexRequest(index);
			indexRequest.id(admissionId).source(data, XContentType.JSON);
			IndexResponse index2 = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
			return index2.toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return "false";
		}
	}

	/**
	 * 根据id和索引删除doc
	 * 
	 * @throws Exception
	 */
	public String delete(String hospitalId, String admissionId, String scene) {
		try {
			String index = "optimus_data_" + hospitalId + "_" + scene;
			GetIndexRequest getIndexRequest = new GetIndexRequest(index);
			boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
			if (!exists) {
				return "success";
			}
			DeleteRequest deleteRequest = new DeleteRequest(index, admissionId);
			DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
			return delete.toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return "false";
		}
	}

	public Map<String,Object> getAdmissionNoInHospital(String index,Integer pageNum,Integer pageSize) {
		Map<String,Object> result = new LinkedHashMap<>();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.trackTotalHits(true);
		searchSourceBuilder.fetchSource(false);
		if (pageNum==null){
			pageNum=1;
		}
		if (pageSize==null){
			pageSize=10;
		}
		searchSourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC)).from((pageNum-1)*pageSize).size(pageSize);
//		searchSourceBuilder.from((pageNum-1)*pageSize);
//		searchSourceBuilder.size(pageSize);
		SearchRequest searchRequest = new SearchRequest(index_pre + index);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search;
		try {
			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			 e.printStackTrace();
			return new HashMap<>();
		}
		SearchHits searchHits = search.getHits();
		SearchHit[] hits = searchHits.getHits();
		List<String> admissionIdList = new ArrayList<>();
		List<String> timestampList = new ArrayList<>();
		String timestamp = "";
		if (hits!=null&&hits.length>0){
			for (int i =0;i<hits.length;i++){
				SearchHit searchHit = hits[i];
				String id = searchHit.getId();
				Map<String, Object> byAdmissionId = this.getByAdmissionId(index, id);
				if (byAdmissionId.containsKey("timestamp")){
					timestamp = (String) byAdmissionId.get("timestamp");
					timestampList.add(timestamp);
				}
				admissionIdList.add(id);
			}
		}
		result.put("admissionIdList",admissionIdList);
		long total = searchHits.getTotalHits().value;
		result.put("total",total);
		result.put("timestampList",timestampList);
		return result;
	}

	public Map<String,Object> getAdmissionNoInHospitalWithoutPage(String index, List<MatchMedicalRecordModel> recordModelList,Integer pageNum,Integer pageSize) throws IOException {
		Map<String,Object> result = new LinkedHashMap<>();
		if (CollectionUtils.isEmpty(recordModelList)){
			return new HashMap<>();
		}
		List<String> referAdmissionIds = recordModelList.stream().map(recordModel -> recordModel.getAdmissionId()).collect(Collectors.toList());
		//查询es的逻辑
//		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//		searchSourceBuilder.trackTotalHits(true);
//		searchSourceBuilder.fetchSource(false);
//		searchSourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));
////		searchSourceBuilder.from((pageNum-1)*pageSize);
////		searchSourceBuilder.size(pageSize);
//		searchSourceBuilder.size(50000);
//		SearchRequest searchRequest = new SearchRequest(index_pre + index);
//		searchRequest.source(searchSourceBuilder);
//		SearchResponse search;
//		try {
//			search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//		} catch (Exception e) {
//			 e.printStackTrace();
//			return new HashMap<>();
//		}
//		SearchHits searchHits = search.getHits();
//		SearchHit[] hits = searchHits.getHits();
//		List<String> admissionIdList = new ArrayList<>();
//		List<String> timestampList = new ArrayList<>();
//		String timestamp = "";
//		if (hits!=null&&hits.length>0){
//			for (int i =0;i<hits.length;i++){
//				SearchHit searchHit = hits[i];
//				String id = searchHit.getId();
//				if (referAdmissionIds.contains(id)){
//					admissionIdList.add(id);
//				}
//			}
//		}
		String timestamp = "";
		List<String> timestampList = new ArrayList<>();
		//手动分页
		//计算起始和结束索引
		int fromIndex = (pageNum-1)*pageSize;
		int toIndex = Math.min(fromIndex + pageSize, referAdmissionIds.size());
		List<String> pagedAdmissionIds = referAdmissionIds.stream().skip(fromIndex).limit(toIndex - fromIndex).collect(Collectors.toList());
		//查询timestampList
		if (!pagedAdmissionIds.isEmpty()){
			timestampList = this.getTimestampsByAdmissionIds(index, pagedAdmissionIds);
		}
		result.put("admissionIdList",pagedAdmissionIds);
		//总量
		int size = recordModelList.size();
		result.put("total",size);
		result.put("timestampList",timestampList);
		return result;
	}

	public Map<String, Object> getByAdmissionId(String index,String admissionId) {
		Map<String,Object> result = new LinkedHashMap<>();
		GetRequest getquRequest = new GetRequest(index_pre + index);
		GetResponse getResponse ;
		List<String> snList = new ArrayList<>();
		String timestamp = "";
		try {
			getResponse = restHighLevelClient.get(getquRequest.id(admissionId), RequestOptions.DEFAULT);
		} catch (IOException e) {
			return  new LinkedHashMap<>();
		}
		if (getResponse!=null){
			boolean exists = getResponse.isExists();
			if (exists){
				//如果存在
				Map<String, Object> source = getResponse.getSource();
//				System.out.println(source);
				if (source.containsKey("data")){
					if (source.get("data")!=null){
						Map<String,Object> dataMap = (Map<String, Object>) source.get("data");
						if (!dataMap.isEmpty()){
							if (dataMap.containsKey("sn")){
								String sn = (String) dataMap.get("sn");
								if (StringUtils.isNotBlank(sn)){
									snList.add(sn);
								}
							}
						}
					}
				}
				if (source.containsKey("@timestamp")){
					timestamp = (String) source.get("@timestamp");
				}
			}
		}
		result.put("admissionIdList",snList);
		result.put("total",snList.size());
		result.put("timestamp",timestamp);
		return result;
	}

	public List<String> getTimestampsByAdmissionIds(String index,List<String> admissionIds) throws IOException {
		List<String> result = new ArrayList<>();
		if (admissionIds.isEmpty()) {
            return result;
        }
		String timestamp = "";
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        for (String id : admissionIds) {
            multiGetRequest.add(new MultiGetRequest.Item(index_pre + index, id));
        }

        MultiGetResponse multiGetResponse = restHighLevelClient.mget(multiGetRequest, RequestOptions.DEFAULT);

        for (MultiGetItemResponse itemResponse : multiGetResponse.getResponses()) {
            if (itemResponse.isFailed()) {
                continue; // 或者记录错误
            }
            Map<String, Object> sourceAsMap = itemResponse.getResponse().getSourceAsMap();
            if (sourceAsMap.containsKey("@timestamp")) {
                timestamp = (String) sourceAsMap.get("@timestamp");
                result.add(timestamp);
            }
        }
        return result;
	}

    public static void main(String[] args) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date zero = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formatDate = format.format(zero);
//        System.out.println(formatDate);
    }
}
