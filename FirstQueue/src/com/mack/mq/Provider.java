package com.mack.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Provider {

	private static final int SEND_NUMBER = 2000;

	public static void main(String[] args) {

		// 连接工厂，JMS用它创建连接
		ConnectionFactory connectionFactory;
		// JMS客户端到JMS Provider的连接
		Connection connection = null;
		// 发送或接收消息的线程
		Session session = null;
		// 消息的目的地，消息发送给谁
		Destination destination;
		// 消息发送者
		MessageProducer producer = null;
		// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://localhost:61616");

		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			// 获取session，“FirstQueue”是一台服务器的queue
			destination = session.createQueue("SecondQueue");
			// 得到消息生产者
			producer = session.createProducer(destination);
			// 设置不持久化
//			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			// 构造消息
			send(session, producer);
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			if (null != connection) {
				try {
					producer.close();
					session.commit();
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void send(Session session, MessageProducer producer)
			throws JMSException {
		for (int i = 0; i < SEND_NUMBER; i++) {
			// 创建消息
			TextMessage msg = session.createTextMessage("消息" + i);
			// 发送消息
			producer.send(msg);
			System.out.println("发送消息：消息" + i);
		}
	}
}
