package com.lucasdsf.syncaccountservice.enums;

import java.util.Arrays;

public enum AccountStatusEnum {
	
	A("Ativo"),	I("Inativo"), B("Bloqueada"), P("Pendente");
	
	AccountStatusEnum(String status){
		this.status = status;
	}
	private String status;

	public static AccountStatusEnum getByValue(String value) {
		return Arrays.stream(AccountStatusEnum.values())
			.filter(object -> object.toString().equals(value))
			.findFirst()
			.orElse(null);
	}

	public String getStatus() {
		return status;
	}
}
