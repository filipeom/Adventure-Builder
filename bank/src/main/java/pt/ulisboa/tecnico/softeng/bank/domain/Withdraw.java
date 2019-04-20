package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

public class Withdraw extends Withdraw_Base {

  public Withdraw(Account account, long value) {
		checkArguments(account, value);

		setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
		setValue(value);
		setTime(DateTime.now());

		setAccount(account);

		setBank(account.getBank());
  }

  @Override
  public void delete() {
    setAccount(null);
    super.delete();
  }

  @Override
  public Type getType() {
    return Type.WITHDRAW;
  }
  
  @Override
  public String revert() {
    setCancellation(getReference() + "_CANCEL");
    return getAccount().deposit(getValue()).getReference();
  }
}
