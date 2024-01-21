package org.example;

public class Employee {
    private String positionId;
    private String positionStatus;
    private String time;
    private String timeOut;
    private String timecard;
    private String payCycleStartDate;
    private String payCycleEndDate;
    private String employeeName;
    private String fileNumber;



    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(String positionStatus) {
        this.positionStatus = positionStatus;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getTimecard() {
        return timecard;
    }

    public void setTimecard(String timecard) {
        this.timecard = timecard;
    }

//    public String getHoursAsTime() {
//        return hoursAsTime;
//    }
//
//    public void setHoursAsTime(String hoursAsTime) {
//        this.hoursAsTime = hoursAsTime;
//    }

    public String getPayCycleStartDate() {
        return payCycleStartDate;
    }

    public void setPayCycleStartDate(String payCycleStartDate) {
        this.payCycleStartDate = payCycleStartDate;
    }

    public String getPayCycleEndDate() {
        return payCycleEndDate;
    }

    public void setPayCycleEndDate(String payCycleEndDate) {
        this.payCycleEndDate = payCycleEndDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public void updateDataFrom(Employee currentEmployee) {

    }

    public int getConsecutiveDaysWorked() {
        return 0;
    }
}
