package com.shoplaptop.admin.user.controller;

import com.shoplaptop.admin.FileUploadUtil;
import com.shoplaptop.admin.UserCsvExporter;
import com.shoplaptop.admin.UserExcelExporter;
import com.shoplaptop.admin.UserPdfExporter;
import com.shoplaptop.admin.common.exception.UserNotFoundException;
import com.shoplaptop.admin.user.service.UserService;
import com.shoplaptop.common.entity.Role;
import com.shoplaptop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model, "firstName", "asc", null);
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
    public String saveUser(User user,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            if (multipartFile.getSize() > (1024 * 1024)) {
                redirectAttributes.addFlashAttribute("message", "" +
                        "Size file must be less than 1MB");

                return "redirect:/users";
            }

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            User savedUser = this.userService.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos() == null || user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            this.userService.save(user);
        }

        redirectAttributes.addFlashAttribute(
                "message", "A new user is created successfully.");

        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
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

    @GetMapping("/users/{id}/enable/{status}")
    public String updateUserEnableStatus(@PathVariable("id") Integer id,
                                         @PathVariable("status") boolean status,
                                         RedirectAttributes redirectAttributes) {
        this.userService.updateUserEnableStatus(id, status);

        String statusMsg = status ? "enabled" : "disabled";
        String msg = String.format("The user ID %d has been %s", id, statusMsg);
        redirectAttributes.addFlashAttribute("message", msg);

        return "redirect:/users";
    }

    @GetMapping("/users/page/{pageNumber}")
    public String listByPage(@PathVariable("pageNumber") int pageNumber, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword) {

        if(sortField == null || sortField.isEmpty() || sortField.isBlank()) {
            sortField = "firstName";
        }

        if (sortDir == null || sortDir.isEmpty() || sortDir.isBlank()) {
            sortDir = "asc";
        }

        Page<User> page = this.userService.listByPage(pageNumber, sortField, sortDir, keyword);
        List<User> list = page.getContent();

        long startCount = (long) (pageNumber - 1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount + UserService.USERS_PER_PAGE - 1;

        if (endCount > page.getTotalPages()) {
            endCount = page.getTotalElements();
        }

        String reverseOrder = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage" , pageNumber);
        model.addAttribute("listUsers", list);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseOrder", reverseOrder);
        model.addAttribute("keyword", keyword);

        return "users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = this.userService.listAll();
        UserCsvExporter exporter = new UserCsvExporter();

        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = this.userService.listAll();
        UserExcelExporter exporter = new UserExcelExporter();

        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = this.userService.listAll();
        UserPdfExporter exporter = new UserPdfExporter();

        exporter.export(listUsers, response);
    }
}
