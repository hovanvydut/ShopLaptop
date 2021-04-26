package com.shoplaptop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.shoplaptop.admin.user.repository.UserRepository;
import com.shoplaptop.common.entity.Role;
import com.shoplaptop.common.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Create user")
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = this.testEntityManager.find(Role.class, 1);

        User user1 = new User("admin@gmail.com", "123123", "Admin", "Super");
        user1.addRole(roleAdmin);

        User savedUser = this.userRepository.save(user1);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Create new User with two Roles")
    public void testCreateNewUserWithTwoRoles() {
        User user = new User("user1@gmail.com", "123123", "One", "User");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        user.addRole(roleEditor);
        user.addRole(roleAssistant);

        User savedUser = this.userRepository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getRoles().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("List all Users")
    public void testListAllUsers() {
        Iterable<User> listUsers = this.userRepository.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    @DisplayName("Get User by Id")
    public void testGetUserById() {
        User user = this.userRepository.findById(1).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("Update UserDetails")
    public void testUpdateUserDetails() {
        User user = this.userRepository.findById(2).get();
        user.setEnable(true);
        user.setEmail("user11@gmail.com");

        this.userRepository.save(user);
    }

    @Test
    @DisplayName("Update User Role")
    public void testUpdateUserRole() {
        User user = this.userRepository.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSalesPerson = new Role(2);

        user.getRoles().remove(roleEditor);
        user.getRoles().add(roleSalesPerson);

        this.userRepository.save(user);
    }

    @Test
    @DisplayName("Delete User")
    public void testDeleteUser() {
        this.userRepository.deleteById(2);

        this.userRepository.findById(2);
    }

    @Test
    @DisplayName("Get user by email")
    public void testGetUserByEmail() {
        String email = "hovanvydut@gmail.com";
        User user = this.userRepository.getUserByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("Count By Id")
    public void testCountById() {
        Integer id = 1;

        Integer countById = this.userRepository.countById(id);
        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    @DisplayName("Disable user")
    public void testDisableUser() {
        Integer id = 1;
        this.userRepository.updateEnableStatus(id, true);
    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = this.userRepository.findAll(pageable);

        List<User> listUsers = page.getContent();
        listUsers.forEach(System.out::println);
    }
}
