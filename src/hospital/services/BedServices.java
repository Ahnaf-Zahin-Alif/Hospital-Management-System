package hospital.services;

import hospital.model.Bed;
import hospital.repository.BedRepo;

import java.util.List;

public class BedServices {

    private final BedRepo repo;

    public BedServices() {
        this.repo = new BedRepo();
    }

    public List<Bed> getAllBeds() {
        return repo.findAll();
    }

    public Bed getBedById(String id) {
        return repo.findById(id);
    }

    public boolean addBed(Bed bed) {
        if (bed == null || bed.getBedNumber() == null || bed.getBedNumber().trim().isEmpty()) {
            return false;
        }
        return repo.save(bed);
    }

    public boolean updateBed(Bed bed) {
        if (bed == null || bed.getId() == null) {
            return false;
        }
        return repo.update(bed);
    }

    public boolean dischargePatient(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.dischargePatient(id);
    }

    public boolean deleteBed(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.delete(id);
    }

    public String getNextBedId() {
        return repo.generateNextId();
    }
}

