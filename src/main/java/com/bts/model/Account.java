package com.bts.model;

public class Account {
	
	private int bank_id;
	private String name;
	private String mobileno;
	private double balance;
	private String accountType;
	
	public Account() {
		
	}
	
	public Account(int bank_id, String name, String mobileno, double balance, String accountType) {
		super();
		this.bank_id = bank_id;
		this.name = name;
		this.mobileno = mobileno;
		this.balance = balance;
		this.accountType = accountType;
	}


	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public int getBank_id() {
		return bank_id;
	}


	public void setBank_id(int bank_id) {
		this.bank_id = bank_id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getMobileno() {
		return mobileno;
	}


	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}


	public double getBalance() {
		return balance;
	}


	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "Bank ID      : " + bank_id + "\n"
			 + "Name         : " + name + "\n"
			 + "Mobile No    : " + mobileno + "\n"
			 + "Balance      : " + balance + "\n"
			 + "Account Type : " + accountType;
	}
	
}
