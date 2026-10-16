package com.bts.model;

import java.sql.Timestamp;

public class Transaction {

	private int txnId;
	private int bankId;
	private String type;
	private double amount;
	private double balanceAfter;
	private Integer relatedAccount;
	private Timestamp date;

	public Transaction(int txnId, int bankId, String type, double amount,
			double balanceAfter, Integer relatedAccount, Timestamp date) {
		this.txnId = txnId;
		this.bankId = bankId;
		this.type = type;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.relatedAccount = relatedAccount;
		this.date = date;
	}

	@Override
	public String toString() {
		String other = (relatedAccount == null) ? "-" : String.valueOf(relatedAccount);
		return String.format("%-4d | %-12s | %10.2f | %10.2f | %-9s | %s",
				txnId, type, amount, balanceAfter, other, date);
	}
}