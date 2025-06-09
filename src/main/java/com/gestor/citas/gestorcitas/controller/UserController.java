package com.gestor.citas.gestorcitas.controller;

import com.gestor.citas.gestorcitas.entity.Appointment;
import com.gestor.citas.gestorcitas.entity.Role;
import com.gestor.citas.gestorcitas.entity.User;
import com.gestor.citas.gestorcitas.repository.AppointmentRepository;
import com.gestor.citas.gestorcitas.repository.RoleRepository;
import com.gestor.citas.gestorcitas.repository.UserRepository;
import com.gestor.citas.gestorcitas.service.LoginService;
import com.gestor.citas.gestorcitas.service.PDFService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PDFService pdfService;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/users")
    public String listUsers(Model model) {

        boolean isAdmin = loginService.hasRoleAdmin();

        if (isAdmin) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            List<User> listUsers = userRepository.findAllByRolesNot(adminRole);
            model.addAttribute("listUsers", listUsers);
            model.addAttribute("isAdmin", isAdmin);
            return "users";
        } else {
            return "appointments";
        }
    }
    @PostMapping("/process_register")
    public String processRegister(User user, Model model) {

        // Comprobamos si el email se encuentra registrado
        User userBBDD = userRepository.findByEmail(user.getEmail());

        if (userBBDD != null) {
            model.addAttribute("error", "Existe un usuario con ese mismo email.");
            return "signup_form";
        }

        // Comprobamos si el telefono se encuentra registrado
        userBBDD = userRepository.findByPhone(user.getPhone());
        if (userBBDD != null) {
            model.addAttribute("error", "Existe un usuario con ese mismo teléfono.");
            return "signup_form";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = checkRoleExist();
        }

        user.setRoles(Arrays.asList(role));

        userRepository.save(user);

        return "register_success";
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    @GetMapping("/users-pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = userRepository.findAll();

        pdfService.exportUsers(response, listUsers);
    }

    @GetMapping("/users-appointments.pdf")
    public void exportAppointmentsToPDF(@RequestParam String userID, HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_appointments_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = userRepository.findAll();

        User user = userRepository.findById(Long.parseLong(userID));

        List<Appointment> appointments = appointmentRepository.findAllByUser(user);

        pdfService.exportUsersAppointments(response, user, appointments);
    }
}
