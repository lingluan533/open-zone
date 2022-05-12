package com.zms.openzone.common.constants;

/**
 * @author: zms
 * @create: 2022/1/19 15:35
 */
public class CommunityConstants {
    //主题：评论
    public static final String TOPIC_COMMENT = "comment";
    //主题：点赞
    public static final String TOPIC_LIKE = "like";
    //主题：关注
    public static final String TOPIC_FOLLOW = "follow";
    public static final int SYSTEM_USER_ID = 1;
    //主题：发帖
    public static final String TOPIC_PUBLISH = "publish";
    //主题：删帖
    public static final String TOPIC_DELETE = "delete";
    //ES 索引
    public static final String ES_DISCUSSPOST_INDEX = "discusspost";

    public enum LoginExpireEnum {
        DEFAULT_EXPIREDSECONDS(3600 * 12, "默认登录过期时间"), REMEMBER_EXIREDSECONDS(3600 * 12 * 100, "记住登录过期时间");

        private int expiredseconds;
        private String msg;

        LoginExpireEnum(int expiredseconds, String msg) {
            this.expiredseconds = expiredseconds;
            this.msg = msg;
        }

        public int getExpiredseconds() {
            return expiredseconds;
        }

        public String getMsg() {
            return msg;
        }
    }


    public enum ActivationEnum {
        ACTIVATION_SUCCESS(0, "激活成功"), ACTIVATION_REPEAT(1, "重复激活"), ACTIVATION_FAILURE(2, "激活失败");
        private int code;
        private String msg;

        ActivationEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

    }

    public enum CommenttypeEnum {
        COMMENT(1, "评论帖子"), REPLY(2, "评论类型");
        private int code;
        private String msg;

        CommenttypeEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        @Override
        public String toString() {
            return "CommenttypeEnum{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    public enum LikeTypeEnum {
        DISCUSS(1, "点赞帖子"), COMMENT(2, "点赞评论"), USER(3, "点赞用户");
        private int code;
        private String type;

        LikeTypeEnum(int code, String type) {
            this.code = code;
            this.type = type;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public enum UserType {
        AUTHORITY_USER("user"), AUTHORITY_ADMIN("admin"), AUTHORITY_MODERATOR("moderator");
        private String type;

        UserType(String user) {
            this.type = user;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
