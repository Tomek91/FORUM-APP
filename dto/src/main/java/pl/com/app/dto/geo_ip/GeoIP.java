package pl.com.app.dto.geo_ip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoIP {
    private String ipAddress;
    private String city;
    private String latitude;
    private String longitude;
}