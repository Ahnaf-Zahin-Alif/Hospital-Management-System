package hospital.model;

public class Patient {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String bloodGroup;
    private String phone;
    private String diagnosis;
    private String address;

    public Patient() {
    }

    public Patient(String id, String name, int age, String gender, String bloodGroup, String phone, String diagnosis, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.diagnosis = diagnosis;
        this.address = address;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
