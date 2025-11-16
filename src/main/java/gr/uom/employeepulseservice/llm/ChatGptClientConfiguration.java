package gr.uom.employeepulseservice.llm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ChatGptClientConfiguration {

    @Bean
    public HttpClient chatGptHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
}
