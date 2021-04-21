package com.shoplaptop.admin.user.controller;

import com.shoplaptop.admin.common.exception.UserNotFoundException;
import com.shoplaptop.admin.user.service.UserService;
import com.shoplaptop.common.entity.Role;
import com.shoplaptop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = this.userService.listAll();
        model.addAttribute("listUsers", listUsers);

        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = this.userService.listRoles();

        User user = new User();
        user.setEnable(true);

        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "CREATE NEW USER");

        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        this.userService.save(user);
        redirectAttributes.addFlashAttribute(
                "message", "A new user is created successfully.");

        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Integer id,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        try {
            User user = this.userService.get(id);
            model.addAttribute("user", user);

            List<Role> listRoles = this.userService.listRoles();
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("pageTitle", "UPDATE USER");

            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/users";
        }

    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id,
                             RedirectAttributes redirectAttributes) {
        try {
            this.userService.delete(id);
            redirectAttributes.addFlashAttribute("message",
                    "The user Id " + id + " has been deleted successfully!");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/users";
    }
}
