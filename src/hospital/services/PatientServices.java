package hospital.services;

import hospital.model.Patient;
import hospital.repository.PatientRepo;

import java.util.List;

public class PatientServices {

    private final PatientRepo repo;

    public PatientServices() {
        this.repo = new PatientRepo();
    }

    public List<Patient> getAllPatients() {
        return repo.findAll();
    }

    public Patient getPatientById(String id) {
        return repo.findById(id);
    }

    public boolean addPatient(Patient patient) {
        if (patient == null || patient.getName() == null || patient.getName().trim().isEmpty()) {
            return false;
        }
        return repo.save(patient);
    }

    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getId() == null || patient.getId().trim().isEmpty()) {
            return false;
        }
        return repo.update(patient);
    }

    public boolean deletePatient(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.delete(id);
    }

    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPatients();
        }
        return repo.search(keyword.trim());
    }

    public String getNextPatientId() {
        return repo.generateNextId();
    }
}
