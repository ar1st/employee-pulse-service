package gr.uom.employeepulseservice.llm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.responses.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ChatGptClient {

    private final String chatGptApiKey;
    private final String chatGptModel;
    private final ObjectMapper objectMapper;

    public ChatGptClient(@Value("${openai.api-key:}") String chatGptApiKey) {
        this.chatGptApiKey = chatGptApiKey;
        this.chatGptModel = "gpt-5.1-2025-11-13";
        this.objectMapper = new ObjectMapper();

        if (StringUtils.isBlank(chatGptApiKey)) {
            log.warn("OpenAI key not found in application properties (property 'openai.api-key').");
        }
    }

    @SneakyThrows
    public List<GeneratedSkill> analyzePerformanceReview(String prompt) {

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

        Optional<ResponseOutputItem> assistantOutputOptional = response.output().stream()
                .filter(it -> it.message()
                        .map(msg -> "assistant".equals(msg._role().asStringOrThrow()))
                        .orElse(false))
                .findFirst();

        if (assistantOutputOptional.isEmpty() || assistantOutputOptional.get().message().isEmpty()) {
            return null;
        }

        String json = assistantOutputOptional.get().message().get()
                .content()
                .getFirst()
                .asOutputText()
                .text();

        log.info("ChatGPT response: {}", json);
        return objectMapper.readValue(json, new TypeReference<>() {});
    }

}
