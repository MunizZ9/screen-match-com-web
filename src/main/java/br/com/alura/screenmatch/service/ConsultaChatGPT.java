//package br.com.alura.screenmatch.service;
//
//import com.theokanning.openai.completion.CompletionRequest;
//import com.theokanning.openai.service.OpenAiService;
//import com.theokanning.openai.OpenAiHttpException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class ConsultaChatGPT {
//
//    private static final Logger logger = LoggerFactory.getLogger(ConsultaChatGPT.class);
//    private static final String API_KEY = "key";
//    private static final String MODEL = "gpt-3.5-turbo-instruct";
//
//    public static String obterTraducao(String texto) {
//        OpenAiService service = new OpenAiService(API_KEY);
//
//        CompletionRequest requisicao = CompletionRequest.builder()
//                .model(MODEL)
//                .prompt("traduza para o português o texto: " + texto)
//                .maxTokens(1000)
//                .temperature(0.7)
//                .build();
//
//        try {
//            var resposta = service.createCompletion(requisicao);
//            return resposta.getChoices().get(0).getText();
//        } catch (OpenAiHttpException e) {
//            logger.error("Erro ao chamar a API do OpenAI: {}", e.getMessage());
//            if (e.getMessage().contains("You exceeded your current quota")) {
//                return "A cota da API do OpenAI foi excedida. Por favor, verifique seu plano e tente novamente mais tarde.";
//            }
//            throw e; // Re-lançar a exceção se for um erro diferente
//        }
//    }
//}
