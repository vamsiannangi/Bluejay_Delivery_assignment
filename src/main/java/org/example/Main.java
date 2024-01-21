package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

public class Main {
    public static final int POSITION_ID = 0;
    public static final int POSITION_STATUS = 1;
    public static final int TIME = 2;
    public static final int TIME_OUT = 3;
    public static final int TIMECARD = 4;
    public static final int PAY_CYCLE_START_DATE = 5;
    public static final int PAY_CYCLE_END_DATE = 6;
    public static final int EMPLOYEE_NAME = 7;
    public static final int FILE_NUMBER = 8;

    public static void main(String[] args) {
        String employeeFile = "src/main/resources/Assignment_Timecard.xlsx - Sheet1.csv";
        ArrayList<Employee> employeeDetails = getEmployeeData(employeeFile);
        analyzeAndPrintResults(employeeDetails);
    }


    private static void analyzeAndPrintResults(List<Employee> employees) {
        System.out.println("a) Employees who have worked for 7 consecutive days:");
        printEmployeesWithConsecutiveDays(employees,7);
        System.out.println("\nb) Employees who have less than 10 hours between shifts but greater than 1 hour:");
        printShortBreakEmployees(employees, 1, 10);
        System.out.println("\nc) Employees who have worked for more than 14 hours in a single shift:");
        printLongSameDayEmployees(employees, 14);
    }

    private static void printEmployeesWithConsecutiveDays(List<Employee> employees, int consecutiveDaysThreshold) {

        HashMap<String, String> employeeDateMap = new HashMap<>();
        HashMap<String,Integer> employeeconsecutiveDays=new HashMap<>();
        try {
            for (Employee employee : employees) {
                String employeeName = employee.getEmployeeName();
                String time = employee.getTime();
                if (time != "") {
                    String date = time.split(" ")[0];
                    String presentDay = date;
                    if (employeeDateMap.containsKey(employeeName)) {
                        int consecutiveDays = employeeconsecutiveDays.get(employeeName);

                        String prevDay = employeeDateMap.get(employeeName);
                        int valid = isConsecutive(prevDay, presentDay);
                        if (valid==1) {

                            consecutiveDays++;
                            employeeconsecutiveDays.put(employeeName, consecutiveDays);
                            employeeDateMap.put(employeeName,presentDay);
                        } else if (valid==0) {
                            employeeconsecutiveDays.put(employeeName,consecutiveDays);
                            employeeDateMap.put(employeeName,presentDay);

                        }

                        else{
                            if (consecutiveDays >= consecutiveDaysThreshold) {
                                System.out.println("Employee: " + employeeName);
                            }
                            employeeconsecutiveDays.put(employeeName, 1);
                            employeeDateMap.put(employeeName, presentDay);
                        }

                    } else {
                        employeeDateMap.put(employeeName, date);
                        employeeconsecutiveDays.put(employeeName, 1);
                    }
                }
                else{
                    continue;
                }
            }
        }catch (Exception e){

        }

        for(Map.Entry<String,Integer> map:employeeconsecutiveDays.entrySet()){
            String employee=map.getKey();
            int days=map.getValue();
            if(days>=consecutiveDaysThreshold){
                System.out.println("Employee: " + employee);
            }
        }
    }
    public static int isConsecutive(String today, String nextDay) {
        String day1 = today.split("/")[1];
        String day2 = nextDay.split("/")[1];
        int today1 = Integer.parseInt(day1);
        int nextDay2 = Integer.parseInt(day2);

        if(today1==nextDay2){
            return 0;
        } else if (today1+1==nextDay2) {
            return 1;
        }
        return -1;
    }


