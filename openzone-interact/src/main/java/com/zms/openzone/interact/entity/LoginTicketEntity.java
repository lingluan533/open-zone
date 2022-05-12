package com.zms.openzone.interact.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: zms
 * @create: 2022/1/21 12:47
 */
@Data
@TableName("login_ticket")
public class LoginTicketEntity {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
