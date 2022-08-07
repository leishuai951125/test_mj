package leishuai.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @Description 存账号信息
 * * 说明：
 * * 0 通过http验证后会存accountId和session对象到map中，主要时存口令用，也可以用redis
 * * 1 ws连接：每次请求房间页面，会生成一个口令，用于ws连接验证，之所以不用账户密码做ws连接验证，
 * * 是因为可能是第三方登陆接口，无法获取密码。ws连接验证需要前台同时发送accountId和token
 * * 2 游戏过程中用ws对象来识别玩家
 * * 3 断网后重连，用accountId和token识别玩家，并重新建立ws对象。
 * * 也就是连接分游戏中的人断网重连（需要恢复一些状态给客户端）、游戏外的人连接
 * * 4 服务器重启时，先获取房间表所有记录，并根据房间创建玩家对象，再通过账户id
 * * 创建在玩账户对象（生成一个hashmap，key为accountId）。
 * @Author leishuai
 * @Date 2018/12/17 10:59
 * @Version 1.0
 */

public class Account {
    long accountId;
    String username;
    String password;
    String headImgUrl;

    @JSONField(serialize = false)
    Player player;//不为空说明在游戏中,重连时使用，不需要参与持久化

    public Account() {
    }

    public Account(long accountId, String username) {
        setAccountId(accountId);
        setUsername(username);
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}

