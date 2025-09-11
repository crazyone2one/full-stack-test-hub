package cn.master.hub;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.mapper.SystemUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class FullStackTestHubApplicationTests {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    SystemUserMapper systemUserMapper;

    @Test
    void contextLoads() {
        List<OptionDTO> options = new ArrayList<>();
        options.add(new OptionDTO("1", "1"));
        options.add(new OptionDTO("1", "2"));
        Map<String, List<OptionDTO>> collect = options.stream().collect(Collectors.groupingBy(OptionDTO::getLabel));
        System.out.println(collect);
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
