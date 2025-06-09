package com.gestor.citas.gestorcitas.controller;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PDFService pdfService;

    @GetMapping("/products")
    public String listProducts(Model model) {

        boolean isAdmin = loginService.hasRoleAdmin();

        if (isAdmin) {
            List<Product> listProducts = productRepository.findAll();
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("isAdmin", isAdmin);
            return "products";
        } else {
            return "appointments";
        }
    }

    @GetMapping("/product_delete")
    public String deleteProduct(@RequestParam String productID) {

        // Buscamos info del producto
        Product product = productRepository.findById(Long.parseLong(productID));

        // Borramos citas relacionadas con el producto
        appointmentRepository.deleteAllByProduct(product);

        // Borramos el producto
        productRepository.delete(product);

        return "redirect:/products";
    }

    @GetMapping("product_create")
    public String createProduct(Model model) {
        boolean isAdmin = loginService.hasRoleAdmin();
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("product", new Product());

        return "product_create";
    }

    @GetMapping("product_edit")
    public String editProduct(@RequestParam String productID, Model model) {
        boolean isAdmin = loginService.hasRoleAdmin();
        model.addAttribute("isAdmin", isAdmin);

        Product product = productRepository.findById(Long.parseLong(productID));
        model.addAttribute("product", product);

        return "product_edit";
    }

    @PostMapping("/product_save")
    public String saveProduct(Product product, Model model) {

        // Comprobamos si el nombre se encuentra registrado
        Product productBBDD = productRepository.findByName(product.getName());

        if (productBBDD != null) {
            model.addAttribute("error", "Existe un producto con ese mismo nombre.");
            return "product_create";
        }

        productRepository.save(product);

        return "redirect:/products";
    }

    @PostMapping("/product_update")
    public String updateProduct(Product product, Model model) {

        Product productBBDD = productRepository.findByNameEqualsAndIdNot(product.getName(), product.getId());

        if (productBBDD != null) {
            model.addAttribute("error", "Existe un producto con ese mismo nombre.");
            return "product_edit";
        }

        productRepository.save(product);

        return "redirect:/products";

    }

    @GetMapping("/product-pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Product> listProducts = productRepository.findAll();

        pdfService.exportProducts(response, listProducts);
    }
}
