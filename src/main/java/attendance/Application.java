package attendance;

import attendance.domain.Attendance;
import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Application {
    public static void main(String[] args) {

        // 출석 기록 불러오기
        HashMap<LocalDateTime, List<Attendance>> myCalender = new HashMap<>();


        try{
            String fileName = "src/main/resources/attendance.md";
            FileReader file = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(file);

            bufferedReader.readLine();
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] input = line.split(",");
                String nickNameInput = input[0];

            }

        }catch (IOException e){
            System.out.println("[ERROR] 파일 불러오기에 실패했습니다.");
        }
        // 파일 입력 받기


        // 로컬 시간 계산
        LocalDateTime systemTime = DateTimes.now();
        int currentMonth = systemTime.getMonthValue();
        int currentDay = systemTime.getDayOfMonth();
        String currentDate = systemTime.getDayOfWeek().toString();
        String currentDateDisplay = systemTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        // 서비스 실행 (루프 시작)
        while(true){

            // 기본 안내 메시지
            System.out.printf("오늘은 %d월 %d일 %s입니다. 기능을 선택해주세요.\n",currentMonth,currentDay, currentDateDisplay);
            System.out.println("1. 출석 확인\n"
                    + "2. 출석 수정\n"
                    + "3. 크루별 출석 기록 확인\n"
                    + "4. 제적 위험자 확인\n"
                    + "Q. 종료");

            // 입력 받기

            String command = Console.readLine();



            // 종료 조건
            boolean isContinue = true;
            while(isContinue){
                String isQuit = Console.readLine();
                if(isQuit.equals("Q")){
                    isContinue = false;
                    return;
                }else{

                }
            }
        }


    }
}
