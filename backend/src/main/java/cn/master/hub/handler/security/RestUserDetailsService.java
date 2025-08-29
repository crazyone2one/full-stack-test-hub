package cn.master.hub.handler.security;

import cn.master.hub.entity.SystemUser;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@Service
public class RestUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return QueryChain.of(SystemUser.class).where(SystemUser::getName).eq(username).oneOpt()
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
