package attendance;

import attendance.domain.Attendance;
import attendance.util.ErrorMessage;
import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import net.bytebuddy.asm.Advice.Local;

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
        Set<String> memberSet = new HashSet<>();

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
                memberSet.add(nickNameInput);
            }


            // 정렬
            sortedCalender = new TreeMap<>(myCalender);

            // 파일 끝
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("[ERROR] 파일 불러오기에 실패했습니다.");
        }

        // 로컬 시간 계산
//        LocalDateTime systemTime = DateTimes.now();
        LocalDateTime systemTime = LocalDateTime.of(2025,12,12,10,10);
        LocalDate currentDateAndTime = systemTime.toLocalDate();
        int currentMonth = systemTime.getMonthValue();
        int currentDay = systemTime.getDayOfMonth();
        String currentDate = systemTime.getDayOfWeek().toString();
        String currentDateDisplay = systemTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        // 디버깅 코드
//        Iterator<Entry<LocalDate, List<Attendance>>> iterator = sortedCalender.entrySet().iterator();
//        while (iterator.hasNext()){
//            Entry<LocalDate, List<Attendance>> entry = iterator.next();
//            LocalDate localDate = entry.getKey();
//            List<Attendance> attendances = entry.getValue();
//            for(Attendance attendance : attendances){
//                System.out.println("날짜 : " + localDate + "/ 출석자 이름 : " + attendance.getName() + "/ 출석 시간 : " + attendance.getAttendanceTime().toString() + "/ 출결 : " + attendance.getRoll());
//
//            }
//        }

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
            if(command.equals("1")){
//            1. 출석 확인 진입 시
//                1. 현재 시간을 로컬로 계산 (반환)
//                2. 캘린더에서 날짜를 조회(반환)
//                3. 요일 조회 (반환)
//                    1. 요일이 (토-일, 공휴일) 인 경우 에러 발생
                if (currentDate.equals("SUNDAY") || currentDate.equals("SATURDAY")) {
                    System.out.printf("%d월 %d일 %s은 등교일이 아닙니다.\n ", currentMonth, currentDay, currentDateDisplay);
                    throw new IllegalArgumentException("등교일이 아닙니다.");
                }

//            2. 이름 입력 받기
                System.out.println("닉네임을 입력해 주세요.");
                String nickNameInput = Console.readLine();;
//                1. 형식 에러

//                2. 이름이 멤버 데이터에 없으면 에러
                if(!memberSet.contains(nickNameInput)){
                    throw new IllegalArgumentException(ErrorMessage.NO_NICKNAME_MATCH_FOUND);
                }
//                3. 캘린더에서 이름이 조회 되면 에러
                System.out.println("조회 날짜 형식 " + currentDateAndTime);
                List<Attendance> attendancesResult = myCalender.get(currentDateAndTime);
                String isAlreadyAttendance = attendancesResult.stream()
                        .filter(attendance -> attendance.getName() == nickNameInput)
                        .toString();
                if(isAlreadyAttendance.length() > 0){
                    throw new IllegalArgumentException(ErrorMessage.ALREADY_ATTENDACNED);
                }

//            3. 시간 입력 받기
                System.out.println("등교 시간을 입력해 주세요.");
                String attendanceTimeInput = Console.readLine();
                LocalTime attendanceTime = LocalTime.parse(attendanceTimeInput, DateTimeFormatter.ofPattern("HH:mm"));
//                1. 캠퍼스 운영시간이 아니면 에러
                if(attendanceTime.isBefore(campusStartTime) || attendanceTime.isAfter(campusEndTime)){
                    throw new IllegalArgumentException(ErrorMessage.NO_CAMPUS_RUNNINGTIME);
                }

                String roll = checkRoll(currentDate,mondayClassStartTime,classEndTime,attendanceTime);

//            4. 캘린더 List에 새로운 데이터로 저장
                Attendance newAttendance = new Attendance(nickNameInput,attendanceTime, roll);
                attendancesResult.add(newAttendance);
                sortedCalender.put(currentDateAndTime,attendancesResult);
                System.out.printf("%d월 %d일 %s %s (%s)\n ", currentMonth, currentDay, currentDateDisplay, attendanceTimeInput, roll);
            }



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
    public static String checkRoll(String dateOfInput, LocalTime startTime, LocalTime endTime, LocalTime time){
        // 4. 요일에 해당하는 출근 시간과 입력 받은 출근 시간 비교
        Duration timeDiff = null;
        if ( dateOfInput.equals("MONDAY") ) {
            timeDiff = Duration.between(startTime,time);
        }else{
            timeDiff = Duration.between(endTime,time);
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
        return roll;
    }
}
