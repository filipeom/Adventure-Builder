package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public abstract class Operation extends Operation_Base {
  public enum Type {
    DEPOSIT, WITHDRAW, TRANSFER
  }

  protected Operation() {
    super();
  }

	public Operation(Account account, long value) {
		checkArguments(account, value);

		setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
		setValue(value);
		setTime(DateTime.now());

		setBank(account.getBank());
	}

	public void delete() {
		setBank(null);

		deleteDomainObject();
	}

	protected void checkArguments(Account account, long value) {
		if (account == null || value <= 0) {
			throw new BankException();
		}
	}

  public abstract Type getType();

	public abstract String revert();

}
