package antiesys.antiepidemic.service;

import antiesys.antiepidemic.mapper.MessageInter;
import antiesys.antiepidemic.mapper.ReportInter;
import antiesys.antiepidemic.mapper.UserInter;

import antiesys.antiepidemic.pojo.Message;
import antiesys.antiepidemic.pojo.Report;
import antiesys.antiepidemic.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserInter userInter;
    @Autowired
    ReportInter reportInter;
    @Autowired
    MessageInter messageInter;

    @Override
    public boolean UserRegister(Users user) {
        //判断是否存在这个用户
        int uid=user.getUserId();
        Users u=userInter.SelectOne(uid);
        //用户存在
        if(u!=null){
            return false;
        }
        //插入
        int t=userInter.InsertUser(user);
        //插入失败
        if(t==0){
            return false;
        }
        return true;
    }

    @Override
    public boolean UserLogin(int userId, String userPW) {
        //查询用户
        Users u=userInter.SelectOne(userId);

        //没有该用户
        if(u==null){
            return false;
        }
        //判断密码
        if(!userPW.equals(u.getUserPW())){
            return false;
        }
        return true;
    }

    @Override
    public Users FindUserOne(int userId) {
        //直接查询
        Users user=userInter.SelectOne(userId);
        if(user==null){
            return null;
        }
        return user;
    }

    @Override
    public int ChangeUser(Users user) {
        //判断是否存在
        Users u=userInter.SelectOne(user.getUserId());
        if(u==null){
            return 0;
        }
        user.setUserIdCard(u.getUserIdCard());
        user.setUserPW(u.getUserPW());
        user.setTemperature(u.getTemperature());
        //修改信息
        int t=userInter.UpdateUser(user);
        if(t==0){
            return 0;
        }
        return t;
    }

    @Override
    public boolean ChangePassword(int userId, String userPW, String newPW) {
    	Users u=userInter.SelectOne(userId);
    	if(!u.getUserPW().equals(userPW)||u.getUserPW().equals(newPW)){
            return false;
        }
    	u.setUserPW(newPW);
    	int t=userInter.UpdateUser(u);
    	if(t==0){
            return false;
        }
        return true;
    }

    @Override
    public int GetNumber(Map<Integer,Integer> map) {
    	Random r = new Random();
    	Set set=map.keySet();
    	int sig=1;
    	int number=0;
    	while(true) {
    		sig=0;
    		number=r.nextInt(900000)+100000;
    		for(Iterator iter=set.iterator();iter.hasNext();) {
    			int key=(int)iter.next();
    			if((Integer)map.get(key)==number) {
    				sig=1;
    				break;
    			}
    		}
    		if(sig==0) {
    			break;
    		}
    	}
        return number;
    }

    @Override
    public Report FindReportOne(int userId) {
        //判断用户是否存在
        Users u=userInter.SelectOne(userId);
        if(u==null){
            return null;
        }
        Report report=reportInter.SelectLastReport(userId);
        if(report==null){
            return null;
        }
        return report;
    }

    @Override
    public Message FindMessageOne(Integer meID) {
        //直接查询
        Message message=messageInter.SelectOne(meID);
        if(message==null){
            return  null;
        }
        return message;
    }

    @Override
    public List<Message> FindMessageAll() {
        List<Message> messageList=messageInter.SelectMessage();
        if(messageList==null||messageList.isEmpty()){
            return  null;
        }
        return messageList;
    }
}
