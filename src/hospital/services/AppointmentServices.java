package hospital.services;

import hospital.model.Appointment;
import hospital.repository.AppointmentRepo;

import java.util.List;

public class AppointmentServices {

    private final AppointmentRepo repo;

    public AppointmentServices() {
        this.repo = new AppointmentRepo();
    }

    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    public Appointment getAppointmentById(String id) {
        return repo.findById(id);
    }

    public boolean addAppointment(Appointment appointment) {
        if (appointment == null || appointment.getPatient() == null || appointment.getDoctor() == null) {
            return false;
        }
        return repo.save(appointment);
    }

    public boolean updateAppointment(Appointment appointment) {
        if (appointment == null || appointment.getId() == null) {
            return false;
        }
        return repo.update(appointment);
    }

    public boolean cancelAppointment(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.updateStatus(id, "Cancelled");
    }

    public boolean deleteAppointment(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.delete(id);
    }

    public List<Appointment> searchAppointments(String keyword, String status) {
        return repo.search(keyword, status);
    }

    public String getNextAppointmentId() {
        return repo.generateNextId();
    }
}
