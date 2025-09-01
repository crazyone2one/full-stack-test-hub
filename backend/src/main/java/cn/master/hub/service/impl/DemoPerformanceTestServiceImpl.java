package cn.master.hub.service.impl;

import cn.master.hub.service.DemoPerformanceTestService;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.springframework.stereotype.Service;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsonExtractor;

import java.io.IOException;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

/**
 * @author Created by 11's papa on 2025/9/1
 */
@Service
public class DemoPerformanceTestServiceImpl implements DemoPerformanceTestService {
    @Override
    public void execute() throws IOException {
        TestPlanStats stats = testPlan()
                .tearDownOnlyAfterMainThreadsDone()
                .children(
                        httpCache()
                                .disable(),
                        httpCookies()
                                .disable(),
                        threadGroup("login", 1, 1,
                                httpHeaders()
                                        .header("tenant-id", "150622020001")
                                        .header("content-type", "application/json"),
                                httpSampler("login", "http://172.16.2.15:9290/prod-api/admin-api/system/auth/login")
                                        .method(HTTPConstants.POST)
                                        .body("{\r\n"
                                                + "\"username\":\"jingll\",\r\n"
                                                + "\"password\":\"Zgkd@83539595\"\r\n"
                                                + "\t}")
                                        .children(
                                                jsonExtractor("accessToken", "$.data.accessToken")
                                                        .queryLanguage(DslJsonExtractor.JsonQueryLanguage.JSON_PATH),
                                                jsonAssertion("$.msg")
                                                        .queryLanguage(DslJsonExtractor.JsonQueryLanguage.JSON_PATH)
                                                        .matches("成功"),
                                                jsonAssertion("JSON断言", "$.code")
                                                        .queryLanguage(DslJsonExtractor.JsonQueryLanguage.JSON_PATH)
                                                        .matches("0")
                                        ),
                                //forLoopController("Loop Controller", 15,
                                //  //httpSampler("dict-list", "http://172.16.2.15:9190/prod-api/admin-api/system/dict-data/list-all-simple")
                                //  //  .header("authorization", "Bearer ${accessToken}")
                                //),
                                resultsTreeVisualizer()
                        )
                ).run();
        System.out.println(stats);
    }
}
