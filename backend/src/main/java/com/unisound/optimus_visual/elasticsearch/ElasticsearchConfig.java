package com.unisound.optimus_visual.elasticsearch;

import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
@Data
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

	private String host;
	private int port;
	@Override
	@Bean
	public RestHighLevelClient elasticsearchClient() {
		// TODO Auto-generated method stub
		RestClientBuilder builder = RestClient.builder(new HttpHost(host, port)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            // 该方法接收一个RequestConfig.Builder对象，对该对象进行修改后然后返回。
            @Override
            public RequestConfig.Builder customizeRequestConfig(
                    RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setConnectTimeout(5 * 1000) // 连接超时（默认为1秒）
                        .setSocketTimeout(60 * 60 * 1000);// 套接字超时（默认为30秒）//更改客户端的超时限制默认30秒现在改为60分钟
            }
		});
		RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
		return restHighLevelClient;
	}

}
