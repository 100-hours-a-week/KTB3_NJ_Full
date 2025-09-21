package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

enum State {
    ENTERED("병원 입장"),
    IN_RECEPTION("접수 중"),
    WAIT_TREATMENT("진료 대기"),
    IN_TREATMENT("진료 중"),
    WAIT_PAYMENT("수납 대기"),
    IN_PAYMENT("수납 중"),
    LEFT("병원 퇴장");

    private final String state;

    public String getState() {
        return state;
    }

    State(String state) {
        this.state = state;
    }
}
enum Department {
    SURGERY("외과"),
    INTERNAL("내과"),
    DENTAL("치과");

    private String department;

    public String getDepartment() {
        return department;
    }

    Department(String department) {
        this.department = department;
    }
}
enum Gender {
    MALE("남"),
    FEMALE("여");

    private String gender;

    public String getGender() {
        return gender;
    }

    Gender(String gender) {
        this.gender = gender;
    }
}

abstract class Person {
    private String name;
    private int age;
    private Gender gender;

    public String getName(){
        return this.name;
    }
    public int getAge(){
        return this.age;
    }
    public Gender getGender(){
        return this.gender;
    }

    public void setName(String name){ this.name = name;}
    public void setAge(int age){ this.age = age;}
    public void setGender(Gender gender){this.gender = gender;}

    public void greeting(){
        System.out.println("\n안녕하세요." + this.name + "라고 합니다.");
    }

    Person(String name, int age, Gender gender){
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    Person(){}
}

abstract class Doctor extends Person{
    private Department medicalDepartment;

    public Department getMedicalDepartment(){
        return this.medicalDepartment;
    }

    public void startTreatment(){
        System.out.println("진료 진행하겠습니다.");
    }

    public void finishTreatment(){
        System.out.println("진료 마치겠습니다. 밖에서 대기하셨다가 수납해주시면 됩니다.");
    }

    abstract public void processTreatment(Patient patient);

    Doctor(String name, int age, Gender gender, Department medicalDepartment){
        super(name, age, gender);
        this.medicalDepartment = medicalDepartment;
    }
}

class Patient extends Person{
    private volatile State state;
    private Department chooseDepartment;
    private int payment = 0;

    public State getState(){
        return state;
    }
    public Department getChooseDepartment(){
        return this.chooseDepartment;
    }
    public int getPayment(){
        return this.payment;
    }

    public void setState(State state){
        this.state = state;
    }
    public void setPayment(int payment){
        this.payment = payment;
    }
    public void setChooseDepartment(Department chooseDepartment){ this.chooseDepartment = chooseDepartment;}

    public Patient(String name, int age, Gender gender, Department chooseDepartment, State state) {
        super(name, age, gender);
        this.chooseDepartment = chooseDepartment;
        this.state = state;
    }

    public Patient(State state) {
        super();
        this.state = state;
    }
}

class Nurse extends Person{
    private final ReceptionDesk receptionDesk = new ReceptionDesk();
    private final PaymentDesk paymentDesk = new PaymentDesk();

    public void processReception(Scanner sc, Patient patient) {
        greeting();
        receptionDesk.reception(sc, patient);
    }

    public Doctor guideTreatment(List<Doctor> doctors, Patient patient, Scanner sc) {
        return receptionDesk.chooseDoctor(doctors, patient, sc);
    }

    public void processPayment(Patient patient, Scanner sc) {
        paymentDesk.pay(patient, sc);
    }

