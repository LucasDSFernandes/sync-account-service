package com.lucasdsf.syncaccountservice.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AccountFileEnum {

	AGENCY("Agencia", 0), 
	ACCOUNT("Conta", 1), 
	BALANCE("Saldo", 2), 
	STATUS("Status", 3),
	PROCESSED_STATUS("processed_status", 4), 
	ERROR("processed_error", 5);

	private String headerName;
	private int index;

	private AccountFileEnum(String headerName, int index) {
		this.headerName = headerName;
		this.index = index;
	}

	public String getHeaderName() {
		return headerName;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static AccountFileEnum getByValue(String value) {
		return Arrays.stream(AccountFileEnum.values())
			.filter(object -> object.toString().equals(value))
			.findFirst()
			.orElse(null);
	}
	
	public static List<String> getHeadersName() {
        List<String> listValue = new ArrayList<>();
        Arrays.stream(AccountFileEnum.values())
				.forEach(value -> listValue.add(value.getHeaderName()));

        return listValue;
    }
	
}
