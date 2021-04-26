package com.shoplaptop.admin.user.repository;

import com.shoplaptop.common.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    @Query("SELECT u FROM User u where u.email = :email")
    public User getUserByEmail(@Param("email") String email);

    public Integer countById(Integer Id);

    @Query("UPDATE User u SET u.enable = ?2 WHERE u.id = ?1")
    @Modifying
    public void updateEnableStatus(Integer id, boolean enable);

}
