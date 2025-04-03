package example.openai;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Example of how to use the openai API.
public class GptExample {

    public static void main(String[] args) throws IOException, URISyntaxException {
        String apiKey = System.getenv("OPENAI_API_KEY");
        String url = "https://api.openai.com/v1/responses";
        String body = """
                {
                    "model": "gpt-4o",
                    "input": [
                        {
                            "role": "user",
                            "content": "As a student, I am looking for a job. I am motivated and have experience in software development. Please provide 1 job option in brief advice, one sentence max!"
                        }
                    ]
                }""";

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Response: " + response.body());
            JSONObject jsonResponse = new JSONObject(response.body());
//            System.out.println("JSON Response: " + jsonResponse);
            String content = jsonResponse
                    .getJSONArray("output")
                    .getJSONObject(0)
                    .getJSONArray("content")
                    .getJSONObject(0)
                    .getString("text");

            System.out.println("API Response: " + content);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
