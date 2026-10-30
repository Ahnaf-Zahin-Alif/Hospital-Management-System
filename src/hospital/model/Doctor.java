package hospital.model;

public class Doctor {
    private String id;
    private String name;
    private String specialty;
    private String qualification;
    private String phone;
    private String email;
    private String fee;
    private String room;
    private String visitingHours;

    public Doctor() {
    }

    public Doctor(String id, String name, String specialty, String qualification, String phone, String email, String fee, String room, String visitingHours) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.qualification = qualification;
        this.phone = phone;
        this.email = email;
        this.fee = fee;
        this.room = room;
        this.visitingHours = visitingHours;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getVisitingHours() { return visitingHours; }
    public void setVisitingHours(String visitingHours) { this.visitingHours = visitingHours; }
}
