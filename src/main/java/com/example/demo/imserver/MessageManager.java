package com.example.demo.imserver;

import com.example.demo.bean.IMMessage;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MessageManager {

	private static MessageManager manager;
	private LinkedBlockingQueue<IMMessage> mMessageQueue = new LinkedBlockingQueue<IMMessage>();
	private ThreadPoolExecutor mPoolExecutor = new ThreadPoolExecutor(5, 10, 15, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());

	private MessageManager() {
	}

	public static MessageManager getInstance() {
		if (manager == null) {
			synchronized (MessageManager.class) {
				if (manager == null) {
					manager = new MessageManager();

				}
			}
		}
		return manager;
	}

	public void putMessage(IMMessage message) {
		log.debug("MessageManager-> putMessage()... + message.getClientID() + ,  " + message.getBody());
		try {
			mMessageQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		log.debug("MessageManager-> start()... ");
		while (true) {
			try {
				IMMessage message = mMessageQueue.take();
				mPoolExecutor.execute(new SendMessageTask(message));
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			} catch (RejectedExecutionException e) {
				log.debug("MessageManager-> 服务器消息队列已满...延时2妙后继续发送...");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}

	}

	public void stop() {
		log.debug("MessageManager-> stop()... ");
		mMessageQueue.clear();
		mPoolExecutor.shutdownNow();
	}

	class SendMessageTask implements Runnable {
		private IMMessage message;

		public SendMessageTask(IMMessage message) {
			this.message = message;
		}

		@Override
		public void run() {
			if (message.getTo().length() > 2) {
				log.debug("MessageManager-> sendMessage... to client: + message.getReceiveID() + ,  "
						+ message.getBody());
				// 发送单聊消息;
				SocketChannel channel = UserManager.getInstance().getUserChannel(message.getTo());
				if (channel != null && channel.isActive()) {
					channel.writeAndFlush(message);
				}
			} else {
//				Log.debug("MessageManager-> sendMessage... to group: " + message.getGroupID() + ",  "
//						+ message.getBody());
//				// 发送群聊消息;
//				CopyOnWriteArrayList<String> userList = UserManager.getInstance()
//						.getUserListInGroup(message.getGroupID());
//				for (String user : userList) {
//					if (!user.equalsIgnoreCase(message.getClientID())) {
//						SocketChannel channel = UserManager.getInstance().getUserChannel(user);
//						if (channel != null && channel.isActive()) {
//							channel.writeAndFlush(message);
//						}
//					}
//				}
			}
		}
	}
}
