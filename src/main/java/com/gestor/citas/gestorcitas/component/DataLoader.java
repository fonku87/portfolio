package com.gestor.citas.gestorcitas.component;

import com.gestor.citas.gestorcitas.entity.Appointment;
import com.gestor.citas.gestorcitas.entity.Product;
import com.gestor.citas.gestorcitas.entity.Role;
import com.gestor.citas.gestorcitas.entity.User;
import com.gestor.citas.gestorcitas.repository.AppointmentRepository;
import com.gestor.citas.gestorcitas.repository.ProductRepository;
import com.gestor.citas.gestorcitas.repository.RoleRepository;
import com.gestor.citas.gestorcitas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class DataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    private final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public DataLoader(final RoleRepository roleRepository, final ProductRepository productRepository, UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // *********************
        // ** ROLES
        // *********************
        Role roleUser = roleRepository.findByName("ROLE_USER");
        if(roleUser == null) { // Creamos rol usuario
            roleUser = new Role();
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);
        }

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        if(roleAdmin == null) { // Creamos rol administrador
            roleAdmin = new Role();
            roleAdmin.setName("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
        }

        // *********************
        // ** USUARIOS
        // *********************
        User userAdmin = userRepository.findByEmail("fonku@test.es"); // ADMINISTRADOR
        if(userAdmin == null) {
            userAdmin = new User();
            userAdmin.setEmail("fonku@test.es");
            userAdmin.setPhone("666112233");
            userAdmin.setFirstName("Jose");
            userAdmin.setLastName("Foncubierta");
            userAdmin.setRoles(Arrays.asList(roleAdmin));
            userAdmin.setPassword("123456");
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(userAdmin.getPassword());
            userAdmin.setPassword(encodedPassword);

            userRepository.save(userAdmin);
        }

        User user01 = userRepository.findByEmail("fany@test.es");
        if(user01 == null) {
            user01 = new User();
            user01.setEmail("fany@test.es");
            user01.setPhone("666223311");
            user01.setFirstName("Estefania");
            user01.setLastName("Rosado");
            user01.setRoles(Arrays.asList(roleUser));
            user01.setPassword("123456");
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user01.getPassword());
            user01.setPassword(encodedPassword);

            userRepository.save(user01);
        }

        // *********************
        // ** PRODUCTOS
        // *********************

        Map<String, String> productsDescriptions = new TreeMap<>();
        productsDescriptions.put("New Born", "Reportaje de fotos para los recien nacidos.");
        productsDescriptions.put("Comunión", "Reportaje de fotos para los pequeños que reciben por primera vez el sacramento de la Eucaristía");
        productsDescriptions.put("Boda", "Reportaje de fotos de la unión de dos personas en el matrimonio");
        productsDescriptions.put("Foto Carnet", "Fotografías en tamaño carnet para su DNI o pasaporte");
        productsDescriptions.put("Cumpleaños", "Reportaje de fotos para celebrar su gran día junto a sus familiares y amigos");
        productsDescriptions.put("Embarazadas", "Descripción Reportaje embarazadas");
        productsDescriptions.put("Pre-bodas", "Descripción Pre-Bodas");
        productsDescriptions.put("Post-bodas", "Descripción Post-Bodas");
        productsDescriptions.put("Navidad", "Reportaje navideños en nuestro estudio.");
        productsDescriptions.put("Bautizo", "Reportaje para recordar este día tan especial.");

        Map<String, Double> productsPrices = new TreeMap<>();
        productsPrices.put("New Born", 69.0);
        productsPrices.put("Comunión", 200.0);
        productsPrices.put("Boda", 1200.0 );
        productsPrices.put("Foto Carnet", 8.0);
        productsPrices.put("Cumpleaños", 59.0);
        productsPrices.put("Embarazadas", 59.0);
        productsPrices.put("Pre-bodas", 69.0);
        productsPrices.put("Post-bodas", 69.0);
        productsPrices.put("Navidad", 59.0);
        productsPrices.put("Bautizo", 200.0);

        for(Map.Entry<String, String> entry : productsDescriptions.entrySet()) {
            Product product = productRepository.findByName(entry.getKey());
            if (product == null) {
                product = new Product();
                product.setName(entry.getKey());
                product.setDescription(entry.getValue());
                product.setPrice(productsPrices.get(entry.getKey()));

                productRepository.save(product);
            }
        }

        // *********************
        // ** CITAS
        // *********************
        if (appointmentRepository.count() == 0) {

            Appointment appointment = new Appointment();
            appointment.setProduct(productRepository.findByName("Bautizo"));
            appointment.setUser(user01);
            appointment.setStartDate(parseTimestamp("2023-06-16 12:15:00"));
            appointment.setChecked(true);
            appointmentRepository.save(appointment);

            appointment = new Appointment();
            appointment.setProduct(productRepository.findByName("Comunion"));
            appointment.setUser(user01);
            appointment.setStartDate(parseTimestamp("2024-06-17 11:00:00"));
            appointmentRepository.save(appointment);

            appointment = new Appointment();
            appointment.setProduct(productRepository.findByName("Boda"));
            appointment.setUser(user01);
            appointment.setStartDate(parseTimestamp("2024-06-30 17:30:00"));
            appointmentRepository.save(appointment);

            // SIN RESERVAS
            appointment = new Appointment();
            appointment.setStartDate(parseTimestamp("2024-08-17 13:25:00"));
            appointmentRepository.save(appointment);

            appointment = new Appointment();
            appointment.setStartDate(parseTimestamp("2024-08-18 19:20:00"));
            appointmentRepository.save(appointment);
        }

    }

    private java.sql.Timestamp parseTimestamp(String timestamp) {
        try {
            return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
