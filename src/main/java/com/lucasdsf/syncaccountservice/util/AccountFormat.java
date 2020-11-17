package com.lucasdsf.syncaccountservice.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lucasdsf.syncaccountservice.config.PropertiesFile;

@Component
public class AccountFormat {
	
	@Autowired
	PropertiesFile properties;
	
	public String formatAgency(String agency) {
		if (!(agency.length() == properties.getAgencyLength()) ) {
				return StringUtils.leftPad(agency, properties.getAgencyLength(), '0' );
		}
		return agency;
	}
	
	public String formatAccount(String account) {
		if (!( account.length() == properties.getAccountLength() ) ) {
			account = account.replace("-", "");
			return StringUtils.leftPad( account, properties.getAccountLength(), "0" );
		} 
		return account;
	}

	public Double formatBalance(String balance) {
			balance = balance.replace(",", ".");
			return new Double(balance);
	}
}
