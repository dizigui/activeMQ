package com.mack.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Customer {

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
		MessageConsumer consumer = null;
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
			// 创建queue
			destination = session.createQueue("SecondQueue");
			// 得到消息消费者
			consumer = session.createConsumer(destination);
			while (true) {
				// 设置消息接收者接收消息的时间，便于测试，设定100S
				TextMessage message = (TextMessage) consumer.receive(100000);
				if (null != message) {
					System.out.println("收到消息：" + message.getText());
				} else {
					break;
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != consumer) {	consumer.close();}
				if (null != session) {	session.commit();}
				if (null != connection) {	connection.close();	}
			} catch (Throwable ignore) {
			}
		}
	}
}
