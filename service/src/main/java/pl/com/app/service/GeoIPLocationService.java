package pl.com.app.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.app.dto.geo_ip.GeoIP;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;

import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class GeoIPLocationService {

    private final DatabaseReader dbReader;
    private final IpPublicService ipPublicService;


    public GeoIP getLocation() {
        try {
            InetAddress ipAddress = InetAddress.getByName(ipPublicService.getIpPublic());
            CityResponse response = dbReader.city(ipAddress);

            return GeoIP.builder()
                    .ipAddress(ipAddress.getHostAddress())
                    .city(response.getCity().getName())
                    .latitude(response.getLocation().getLatitude().toString())
                    .longitude(response.getLocation().getLongitude().toString())
                    .build();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.FILE_IO, e.getMessage());
        }

    }


}
