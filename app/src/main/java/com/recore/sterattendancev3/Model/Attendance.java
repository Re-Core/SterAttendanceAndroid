package com.recore.sterattendancev3.Model;

public class Attendance {
    private String attendance_id;
    private String gps_longitude;
    private String gps_latitude;
    private String attdatte_time;
    private String employees_id;
    private String check_type;

    public Attendance() {
    }

    public Attendance(String attendance_id, String gps_longitude, String gps_latitude, String attdatte_time, String employees_id, String check_type) {
        this.attendance_id = attendance_id;
        this.gps_longitude = gps_longitude;
        this.gps_latitude = gps_latitude;
        this.attdatte_time = attdatte_time;
        this.employees_id = employees_id;
        this.check_type = check_type;
    }

    public String getAttendance_id() {
        return attendance_id;
    }

    public void setAttendance_id(String attendance_id) {
        this.attendance_id = attendance_id;
    }

    public String getGps_longitude() {
        return gps_longitude;
    }

    public void setGps_longitude(String gps_longitude) {
        this.gps_longitude = gps_longitude;
    }

    public String getGps_latitude() {
        return gps_latitude;
    }

    public void setGps_latitude(String gps_latitude) {
        this.gps_latitude = gps_latitude;
    }

    public String getAttdatte_time() {
        return attdatte_time;
    }

    public void setAttdatte_time(String attdatte_time) {
        this.attdatte_time = attdatte_time;
    }

    public String getEmployees_id() {
        return employees_id;
    }

    public void setEmployees_id(String employees_id) {
        this.employees_id = employees_id;
    }

    public String getCheck_type() {
        return check_type;
    }

    public void setCheck_type(String check_type) {
        this.check_type = check_type;
    }
}
