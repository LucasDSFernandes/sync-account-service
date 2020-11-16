package com.lucasdsf.syncaccountservice.services.account;


import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.enums.AccountUpdateStatusEnum;
import com.lucasdsf.syncaccountservice.integrations.ReceitaService;

@Component
public class IntegrationAccountService  {
	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationAccountService.class);

	private ReceitaService receitaService;
	
	public IntegrationAccountService() {
		this.receitaService = new ReceitaService();
	}
	
	/*
	 * O serviço de atualização de conta poderia ser feito em outro micro servico apartado, onde so seria responsavel por fazer integrações.
	 * Sendo assim utilizaria a anotação com FeignClient para este micro servico AccountUpdates se comunicar com o micro servico de integrações.
	 * Na classe FeignClient poderia apontar para outra classe de configurção IntegrationServiceConfiguration, utilizando bens de feigns, como no exemplo de uso desta.
	 * Teria um balace configurado para o servidor utilizando com eureka-service, para poder balancer a carga entre as maquinas.
	 * 		@Bean
			public Feign.Builder feignBuilder() {
				return Feign.builder()
						.logger(new Slf4jLogger(IntegrationServiceConfiguration.class))
			            .logLevel(feign.Logger.Level.FULL) -> mostraria todo o log da comunicação configurado
			            .options(new Options(60000, 60000)) -> teria dois confs de timeout como read Timeout e conection Timeout, nesse caso utilizaria 6000ms ou 6seg de cada.
						.encoder(new JacksonEncoder()) -> serializar a comunicação entre os ms usando JacksonEncoder
						.decoder(new JacksonDecoder()) -> desserializar a comunicação entre os ms usando JcksonDecoder
						.errorDecoder(new StashErrorDecoder()); -> essa classe seria para caso desse erro no ms de Integração cairia nessa classe e teria tratamento de acordo com o codigo do erro(400,401, 500...).
			}
	 * */
	/*
	 * O tratamento de Status deveria ser feito no micro servico de integração, onde traria o resultado ja prontinho.
	 * */

	@Async("asyncExecutor")
	public CompletableFuture<AccountInfoDTO>  upgradingFromCentralBanckAccount(AccountInfoDTO accountInfoDTO) {
		try {
			LOGGER.info("Running service update account.");
			boolean isUpdated = receitaService.atualizarConta(accountInfoDTO.getAgencia(), accountInfoDTO.getConta(),
					accountInfoDTO.getSaldo(), accountInfoDTO.getStatus().name());
			if ( isUpdated ) {
				LOGGER.info("Return Account update status: {}",  AccountUpdateStatusEnum.UPDATED);
				accountInfoDTO.setProcessedStatus( AccountUpdateStatusEnum.UPDATED );
			} else {
				LOGGER.info("Return Account update status: {}",  AccountUpdateStatusEnum.NOT_UPDATED );
				accountInfoDTO.setProcessedStatus( AccountUpdateStatusEnum.NOT_UPDATED );
			}
			
		} catch (Exception e) {
			/*
			 * Caso de erro em um conta especifica nao irá interromper o fluxo todo, apenas registraria a mensagem de erro que aprensentou para aquela conta;
			 * */
			LOGGER.error("Error updating account: {} ", e.getMessage());
			LOGGER.info("Return Account update status: {}",  AccountUpdateStatusEnum.ERROR_UPDATE);
			accountInfoDTO.setProcessedStatus( AccountUpdateStatusEnum.ERROR_UPDATE );
			accountInfoDTO.setProcessError( e.getMessage() );
		}
		return CompletableFuture.completedFuture(accountInfoDTO);
	}
	
}
