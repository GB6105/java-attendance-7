package attendance.domain;

import java.time.LocalDate;

public class Attendance {

    private String name;
    private LocalDate attendanceTime;
    private String roll;

    /*
    * 출결 결과 저장 객체
    * 이름, 출석 시간, 출결*/
    public Attendance(String name, LocalDate attendanceTime, String roll) {
        this.name = name;
        this.attendanceTime = attendanceTime;
        this.roll = roll;
    }
}
