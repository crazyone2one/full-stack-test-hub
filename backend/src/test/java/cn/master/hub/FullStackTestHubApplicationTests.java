package cn.master.hub;

import cn.master.hub.mapper.SystemUserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@SpringBootTest
class FullStackTestHubApplicationTests {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    SystemUserMapper systemUserMapper;

    @Test
    void contextLoads() {
        // 生成安全的256位密钥示例（仅用于生成新密钥）
        SecretKey key = Jwts.SIG.HS256.key().build();
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated secret: " + secretString);
    }

}
