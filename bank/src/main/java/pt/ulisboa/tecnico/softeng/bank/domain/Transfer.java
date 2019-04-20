package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Transfer extends Transfer_Base {

  public Transfer(Account source, Account target, long value) {
		checkArguments(source, target, value);

		setReference(source.getBank().getCode() + Integer.toString(source.getBank().getCounter()));
		setValue(value);
		setTime(DateTime.now());

		setSourceAccount(source);
		setTargetAccount(target);

		setBank(source.getBank());
  }

  @Override
  public void delete() {
    setSourceAccount(null);
    setTargetAccount(null);
    super.delete();
  }

  private void checkArguments(Account source, Account target, long value) {
    if (source == null || target == null || value <= 0) 
      throw new BankException();
  }

  @Override
  public Type getType() {
    return Type.TRANSFER;
  }

  @Override
  public String revert() {
    setCancellation(getReference() + "_CANCEL");

    getSourceAccount().deposit(getValue());
    getTargetAccount().withdraw(getValue());

    setTransactionSource("REVERT");
    setTransactionReference(getReference());
    return "REVERTED";
  }
}
