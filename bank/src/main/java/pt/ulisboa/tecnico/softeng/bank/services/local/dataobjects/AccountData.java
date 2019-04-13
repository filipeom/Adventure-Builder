package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;

public class AccountData {
	private String iban;
	private Double balance;
	private Double amount;

	public AccountData() {
	}

	public AccountData(Account account) {
		this.iban = account.getIBAN();
		this.balance = (double) (Math.round((account.getBalance() / 1000.0) * 1000) / 1000);
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public long getBalance() {
		return (long) (this.balance.doubleValue() * 1000);
	}

	public void setBalance(long balance) {
		this.balance = (double) (Math.round((balance / 1000.0) * 1000) / 1000);
	}

	public long getAmount() {
		return (long) (this.amount.doubleValue() * 1000);
	}

	public void setAmount(long amount) {
		this.amount = (double) (Math.round((amount / 1000.0) * 1000) / 1000);
	}

}
