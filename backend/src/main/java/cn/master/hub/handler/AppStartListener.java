package cn.master.hub.handler;

import cn.master.hub.handler.uid.DefaultUidGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Created by 11's papa on 2025/9/26
 */
@Component
@RequiredArgsConstructor
public class AppStartListener implements ApplicationRunner {
    private final DefaultUidGenerator defaultUidGenerator;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        defaultUidGenerator.init();
    }
}
