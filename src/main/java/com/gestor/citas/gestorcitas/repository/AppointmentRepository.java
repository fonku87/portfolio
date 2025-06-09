package com.gestor.citas.gestorcitas.repository;

import com.gestor.citas.gestorcitas.entity.Appointment;
import com.gestor.citas.gestorcitas.entity.Product;
import com.gestor.citas.gestorcitas.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

@Transactional
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Appointment findById(long id);

    List<Appointment> findByUser(User user);

    List<Appointment> findAllByUser(User user);

    List<Appointment> findAllByUserOrderByStartDateAsc(User user);

    List<Appointment> findByStartDateAfterAndCheckedIsFalseAndProductIsNullAndUserIsNullOrderByStartDateAsc(Date startDate);

    List<Appointment> findByCheckedIsFalseAndProductNotNullAndUserNotNullOrderByStartDateAsc();

    List<Appointment> findByStartDateAfterAndCheckedIsTrueOrderByIdAsc(Date startDate);

    void deleteAllByProduct(Product product);
}
