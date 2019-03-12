package com.techprimers.db.resource;

import com.techprimers.db.constants.StringResponse;
import com.techprimers.db.model.Weather;
import com.techprimers.db.services.MailService;
import com.techprimers.db.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WeatherController {


    @Autowired
    WeatherService weatherService;

    @Autowired
    MailService mailService;

    //zip code, weather map
    Map<String, Weather> weatherMap = new HashMap<>();

    @GetMapping(value = "/weather/{zipCode}", produces = "application/json")
    public StringResponse weather(@PathVariable String zipCode) throws IOException {

        if (weatherMap.get(zipCode) != null) {

            //if time difference is less than 10mins

            long diff = System.currentTimeMillis() - weatherMap.get(zipCode).getTime();
            if (diff <= 60000)
                return new StringResponse(weatherMap.get(zipCode).getCondition());
        }

        String condition = weatherService.getWeatherByZip(zipCode);

        long currentTimeMillis = System.currentTimeMillis();
        weatherMap.put(zipCode,new Weather(currentTimeMillis,condition));

        return new StringResponse(condition);
    }

    @GetMapping(value = "/weather/{zipCode}/sendemail/{mailto}/")
    public String sendEmail(@PathVariable String mailto,@PathVariable String zipCode) throws MessagingException {
        String weather = null;
        try {
            weather = weatherService.getWeatherByZip(zipCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailService.sendmail(mailto,weather);

        return "Email sent successfully to: " +mailto;
    }
}
