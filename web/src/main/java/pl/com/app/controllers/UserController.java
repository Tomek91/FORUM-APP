package pl.com.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.com.app.service.UserService;

@Controller
@RequestMapping( "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


}
