package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.com.app.dto.UserDTO;
import pl.com.app.service.RegistrationService;
import pl.com.app.service.RoleService;
import pl.com.app.validators.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final RoleService roleService;
    private final UserValidator userValidator;


    @InitBinder("userDTO")
    private void initUserBinder(WebDataBinder binder) {
        binder.setValidator(userValidator);
    }


    @GetMapping("/add-user")
    public String addUser(Model model) {
        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("errors", new HashMap<>());
        model.addAttribute("errorsGlobal", new ArrayList<>());
        return "registration/registration";
    }

    @PostMapping("/add-user")
    public String addUser(@Valid @ModelAttribute(name = "userDTO") UserDTO userDTO,
                          BindingResult bindingResult,
                          Model model,
                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDTO);
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getCode)));
            model.addAttribute("errorsGlobal", bindingResult
                    .getGlobalErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
            return "registration/registration";
        }
        registrationService.registerNewUser(userDTO, request);
        return "redirect:/index";
    }


    @GetMapping("/registerConfirmation")
    public String registrationConfirmation(@RequestParam String token) {
        registrationService.confirmRegistration(token);
        return "redirect:/login";
    }
}
