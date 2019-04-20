package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.domain.Deposit;
import pt.ulisboa.tecnico.softeng.bank.domain.Withdraw;
import pt.ulisboa.tecnico.softeng.bank.domain.Transfer;

public class BankOperationData {
	private String reference;
	private String type;
	private String sourceIban;
	private String targetIban;
	private Double value;
	private DateTime time;
	private String transactionSource;
	private String transactionReference;

	public BankOperationData() {
	}

	public BankOperationData(Operation operation) {
		this.reference = operation.getReference();
    this.type = operation.getType().name();
    if (operation instanceof Deposit) {
      this.sourceIban = ((Deposit) operation).getAccount().getIBAN();
    } else if (operation instanceof Withdraw) {
      this.sourceIban = ((Withdraw) operation).getAccount().getIBAN();
    } else {
      this.sourceIban = ((Transfer) operation).getSourceAccount().getIBAN();
      this.targetIban = ((Transfer) operation).getTargetAccount().getIBAN();
    }
		this.value = (double) (Math.round((operation.getValue() / 1000.0) * 1000) / 1000);
		this.time = operation.getTime();
		this.transactionSource = operation.getTransactionSource();
		this.transactionReference = operation.getTransactionReference();
	}

	public BankOperationData(String source, String target, long value, String transactionSource, String transactionReference) {
		this.sourceIban = source;
		this.targetIban = target;
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

  public String getSourceIban() {
    return this.sourceIban;
  }

  public void setSourceIban(String source) {
    this.sourceIban = source;
  }

  public String getTargetIban() {
    return this.targetIban;
  }

  public void setTargetIban(String target) {
    this.targetIban = target;
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
