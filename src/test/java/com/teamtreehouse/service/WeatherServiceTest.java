package com.teamtreehouse.service;

import com.teamtreehouse.config.AppConfig;
import com.teamtreehouse.service.dto.geocoding.Location;
import com.teamtreehouse.service.dto.weather.Weather;
import com.teamtreehouse.service.resttemplate.weather.WeatherServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration //When provided no parameters Spring looks for classes in here with @Configuration annotation
                      // (trigger loading of a test context)
public class WeatherServiceTest {

    @Autowired
    WeatherService service;

    private Location loc;
    private Weather weather;

    private static final double ERROR_GEO = 0.0000001;
    private static final double ERROR_TIME = 60000; //Within 60 seconds error (1 minute error)

    //So we config before we run tests. @ContextConfiguration points to this so it sets us up (normal test wouldn't hit config beans)
    @Configuration
    @PropertySource("api.properties")
    public static class TestConfig{
        @Autowired
        Environment env;

        @Bean
        public RestTemplate restTemplate(){
            return AppConfig.defaultRestTemplate();
        }

        @Bean
        public WeatherService weatherService(){
            WeatherService weatherService = new WeatherServiceImpl(
                    env.getProperty("weather.api.name"),
                    env.getProperty("weather.api.key"),
                    env.getProperty("weather.api.host")
            );
            return weatherService;
        }
    }

    @Before
    public void setUp(){
        loc = new Location(38.3494057, -75.5755258);
        weather = service.findByLocation(loc);
    }

    @Test
    public void findByLocation_ShouldReturnSameCoords() throws Exception{
        //See if weather.getLat is CLOSE TO response latitude with lenient Error (ERROR_GEO)
        assertThat(weather.getLatitude(), closeTo(loc.getLatitude(), ERROR_GEO));

        //(fetchedValueWeAreTestingFor, realValueItShouldBe)
        assertThat(weather.getLongitude(), closeTo(loc.getLongitude(), ERROR_GEO));
    }

    @Test
    public void findByLocation_ShouldReturn8DaysForecastData(){
        assertThat(weather.getDaily().getData(), hasSize(8));
    }

    @Test
    public void findByLocation_ShouldReturnCurrentConditions(){
        Instant now = Instant.now(); //Get Now's time
        double duration = Duration.between(now, weather.getCurrently().getTime()).toMillis();
        assertThat(duration, closeTo(0, ERROR_TIME)); //Margin of error between now and what weather fetched is no more than +- ERROR_TIME
    }

}
