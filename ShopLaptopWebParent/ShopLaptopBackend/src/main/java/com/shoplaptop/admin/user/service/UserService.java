package com.shoplaptop.admin.user.service;

import com.shoplaptop.admin.common.exception.UserNotFoundException;
import com.shoplaptop.admin.user.repository.RoleRepository;
import com.shoplaptop.admin.user.repository.UserRepository;
import com.shoplaptop.common.entity.Role;
import com.shoplaptop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 4;
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

    public User save(User user) {
        boolean updateMode = (user.getId() != null);

        if (updateMode) {
            Optional<User> userOpt = this.userRepository.findById(user.getId());

            if (user.getPassword().isEmpty()) {
                // still use old password
                userOpt.ifPresent(existingUser ->
                        user.setPassword(existingUser.getPassword()));
            } else {
                // set new password
                encodePassword(user);
            }

            if (user.getPhotos() == null) {
                // still use old picture
                userOpt.ifPresent(existingUser -> user.setPhotos(existingUser.getPhotos()));
            }
        } else {
            // Create new user MODE
            encodePassword(user);
        }

        return this.userRepository.save(user);
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

    public Page<User> listByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, USERS_PER_PAGE, sort);

        if (keyword != null) {
            return this.userRepository.findAll(keyword, pageable);
        }

        return this.userRepository.findAll(pageable);
    }
}
