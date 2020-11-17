package com.lucasdsf.syncaccountservice.service;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.enums.AccountStatusEnum;
import com.lucasdsf.syncaccountservice.integrations.ReceitaService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "test")
class IntegraitonAccountServiceTest {
	
	@Test
	void testUpgradingFromCentralBanckAccount() {
		ReceitaService receitaService = new ReceitaService();
		boolean isUpdate =  false;
		try {
			isUpdate = receitaService.atualizarConta(buildAccountInfoDto().getAgencia(), buildAccountInfoDto().getConta(), 
					buildAccountInfoDto().getSaldo(), buildAccountInfoDto().getStatus().name());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertThat(isUpdate);
	}
	@Test
	void testUpgradingFromCentralBanckAccountNotUpdate() {
		ReceitaService receitaService = new ReceitaService();

		boolean isUpdate =  false;
		try {
			isUpdate = receitaService.atualizarConta(null, buildAccountInfoDto().getConta(), 
					buildAccountInfoDto().getSaldo(), buildAccountInfoDto().getStatus().name());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertThat(isUpdate);
	}

	private AccountInfoDTO buildAccountInfoDto() {
		AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
		accountInfoDTO.setAgencia("1234");
		accountInfoDTO.setConta("888888");
		accountInfoDTO.setSaldo(20.2);
		accountInfoDTO.setStatus(AccountStatusEnum.A);
		return accountInfoDTO;
	}

}
