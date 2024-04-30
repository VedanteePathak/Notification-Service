package org.adrij.common.Configs;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;


import static org.adrij.common.Constants.ElasticsearchConstants.*;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.adrij.common.Repositories")
public class ElasticsearchConfig {
//    @Bean
//    public ElasticsearchClient elasticsearchClient() {
//        RestClient httpClient = RestClient.builder(new HttpHost(ELASTICSEARCH_HOST, ELASTICSEARCH_PORT)).build();
//        ElasticsearchTransport transport = new RestClientTransport(httpClient, new JacksonJsonpMapper());
//        return new ElasticsearchClient(transport);
//    }
//
//    @Bean
//    public RestClient getRestClient() {
//        RestClient httpClient;
//        httpClient = RestClient.builder(new HttpHost(ELASTICSEARCH_HOST, ELASTICSEARCH_PORT)).build();
//        return httpClient;
//    }
//
//    @Bean
//    public ElasticsearchConverter getElastic
//
//    @Bean
//    public ElasticsearchTransport getElasticsearchTransport() {
//        return new RestClientTransport(getRestClient(), new JacksonJsonpMapper());
//    }
//
//    @Bean
//    public ElasticsearchClient getElasticsearchClient() {
//        ElasticsearchClient esClient = new ElasticsearchClient(getElasticsearchTransport());
//        return esClient;
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() throws UnknownHostException {
//        return new ElasticsearchTemplate(elasticsearchClient());
//    }
//
////    @Bean
////    public ElasticsearchClient getClient() throws UnknownHostException {
////        Settings setting = Settings
////                .builder()
////                .put("client.transport.sniff", true)
////                .put("path.home", "/downloads/bin/elasticsearch") //elasticsearch home path
////                .put("cluster.name", "elasticsearch")
////                .build();
////        //please note that client port here is 9300 not 9200!
////        ElasticsearchClient client = new ElasticsearchClient(setting)
////                .addTransportAddress(new TransportAddress(InetAddress.getByName(ELASTICSEARCH_HOST), ELASTICSEARCH_PORT));
////        return client;
////    }


    @Bean
    public RestHighLevelClient elasticsearchClient() {

        RestHighLevelClient client = new RestHighLevelClient(RestClient
                .builder(new HttpHost("localhost",9200,"http")));

        return client;
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }

}
