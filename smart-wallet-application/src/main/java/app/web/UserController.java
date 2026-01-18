package app.web;


import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.EditProfileRequest;
import app.web.dto.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @RequireAdminRole  анотация, която ние сме направили в security пакета

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllUsers() {

        List<User> allUsers = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", allUsers );
        return modelAndView;
    }


    @GetMapping("/{id}/profile")
    public ModelAndView getEditUserPage(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView();

        User userById = userService.getUserById(id);

        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("user", userById );// трябва ни user-a, който прави промените в профила си
        modelAndView.addObject("editProfile", DtoMapper.mapUserToEditProfileRequest(userById));



        return modelAndView;
    }


    @PutMapping("/{id}/profile")
    public ModelAndView editUser(@PathVariable UUID id,@Valid EditProfileRequest editProfileRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            User userById = userService.getUserById(id);

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile-menu");
            modelAndView.addObject("user", userById );// ако не добавим user, ще хвърли грешка, зашото в този html е използвано user, освен editProfileRequest
            modelAndView.addObject("editProfile", editProfileRequest);
            return modelAndView;
        }


            userService.editUserDetails(id, editProfileRequest);

        return new ModelAndView("redirect:/home") ;
    }

    @PutMapping("/{id}/status")
    public String switchUserStatus(@PathVariable UUID id) {

        userService.switchStatus(id);

        return "redirect:/users" ;
    }


    @PutMapping("/{id}/role")
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/users" ;
    }
}

