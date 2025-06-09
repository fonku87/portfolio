package com.gestor.citas.gestorcitas.controller;

import com.gestor.citas.gestorcitas.entity.Appointment;
import com.gestor.citas.gestorcitas.entity.Product;
import com.gestor.citas.gestorcitas.entity.User;
import com.gestor.citas.gestorcitas.repository.AppointmentRepository;
import com.gestor.citas.gestorcitas.repository.ProductRepository;
import com.gestor.citas.gestorcitas.service.LoginService;
import com.gestor.citas.gestorcitas.service.PDFService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PDFService pdfService;

    @GetMapping("/appointments")
    public String appointments(Model model) {

        if(loginService.isLoggedUser()) {

            User userLogged = loginService.getInfoUserLogged();

            boolean isAdmin = loginService.hasRole("ROLE_ADMIN");
            model.addAttribute("isAdmin", isAdmin);

            List<Appointment> appointmentList;
            if (!isAdmin) {
                // Buscamos los registros del cliente
                appointmentList = appointmentRepository.findAllByUserOrderByStartDateAsc(userLogged);
            } else {
                // Buscamos los registros a partir del dia actual y que esten libres
                appointmentList = appointmentRepository.findByStartDateAfterAndCheckedIsFalseAndProductIsNullAndUserIsNullOrderByStartDateAsc
                        (new java.sql.Date(new Date().getTime()));
            }

            model.addAttribute("appointmentList", appointmentList);

            return "appointments";

        } else {

            return "redirect:/register";

        }
    }

    @GetMapping("/appointments-pending")
    public String appointmentsPending(Model model) {

        User userLogged = loginService.getInfoUserLogged();

        boolean isAdmin = loginService.hasRole("ROLE_ADMIN");
        model.addAttribute("isAdmin", isAdmin);

        List<Appointment> appointmentList;
        if (!isAdmin) {
            return "redirect:/appointments";
        } else {
            // Buscamos los registros pendientes de confirmar
            appointmentList = appointmentRepository.findByCheckedIsFalseAndProductNotNullAndUserNotNullOrderByStartDateAsc();
        }

        model.addAttribute("appointmentList", appointmentList);

        return "appointments-pending";
    }

    @GetMapping("/appointments-confirmed")
    public String appointmentsConfirmed(Model model) {

        User userLogged = loginService.getInfoUserLogged();

        boolean isAdmin = loginService.hasRole("ROLE_ADMIN");
        model.addAttribute("isAdmin", isAdmin);

        List<Appointment> appointmentList;
        if (!isAdmin) {
            return "redirect:/appointments";
        } else {
            // Buscamos los registros confirmados por el administrador
            appointmentList = appointmentRepository.findByStartDateAfterAndCheckedIsTrueOrderByIdAsc(new java.sql.Date(new Date().getTime()));
        }

        model.addAttribute("appointmentList", appointmentList);

        return "appointments-confirmed";
    }

    @GetMapping("/appointment-create")
    public String createAppointment(Model model) {
        boolean isAdmin = loginService.hasRoleAdmin();

        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            model.addAttribute("appointment", new Appointment());
            return "appointment_create";
        } else {

            // Cargamos todas las citas disponibles
            List<Appointment> appointmentList = appointmentRepository.findByStartDateAfterAndCheckedIsFalseAndProductIsNullAndUserIsNullOrderByStartDateAsc(new java.sql.Date(new Date().getTime()));

            // Agrupamos las citas por fecha y horas
            Map<String, Map<String, Long>> mapDatesAppointment = new TreeMap<>();
            for(Appointment a : appointmentList) {
                Map<String, Long> auxMap;
                if (mapDatesAppointment.get(a.getStrDate()) != null) {
                    auxMap = mapDatesAppointment.get(a.getStrDate());
                    auxMap.put(a.getStrTime(), a.getId());
                } else {
                    auxMap = new TreeMap<>();
                    auxMap.put(a.getStrTime(), a.getId());
                    mapDatesAppointment.put(a.getStrDate(), auxMap);
                }
            }

            model.addAttribute("mapDatesAppointment", mapDatesAppointment);

            List<Product> productList = productRepository.findAll();
            model.addAttribute("productList", productList);

            return "appointment_update";
        }
    }

    @PostMapping("/appointment_save")
    public String saveAppointment(@RequestParam String startDate) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date date = df.parse(startDate, new ParsePosition(0));

        Appointment appointment = new Appointment();
        appointment.setStartDate(new java.sql.Timestamp(date.getTime()));

        appointmentRepository.save(appointment);

        return "redirect:/appointments";
    }

    @GetMapping("/appointment_update")
    public String updateAppointment(@RequestParam String appointmentID, @RequestParam String productID) {

        User userLogged = loginService.getInfoUserLogged();
        Product product = productRepository.findById(Long.parseLong(productID));
        Appointment appointment = appointmentRepository.findById(Long.parseLong(appointmentID));

        appointment.setProduct(product);
        appointment.setUser(userLogged);

        appointmentRepository.save(appointment);

        return "redirect:/appointments";
    }

    @GetMapping("/appointment_confirm")
    public String confirmAppointment(@RequestParam String appointmentID) {

        Appointment appointment = appointmentRepository.findById(Long.parseLong(appointmentID));
        appointment.setChecked(true); // Confirmada
        appointment.setStatus(true); // Aprobada

        appointmentRepository.save(appointment);

        return "redirect:/appointments";
    }

    @GetMapping("/appointment_declined")
    public String declineAppointment(@RequestParam String appointmentID) {

        Appointment appointment = appointmentRepository.findById(Long.parseLong(appointmentID));
        appointment.setChecked(true); // Confirmada
        appointment.setStatus(false); // Rechazada

        appointmentRepository.save(appointment);

        return "redirect:/appointments";
    }

    @GetMapping("/appointment-confirmed-pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=appointments_confirmed_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Appointment> appointmentList = appointmentRepository.findByStartDateAfterAndCheckedIsTrueOrderByIdAsc(new java.sql.Date(new Date().getTime()));

        pdfService.exportAppointments(response, appointmentList);
    }
}
