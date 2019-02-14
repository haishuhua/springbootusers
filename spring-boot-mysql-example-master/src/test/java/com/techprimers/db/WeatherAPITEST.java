package com.techprimers.db;

import com.mysql.jdbc.log.Log;
import com.techprimers.db.services.WeatherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherAPITEST {


    @Mock
    WeatherService weatherService;

    @Test
    public void weatherTestForZipCodeWithNullZip() throws IOException {
        assertEquals( weatherService.getWeatherByZip(null), null);
    }

    @Test
    public void weatherTestForZipCodeWithRealZip() throws IOException {
      //  assertEquals( weatherService.getWeatherByZip("11363"), "TEST");
    }

}
