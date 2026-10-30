package hospital.services;

import hospital.model.Doctor;
import hospital.repository.DoctorRepo;

import java.util.List;

public class DoctorServices {

    private final DoctorRepo repo;

    public DoctorServices() {
        this.repo = new DoctorRepo();
    }

    public List<Doctor> getAllDoctors() {
        return repo.findAll();
    }

    public Doctor getDoctorById(String id) {
        return repo.findById(id);
    }

    public boolean addDoctor(Doctor doctor) {
        if (doctor == null || doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            return false;
        }
        return repo.save(doctor);
    }

    public boolean updateDoctor(Doctor doctor) {
        if (doctor == null || doctor.getId() == null || doctor.getId().trim().isEmpty()) {
            return false;
        }
        return repo.update(doctor);
    }

    public boolean deleteDoctor(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.delete(id);
    }

    public List<Doctor> searchDoctors(String keyword, String specialty) {
        return repo.search(keyword, specialty);
    }

    public String getNextDoctorId() {
        return repo.generateNextId();
    }
}
