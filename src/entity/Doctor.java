package entity;
import java.util.*;

public class Doctor implements User {
    private String name;
    private String password;
    private ArrayList<Patient> patients;

    public void Doctor(String name, String password, ArrayList<Patient> patients) {
        this.name = name;
        this.password = password;
        this.patients = patients;
    }
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    public void addPatient(Patient new_patient) {
        this.patients.add(new_patient);
    }

    public void removePatient(Patient patient) {
        this.patients.remove(patient);
    }
}
