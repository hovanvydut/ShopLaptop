package com.shoplaptop.admin.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.shoplaptop.common.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
}
