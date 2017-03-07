package com.mack.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {

	// 连接工厂
	private static ConnectionFactory connectionFactory;

	static {
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");
	}

	public static void send(Session session, MessageProducer producer)
			throws JMSException {
		for (int i = 0; i < 1000; i++) {
			// 创建消息
			TextMessage msg = session.createTextMessage("消息" + i);
			// 发送消息
			producer.send(msg);
			System.out.println("发送消息：消息" + i);
		}
	}

	public static void main(String[] args) {
		// 连接
		Connection connection = null;
		// 会话
		Session session = null;
		// 目的地，消息要发往的地方
		Destination destination;
		// 消息发送者
		MessageProducer producer = null;
		try {
			// 获取消息连接
			connection = connectionFactory.createConnection();
			// 开启连接
			connection.start();
			// 通过连接创建会话
			session = connection.createSession(Boolean.TRUE,
					Session.AUTO_ACKNOWLEDGE);
			// 创建消息队列
			destination = session.createQueue("FirstQueue");
			// 创建发送者
			producer = session.createProducer(destination);
			// 发送消息
			send(session, producer);
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			try {
				session.commit();
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
