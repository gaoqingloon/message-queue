/*******************************************************************************
 * Copyright (c) 2020, 2020 Hirain Technologies Corporation.
 ******************************************************************************/
package com.bigdata.kafka.consumer;

/**
 *
 */
public class ClazzInstanceTest {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clz = Class.forName("com.bigdata.kafka.consumer.KafkaConsumerProperties");
		KafkaConsumerProperties props = (KafkaConsumerProperties) clz.newInstance();
		System.out.println(props);
	}

}
