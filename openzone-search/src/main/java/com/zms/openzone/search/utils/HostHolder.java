package com.zms.openzone.search.utils;



import com.zms.openzone.common.entity.UserEntityVo;
import org.springframework.stereotype.Component;

/**
 * @author: zms
 * @create: 2022/1/21 15:20
 */
@Component
public class HostHolder {
    private ThreadLocal<UserEntityVo> users = new ThreadLocal<>();

    public void setUser(UserEntityVo user) {
        users.set(user);
    }

    public UserEntityVo getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
