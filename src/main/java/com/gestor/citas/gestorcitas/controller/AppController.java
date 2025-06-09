package com.gestor.citas.gestorcitas.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.gestor.citas.gestorcitas.entity.Role;
import com.gestor.citas.gestorcitas.entity.User;
import com.gestor.citas.gestorcitas.repository.RoleRepository;
import com.gestor.citas.gestorcitas.repository.UserRepository;
import com.gestor.citas.gestorcitas.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {

    private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private LoginService loginService;

    @GetMapping("")
    public String viewHomePage(Model model) {

        if (loginService.isLoggedUser()) {
            boolean isAdmin = loginService.hasRole("ROLE_ADMIN");
            model.addAttribute("isAdmin", isAdmin);
        }

        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        if (loginService.isLoggedUser()) {
            boolean isAdmin = loginService.hasRole("ROLE_ADMIN");
            model.addAttribute("isAdmin", isAdmin);
        }

        model.addAttribute("user", new User());

        return "signup_form";
    }
}
