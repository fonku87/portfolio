package com.gestor.citas.gestorcitas.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Temporal(TemporalType.TIMESTAMP)
    private java.sql.Timestamp startDate;

    @ManyToOne
    @JoinColumn(name="product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @Column(columnDefinition = "boolean default false")
    private Boolean checked = false;

    @Column(columnDefinition = "boolean default false")
    private Boolean status = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStrDate() {
        String strDate = startDate.toString().split(" ")[0];

        DateFormat dateFormatTable = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date date = dateFormatTable.parse(strDate, new ParsePosition(0));

        return date == null ? "" : dateFormat.format(date);
    }

    public String getStrTime() {
        String strTime = startDate.toString().split(" ")[1];

        DateFormat dateFormatTable = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Date date = dateFormatTable.parse(strTime, new ParsePosition(0));

        return date == null ? "" : dateFormat.format(date);
    }
}
