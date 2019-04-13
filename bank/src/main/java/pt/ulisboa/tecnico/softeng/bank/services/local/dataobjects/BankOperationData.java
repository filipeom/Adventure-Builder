package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.domain.Operation;

public class BankOperationData {
	private String reference;
	private String type;
	private String iban;
	private Double value;
	private DateTime time;
	private String transactionSource;
	private String transactionReference;

	public BankOperationData() {
	}

	public BankOperationData(Operation operation) {
		this.reference = operation.getReference();
		this.type = operation.getType().name();
		this.iban = operation.getAccount().getIBAN();
		this.value = (double) (Math.round((operation.getValue() / 1000.0) * 1000) / 1000);
		this.time = operation.getTime();
		this.transactionSource = operation.getTransactionSource();
		this.transactionReference = operation.getTransactionReference();
	}

	public BankOperationData(String iban, long value, String transactionSource, String transactionReference) {
		this.iban = iban;
		this.value = (double) (Math.round((value / 1000.0) * 1000) / 1000);
		this.transactionSource = transactionSource;
		this.transactionReference = transactionReference;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public long getValue() {
		return (long) (this.value.doubleValue() * 1000);
	}

	public void setValue(long value) {
		this.value = (double) (Math.round((value / 1000.0) * 1000) / 1000);
	}

	public DateTime getTime() {
		return this.time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

	public String getTransactionSource() {
		return this.transactionSource;
	}

	public void setTransactionSource(String transactionSource) {
		this.transactionSource = transactionSource;
	}

	public String getTransactionReference() {
		return this.transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

}
