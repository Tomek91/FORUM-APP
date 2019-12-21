package pl.com.app.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

@Component
public class Request {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HttpRequest requestGet(final String path) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(path))
                    .version(HttpClient.Version.HTTP_2)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

        } catch (Exception e) {
            throw new MyException(ExceptionCode.HTTP, e.getMessage());
        }
    }

    public <T> HttpRequest requestPost(final String path, T body) {
        try {
            String json = gson.toJson(body);

            return HttpRequest.newBuilder()
                    .uri(new URI(path))
                    .version(HttpClient.Version.HTTP_2)
                    .header("content-type", "application/json;charset=UTF-8")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

        } catch (Exception e) {
            throw new MyException(ExceptionCode.HTTP, e.getMessage());
        }


    }
}
