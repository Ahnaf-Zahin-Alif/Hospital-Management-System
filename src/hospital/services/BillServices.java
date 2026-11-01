package hospital.services;

import hospital.model.Bill;
import hospital.repository.BillRepo;

import java.util.List;

public class BillServices {

    private final BillRepo repo;

    public BillServices() {
        this.repo = new BillRepo();
    }

    public List<Bill> getAllBills() {
        return repo.findAll();
    }

    public Bill getBillById(String id) {
        return repo.findById(id);
    }

    public boolean addBill(Bill bill) {
        if (bill == null || bill.getPatient() == null || bill.getPatient().trim().isEmpty()) {
            return false;
        }
        return repo.save(bill);
    }

    public boolean updateBill(Bill bill) {
        if (bill == null || bill.getId() == null) {
            return false;
        }
        return repo.update(bill);
    }

    public boolean deleteBill(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repo.delete(id);
    }

    public String getNextBillId() {
        return repo.generateNextId();
    }
}

