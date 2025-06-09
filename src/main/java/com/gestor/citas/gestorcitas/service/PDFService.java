package com.gestor.citas.gestorcitas.service;

import com.gestor.citas.gestorcitas.entity.Appointment;
import com.gestor.citas.gestorcitas.entity.Product;
import com.gestor.citas.gestorcitas.entity.User;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PDFService {

    public void exportUsersAppointments(HttpServletResponse response, User user, List<Appointment> appointments) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Datos Usuario", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        List<String> headers = Arrays.asList(new String[]{"User ID", "E-mail", "Full Name", "Telefono"});

        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 1.5f, 2.5f, 2.5f});
        table.setSpacingBefore(10);

        List<User> users = new ArrayList<>();
        users.add(user);
        writeTableHeader(table, headers);
        writeTableUsers(table, users);

        document.add(table);

        p = new Paragraph("Historial citas", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        headers = Arrays.asList(new String[]{"Fecha", "Hora", "Producto", "Revisado", "Estado"});

        table = new PdfPTable(headers.size());
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 1.5f, 3.5f, 1.5f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table, headers);
        writeTableAppointments(table, appointments);

        document.add(table);
        document.close();
    }

    public void exportAppointments(HttpServletResponse response, List<Appointment> appointments) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Citas confirmadas", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        List<String> headers = Arrays.asList(new String[]{"Fecha", "Hora", "Producto", "Cliente", "Telefono Contacto"});

        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 1.5f, 2.5f, 2.5f, 2.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table, headers);
        writeTableAppointmentsConfirmed(table, appointments);

        document.add(table);

        document.close();
    }

    public void exportProducts(HttpServletResponse response, List<Product> products) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Lista de Productos", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        List<String> headers = Arrays.asList(new String[]{"Product ID", "Nombre", "Precio", "Descripcion"});

        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 2.5f, 1.5f, 4.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table, headers);
        writeTableProducts(table, products);

        document.add(table);

        document.close();
    }

    public void exportUsers(HttpServletResponse response, List<User> users) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Lista de Clientes", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        List<String> headers = Arrays.asList(new String[]{"User ID", "E-mail", "Full Name", "Phone"});

        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table, headers);
        writeTableUsers(table, users);

        document.add(table);

        document.close();
    }

    private void writeTableAppointments(PdfPTable table, List<Appointment> appointments) {
        for(Appointment appointment : appointments) {
            table.addCell(appointment.getStrDate());
            table.addCell(appointment.getStrTime());
            table.addCell(appointment.getProduct().getName());
            table.addCell(appointment.getChecked() ? "Si" : "No");
            table.addCell(!appointment.getChecked() ? "Pendiente" : (appointment.getStatus() ? "Aprobada" : "Rechazada"));
        }
    }

    private void writeTableAppointmentsConfirmed(PdfPTable table, List<Appointment> appointments) {
        for(Appointment appointment : appointments) {
            table.addCell(appointment.getStrDate());
            table.addCell(appointment.getStrTime());
            table.addCell(appointment.getProduct().getName());
            table.addCell(appointment.getUser().getDisplayName());
            table.addCell(appointment.getUser().getPhone());
        }
    }

    private void writeTableUsers(PdfPTable table, List<User> users) {
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getDisplayName());
            table.addCell(user.getPhone());
        }
    }

    private void writeTableProducts(PdfPTable table, List<Product> products) {
        for (Product product : products) {
            table.addCell(String.valueOf(product.getId()));
            table.addCell(product.getName());
            table.addCell(Double.toString(product.getPrice()));
            table.addCell(product.getDescription());
        }
    }

    private void writeTableHeader(PdfPTable table, List<String> headers) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        for(String header : headers) {
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }

}
