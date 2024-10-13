package system;

import javafx.beans.property.*;

public class Student {
    private final StringProperty name;
    private final StringProperty gender;
    private final StringProperty email;
    private final StringProperty contactNumber;
    private final StringProperty address;
    private final IntegerProperty mathematicsMarks;
    private final IntegerProperty physicsMarks;
    private final IntegerProperty chemistryMarks;
    private final StringProperty admissionNumber;
    private final StringProperty semester;
    private final IntegerProperty totalMarks;

    public Student(String name, String gender, String email, String contactNumber, String address, int mathematicsMarks, int physicsMarks, int chemistryMarks, String admissionNumber, String semester) {
        this.name = new SimpleStringProperty(name);
        this.gender = new SimpleStringProperty(gender);
        this.email = new SimpleStringProperty(email);
        this.contactNumber = new SimpleStringProperty(contactNumber);
        this.address = new SimpleStringProperty(address);
        this.mathematicsMarks = new SimpleIntegerProperty(mathematicsMarks);
        this.physicsMarks = new SimpleIntegerProperty(physicsMarks);
        this.chemistryMarks = new SimpleIntegerProperty(chemistryMarks);
        this.admissionNumber = new SimpleStringProperty(admissionNumber);
        this.semester = new SimpleStringProperty(semester);
        this.totalMarks = new SimpleIntegerProperty(mathematicsMarks + physicsMarks + chemistryMarks);
    }

    public String getName() {
        return name.get();
    }

    public String getGender() {
        return gender.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getContactNumber() {
        return contactNumber.get();
    }

    public String getAddress() {
        return address.get();
    }

    public int getMathematicsMarks() {
        return mathematicsMarks.get();
    }

    public int getPhysicsMarks() {
        return physicsMarks.get();
    }

    public int getChemistryMarks() {
        return chemistryMarks.get();
    }

    public String getAdmissionNumber() {
        return admissionNumber.get();
    }

    public String getSemester() {
        return semester.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty contactNumberProperty() {
        return contactNumber;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public IntegerProperty mathematicsMarksProperty() {
        return mathematicsMarks;
    }

    public IntegerProperty physicsMarksProperty() {
        return physicsMarks;
    }

    public IntegerProperty chemistryMarksProperty() {
        return chemistryMarks;
    }

    public StringProperty admissionNumberProperty() {
        return admissionNumber;
    }

    public StringProperty semesterProperty() {
        return semester;
    }

    public int getTotalMarks() {
        return totalMarks.get();
    }

    public IntegerProperty totalMarksProperty() {
        return totalMarks;
    }
}