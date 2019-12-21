package pl.com.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.exceptions.MyException;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Service
@Transactional
public class IpPublicService {

    @Value("${ipPublicPath}")
    private String ipPublicPath;

    public IpPublicService() {
    }

    public String getIpPublic() {
        String ipAddress;
        try {
            URL url = new URL(ipPublicPath);
            BufferedReader sc = new BufferedReader(new InputStreamReader(url.openStream()));

            ipAddress = sc.readLine().trim();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
        return ipAddress;
    }


}
