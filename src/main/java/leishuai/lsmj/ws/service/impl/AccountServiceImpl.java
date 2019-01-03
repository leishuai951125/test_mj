package leishuai.lsmj.ws.service.impl;


import leishuai.lsmj.ws.bean.Account;
import leishuai.lsmj.ws.service.AccountService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2018/12/18 10:42
 * @Version 1.0
 */
public class AccountServiceImpl implements AccountService,HttpSessionListener {
    //在房间中的账户信息,这些信息不能随httpsession消失而消失，所以不存seeion里面
    private static ConcurrentMap<Long,Account> accountsOnGame=new ConcurrentHashMap<Long, Account>(200);
    //存储通过http验证的用户HttpSession,用httpsession监听器管理这个map,也可以存用户其它信息，如Account对象
    private static ConcurrentMap<Long,HttpSession> httpSessionMap=new ConcurrentHashMap<Long,HttpSession>(1000);

    @Override
    public Account getAccountOnGame(Long accountId) {
        return accountsOnGame.get(accountId);
    }
    @Override
    public void putOnGameAccount(Long accountId,Account account) {
        accountsOnGame.put(accountId,account);
    }
    @Override
    public void removeOnGameAccount(Long accountId) {
        accountsOnGame.remove(accountId);
    }


    @Override
    public String getAccountToken(Long accountId) {
        HttpSession httpSession=httpSessionMap.get(accountId);
        if(httpSession==null){
            return null;
        }
        String token=(String)httpSession.getAttribute("token");
        return token;
    }
    @Override
    public Account getAccountBySession(Long accountId) {
        HttpSession httpSession=httpSessionMap.get(accountId);
        if(httpSession==null){
            return null;
        }else {
            return (Account)httpSession.getAttribute("account");
        }
    }
    @Override
    public void addAccountIntoSessionMap(Long accountId, HttpSession httpSession) {
        httpSessionMap.put(accountId,httpSession);
    }


    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        //创建时也不好传参，干脆创建后在别处操作
        //创建后应该添加token和Account到session中
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        Account account=(Account)httpSessionEvent.getSession().getAttribute("account");
        httpSessionMap.remove(account.getAccountId());
    }
}
