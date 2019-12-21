package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.com.app.authentication.AuthenticationFacade;
import pl.com.app.dto.AnswerDTO;
import pl.com.app.dto.SubjectDTO;
import pl.com.app.dto.UserDTO;
import pl.com.app.service.AnswerService;
import pl.com.app.service.SubjectService;
import pl.com.app.service.UserService;
import pl.com.app.service.WeatherService;
import pl.com.app.validators.SubjectValidator;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;
    private final AnswerService answerService;
    private final UserService userService;
    private final WeatherService weatherService;
    private final SubjectValidator subjectValidator;
    private final AuthenticationFacade authenticationFacade;


    @InitBinder("subjectDTO")
    private void initSubjectBinder(WebDataBinder binder) {
        binder.setValidator(subjectValidator);
    }

    @GetMapping(value = "/add")
    public String addSubject(Model model) {
        model.addAttribute("subject", new SubjectDTO());
        model.addAttribute("errors", new HashMap<>());

        return "subjects/add";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add")
    public String addSubject(@Valid @ModelAttribute(name = "subjectDTO") SubjectDTO subjectDTO,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subject", subjectDTO);
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
            return "subjects/add";
        }
        UserDTO loggedUser = authenticationFacade.getLoggedUser();
        subjectDTO.setUserDTO(loggedUser);
        subjectService.addSubject(subjectDTO);
        return "redirect:/";
    }

    @GetMapping(value = "/{id}")
    public String editSubject(@PathVariable Long id,
                              Model model) {
        SubjectDTO subject = subjectService.getOneSubject(id);
        model.addAttribute("subject", subject);
        List<AnswerDTO> answersForSubject = answerService.getAnswersForSubject(id);
        model.addAttribute("answers", answersForSubject);
        model.addAttribute("weatherForSubject", weatherService.getWeatherForUser(subject.getUserDTO()));
        model.addAttribute("weather", weatherService.getWeatherForUsers(answersForSubject));
        model.addAttribute("errors", new HashMap<>());

        return "subjects/edit";
    }

    @GetMapping(value = "/modify/{id}")
    public String modifySubject(@PathVariable Long id,
                                Model model) {
        model.addAttribute("subject", subjectService.getOneSubject(id));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("errors", new HashMap<>());
        return "subjects/modify";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/modify")
    public String modifySubject(@Valid @ModelAttribute(name = "subjectDTO") SubjectDTO subjectDTO,
                                BindingResult bindingResult,
                                Model model) {

        subjectValidator.validate(subjectDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("subject", subjectDTO);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
            return "subjects/modify";
        }
        subjectService.modifySubject(subjectDTO);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/delete")
    public String deleteSubject(@RequestParam Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/";
    }

}
