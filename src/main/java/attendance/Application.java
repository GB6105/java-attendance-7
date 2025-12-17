package attendance;

import attendance.domain.Attendance;
import attendance.util.ErrorMessage;
import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

                if(timeDiff.getSeconds() <= 5 * 60){
                    roll = "출석";
                }else if(timeDiff.getSeconds() <= 30 * 60){
                    roll = "지각";
                }else
                {
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
        LocalDateTime systemTime = DateTimes.now();
//        LocalDateTime systemTime = LocalDateTime.of(2024,12,12,10,10);
        LocalDate currentDateAndTime = systemTime.toLocalDate();
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
            if(command.equals("1")){
//            1. 출석 확인 진입 시
//                1. 현재 시간을 로컬로 계산 (반환)
//                2. 캘린더에서 날짜를 조회(반환)
//                3. 요일 조회 (반환)
//                    1. 요일이 (토-일, 공휴일) 인 경우 에러 발생
                if (currentDate.equals("SUNDAY") || currentDate.equals("SATURDAY")) {
                    String errorMessage = String.format("[ERROR] %d월 %d일 %s은 등교일이 아닙니다.\n ", currentMonth, currentDay, currentDateDisplay);
                    throw new IllegalArgumentException(errorMessage);
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

                //현재 날짜로 조회한 출석 부
                List<Attendance> attendancesResult = sortedCalender.get(currentDateAndTime);

                 boolean isAlreadyAttendance = attendancesResult.stream()
                        .filter(attendance -> attendance.getName() == nickNameInput).findFirst().isPresent();

                if(isAlreadyAttendance){
                    throw new IllegalArgumentException(ErrorMessage.ALREADY_ATTENDACNED);
                }

//            3. 시간 입력 받기
                System.out.println("등교 시간을 입력해 주세요.");
                String attendanceTimeInput = Console.readLine();
//                0. 형식 에러
                checkTimeInputType(attendanceTimeInput);
                LocalTime attendanceTime = LocalTime.parse(attendanceTimeInput, DateTimeFormatter.ofPattern("HH:mm"));
//                1. 캠퍼스 운영시간이 아니면 에러
                if(attendanceTime.isBefore(campusStartTime) || attendanceTime.isAfter(campusEndTime)){
                    throw new IllegalArgumentException(ErrorMessage.NO_CAMPUS_RUNNINGTIME);
                }

                String roll = checkRoll(currentDate,mondayClassStartTime,classStartTime,attendanceTime);

//            4. 캘린더 List에 새로운 데이터로 저장
                Attendance newAttendance = new Attendance(nickNameInput,attendanceTime, roll);
                attendancesResult.add(newAttendance);
                sortedCalender.put(currentDateAndTime,attendancesResult);
                System.out.printf("%d월 %d일 %s %s (%s)\n ", currentMonth, currentDay, currentDateDisplay, attendanceTimeInput, roll);
            }


//            1. 출석 수정 진입 시
            if(command.equals("2")){

//                1. 현재 시간을 로컬로 계산 (저장)
//                2. 캘린더에서 날자 조회 (저장)
//                3. 요일 조회 (저장)
//                    1. 요일이 (토-일, 공휴일) 인 경우 에러 발생
//            2. 이름 입력 받기
                System.out.println("출석을 수정하려는 크루의 닉네임을 입력해 주세요.");
                String nickNameInput = Console.readLine();
//                1. 형식 에러
//                2. 이름이 멤버 데이터에 없으면 에러
                if(!memberSet.contains(nickNameInput)){
                    throw new IllegalArgumentException(ErrorMessage.NO_NICKNAME_MATCH_FOUND);
                }
//            3. 날짜 입력 받기
                System.out.println("수정하려는 날짜(일) 을 입력해 주세요.");
                String fixDateInput = Console.readLine();
                if(fixDateInput.length() == 1){
                    fixDateInput = "0" +fixDateInput;
                }
                String prefixDateInput = "2024-12-"+fixDateInput;
                LocalDate fixDate = LocalDate.parse(prefixDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String dateOfFixDate = fixDate.getDayOfWeek().toString();
//                1. 형식 에러
//                2. 로컬 날짜 이후인 경우 에러
                if(fixDate.isAfter(currentDateAndTime)){
                    throw new IllegalArgumentException(ErrorMessage.CANNOT_FIX_TIME);
                }
//                3. 공휴일 일 경우 에러
                if (dateOfFixDate.equals("SUNDAY") || dateOfFixDate.equals("SATURDAY")) {
                    String errorMessage = String.format("%d월 %d일 %s은 등교일이 아닙니다.\n ", currentMonth, currentDay, currentDateDisplay);
                    throw new IllegalArgumentException(errorMessage);
                }
//            4. 시간 입력 받기
                System.out.println("언제로 변경하겠습니까?");
                String fixTimeInput = Console.readLine();
//                1. 형식 에러
                checkTimeInputType(fixTimeInput);
                LocalTime fixTime = LocalTime.parse(fixTimeInput, DateTimeFormatter.ofPattern("HH:mm"));
//                2. 캠퍼스 운영시간보다 이르거나 느리면 에러 발생
                if(fixTime.isBefore(campusStartTime) || fixTime.isAfter(campusEndTime)){
                    throw new IllegalArgumentException(ErrorMessage.NO_CAMPUS_RUNNINGTIME);
                }

                String roll = checkRoll(dateOfFixDate,mondayClassStartTime,classStartTime,fixTime);
//            5. 캘린더 List에 새로운 데이터로 저장
                List<Attendance> attendancesResult = sortedCalender.get(fixDate);
                System.out.println("수정 목표 날짜" + fixDate + "수정 목표 이름 " + nickNameInput);
//                for(Attendance attendance : attendancesResult){
//                    System.out.println(attendance.getName() + " 이름 " );
//                }
                Attendance oldAttendance = attendancesResult.stream()
                        .filter(attendance -> attendance.getName().equals(nickNameInput))
                        .findFirst()
                        .orElse(null);

                LocalTime oldTime = oldAttendance.getAttendanceTime();
                String oldRoll = oldAttendance.getRoll();

                int newMonth = fixDate.getMonthValue();
                int newDay = fixDate.getDayOfMonth();
                String newDate = fixDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

                attendancesResult.remove(oldAttendance);
                Attendance newAttendance = new Attendance(nickNameInput,fixTime, roll);
                attendancesResult.add(newAttendance);
                sortedCalender.put(fixDate,attendancesResult);
                System.out.printf("%d월 %d일 %s %s (%s) -> %s (%s) 수정 완료!\n",
                        newMonth, newDay,newDate, oldTime.toString(),oldRoll,
                        fixTime.toString(), roll);

            }

            if(command.equals("3")){
                System.out.println("닉네임을 입력해 주세요.");
                String nickNameInput = Console.readLine();
                if(!memberSet.contains(nickNameInput)){
                    throw new IllegalArgumentException(ErrorMessage.NO_NICKNAME_MATCH_FOUND);
                }
                System.out.println("이번 달 " + nickNameInput +"의 출석 기록 입니다.");

                int attendanceCount = 0;
                int lateCount = 0;
                int absenceCount = 0;

                Iterator<Entry<LocalDate,List<Attendance>>> entries = sortedCalender.entrySet().iterator();
                while(entries.hasNext()){
                    Entry<LocalDate,List<Attendance>> entry = entries.next();
                    LocalDate currDate = entry.getKey();
                    if(currDate.isEqual(currentDateAndTime)){
                        continue;
                    }
                    List<Attendance> attendancesList = entry.getValue();
                    Attendance attendanceResult = attendancesList.stream()
                            .filter(attendance -> attendance.getName().equals(nickNameInput))
                            .findFirst()
                            .orElse(null);
                    String time = "";
                    String roll = "";
                    if(attendanceResult == null){
                        time = "--:--";
                        roll = "결석";
                    }else{
                        time = attendanceResult.getAttendanceTime().toString();
                        roll = attendanceResult.getRoll();
                    }

                    if(roll == "출석"){
                        attendanceCount++;
                    }else if(roll == "지각"){
                        lateCount++;
                    }else{
                        absenceCount++;
                    }

                    int currMonth = currDate.getMonthValue();
                    int currDay = currDate.getDayOfMonth();
                    String formattedDay = String.format("%02d",currDay);
                    String currDateDisplay = currDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

                    System.out.println(currMonth + "월 " +formattedDay + "일 " + currDateDisplay +" " + time + " (" + roll + ")");

                }
                System.out.println("출석: " + attendanceCount + "회");
                System.out.println("지각: " + lateCount + "회");
                System.out.println("결석: " + absenceCount + "회");

                if(lateCount >3){
                    absenceCount += lateCount/3;
                }
                if(absenceCount >5){
                    System.out.println("제적 대상자입니다.");
                }else if(absenceCount >=3){
                    System.out.println("면담 대상자입니다.");
                }else if(absenceCount >=2){
                    System.out.println("경고 대상자입니다.");
                }
            }

            if(command.equals("4")){
                System.out.println("제적 위험자 조회 결과");

            }

            if(command.equals("Q")){
                return;
            }


        }


    }
    public static void checkTimeInputType(String timeInput){
        String[] times = timeInput.split(":");
        if(times.length != 2){
            throw new IllegalArgumentException(ErrorMessage.TYPE_ERROR);
        }
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);
        if(hour < 0 || hour > 23 || minute < 0 || minute > 59){
            throw new IllegalArgumentException(ErrorMessage.TYPE_ERROR);
        }

    }
    public static String checkRoll(String dateOfInput, LocalTime mondayStartTime, LocalTime startTime, LocalTime time){
        // 4. 요일에 해당하는 출근 시간과 입력 받은 출근 시간 비교
        Duration timeDiff = null;
        if ( dateOfInput.equals("MONDAY") ) {
            timeDiff = Duration.between(mondayStartTime,time);
        }else{
            timeDiff = Duration.between(startTime,time);
        }

        // 5. 출석,결석,지각 여부 판별
        String roll = "";

        if(timeDiff.getSeconds() <= 5 * 60){
            roll = "출석";
        }else if(timeDiff.getSeconds() <= 30 * 60){
            roll = "지각";
        }else
        {
            roll = "결석";
        }
        return roll;
    }
}
