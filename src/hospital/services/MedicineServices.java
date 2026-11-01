package hospital.services;

import hospital.model.Medicine;
import hospital.repository.MedicineRepo;

import java.util.List;

public class MedicineServices {

    private final MedicineRepo repo;

    public MedicineServices() {
        this.repo = new MedicineRepo();
    }

    public List<Medicine> getAllMedicines() {
        return repo.findAll();
    }

    public Medicine getMedicineById(String id) {
        return repo.findById(id);
    }

    public boolean addMedicine(Medicine medicine) {
        if (medicine == null || medicine.getName() == null || medicine.getName().trim().isEmpty()) {
            return false;
        }
        return repo.save(medicine);
    }

    public boolean updateMedicine(Medicine medicine) {
        if (medicine == null || medicine.getId() == null) {
            return false;
        }
        return repo.update(medicine);
    }

    public boolean adjustStock(String id, int quantity) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.updateStock(id, quantity);
    }

    public boolean deleteMedicine(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.delete(id);
    }

    public String getNextMedicineId() {
        return repo.generateNextId();
    }
}

