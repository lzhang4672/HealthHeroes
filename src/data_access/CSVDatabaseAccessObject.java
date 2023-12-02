package data_access;
import entity.*;
import entity.Drug;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;


import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;


public class CSVDatabaseAccessObject implements SignupUserDataAccessInterface, CSVDatabaseAccessInterface, LoginUserDataAccessInterface {


    private final String[] patient_headers = {"id", "full_name", "height", "weight", "appointment_date", "date_added", "prescribed_drugs",
            "allergies", "illnesses", "symptoms", "lifestyle_information", "isPregnant", "additional_notes"};
    private final String[] doctor_headers = {"username", "password", "patients"};
    private Map<Integer, Patient> patients = new HashMap<>();
    private String[] allPatientIDs;
    private final String filePath;
    private String username;
    private String password;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public CSVDatabaseAccessObject(String doctorFilePath) throws IOException{
        this.filePath = doctorFilePath;
        File doctorFile = new File(doctorFilePath);
        if (doctorFile.length() == 0) {
            save();
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(doctorFile))) {
                reader.readLine();
                this.username = String.valueOf(reader.readLine());
                reader.readLine();
                this.password = String.valueOf(reader.readLine());
                reader.readLine();
                String[] patientList = String.valueOf(reader.readLine()).split(",");
                for (String patientID : patientList) {
                    patients.put(Integer.parseInt(patientID), readPatientFromCSV(new File("data/Patient " + patientID + ".csv")));
                }
            }
        }
    }


    public Patient readPatientFromCSV(File csvFile) throws  IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            reader.readLine();
            int id = Integer.parseInt(String.valueOf(reader.readLine()));
            reader.readLine();
            String fullName = String.valueOf(reader.readLine());
            reader.readLine();
            float height = Float.parseFloat(String.valueOf(reader.readLine()));
            reader.readLine();
            float weight = Float.parseFloat(String.valueOf(reader.readLine()));
            reader.readLine();
            String[] appointmentDates = String.valueOf(reader.readLine()).split(",");
            reader.readLine();
            LocalDate dateAdded = LocalDate.parse(String.valueOf(reader.readLine()), formatter);
            reader.readLine();
            String[] drugs = String.valueOf(reader.readLine()).split(",");
            reader.readLine();
            String[] allergies = String.valueOf(reader.readLine()).split(",");
            reader.readLine();
            String[] illnesses = String.valueOf(reader.readLine()).split(",");
            reader.readLine();
            String[] symptoms = String.valueOf(reader.readLine()).split(",");
            reader.readLine();
            String lifestyleInfomation = String.valueOf(reader.readLine());
            reader.readLine();
            boolean isPregnant = Boolean.parseBoolean(String.valueOf(reader.readLine()));
            reader.readLine();
            String additionalNotes = String.valueOf(reader.readLine());
            return new Patient(id, fullName, height, weight, getDates(appointmentDates), dateAdded, getDrugs(drugs),
                    new ArrayList<>(List.of(allergies)),
                    new ArrayList<>(List.of(illnesses)),
                    new ArrayList<>(List.of(symptoms)),
                    lifestyleInfomation, isPregnant, additionalNotes);
        }
    }


    public ArrayList<LocalDate> getDates(String[] dates) {
        ArrayList<LocalDate> localDates = new ArrayList<>();
        for (String date : dates) {
            localDates.add(LocalDate.parse(date, formatter));
        }
        return localDates;
    }


    public ArrayList<Drug> getDrugs(String[] drugs) {
        ArrayList<Drug> drugsList = new ArrayList<>();
        for (String drug : drugs) {
            String[] drug_info = drug.split(" ");
            // System.out.println(Arrays.toString(drug_info));
            drugsList.add(new Drug(drug_info[0], Float.parseFloat(drug_info[1]),
                    LocalDate.parse(drug_info[2]),
                    LocalDate.parse(drug_info[3])));
        }
        return drugsList;
    }


    @Override
    public Patient getPatient(int id) {
        return patients.get(id);
    }


    @Override
    public Map<Integer, Patient> getAllPatients() {
        return this.patients;
    }

    @Override
    public void savePatients() {
        BufferedWriter writer;
        try {
            for (Patient patient: patients.values()) {
                String file_path = "Patient " + patient.getID();
                writer = new BufferedWriter(new FileWriter(file_path, false));
                String[] patientData = new String[]{String.valueOf(patient.getID()), patient.fullName, String.valueOf(patient.getHeight()),
                        String.valueOf(patient.getWeight()), patient.getAppointmentDatesAsString(), patient.getDateAdded().toString(),
                        patient.getPrescribedDrugsAsString(), patient.getAllergiesAsString(), patient.getIllnessesAsString(),
                        patient.getSymptomsAsString(), patient.getLifestyleInformation(), String.valueOf(patient.getIsPregnant()),
                        patient.getAdditionalNotes()};
                for (int i = 0; i < patient_headers.length; i++) {
                    writer.write(patient_headers[i]);
                    writer.newLine();
                    writer.write(patientData[i]);
                }
                writer.close();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public boolean existsById(int id) {
        return patients.containsKey(id);
    }

    /**
     *
     * @param identifier
     * @return returns whether there already exists a doctor with the identifier
     */

    @Override
    public boolean existsByName(String identifier) {
        return false;
    }

    @Override
    public void saveNewDoctor() {

    }


    @Override
    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write("username");
            writer.newLine();
            writer.write(username);
            writer.newLine();
            writer.write("password");
            writer.write(password);
            writer.newLine();
            writer.write("patients");
            writer.newLine();
            String temp = "";
            for (int id : patients.keySet()) {
                temp += "," + id;
            }
            writer.write(temp);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Doctor get(String username) {
        return null;
    }

    @Override
    public void deletePatient(int id) {

    }

    @Override
    public void addPatient(Patient patient) {
        this.patients.put(patient.getID(), patient);
        this.savePatients();
    }
}

