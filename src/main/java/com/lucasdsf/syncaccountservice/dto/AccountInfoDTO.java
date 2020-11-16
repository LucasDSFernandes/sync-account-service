package com.lucasdsf.syncaccountservice.dto;

import com.lucasdsf.syncaccountservice.constants.Constants;
import com.lucasdsf.syncaccountservice.enums.AccountStatusEnum;
import com.lucasdsf.syncaccountservice.enums.AccountUpdateStatusEnum;

public class AccountInfoDTO {

	private String agencia;
	private String conta;
	private Double saldo;
	private AccountStatusEnum status;
	
	private AccountUpdateStatusEnum processedStatus;
	private String processError = Constants.ERROR_MESSAGE;

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public AccountStatusEnum getStatus() {
		return status;
	}

	public void setStatus(AccountStatusEnum status) {
		this.status = status;
	}

	public AccountUpdateStatusEnum getProcessedStatus() {
		return processedStatus;
	}

	public void setProcessedStatus(AccountUpdateStatusEnum processedStatus) {
		this.processedStatus = processedStatus;
	}

	public String getProcessError() {
		return processError;
	}

	public void setProcessError(String processError) {
		this.processError = processError;
	}
}
