package com.zms.openzone.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.mysql.cj.util.StringUtils;
import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.utils.CommunityUtils;
import com.zms.openzone.member.utils.MailClient;
import com.zms.openzone.common.utils.RedisKeyUtil;
import com.zms.openzone.member.dao.UserDao;
import com.zms.openzone.member.entity.UserEntity;
import com.zms.openzone.member.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: zms
 * @create: 2022/1/19 0:01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Autowired
    private MailClient mailClient;
    @Autowired
    UserDao userDao;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired(required = false)
    private RedissonClient redissonClient;

    @Value("${community.path.domain}")
    private String domain;
//    @Autowired
//    private LoginService loginService;

    @Override
    public UserEntity findUserById(int userId) {
//        UserEntity userEntity =  baseMapper.selectById(userId);
//
//        return userEntity;
        //引入redis缓存逻辑
        /*
         * 1.空结果缓存：解决缓存穿透：缓存穿透是指缓存和数据库中都没有的数据，而用户不断发起请求，
         *              如发起为id为“-1”的数据或id为特别大不存在的数据。这时的用户很可能是攻击者，攻击会导致数据库压力过大。
         *              从缓存取不到的数据，在数据库中也没有取到，这时也可以将key-value对写为key-null，缓存有效时间可以设置短点，如30秒
         *              （ 设置太长会导致正常情况也没法使用）。这样可以防止攻击用户反复用同一个id暴力攻击
         * 2.设置过期时间（加随机值）：解决缓存雪崩
         * 3.加锁：解决缓存击穿 （防止大量请求请求同一个缓存数据，但是缓存中没有，导致大量请求落到了数据库服务器上，加锁之后就变为一个一个的查询了，这样使得
         * 第一个请求执行完后，缓存中就有了该数据，后续的请求就不会再去请求数据库了）
         * */
        //1.首先去查缓存，如果缓存命中则直接返回
        String redisKey = RedisKeyUtil.getUserKey(userId);
        UserEntity userEntity = (UserEntity) redisTemplate.opsForValue().get(redisKey);
        if (userEntity != null) return userEntity;
        //1.锁的名字。锁的粒度，越细越好
        //锁的粒度：具体缓存的是某个数据  11-号商品 product-11-lock product-12-lock
        RLock lock = redissonClient.getLock("user:" + userId + "-lock");
        lock.lock();

        try {
            //如果缓存不命中，则查数据库并且更新缓存
            userEntity = getUserFromDB(userId);
        } finally {
            lock.unlock();
        }
        return userEntity;
    }

    public UserEntity getUserFromDB(int userId) {
        //进来查询函数之后首先再次判断缓存是否能命中
        String redisKey = RedisKeyUtil.getUserKey(userId);
        UserEntity userEntity = (UserEntity) redisTemplate.opsForValue().get(redisKey);
        if (userEntity != null) return userEntity;

        //如果还是没命中
        //1.查询数据库
        UserEntity userFromDB = baseMapper.selectById(userId);
        //2.更新缓存
        redisTemplate.opsForValue().set(redisKey, userFromDB, 3600, TimeUnit.SECONDS);
        return userFromDB;
    }

    @Override
    public Map<String, Object> register(UserEntity userEntity) throws  MessagingException {
        Map<String, Object> map = new HashMap<>();
        UserEntity u;
        u = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", userEntity.getUsername()));
        if (u != null) {
            map.put("usernameMsg", "该用户名已经被注册！");
            return map;
        }
        u = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("email", userEntity.getEmail()));
        if (u != null) {
            map.put("emailMsg", "该邮箱已经被注册！");
            return map;
        }
        //注册用户
        userEntity.setSalt(CommunityUtils.gennerateUUID().substring(0, 5));
        userEntity.setPassword(CommunityUtils.md5(userEntity.getPassword() + userEntity.getSalt()));
        userEntity.setType(0);
        userEntity.setStatus(0);
        userEntity.setActivationCode(CommunityUtils.gennerateUUID());
        userEntity.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userEntity.setCreateTime(new Date());
        baseMapper.insert(userEntity);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", userEntity.getEmail());
        //http://localhost:8888/activation/userId/code
        String url = domain + "/activation/" + userEntity.getId() + "/" + userEntity.getActivationCode();
        context.setVariable("url", url);
        System.out.println("url:" + url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(userEntity.getEmail(), "欢迎您" + userEntity.getUsername() + "注册", content);
        return map;
    }

    @Override
    public int activation(int userId, String code) {
        UserEntity user = baseMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return CommunityConstants.ActivationEnum.ACTIVATION_REPEAT.getCode();
        } else if (user.getActivationCode().equals(code)) {
            user.setStatus(1);
            baseMapper.updateById(user);
            return CommunityConstants.ActivationEnum.ACTIVATION_SUCCESS.getCode();
        } else {
            return CommunityConstants.ActivationEnum.ACTIVATION_FAILURE.getCode();
        }
    }

    @Override
    public HashMap<String, Object> login(String username, String password) {
        HashMap<String, Object> map = new HashMap<>();
        //空值处理
        if (StringUtils.isNullOrEmpty(username)) {
            map.put("msg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isNullOrEmpty(password)) {
            map.put("msg", "密码不能为空！");
            return map;
        }

        //验证账号
        UserEntity userEntity = baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
        if (userEntity == null) {
            map.put("msg", "账号不存在！");
            return map;
        }
        if (userEntity.getStatus() == 0) {
            map.put("msg", "账号未激活！");
            return map;
        }

        //验证密码
        password = CommunityUtils.md5(password + userEntity.getSalt());
        if (!userEntity.getPassword().equals(password)) {
            map.put("msg", "密码不正确！");
            return map;
        }

        //生成登录凭证
//        LoginTicketEntity loginTicketEntity = new LoginTicketEntity();
//        loginTicketEntity.setUserId(userEntity.getId());
//        loginTicketEntity.setTicket(CommunityUtils.gennerateUUID());
//        loginTicketEntity.setStatus(0);
//        loginTicketEntity.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //loginService.addTicket(loginTicketEntity);
        //将登录凭证存放到redis中
//        String loginTicketKey = RedisKeyUtil.getTicketKey(loginTicketEntity.getTicket());
//       redisTemplate.opsForValue().set(loginTicketKey,loginTicketEntity);
//        map.put("ticket",loginTicketEntity.getTicket());
        return null;
    }

    @Override
    public void updateHeader(UserEntity user, String headerUrl) {

        baseMapper.update(null, new UpdateWrapper<UserEntity>().eq("id", user.getId()).set("header_url", headerUrl));
        clearCache(user.getId());
    }

    @Override
    public void updatePassword(UserEntity userEntity, String newPass) {
        int res = baseMapper.update(null, new UpdateWrapper<UserEntity>().eq("id", userEntity.getId()).set("password", CommunityUtils.md5(newPass + userEntity.getSalt())));
        System.out.println("更新结果：" + res);
    }

    @Override
    public UserEntity findUserByUserName(String targetUsername) {

        return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", targetUsername));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        UserEntity userEntity = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (userEntity.getType()) {
                    case 1:
                        return CommunityConstants.UserType.AUTHORITY_ADMIN.getType();
                    case 2:
                        return CommunityConstants.UserType.AUTHORITY_MODERATOR.getType();
                    default:
                        return CommunityConstants.UserType.AUTHORITY_USER.getType();
                }

            }
        });
        return list;
    }

    //查找给定帖子的作者
    @Override
    public Map<Integer, UserEntity> findUserByPostIds(List<Integer[]> postIds) {
        Map<Integer, UserEntity> res = new HashMap<>();
        for (Integer[] id : postIds) {
            UserEntity userEntity = this.baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("id", id[1]));
            res.put(id[0], userEntity);
        }
        return res;
    }

    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}
