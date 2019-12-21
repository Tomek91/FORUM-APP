package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.com.app.dto.SearchSubjectDTO;
import pl.com.app.dto.SubjectDTO;
import pl.com.app.service.AnswerService;
import pl.com.app.service.GeoIPLocationService;
import pl.com.app.service.SubjectService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping()
@RequiredArgsConstructor
public class MainController {

    private final SubjectService subjectService;
    private final AnswerService answerService;
    private final GeoIPLocationService locationService;

    @GetMapping({"/", "", "index"})
    public String welcome(@RequestParam("page") Optional<Integer> page,
                          @RequestParam("size") Optional<Integer> size,
                          Model model) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(3);

        Page<SubjectDTO> subjectDTOPage = subjectService.findPage(
                PageRequest.of(currentPage - 1, pageSize, Sort.by("dateTimeStart").descending()));


        List<Long> subjectIds = getSubjectIds(subjectDTOPage.getContent());

        model.addAttribute("page", subjectDTOPage);
        model.addAttribute("answers", answerService.getGroupedAnswers(subjectIds));
        model.addAttribute("lastAnswer", answerService.getLastAnswerBySubject(subjectIds));
        model.addAttribute("searchSubjects", new SearchSubjectDTO());

        return "index";
    }

    @PostMapping(value = "search")
    public String searchSubject(@ModelAttribute SearchSubjectDTO searchSubjectDTO,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (searchSubjectDTO.getTitle().isBlank()) {
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("searchSubjectDTO", searchSubjectDTO);
        return "redirect:/result-search";
    }

    @GetMapping("/result-search")
    public String resultSearch(Model model) {
        Map<String, Object> objectMap = model.asMap();
        SearchSubjectDTO searchSubjectDTO = (SearchSubjectDTO) objectMap.get("searchSubjectDTO");
        List<SubjectDTO> subjectsByTitle = subjectService.findSubjectsByTitle(searchSubjectDTO);
        List<Long> subjectIds = getSubjectIds(subjectsByTitle);

        model.addAttribute("searchSubjects", subjectsByTitle);
        model.addAttribute("answers", answerService.getGroupedAnswers(subjectIds));
        model.addAttribute("lastAnswer", answerService.getLastAnswerBySubject(subjectIds));
        return "resultSearch";
    }

    private List<Long> getSubjectIds(List<SubjectDTO> subjects){
        return subjects
                .stream()
                .map(SubjectDTO::getId)
                .collect(Collectors.toList());
    }

    @GetMapping("/notFound")
    public String notFound() {
        return "error-page-404";
    }

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "security/accessDenied";
    }

    @GetMapping("dashboard")
    public String dashboard(Model model) {

        model.addAttribute("geo", locationService.getLocation());
        return "dashboard";
    }

}
