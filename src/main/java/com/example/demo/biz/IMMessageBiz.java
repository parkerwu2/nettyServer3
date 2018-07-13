package com.example.demo.biz;


import com.alibaba.fastjson.JSON;
import com.example.demo.bean.IMMessage;
import com.example.demo.biz.protocol.IMProtocol;
import com.example.demo.biz.protocol.Response;
import com.example.demo.imserver.ChannelGroups;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IMMessageBiz {

//	private MessageDAO messagedao = new MessageDAO();
	
	public void saveMsg(IMMessage msg) {
		log.info("saveMsg={}", msg);
//		messagedao.save(msg);
	}
	
	public List<IMMessage> getUnReadMsg(String uname) {
//		List<IMMessage> msgs = messagedao.find(" `to` = '"+uname+"' and readTiem = -1 ");
		List<IMMessage> msgs = new ArrayList<>();
		return msgs ;
	}

	public void makeRead(String[] ids) {
		for (String id : ids) {
			makeRead(id);
		}
	}
	
	public void makeRead(String mid) {
//		IMMessage msg = messagedao.findById(mid);
//		if(msg != null) {
//			msg.setReadTiem(System.currentTimeMillis());
//			messagedao.update(msg);
//		}
	}
	
	public void transformMessage(IMMessage message) {
		
		Channel channel = ChannelGroups.getChannel(ChannelGroups.getChannelId(message.getTo()));
		if(channel != null && channel.isActive()) {
			Response response = new Response();
			response.setBody(JSON.toJSONString(message));
			response.setStatus(0);
			response.setMethod(IMProtocol.REV);
			response.setSendTime(System.currentTimeMillis());
			channel.writeAndFlush(JSON.toJSON(response)+"\r\n");
		}
		
	}
	
}
