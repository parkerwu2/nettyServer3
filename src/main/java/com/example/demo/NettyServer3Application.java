package com.example.demo;

import com.example.demo.imserver.MessageManager;
import com.example.demo.imserver.NettyServer;
import com.example.demo.imserver.UserManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyServer3Application {

	public static void main(String[] args) {
		SpringApplication.run(NettyServer3Application.class, args);
		NettyServer server = null;
		try {
			server = new NettyServer();

			server.bind(1000);
		} catch (InterruptedException e) {
			MessageManager.getInstance().stop();
			UserManager.getInstance().clearAll();
			server.stop();
			e.printStackTrace();
		}
	}
}
