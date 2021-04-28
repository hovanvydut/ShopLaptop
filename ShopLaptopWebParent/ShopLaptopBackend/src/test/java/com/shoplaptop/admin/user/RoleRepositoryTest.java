package com.shoplaptop.admin.user;


import com.shoplaptop.admin.user.repository.RoleRepository;
import com.shoplaptop.common.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository repo;

    @Test
    @DisplayName("Create Admin Role")
    public void testCreateFirstRole() {
        Role roleAdmin = new Role("ADMIN", "Manage everything");
        Role savedRole = this.repo.save(roleAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Create rest Roles")
    public void createRestRoles() {
        Role roleSalesPersons = new Role("SALESPERSON", "Manage Product price, Customer, Shipping" +
                "orders and sales reports");
        Role roleEditor = new Role("EDITOR", "Manage categories, brands, products, " +
                "articles and menus");
        Role roleShipper = new Role("SHIPPER", "View product, view orders, and update order status");
        Role roleAssistant = new Role("ASSISTANT", "Manage question and reviews");

        this.repo.saveAll(List.of(roleSalesPersons, roleEditor, roleShipper, roleAssistant));
    }
}
