
package pl.com.app.dto.weather;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Main {
    private Long humidity;
    private Long pressure;
    private Double temp;
    private Double temp_max;
    private Double temp_min;


}
