package org.vedantee.consumer.Configs.ThirdPartyAPIConfigs;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vedantee.consumer.Constants.ThirdPartyAPIConstants;

@Configuration
public class OkHttpClientConfig {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(ThirdPartyAPIConstants.MAX_TIME_TO_RECEIVE_RESPONSE_IN_MILLISECONDS, java.util.concurrent.TimeUnit.MILLISECONDS) // Set connect timeout
                .readTimeout(ThirdPartyAPIConstants.MAX_TIME_TO_RECEIVE_RESPONSE_IN_MILLISECONDS, java.util.concurrent.TimeUnit.MILLISECONDS) // Set read timeout
                .build();
    }
}
