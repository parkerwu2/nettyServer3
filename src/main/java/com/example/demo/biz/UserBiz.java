package com.example.demo.biz;

import com.alibaba.fastjson.JSON;
import com.example.demo.bean.User;
import com.example.demo.biz.protocol.IMProtocol;
import com.example.demo.biz.protocol.Response;
import com.example.demo.imserver.ChannelGroups;

import java.util.List;

public class UserBiz {
//	private UserDAO userdao = new UserDAO();
	public boolean login(User user) {
//		List<User> users = userdao.find("uname = '"+user.getUname()+"' and pwd = '"+user.getPwd()+"' ");
//		if(users != null && users.size() > 0) {
//			return true ;
//		}
		return true;
//		return false ;
	}
	
	/**
	 * 是否存在相同的用户名
	 * @param uname
	 */
	public boolean exitsUserName(String uname) {
//		List<User> users = userdao.find("uname = '"+uname+"'");
//		if(users != null && users.size() > 0) {
//			return true ;
//		}
		return false;
	}
	
	public void reg(User user) {
//		userdao.save(user);
		
	}
	
	public void del(String uid) {
//		userdao.del(uid);
	}

	public User findUserByName(String uname) {
//		List<User> users = userdao.find("uname = '"+uname+"'");
//		if(users != null && users.size() > 0) {
//			return users.get(0);
//		}
//		return null;
		return new User();
	}
	
	public User findUserById(String id) {
//		List<User> users = userdao.find("uid = "+id);
//		if(users != null && users.size() > 0) {
//			return users.get(0);
//		}
		return null;
	} 
	
	public void freshUserLoginStatus(User user) {
		//通过全局 xxx登录成功了
		Response response = new Response();
		response.setMethod(IMProtocol.LOGINNOW);
		response.setSendTime(System.currentTimeMillis());
		response.setBody(user.getUname());
		ChannelGroups.broadcast(JSON.toJSONString(response) + "\r\n");
	}
	
	public void freshUserLoginoutStatus(User user) {
		//通过全局 xxx退出登录了
		Response response = new Response();
		response.setMethod(IMProtocol.LOGINOUT);
		response.setSendTime(System.currentTimeMillis());
		response.setBody(user.getUname());
		ChannelGroups.broadcast(JSON.toJSONString(response)+"\r\n");
	}
	
}
