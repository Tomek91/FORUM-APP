package pl.com.app.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.com.app.dto.AnswerDTO;
import pl.com.app.dto.UserDTO;
import pl.com.app.dto.weather.WeatherData;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;
import pl.com.app.http.Request;

import javax.transaction.Transactional;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WeatherService {

    @Value("${weatherPath}")
    private String weatherPath;
    private Request request;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public WeatherService(Request request) {
        this.request = request;
    }

    private WeatherData getWeatherByCity(final String city) {
        WeatherData weatherData = null;
        try {
            if (city == null) {
                throw new NullPointerException("CITY IS NULL");
            }

            HttpResponse<String> res = HttpClient
                    .newBuilder()
                    .proxy(ProxySelector.getDefault())
                    .build()
                    .send(request.requestGet(weatherPath.replace("__city__", city)), HttpResponse.BodyHandlers.ofString());


            weatherData = gson.fromJson(res.body(), WeatherData.class);

        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
        return weatherData;
    }

    public Map<Long, WeatherData> getWeatherForUsers(List<AnswerDTO> answerDTOList) {
        try {
            if (answerDTOList == null) {
                throw new NullPointerException("ANSWER LIST IS NULL");
            }

            return answerDTOList
                    .stream()
                    .map(AnswerDTO::getUserDTO)
                    .distinct()
                    .collect(Collectors.toMap(
                            UserDTO::getId,
                            x -> this.getWeatherByCity(x.getCity())
                    ));


        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public WeatherData getWeatherForUser(UserDTO userDTO) {
        try {
            if (userDTO == null) {
                throw new NullPointerException("USER IS NULL");
            }
            if (userDTO.getCity() == null) {
                throw new NullPointerException("USER CITY IS NULL");
            }

            return this.getWeatherByCity(userDTO.getCity());


        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}
