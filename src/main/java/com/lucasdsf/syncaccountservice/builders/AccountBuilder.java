package com.lucasdsf.syncaccountservice.builders;

import com.lucasdsf.syncaccountservice.dto.AccountInfoDTO;
import com.lucasdsf.syncaccountservice.enums.AccountStatusEnum;

public class AccountBuilder {
	
	private String agencia;
	private String conta;
	private Double saldo;
	private AccountStatusEnum status;

	public static AccountBuilder getInstance() {
		return new AccountBuilder();
	}
	
	public AccountBuilder setAgency(String agencia) {
		this.agencia = agencia;
		return this;
	}
	
	public AccountBuilder setAccount(String conta) {
		this.conta = conta;
		return this;
	}
	
	public AccountBuilder setBalance(Double saldo) {
		this.saldo = saldo;
		return this;
	}
	
	public AccountBuilder setStatus(AccountStatusEnum status) {
		this.status = status;
		return this;
	}
	
	public AccountInfoDTO build() {
		AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
		
		accountInfoDTO.setAgencia( agencia );
		accountInfoDTO.setConta( conta );
		accountInfoDTO.setSaldo( saldo );
		accountInfoDTO.setStatus( status );
		
		return accountInfoDTO;
	}
}

