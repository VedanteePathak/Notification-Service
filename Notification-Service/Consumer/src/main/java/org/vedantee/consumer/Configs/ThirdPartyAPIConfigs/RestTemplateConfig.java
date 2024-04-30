package org.adrij.consumer.Configs.ThirdPartyAPIConfigs;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import static org.adrij.consumer.Constants.ThirdPartyAPIConstants.*;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(5)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(MAX_TIME_TO_RECEIVE_RESPONSE_IN_MILLISECONDS);
        requestFactory.setReadTimeout(MAX_TIME_TO_RECEIVE_RESPONSE_IN_MILLISECONDS);

        return new RestTemplate(requestFactory);
    }
}
