package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.com.app.authentication.AuthenticationFacade;
import pl.com.app.dto.AnswerDTO;
import pl.com.app.dto.SubjectDTO;
import pl.com.app.dto.UserDTO;
import pl.com.app.exceptions.AccessDeniedException;
import pl.com.app.exceptions.ExceptionCode;
import pl.com.app.service.AnswerService;
import pl.com.app.service.UserService;
import pl.com.app.validators.AnswerValidator;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final UserService userService;
    private final AnswerValidator answerValidator;
    private final AuthenticationFacade authenticationFacade;

    @InitBinder("answerDTO")
    private void initAnswerBinder(WebDataBinder binder) {
        binder.setValidator(answerValidator);
    }

    @PostMapping("/subject")
    public String answerToSubject(@RequestParam Long subjectId,
                                  RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("subjectId", subjectId);
        return "redirect:/answers/add";
    }

    @GetMapping(value = "/add")
    public String addAnswerToSubject(Model model) {
        Map<String, Object> stringObjectMap = model.asMap();
        Long subjectId = (Long)stringObjectMap.get("subjectId");
        if (subjectId == null){
            throw new AccessDeniedException(ExceptionCode.ACCESS_DENIED, "You can't be here");
        }

        model.addAttribute("answer",
                AnswerDTO
                .builder()
                .subjectDTO(
                        SubjectDTO.builder().id(subjectId).build()
                ).build()
        );
        model.addAttribute("errors", new HashMap<>());

        return "answers/add";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add")
    public String addAnswerToSubject(@Valid @ModelAttribute(name = "answerDTO") AnswerDTO answerDTO,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answerDTO);
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getCode)));
            return "answers/add";
        }
        UserDTO loggedUser = authenticationFacade.getLoggedUser();
        answerDTO.setUserDTO(loggedUser);
        answerService.addAnswer(answerDTO);

        return "redirect:/subjects/" + answerDTO.getSubjectDTO().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/grade/negative")
    public String addNegativeGradeToAnswer(@RequestParam Long id,
                                           @RequestParam Long subjectId) {

        answerService.addNegativeGradeToAnswer(id);
        return "redirect:/subjects/" + subjectId;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/grade/positive")
    public String addPositiveGradeToAnswer(@RequestParam Long id,
                                           @RequestParam Long subjectId) {

        answerService.addPositiveGradeToAnswer(id);
        return "redirect:/subjects/" + subjectId;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/delete")
    public String deleteAnswer(@RequestParam Long id,
                               @RequestParam Long subjectId) {
        answerService.deleteAnswer(id);
        return "redirect:/subjects/" + subjectId;
    }

    @GetMapping(value = "/modify/{id}")
    public String modifyAnswer(@PathVariable Long id,
                               Model model) {
        model.addAttribute("answer", answerService.getOneAnswer(id));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("errors", new HashMap<>());
        return "answers/modify";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/modify")
    public String modifyAnswer(@Valid @ModelAttribute(name = "answerDTO") AnswerDTO answerDTO,
                               BindingResult bindingResult,
                               Model model) {

        answerValidator.validate(answerDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answerDTO);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("errors", bindingResult
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getCode)));
            return "answers/modify";
        }
        answerService.modifyAnswer(answerDTO);
        return "redirect:/subjects/" + answerDTO.getSubjectDTO().getId();
    }
}

