package com.zms.openzone.member.service;


import com.zms.openzone.member.entity.UserEntity;
import com.zms.openzone.member.to.PostIdAuthorTo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zms
 * @create: 2022/1/18 23:58
 */
@Service
public interface UserService {


    UserEntity findUserById(int userId);

    Map<String, Object> register(UserEntity userEntity) throws MessagingException, javax.mail.MessagingException;

    int activation(int userId, String code);


    HashMap<String, Object> login(String username, String password);

    void updateHeader(UserEntity id, String headerUrl);

    void updatePassword(UserEntity userEntity, String newPass);

    UserEntity findUserByUserName(String targetUsername);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);

    Map<Integer, UserEntity> findUserByPostIds(List<Integer[]> postIds);
}
