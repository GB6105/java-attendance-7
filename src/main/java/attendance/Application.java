package attendance;

import attendance.domain.Attendance;
import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import com.sun.jdi.connect.AttachingConnector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Application {
    public static void main(String[] args) {

        // 캘린더 생성 (출석 기록)
        HashMap<LocalDate, List<Attendance>> myCalender = new HashMap<>();
        TreeMap<LocalDate, List<Attendance>> sortedCalender = new TreeMap<>();
        LocalTime mondayClassStartTime = null;
        LocalTime classStartTime = null;
        LocalTime classEndTime = null;
        LocalTime campusStartTime = null;
        LocalTime campusEndTime = null;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
        mondayClassStartTime = LocalTime.parse("13:00",format);
        classStartTime =LocalTime.parse("10:00", format);
        classEndTime = LocalTime.parse("18:00", format);
        campusStartTime = LocalTime.parse("08:00", format);
        campusEndTime = LocalTime.parse("23:00", format);

        // 멤버 데이터 저장


        // 파일 입력 받고 저장하기
        try {
            String fileName = "src/main/resources/attendances.csv";
            FileReader file = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(file);

            bufferedReader.readLine();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            while (true) {
//              1. 파일 한 줄 씩 읽기
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
            // 2. 이름, 날짜, 요일, 시간 구분
                String[] input = line.split(",");

                // 이름
                String nickNameInput = input[0];

                // 날짜만 yyyy-mm-dd
                LocalDateTime dateTimeInput = LocalDateTime.parse(input[1], dateTimeFormatter);
                // 날짜
                LocalDate date = dateTimeInput.toLocalDate();
                // 시간
                LocalTime time = dateTimeInput.toLocalTime();

                // 디버깅 코드
//                System.out.println("Nickname: " + nickNameInput + " attendance time : " + dateTimeInput + " local date: " + date + " time: " + time2);


            //3. 날짜에서 요일 구별
                String dateOfInput = dateTimeInput.getDayOfWeek().toString();

            // 4. 요일에 해당하는 출근 시간과 입력 받은 출근 시간 비교
//               long timeDiff = 0 ;
//
//                if (dateOfInput.equals("Monday")) {
//                    timeDiff = mondayClassStartTime.getTime() - time;
//                }else{
//                    timeDiff = classStartTime.getTime() - time;
//                }
                Duration timeDiff = null;
                if ( dateOfInput.equals("MONDAY") ) {
                    timeDiff = Duration.between(mondayClassStartTime,time);
                }else{
                    timeDiff = Duration.between(classStartTime,time);
                }

            // 5. 출석,결석,지각 여부 판별
                String roll = "";

                if(timeDiff.getSeconds() <= 0){
                    roll = "출석";
                }else if(timeDiff.getSeconds() <= 5 * 60){
                    roll = "지각";
                }else{
                    roll = "결석";
                }

            // 6. 해당 날짜에 이름, 시간, 출결 여부 저장

                Attendance attendance = new Attendance(nickNameInput,time,roll);

                //일단 조회
                List<Attendance> attendances = myCalender.get(date);
                if (attendances == null || attendances.size() == 0) {
                    List<Attendance> newAttendances = new ArrayList<>();
                    newAttendances.add(attendance);
                    myCalender.put(date,newAttendances);
                }else{
                    attendances.add(attendance);
                    myCalender.put(date,attendances);
                }
            }
            sortedCalender = new TreeMap<>(myCalender);

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("[ERROR] 파일 불러오기에 실패했습니다.");
        }

        // 로컬 시간 계산
        LocalDateTime systemTime = DateTimes.now();
        int currentMonth = systemTime.getMonthValue();
        int currentDay = systemTime.getDayOfMonth();
        String currentDate = systemTime.getDayOfWeek().toString();
        String currentDateDisplay = systemTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        // 디버깅 코드
        Iterator<Entry<LocalDate, List<Attendance>>> iterator = sortedCalender.entrySet().iterator();
        while (iterator.hasNext()){
            Entry<LocalDate, List<Attendance>> entry = iterator.next();
            LocalDate localDate = entry.getKey();
            List<Attendance> attendances = entry.getValue();
            for(Attendance attendance : attendances){
                System.out.println("날짜 : " + localDate + "/ 출석자 이름 : " + attendance.getName() + "/ 출석 시간 : " + attendance.getAttendanceTime().toString() + "/ 출결 : " + attendance.getRoll());

            }
        }

        // 서비스 실행 (루프 시작)
        while (true) {

            // 기본 안내 메시지
            System.out.printf("오늘은 %d월 %d일 %s입니다. 기능을 선택해주세요.\n", currentMonth, currentDay, currentDateDisplay);
            System.out.println("1. 출석 확인\n"
                    + "2. 출석 수정\n"
                    + "3. 크루별 출석 기록 확인\n"
                    + "4. 제적 위험자 확인\n"
                    + "Q. 종료");

            // 입력 받기

            String command = Console.readLine();

            // 종료 조건
            boolean isContinue = true;
            while (isContinue) {
                String isQuit = Console.readLine();
                if (isQuit.equals("Q")) {
                    isContinue = false;
                    return;
                } else {

                }
            }
        }


    }
}
