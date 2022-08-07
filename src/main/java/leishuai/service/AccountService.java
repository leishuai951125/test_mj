package leishuai.service;


import leishuai.bean.Account;

import javax.servlet.http.HttpSession;

public interface AccountService {
    String getAccountToken(Long accountId);//根据账户id获取口令

    Account getAccountOnGame(Long accountId); //根据账户id，获取在游戏中的账户对象

    Account getAccountBySession(Long accountId); //根据账户id，在sessionList中获取账户对象

    //往sessionList中添加accountId和httpSession的键值对
    void addAccountIntoSessionMap(Long accountId, HttpSession httpSession);

    void putOnGameAccount(Long accountId, Account account);

    void removeOnGameAccount(Long accountId);
}
