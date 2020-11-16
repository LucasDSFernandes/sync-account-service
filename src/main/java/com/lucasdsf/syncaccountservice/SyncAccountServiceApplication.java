package com.lucasdsf.syncaccountservice;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jmx.access.InvocationFailureException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.services.account.impl.SyncAccountServiceImpl;

@SpringBootApplication
@EnableAsync
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.lucasdsf.syncaccountservice", "com.lucasdsf.syncaccountservice.services" })
public class SyncAccountServiceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncAccountServiceApplication.class);

	/*
	 * Este microservico poderia ser uma classe de um microServico Scheduler, onde
	 * ficaria mais automatizado. Exemplo de segunda a sexta, apartir das 6hrs as
	 * ate as 9hrs chamava o servico para syncronizar as contas e atualizar-las. Com
	 * a anotation
	 * 
	 * @Scheduled(cron = "0 0 6-9 * * MON-FRI", zone = "America/Sao_Paulo")
	 */

	@Bean(name = "asyncExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(1500);
		executor.initialize();
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(SyncAccountServiceApplication.class);
		ApplicationContext applicationContext = springApplication.run(args);
		SyncAccountServiceImpl accountUpdateService = applicationContext.getBean(SyncAccountServiceImpl.class);

		long start = System.currentTimeMillis();

//		TODO Poderia ter profiles configurado no application.yml de ambiente, 
//		para usar a linha a baixo so era apontar para o profile de desenv

//		String inputPathFile = System.getProperty("user.dir").concat("\\src\\main\\resources\\files\\accounts-receita.csv");
		if(args.length>0) {
			String inputPathFile = args[0];
			accountUpdateService.executeProcessFile(inputPathFile);
		}else {
			LOGGER.error("Input parameter not found.");
		}
		LOGGER.info("Finalizing account update in {} .", formatTime(System.currentTimeMillis() - start));

		shutiingDown(applicationContext);
	}

	private static String formatTime(long timeMilis) {
		return String.format("%02d:%02d:%02d", timeMilis / 3600000, timeMilis / 60000, timeMilis / 1000);
	}

	private static void shutiingDown(ApplicationContext applicationContext) {
		LOGGER.info("Realizing shutdown Application!");
		SpringApplication.exit(applicationContext);
		System.exit(0);
	}

}
