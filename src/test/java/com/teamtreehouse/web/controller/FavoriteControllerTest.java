package com.teamtreehouse.web.controller;

import com.teamtreehouse.domain.Favorite;
import com.teamtreehouse.service.FavoriteNotFoundException;
import com.teamtreehouse.service.FavoriteService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static com.teamtreehouse.domain.Favorite.FavoriteBuilder;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.Assert.*;

// IMPORTANT: The controller tests should have no dependency on other layers...this is why we MOCK
//instead of using the actual services and DAO's. We isolate functionalities so that tests are legitimate
//and narrow to EXACTLY what we are testing

@RunWith(MockitoJUnitRunner.class) //Let's us use annotations in the main class for MockitoJUnitRunner vs JUnit JunitUnitRunner
public class FavoriteControllerTest {

    private MockMvc mockMvc;

    @InjectMocks //From Mockito...makes instance of Favorite Controller & Injects any fields annotated with @Mock
    private FavoriteController controller;

    @Mock //This favorite service will be populated by mock object & injected into FavoriteController
    private FavoriteService service;

    //Set up all objects that we will test initially
    @Before
    public void setUp() {
        mockMvc = new MockMvcBuilders().standaloneSetup(controller).build();
    }

    @Test
    public void index_ShouldIncludeFavoritesInTheModel() throws Exception{
        // Arrange the mock behavior (sample list of favorite objects)
        List<Favorite> favorites = Arrays.asList(
                new FavoriteBuilder(1L).withAddress("Chicago").withPlaceId("chicago1").build(),
                new FavoriteBuilder(2L).withAddress("Nebraska").withPlaceId("omaha1").build()
        );
        //When 'service' findAll method is called then return list of favorites
        when(service.findAll()).thenReturn(favorites);

        // Act (perform the MVC request) and assert the result
        mockMvc.perform(get("/favorites"))
                .andExpect(status().isOk())
                .andExpect(view().name("favorite/index"))
                .andExpect(model().attribute("favorites", favorites)); //Checks to see if models list matches the one above

        //Verify that the service's findAll() method was actually called in controller method
        verify(service).findAll();
    }

    @Test
    public void add_ShouldRedirectToNewFavorite() throws Exception{
        // Arrange the mock behavior
        //Mockito method...'invocation' is the favorite passed into the save() method we are mocking (that's why we cast)
        //This answer will be given WHEN service .save() is called with ANY Favorite passed in
        doAnswer(invocation -> {
            Favorite f = (Favorite) invocation.getArguments()[0];
            f.setId(1L);
            return null; //Interface method requires a return although the method we are stubbing is void
        }).when(service).save(any(Favorite.class));

        // Act (perform the MVC request) and assert the result
        mockMvc.perform(
                post("/favorites")
                    .param("formattedAddress", "Chicago", "il") //Just adding parameters to post
                    .param("placeId", "windycity")
        ).andExpect(redirectedUrl("/favorites/1"));

        verify(service).save(any(Favorite.class));
    }

    @Test
    public void detail_ShouldErrorOnNotFound() throws Exception{
        // Arrange the mock behavior
        when(service.findById(1L)).thenThrow(FavoriteNotFoundException.class);

        // Act (perform the MVC request) and assert the result
        mockMvc.perform(get("/favorites/1"))
                .andExpect(view().name("error"))
                .andExpect(model().attribute("ex", Matchers.instanceOf(FavoriteNotFoundException.class)));
        verify(service).findById(1L);
    }

}