    Nurse(String name, int age, Gender gender) {
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

    Surgeon(String name, int age, Gender gender, Department medicalDepartment) {
        super(name, age, gender, medicalDepartment);
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

    Physician(String name, int age, Gender gender, Department medicalDepartment) {
        super(name, age, gender, medicalDepartment);
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

    Dentist(String name, int age, Gender gender, Department medicalDepartment) {
        super(name, age, gender, medicalDepartment);
    }
}

class ReceptionDesk{
    public void reception(Scanner answer, Patient patient){
        System.out.println("진료 접수하겠습니다.먼저 환자 정보 입력해주세요\n----------------------------------");

        System.out.print("이름을 적어주세요. 예) 김환자\n> ");
        String name = answer.nextLine().trim();

        int age = 0;
        while (age < 1) {
            System.out.print("나이를 적어주세요. 예) 26\n> ");
            try {
                age = Integer.parseInt(answer.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\n나이는 숫자로 입력해주세요!");
            }
        }

        Gender gender = null;
        while (gender == null) {
            System.out.print("성별을 입력해주세요. [1]남 [2]여\n> ");
            String input = answer.nextLine().trim();
            switch (input) {
                case "1" -> gender = Gender.MALE;
                case "2" -> gender = Gender.FEMALE;
                default -> System.out.println("\n잘못 입력했습니다. 1 또는 2를 선택해주세요.");
            }
        }

        Department department = null;
        while (department == null) {
            System.out.print("어떤 진료과를 찾으시나요? [1]외과 [2]내과 [3]치과\n> ");
            String input = answer.nextLine().trim();
            switch (input) {
                case "1" -> department = Department.SURGERY;
                case "2" -> department = Department.INTERNAL;
                case "3" -> department = Department.DENTAL;
                default -> System.out.println("\n잘못 입력했습니다. 1, 2, 3 중에서 선택해주세요.");
            }
        }

        System.out.println("----------------------------------");

        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setChooseDepartment(department);

        System.out.printf("\n접수 완료: %s (%d세/%s)\n",patient.getName(), patient.getAge(), patient.getGender());
        System.out.println(patient.getChooseDepartment() + "에 접수 완료되었어요! 잠시만 기다려주세요");
    }
    public Doctor chooseDoctor(List<Doctor> doctors, Patient patient, Scanner answer) {
        System.out.printf("\n%s님! 진료 보실 차례입니다!\n", patient.getName());
        while(true){
            System.out.println("진료 받을 의사를 선택해주세요");
            List<Doctor> chooseDoctor = new ArrayList<>();;
            for (int i = 0; i < doctors.size(); i++) {
                Doctor d = doctors.get(i);
                if(d.getMedicalDepartment().equals(patient.getChooseDepartment())) {
                    chooseDoctor.add(d);
                    System.out.printf("[%d]%s ", chooseDoctor.size(), d.getName());
                }
            }
            System.out.print("\n> ");
            int chooseDoctorNum = Integer.parseInt(answer.nextLine().trim());

            if (chooseDoctorNum < 1 || chooseDoctorNum > chooseDoctor.size()) {
                System.out.println("\n의사를 잘못 선택했습니다. 다시 선택해주세요!");
                continue;
            }
            return chooseDoctor.get(chooseDoctorNum-1);
        }
    }
}

class PaymentDesk{
    public void pay(Patient patient, Scanner answer){
        System.out.printf("\n%s님! 수납해주세요!\n", patient.getName());

        while(true){
            System.out.printf("%d원 지불해주세요!\n", patient.getPayment());
            System.out.print("> 지불 금액: ");
            int newPayment = patient.getPayment() - Integer.parseInt(answer.nextLine().trim());
            patient.setPayment(newPayment);
            if(patient.getPayment() == 0) break;
            if(patient.getPayment() < 0){
                System.out.printf("\n거스름돈 %d원 드리겠습니다.", 0-patient.getPayment());
                break;
            }
        }
        System.out.println("\n지불 완료하였습니다!");
    }
}

class CheckTime implements Runnable {
    private final AtomicInteger progressedTime = new AtomicInteger(0);

    public int getTime() {
        return progressedTime.get();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
                progressedTime.incrementAndGet(); // 1초마다 값만 증가
            }
        } catch (InterruptedException ignored) { }
    }
}

class CheckStatus implements Runnable {
    private Patient patient;
    private CheckTime time;

    CheckStatus(Patient patient, CheckTime time) {
        this.patient = patient;
        this.time = time;
    }

    @Override
    public void run() {
        State lastState = State.ENTERED;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                State nowState = patient.getState();
                if (nowState != lastState) {
                    String name = (patient.getName() == null) ? "손님" : patient.getName();
                    int nowTime = time.getTime();
                    System.out.printf("\n-----[현황] %s / 상태:%s / 병원 들어온 지:%d초-----\n\n",
                            name, nowState.getState(), nowTime);
                    lastState = nowState;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException ignored) { }
    }
}

public class Main{
    public static void main(String[] args) {
        List<Doctor> doctors = List.of(
                new Surgeon("김외과", 45, Gender.MALE, Department.SURGERY),
                new Surgeon("박외과", 40, Gender.MALE, Department.SURGERY),
                new Surgeon("이외과", 38, Gender.FEMALE, Department.SURGERY),
                new Physician("김내과", 52, Gender.MALE, Department.INTERNAL),
                new Physician("박내과", 44, Gender.FEMALE, Department.INTERNAL),
                new Dentist("홍치과", 32, Gender.MALE, Department.DENTAL)
        );
        Nurse N1 = new Nurse("박간호사", 33, Gender.FEMALE);
        Patient patient = new Patient(State.ENTERED);
        Scanner scanner = new Scanner(System.in);
        boolean enterHospital = true;

        CheckTime time = new CheckTime();
        CheckStatus status = new CheckStatus(patient, time);
        Thread patientTime = new Thread(time, "PatientTime");
        Thread patientStatus = new Thread(status, "PatientStatus");
        patientTime.start();
        patientStatus.start();

        System.out.println("<<<<<<<<<<카테부 종합병원>>>>>>>>>>\n");
        System.out.println("안녕하세요, 카테부 병원입니다.어떻게 찾아오셨나요?");
        System.out.print("처음 오실 경우 접수를 진행해주세요\n[1] 접수 [2] 나가기\n> ");

        while(enterHospital){
            State answer = patient.getState();

            // 환자 진행 상태 및 대답에 따른 진행
            switch (answer) {
                case State.ENTERED: // 병원 입장
                    String input = scanner.nextLine().trim();
                    switch (input) {
                        case "1" -> patient.setState(State.IN_RECEPTION);
                        case "2" -> patient.setState(State.LEFT);
                        default -> System.out.print("\n잘못 입력했습니다. 1 또는 2를 선택해주세요.\n> ");
                    }
                    break;
                case State.IN_RECEPTION: // 접수
                    N1.processReception(scanner, patient);
                    patient.setState(State.WAIT_TREATMENT);
                    break;
                case State.WAIT_TREATMENT: // 진료 대기
                    pause();
                    patient.setState(State.IN_TREATMENT);
                    break;
                case State.IN_TREATMENT: // 진료
                    Doctor doctor = N1.guideTreatment(doctors, patient, scanner);
                    doctor.processTreatment(patient);
                    patient.setState(State.WAIT_PAYMENT);
                    break;
                case State.WAIT_PAYMENT: // 수납 대기
                    pause();
                    patient.setState(State.IN_PAYMENT);
                    break;
                case State.IN_PAYMENT: // 수납
                    N1.processPayment(patient, scanner);
                    patient.setState(State.LEFT);
                    break;
                case State.LEFT: // 병원 나가기
                    enterHospital = false;
                    break;
                default:
                    System.out.println("\n잘못된 번호입니다. 다시 선택해주세요!");
            }
        }
        System.out.println("감사합니다! 조심히 가세요!");
        scanner.close();

        patientTime.interrupt();
        patientStatus.interrupt();
    }

    public static void pause() {
        try {
            System.out.print("\n(..기다리는 중..)\n");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}