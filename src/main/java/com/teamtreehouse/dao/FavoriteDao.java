package com.teamtreehouse.dao;

import com.teamtreehouse.domain.Favorite;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//Spring data makes the implementations when we boot the application

@Repository
public interface FavoriteDao extends CrudRepository<Favorite,Long> {
    @Query("select f from Favorite f where f.user.id=:#{principal.id}")
    List<Favorite> findAll();

    @Query("select f from Favorite f where f.user.id=:#{principal.id} and f.placeId=:#{#placeId}")
    Favorite findByPlaceId(@Param("placeId") String placeId);

    @Modifying //Indicates a method should be regarded as modifying query.
    @Transactional //Ensures changes are rolled back if error occurs
    //nativeQuery tells Spring that the value (SQL statement) will be in native SQL as opposed to the above queries
    @Query(nativeQuery = true, value = "insert into favorite (user_id,formattedAddress,placeId) values (:#{principal.id},:#{#favorite.formattedAddress},:#{#favorite.placeId})")
    int saveForCurrentUser(@Param("favorite") Favorite favorite);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from favorite where id=:#{#id} and user_id=:#{principal.id}")
    int deleteForCurrentUser(@Param("id") Long id);
}