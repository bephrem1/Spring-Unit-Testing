package com.teamtreehouse.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.teamtreehouse.Application;
import com.teamtreehouse.domain.Favorite;
import com.teamtreehouse.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static com.teamtreehouse.domain.Favorite.FavoriteBuilder;


@RunWith(SpringJUnit4ClassRunner.class) //Makes sure @SpringApplicationConfiguration annotation is ran
@SpringApplicationConfiguration(Application.class) //Makes this an integration test & we tell it which class contains top level configurations to set up in test
                                                    //Runs full application context.
@DatabaseSetup("classpath:favorites.xml") //Db Unit Injects starter data into mock database (H2 Database)
                                          //Can be used on individual methods too but on class it is applied for every method
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class, //Needed so Autowired components can be injects from application context
    DbUnitTestExecutionListener.class //Needed so that Db unit can inject data into our database
}) //Defines a listener API for acting to test execution events
public class FavoriteDaoTest {
    @Autowired
    private FavoriteDao dao;

    @Before
    public void setUp(){
        User user = new User();
        user.setId(1L); //We will be using user 1
        //Tell Spring that this is an authenticated user before out tests
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Test
    public void findAll_ShouldReturnTwoFavorites() throws Exception{
        assertThat(dao.findAll(), hasSize(2));
    }

    @Test
    public void save_ShouldPersistEntity() throws Exception{
        String placeId = "salisbury";
        Favorite fave = new FavoriteBuilder().withPlaceId(placeId).build();
        dao.saveForCurrentUser(fave);
        assertThat(dao.findByPlaceId(placeId), notNullValue(Favorite.class));
    }

}
