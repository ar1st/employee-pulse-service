package gr.uom.employeepulseservice.llm;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.responses.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class ChatGptClient {

    private final String chatGptApiKey;
    private final String chatGptModel;

    public ChatGptClient() {
        this.chatGptApiKey = System.getenv("EMPLOYEE_PULSE_SERVICE_OPENAI_KEY");
        this.chatGptModel = "gpt-5.1-2025-11-13";

        if (this.chatGptApiKey == null) {
            log.warn("OpenAI key not found!");
        }
    }

    @SneakyThrows
    public String analyzePerformanceReview(String prompt) {

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(chatGptApiKey)
                .build();

        ResponsePrompt responsePrompt = ResponsePrompt.builder()
                .id("pmpt_6919c607a4f48197b1f7057c487fcf5b03471568c2b2f7a9")
                .variables(
                        ResponsePrompt.Variables.builder()
                                .putAdditionalProperty("review", JsonValue.from(prompt))
                                .build()
                )
                .build();

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(chatGptModel)
                .prompt(responsePrompt)
                .build();

        Response response = client.responses().create(params);
        ResponseOutputItem output = response.output().getFirst();
        System.out.println(output.message().get().content().getFirst().outputText().get());
        return "";
    }

}
