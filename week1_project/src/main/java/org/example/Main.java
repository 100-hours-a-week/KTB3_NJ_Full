package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract class Person {
    private String name;
    private int age;
    private String gender;

    public String getName(){
        return this.name;
    }

    public int getAge(){
        return this.age;
    }

    public String getGender(){
        return this.gender;
    }

    public void greeting(){
        System.out.println("\n안녕하세요." + this.name + "라고 합니다.");
    }

    Person(String name, int age, String gender){
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}

abstract class Doctor extends Person{
    private String medicalSpecialty;

    public String getMedicalSpecialty(){
        return this.medicalSpecialty;
    }

    public void startTreatment(){
        System.out.println("진료 진행하겠습니다.");
    }

    public void finishTreatment(){
        System.out.println("진료 마치겠습니다. 밖에서 대기하셨다가 수납해주시면 됩니다.");
    }

    abstract public void processTreatment(Patient patient);

    Doctor(String name, int age, String gender, String medicalSpecialty){
        super(name, age, gender);
        this.medicalSpecialty = medicalSpecialty;
    }
}

class Patient extends Person{
    private String stage = "1";
    private String chooseSpeciality;
    private int payment = 0;

    public String getStage(){
        return this.stage;
    }
    public String getChooseSpeciality(){
        return this.chooseSpeciality;
    }
    public int getPayment(){
        return this.payment;
    }

    public void setStage(String stage){
        this.stage = stage;
    }
    public void setPayment(int payment){
        this.payment = payment;
    }

    Patient(String name, int age, String gender,String chooseSpeciality) {
        super(name, age, gender);
        this.chooseSpeciality = chooseSpeciality;
    }
}

class Nurse extends Person{
    public Patient processReception(Scanner answer){
        greeting();
        System.out.println("진료 접수하겠습니다.먼저 환자 정보 입력해주세요\n----------------------------------");
        System.out.print("이름을 적어주세요. 예) 김환자\n> ");
        String name = answer.nextLine();
        System.out.print("나이를 적어주세요. 예) 26\n> ");
        int age = Integer.parseInt(answer.nextLine());
        System.out.print("성별을 적어주세요. 예) 남/여\n> ");
        String gender;
        while (true) {
            gender = answer.nextLine();
            if (gender.equals("남") || gender.equals("여")) {
                break;
            }
            System.out.print("성별을 잘못 입력했습니다. 다시 입력해주세요! 예) 남/여\n> ");
        }
        System.out.print("어디 병원 찾으시나요?. 예) 외과/내과/치과\n> ");
        String chooseSpeciality;
        while (true) {
            chooseSpeciality = answer.nextLine().trim();
            if (chooseSpeciality.equals("외과") || chooseSpeciality.equals("내과") || chooseSpeciality.equals("치과")) {
                break;
            }
            System.out.print("병원을 잘못 입력했습니다. 다시 입력해주세요! 예) 외과/내과/치과\n> ");
        }
        System.out.println("----------------------------------");

        Patient newPatient = new Patient(name, age, gender, chooseSpeciality);

        System.out.printf("\n접수 완료: %s (%d세/%s)\n",newPatient.getName(), newPatient.getAge(), newPatient.getGender());
        System.out.println(newPatient.getChooseSpeciality() + "에 접수 완료되었어요! 잠시만 기다려주세요");
        return newPatient;
    }

    public Doctor guideTreatment(List<Doctor> doctors, Patient patient, Scanner answer) {
        System.out.printf("\n%s님! 진료 보실 차례입니다!\n", patient.getName());
        while(true){
            System.out.println("진료 받을 의사를 선택해주세요");
            List<Doctor> chooseDoctor = new ArrayList<>();;
            for (int i = 0; i < doctors.size(); i++) {
                Doctor d = doctors.get(i);
                if(d.getMedicalSpecialty().equals(patient.getChooseSpeciality())) {
                    chooseDoctor.add(d);
                    System.out.printf("[%d]%s ", chooseDoctor.size(), d.getName());
                }
            }
            System.out.print("\n> ");
            int chooseDoctorNum = Integer.parseInt(answer.nextLine());

            if (chooseDoctorNum < 1 || chooseDoctorNum > chooseDoctor.size()) {
                System.out.println("\n의사를 잘못 선택했습니다. 다시 선택해주세요!");
                continue;
            }
            return chooseDoctor.get(chooseDoctorNum-1);
        }
    }

    public void processPayment(Patient patient, Scanner answer){
        System.out.printf("\n%s님! 수납해주세요!\n", patient.getName());

        while(true){
            System.out.printf("%d원 지불해주세요!\n", patient.getPayment());
            System.out.print("> 지불 금액: ");
            int newPayment = patient.getPayment() - Integer.parseInt(answer.nextLine());
            patient.setPayment(newPayment);
            if(patient.getPayment() == 0) break;
            if(patient.getPayment() < 0){
                System.out.printf("\n거스름돈 %d원 드리겠습니다.", 0-patient.getPayment());
                break;
            }
        }
        System.out.println("\n지불 완료하였습니다!");
    }

    Nurse(String name, int age, String gender) {
        super(name, age, gender);
    }
}

class Surgeon extends Doctor{
    void performSurgery(){
        try {
            System.out.print("\n(..수술 중..)\n");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 발생 시 안전하게 처리
        }
    }

    @Override
    public void processTreatment(Patient patient) {
        greeting();
        startTreatment();
        performSurgery();
        finishTreatment();
        patient.setPayment(200000);
    }

    Surgeon(String name, int age, String gender, String medicalSpecialty) {
        super(name, age, gender, medicalSpecialty);
    }
}

class Physician extends Doctor{

    void performTreatment(){
        try {
            System.out.print("\n(..진료 중..)\n");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 발생 시 안전하게 처리
        }
    }

    @Override
    public void processTreatment(Patient patient) {
        greeting();
        startTreatment();
        performTreatment();
        finishTreatment();
        patient.setPayment(10000);
    }

    Physician(String name, int age, String gender, String medicalSpecialty) {
        super(name, age, gender, medicalSpecialty);
    }
}

class Dentist extends Doctor{
    void performDental(){

        try {
            System.out.print("\n(..이빨 뽑는 중..)\n");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 발생 시 안전하게 처리
        }
    }

    @Override
    public void processTreatment(Patient patient) {
        greeting();
        startTreatment();
        performDental();
        finishTreatment();
        patient.setPayment(50000);
    }

    Dentist(String name, int age, String gender, String medicalSpecialty) {
        super(name, age, gender, medicalSpecialty);
    }
}

public class Main{
    public static void main(String[] args) {
        List<Doctor> doctors = List.of(
                new Surgeon("김외과", 45, "남", "외과"),
                new Surgeon("박외과", 40, "남", "외과"),
                new Surgeon("이외과", 38, "여", "외과"),
                new Physician("김내과", 52, "남", "내과"),
                new Physician("박내과", 44, "여", "내과"),
                new Dentist("홍치과", 32, "남", "치과")
        );
        Nurse N1 = new Nurse("박간호사", 33, "여");
        Patient patient = null;

        Scanner scanner = new Scanner(System.in);

        System.out.println("<<<<<<<<<<카테부 종합병원>>>>>>>>>>\n");

        boolean enterHospital = true;
        System.out.println("안녕하세요, 카테부 병원입니다.어떻게 찾아오셨나요?");

        while(enterHospital){
            String answer = "";

            //환자 진행 상태 확인
            if(patient == null){
                System.out.println("처음 오실 경우 접수를 진행해주세요");
                System.out.print("[1] 접수 [0] 나가기\n> ");
                answer = scanner.nextLine();
            } else{
                answer = patient.getStage();
            }

            // 환자 진행 상태 및 대답에 따른 진행
            switch (answer) {
                case "1": // 접수
                    patient = N1.processReception(scanner);
                    pause();
                    patient.setStage("2");
                    break;
                case "2": // 진료
                    Doctor doctor = N1.guideTreatment(doctors, patient, scanner);
                    doctor.processTreatment(patient);
                    pause();
                    patient.setStage("3");
                    break;
                case "3": // 수납
                    N1.processPayment(patient, scanner);
                    patient.setStage("0");
                    break;
                case "0": // 병원 나가기
                    enterHospital = false;
                    break;
                default:
                    System.out.println("\n잘못된 번호입니다. 다시 선택해주세요!");
            }
        }
        System.out.println("감사합니다! 조심히 가세요!");
        scanner.close();
    }

    public static void pause() { // 기다리는 함수
        try {
            System.out.print("\n(..기다리는 중..)\n");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}