package cn.master.hub;

import cn.master.hub.mapper.SystemUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FullStackTestHubApplicationTests {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    SystemUserMapper systemUserMapper;

    @Test
    void contextLoads() {
        String param = "07:00~8:00(M),14:00~17:50(N),23:10~01:40(E)";
        String[] timeStrList = param.split(",");
        String shiftTime;
        String[] shiftTimeStrList;
        String morningShiftEndTimeStr = "";
        for (String time : timeStrList) {
            shiftTime = time.substring(0, time.indexOf("("));
            // 班次时间段，['09:20','11:50']
            shiftTimeStrList = shiftTime.split("~");
            if (time.contains("M")) {
                morningShiftEndTimeStr = shiftTimeStrList[1];
            }
        }
        String shiftFlag = parseShiftTime(timeStrList);
        System.out.println(morningShiftEndTimeStr);
    }

    private String parseShiftTime(String[] timeStrList) {
        List<LocalTime> timeRangeList4m = new ArrayList<>();
        for (String time : timeStrList) {
            String shiftTime = time.substring(0, time.indexOf("("));
            if (time.contains("M")) {
                timeRangeList4m = convertTimeRangeToList(shiftTime);
            }
        }
        System.out.println(timeRangeList4m);
        return null;
    }

    private List<LocalTime> convertTimeRangeToList(String timeRange) {
        String[] times = timeRange.split("~");
        List<LocalTime> timeList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (String timeStr : times) {
            LocalTime time = LocalTime.parse(timeStr, formatter);
            timeList.add(time);
        }
        return timeList;
    }

}
