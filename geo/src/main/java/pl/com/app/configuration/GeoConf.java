package pl.com.app.configuration;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;

import java.io.File;

@Configuration
public class GeoConf {

    @Value("${geoLitePath}")
    private String geoLitePath;

    @Bean
    public DatabaseReader databaseReader() {
        try {
            return new DatabaseReader.Builder(new File(geoLitePath)).build();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.FILE_IO, e.getMessage());
        }
    }
}
