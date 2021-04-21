package com.shoplaptop.admin.user.service;

import com.shoplaptop.admin.common.exception.UserNotFoundException;
import com.shoplaptop.admin.user.repository.RoleRepository;
import com.shoplaptop.admin.user.repository.UserRepository;
import com.shoplaptop.common.entity.Role;
import com.shoplaptop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return (List<User>) this.userRepository.findAll();
    }

    public List<Role> listRoles() {
        return (List<Role>) this.roleRepository.findAll();
    }

    public void save(User user) {
        boolean updateMode = (user.getId() != null);

        if (updateMode) {
            if (user.getPassword().isEmpty()) {
                User existingUser = this.userRepository.findById(user.getId()).get();

                user.setPassword(existingUser.getPassword());
                // still use old password
            } else {
                // set new password
                encodePassword(user);
            }
        } else {
            // Create new user MODE
            encodePassword(user);
        }

        this.userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User user = this.userRepository.getUserByEmail(email);
        boolean isCreateNewMODE = (id == null);

        if (user == null) return true;

        if (isCreateNewMODE) {
            // Create new user MODE
            if (user == null) {
                return true;
            } else {
                return false;
            }
        } else {
            // Edit MODE
            if (user.getId() != id) {
                return false;
            } else {
                return true;
            }
        }
    }

    public User get(Integer id) throws UserNotFoundException {
        try {
            return this.userRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any user with id = " + id);
        }
    }

    public void delete(Integer id) throws UserNotFoundException{
        Integer countById = this.userRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with Id: " + id);
        }

        this.userRepository.deleteById(id);
    }

    public void updateUserEnableStatus(Integer id, boolean enable) {
        this.userRepository.updateEnableStatus(id, enable);
    }
}
