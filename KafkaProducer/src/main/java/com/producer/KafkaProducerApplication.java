package com.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class KafkaProducerApplication implements CommandLineRunner {

	@Autowired
	ApplicationContext applicationContext;

	public static void main(String[] args) {

		SpringApplication.run(KafkaProducerApplication.class, args);

		// if arg --init.data=true
		Arrays.stream(args).filter(arg -> arg.startsWith("--init.data")).findFirst().ifPresent(arg -> {
			String[] argAndValue = arg.split("=");
			if(argAndValue.length == 2 && "true".equals(argAndValue[1])) {
				System.out.println("init arg is present and trueeeeee");

				// 1 populate stringTopic with random messages

				// 2 populate jsonTopic

				// 2 populate avroTopic
			}
		});
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
