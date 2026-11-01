package hospital.model;

public class Bill {
    private String id;
    private String patient;
    private String doctor;
    private double consultationFee;
    private double treatmentFee;
    private double medicineFee;
    private double totalAmount;
    private String status;
    private String paymentMethod;
    private String billDate;

    public Bill() {
    }

    public Bill(String id, String patient, String doctor, double consultationFee, double treatmentFee, 
                double medicineFee, double totalAmount, String status, String paymentMethod, String billDate) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.consultationFee = consultationFee;
        this.treatmentFee = treatmentFee;
        this.medicineFee = medicineFee;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.billDate = billDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatient() { return patient; }
    public void setPatient(String patient) { this.patient = patient; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public double getTreatmentFee() { return treatmentFee; }
    public void setTreatmentFee(double treatmentFee) { this.treatmentFee = treatmentFee; }

    public double getMedicineFee() { return medicineFee; }
    public void setMedicineFee(double medicineFee) { this.medicineFee = medicineFee; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }
}