    private static void printShortBreakEmployees(List<Employee> employees, int minHours, int maxHours) {

        Set<String> shortBreakPrinted = new HashSet<>();
        Map<String, String> employeeBreaks = new HashMap<>();

        for (int index = 0; index < employees.size(); index++) {
            Employee currentEmployee = employees.get(index);
            String employeeName = currentEmployee.getEmployeeName();
            String positionId = currentEmployee.getPositionId();

            if (shortBreakPrinted.contains(employeeName)) {
                continue;
            }

            if (employeeBreaks.containsKey(employeeName)) {
                String lastTimeOutStr = employeeBreaks.get(employeeName);
                String currentTimeInStr = currentEmployee.getTime();

                if (isValidDateTimeString(currentTimeInStr) && isValidDateTimeString(lastTimeOutStr)) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        Date timeIn = dateFormat.parse(currentTimeInStr);
                        Date lastTimeOut = dateFormat.parse(lastTimeOutStr);

                        long timeDiffMillis = timeIn.getTime() - lastTimeOut.getTime();
                        long timeDiffHours = timeDiffMillis / (60 * 60 * 1000);

                        if (1 < timeDiffHours && timeDiffHours < 10) {
                            System.out.println("Employee: " + employeeName + ", Position: " + positionId);
                            shortBreakPrinted.add(employeeName);
                        }
                    } catch (ParseException e) {
                        System.out.println("Error parsing date for Employee: " + employeeName + ", Position: " + positionId +
                                ". Exception: " + e.getMessage());
                        e.printStackTrace(); // Print the full stack trace for debugging
                    }
                }
            }

            employeeBreaks.put(employeeName, currentEmployee.getTimeOut());
        }
    }

    private static boolean isValidDateTimeString(String dateTimeString) {
        return dateTimeString != null && !dateTimeString.trim().isEmpty();
    }


    private static void printLongSameDayEmployees(List<Employee> employees, int maxHours) {
        Map<String, Map<String, Integer>> employeeDayHoursMap = new HashMap<>();
        for (Employee employee : employees) {
            String employeeName = employee.getEmployeeName();
            String time = employee.getTime();
            String timecardHours = employee.getTimecard();
            String endDate=employee.getTimeOut();
            if (!time.isEmpty() && !timecardHours.isEmpty()) {
                String date = time.split(" ")[0];  // Extracting the date from the timestamp
                String shiftEndDate=endDate.split("")[0];
                // Creating a key for the employee and date
                String key = employeeName + "_" + date;

                // Initializing the inner map if it doesn't exist
                employeeDayHoursMap.putIfAbsent(key, new HashMap<>());


                // Updating the total hours for the specific day
                int totalMinutes = employeeDayHoursMap.get(key).getOrDefault("totalMinutes", 0);
                int hoursWorked = Integer.parseInt(timecardHours.split(":")[0]) * 60;  // Convert hours to minutes
                int minutesWorked = Integer.parseInt(timecardHours.split(":")[1]);  // Extracting minutes
                totalMinutes += hoursWorked + minutesWorked;

                if(date==shiftEndDate){
                    employeeDayHoursMap.get(key).put("totalMinutes", totalMinutes);
                }

                // Checking if the total hours exceed the threshold
                if (totalMinutes / 60 > maxHours) {
                    System.out.println("Employee: " + employeeName + ", Date: " + date + ", Total Hours: " + totalMinutes / 60);
                }
            }
        }
    }

    public static ArrayList<Employee> getEmployeeData(String fileName) {
        ArrayList<Employee> employeeList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip header line

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 10) {
                    String employeeName = data[7]+data[8];  // Extracting the employee name without splitting
                    String[] remainingData = Arrays.copyOfRange(data, 0, 7);
                    String fileNumber=data[9];
                    Employee employeeData = new Employee();
                    employeeData.setEmployeeName(employeeName);
                    employeeData.setFileNumber(fileNumber);
                    populateEmployeeData(employeeData, remainingData);

                    employeeList.add(employeeData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    private static void populateEmployeeData(Employee employeeData, String[] data) {
        employeeData.setPositionId(data.length > POSITION_ID ? data[POSITION_ID] : "");
        employeeData.setPositionStatus(data.length > POSITION_STATUS ? data[POSITION_STATUS] : "");
        employeeData.setTime(data.length > TIME ? data[TIME] : "");
        employeeData.setTimeOut(data.length > TIME_OUT ? data[TIME_OUT] : "");
        employeeData.setTimecard(data.length > TIMECARD ? data[TIMECARD] : "");
        employeeData.setPayCycleStartDate(data.length > PAY_CYCLE_START_DATE ? data[PAY_CYCLE_START_DATE] : "");
        employeeData.setPayCycleEndDate(data.length > PAY_CYCLE_END_DATE ? data[PAY_CYCLE_END_DATE] : "");
    }

}