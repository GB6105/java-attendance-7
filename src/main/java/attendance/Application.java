package attendance;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.MonthDay;
import javax.swing.plaf.TreeUI;

public class Application {
    public static void main(String[] args) {
        // TODO: 프로그램 구현

        // 로컬 시간 계산
        LocalDateTime systemTime = DateTimes.now();

        // 서비스 실행 (루프 시작)
        while(true){
            int currentMonth = systemTime.getMonthValue();
            int currentDay = systemTime.getDayOfMonth();
            String currentDate = systemTime.getDayOfWeek().toString();
            System.out.printf("오늘은 %d월 %d일 %s요일입니다. 기능을 선택해주세요.\n",currentMonth,currentDay, currentDate);




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
