package org.adrij.consumer.Configs.ThirdPartyAPIConfigs;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.adrij.consumer.Constants.ThirdPartyAPIConstants.*;

@Configuration
public class OkHttpClientConfig {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(MAX_TIME_TO_RECEIVE_RESPONSE_IN_MILLISECONDS, java.util.concurrent.TimeUnit.MILLISECONDS) // Set connect timeout
                .readTimeout(MAX_TIME_TO_RECEIVE_RESPONSE_IN_MILLISECONDS, java.util.concurrent.TimeUnit.MILLISECONDS) // Set read timeout
                .build();
    }
}
