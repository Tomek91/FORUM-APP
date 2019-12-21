package pl.com.app.dto.weather;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Weather {
    private String description;
    private String icon;
    private Long id;
    private String main;


}
