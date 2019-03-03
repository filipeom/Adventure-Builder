package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll;

class AccountContructorMethodSpockTest extends SpockRollbackTestAbstractClass {

    def bank
    def client


	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01");
		this.client = new Client(this.bank, "António");
	}

	def "success"() {
		when: "creating a new account"
		def account = new Account(this.bank, this.client);

		then: "should succeed"
		this.bank == account.getBank();
		account.getIBAN().startsWith(this.bank.getCode());
		this.client == account.getClient();
		account.getBalance() == 0;
		this.bank.getAccountSet().size() == 1;
		this.bank.getClientSet().contains(this.client);
	}

	@Unroll("Account: #bank, #client")
	def "exceptions"() {
		when: 'creating an Account with invalid arguments'
		new Account(bank, client);

		then:
		thrown(BankException)

		where:
		bank       | client
		null       | this.client
		this.bank  | null
		this.bank  | Client allien = new Client(new Bank("MoneyPlus", "BK02"), "António");

	}

}
