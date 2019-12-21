package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.com.app.authentication.AuthenticationFacade;
import pl.com.app.dto.MailDTO;
import pl.com.app.dto.UserDTO;
import pl.com.app.dto.UserResetPasswordDTO;
import pl.com.app.service.LoginService;
import pl.com.app.service.UserService;
import pl.com.app.validators.MailValidator;
import pl.com.app.validators.PasswordsValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final LoginService loginService;
    private final MailValidator mailValidator;
    private final PasswordsValidator passwordsValidator;
    private final AuthenticationFacade authenticationFacade;

    @InitBinder("mailDTO")
    private void initMailBinder(WebDataBinder binder) {
        binder.setValidator(mailValidator);
    }

    @InitBinder("userResetPasswordDto")
    private void initPasswordBinder(WebDataBinder binder) {
        binder.setValidator(passwordsValidator);
    }


    @GetMapping()
    public String login(Model model) {
        model.addAttribute("error", "");
        return "login/loginPage";
    }

    @GetMapping("/error")
    public String loginError(Model model) {
        model.addAttribute("error", "Login error");
        return "login/loginPage";
    }

    @GetMapping("/remind-password")
    public String remindPassword(Model model) {
        model.addAttribute("mail", new MailDTO());
        model.addAttribute("errors", new HashMap<>());
        model.addAttribute("errorsGlobal", new ArrayList<>());
        return "login/remindPassword";
    }

    @PostMapping("/remind-password")
    public String remindPassword(@Valid @ModelAttribute(name = "mailDTO") MailDTO mailDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpServletRequest request) {
        if (bindingResult.hasErrors()){
            model.addAttribute("mail", mailDTO);
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getCode)));
            model.addAttribute("errorsGlobal", bindingResult
                    .getGlobalErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
            return "login/remindPassword";
        }
        loginService.sendRemindPasswordMail(mailDTO, request);
        return "redirect:/index";
    }

    @GetMapping("/remind-password/reset")
    public String remindPasswordConfirmation(@RequestParam String token,
                                             Model model) {
        UserResetPasswordDTO userResetPasswordDto = loginService.remindPasswordConfirmation(token);
        model.addAttribute("user", userResetPasswordDto);
        model.addAttribute("errors", new HashMap<>());
        model.addAttribute("errorsGlobal", new ArrayList<>());
        return "login/resetRemindPassword";
    }

    @PostMapping("/remind-password/reset")
    public String remindPasswordConfirmation(@Valid @ModelAttribute(name = "userResetPasswordDto") UserResetPasswordDTO userResetPasswordDto,
                                             BindingResult bindingResult,
                                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userResetPasswordDto);
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
            model.addAttribute("errorsGlobal", bindingResult
                    .getGlobalErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
            return "login/resetRemindPassword";
        }
        loginService.resetRemindPassword(userResetPasswordDto);
        return "redirect:/index";
    }


    @GetMapping("/reset")
    public String resetPassword(Model model) {

        UserDTO loggedUser = authenticationFacade.getLoggedUser();

        model.addAttribute("user", UserResetPasswordDTO.builder().id(loggedUser.getId()).build());
        model.addAttribute("errors", new HashMap<>());
        model.addAttribute("errorsGlobal", new ArrayList<>());
        return "login/resetPassword";
    }

    @PostMapping("/reset")
    public String resetPassword(@Valid @ModelAttribute(name = "userResetPasswordDto") UserResetPasswordDTO userResetPasswordDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userResetPasswordDto);
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
            model.addAttribute("errorsGlobal", bindingResult
                    .getGlobalErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
            return "login/resetPassword";
        }
        loginService.resetPassword(userResetPasswordDto);
        return "redirect:/index";
    }
}
