package attendance.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Attendance {

    private String name;
    private LocalTime attendanceTime;
    private String roll;

    /*
    * 출결 결과 저장 객체
    * 이름, 출석 시간, 출결*/
    public Attendance(String name, LocalTime attendanceTime, String roll) {
        this.name = name;
        this.attendanceTime = attendanceTime;
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(LocalTime attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public LocalTime getAttendanceTime() {
        return attendanceTime;
    }

    public String getRoll() {
        return roll;
    }
}
