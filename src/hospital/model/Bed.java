package hospital.model;

public class Bed {
    private String id;
    private String wardName;
    private String bedNumber;
    private String patient;
    private String status;
    private double dailyCharge;
    private String admissionDate;

    public Bed() {
    }

    public Bed(String id, String wardName, String bedNumber, String patient, String status, double dailyCharge, String admissionDate) {
        this.id = id;
        this.wardName = wardName;
        this.bedNumber = bedNumber;
        this.patient = patient;
        this.status = status;
        this.dailyCharge = dailyCharge;
        this.admissionDate = admissionDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWardName() { return wardName; }
    public void setWardName(String wardName) { this.wardName = wardName; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public String getPatient() { return patient; }
    public void setPatient(String patient) { this.patient = patient; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getDailyCharge() { return dailyCharge; }
    public void setDailyCharge(double dailyCharge) { this.dailyCharge = dailyCharge; }

    public String getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(String admissionDate) { this.admissionDate = admissionDate; }
}

