package com.teamtreehouse.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WeatherControllerTest {

    private MockMvc mockMvc;
    private WeatherController controller;

    //Set up all objects that we will test initially
    @Before
    public void setUp(){
        controller = new WeatherController();
        mockMvc = new MockMvcBuilders().standaloneSetup(controller).build();
    }

    //Using our mockMvc we perform a get on "/" and EXPECT it to return to us the expected view name weather/detail
    @Test
    public void home_ShouldRenderDetailView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(view().name("weather/detail"));
    }

    //Entering 60657 as the param 'q' into "/search" method, we expect this to redirect to /search/60657
    @Test
    public void search_ShouldRedirectWithPathParam() throws Exception{
        mockMvc.perform(get("/search").param("q", "60657"))
                .andExpect(redirectedUrl("/search/60657"));
    }

}
