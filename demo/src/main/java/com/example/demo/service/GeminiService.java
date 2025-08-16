package com.example.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    public GeminiService(WebClient.Builder webClientBuilder,
                         ObjectMapper objectMapper,
                         @Value("${gemini.api.url}") String apiUrl,
                         @Value("${gemini.api.key}") String apiKey) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public String extractTextFromImage(File imageFile) throws Exception {
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("inline_data", Map.of(
                                        "mime_type", Files.probeContentType(imageFile.toPath()),
                                        "data", base64Image
                                )),
                                Map.of("text", "Extract medicine name and time clearly in plain format: 'Medicine: XYZ, Time: HH:MM AM/PM'")
                        })
                }
        );

        String url = apiUrl.contains("?key=") ? apiUrl + apiKey : apiUrl + "?key=" + apiKey;
        String response = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("📥 Gemini raw response: " + response);

        return extractTextFromResponse(response);
    }

    private String extractTextFromResponse(String response) {
        try {
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if (firstCandidate.getContent() != null &&
                        firstCandidate.getContent().getParts() != null &&
                        !firstCandidate.getContent().getParts().isEmpty()) {
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }
            return "No text extracted.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Parsing error: " + e.getMessage();
        }
    }
}


//Niche wale me url me key alag se likh k nhi hai
// package com.example.demo.service;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;

// import java.io.File;
// import java.nio.file.Files;
// import java.util.Base64;
// import java.util.Map;

// @Service
// public class GeminiService {

//     private final WebClient webClient;
//     private final String apiKey;
//     private final String apiUrl;
//     private final ObjectMapper objectMapper;

//     public GeminiService(WebClient.Builder webClientBuilder,
//                          ObjectMapper objectMapper,
//                          @Value("${gemini.api.url}") String apiUrl,
//                          @Value("${gemini.api.key}") String apiKey) {
//         this.webClient = webClientBuilder.build();
//         this.objectMapper = objectMapper;
//         this.apiUrl = apiUrl;
//         this.apiKey = apiKey;
//     }

//     public String extractTextFromImage(File imageFile) throws Exception {
//         byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
//         String base64Image = Base64.getEncoder().encodeToString(imageBytes);

//         // ✅ Construct Gemini request payload
//         Map<String, Object> requestBody = Map.of(
//             "contents", new Object[]{
//                 Map.of("parts", new Object[]{
//                     Map.of("inline_data", Map.of(
//                         "mime_type", "image/jpeg", // change to image/png if needed
//                         "data", base64Image
//                     )),
//                     Map.of("text", "Extract the text from this prescription image clearly.")
//                 })
//             }
//         );

//         // ✅ Send request
//         String response = webClient.post()
//                 .uri(apiUrl + apiKey)
//                 .header("Content-Type", "application/json")
//                 .bodyValue(requestBody)
//                 .retrieve()
//                 .bodyToMono(String.class)
//                 .block();

//         // ✅ Parse and return clean text
//         return extractTextFromResponse(response);
//     }

//     private String extractTextFromResponse(String response) {
//         try {
//             GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
//             if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
//                 GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
//                 if (firstCandidate.getContent() != null &&
//                     firstCandidate.getContent().getParts() != null &&
//                     !firstCandidate.getContent().getParts().isEmpty()) {
//                     return firstCandidate.getContent().getParts().get(0).getText();
//                 }
//             }
//             return "No text extracted from prescription.";
//         } catch (Exception e) {
//             return "Parsing error: " + e.getMessage();
//         }
//     }
// }
