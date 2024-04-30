package org.vedantee.common.Configs;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.vedantee.common.Repositories")
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
