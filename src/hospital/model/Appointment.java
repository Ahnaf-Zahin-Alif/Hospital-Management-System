package hospital.model;

public class Appointment {
    private String id;
    private String patient;
    private String doctor;
    private String date;
    private String time;
    private String department;
    private String status;
    private String notes;

    public Appointment() {
    }

    public Appointment(String id, String patient, String doctor, String date, String time, String department, String status, String notes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.department = department;
        this.status = status;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatient() { return patient; }
    public void setPatient(String patient) { this.patient = patient; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
