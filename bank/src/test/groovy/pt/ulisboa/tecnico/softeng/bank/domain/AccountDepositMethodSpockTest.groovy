package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class AccountDepositMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		def client = new Client(this.bank, "António")
		this.account = new Account(this.bank, client)
	}

	def "success"() {
		given:
		def reference = this.account.deposit(50).getReference()

		expect:
		this.account.getBalance() == 50
		def operation = this.bank.getOperation(reference)
		operation != null
		Operation.Type.DEPOSIT == operation.getType()
		this.account == operation.getAccount()
		operation.getValue() == 50
	}

	def "zeroAmount"() {
		when:
		this.account.deposit(0)

		then:
		thrown(BankException)
	}

	def "negativeAmount"() {
		when:
		this.account.deposit(-100)

		then:
		thrown(BankException)
	}

	def "oneAmount"() {
		this.account.deposit(1)
	}

}
