package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

class BankGetAccountMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def client

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01");
		this.client = new Client(this.bank, "António");
	}

	def "success"() {
		given:
		def account = new Account(this.bank, this.client)

		when:
		def result = this.bank.getAccount(account.getIBAN())

		then:
		account == result
	}

	def "nullIBAN"() {
		when:
		this.bank.getAccount(null)

		then:
		thrown(BankException)
	}

	def "emptyIBAN"() {
		when:
		this.bank.getAccount("")

		then:
		thrown(BankException)
	}

	def "blankIBAN"() {
		when:
		this.bank.getAccount("    ")

		then:
		thrown(BankException)
	}

	def "emptySetOfAccounts"() {
		this.bank.getAccount("XPTO") == null
	}

	def "severalAccountsDoNoMatch"() {
		given:
		new Account(this.bank, this.client)

		when:
		new Account(this.bank, this.client)

		then:
		this.bank.getAccount("XPTO") == null
	}

}
