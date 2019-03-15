package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountDepositMethodSpockTest extends SpockRollbackTestAbstractClass {

	def account
	def bank

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		def client = new Client(this.bank, "António")
		this.account = new Account(this.bank, client)
	}

	def "success"() {
		when:
		def reference = this.account.deposit(50).getReference()

		then:
		this.account.getBalance() == 50
		def operation = this.bank.getOperation(reference)
		operation != null
		operation.getType() == Operation.Type.DEPOSIT
		operation.getAccount() == this.account
		operation.getValue() == 50
	}

	@Unroll("Deposit: #quantia")
	def "exceptions"() {
		when: "making a deposit with invalid amounts"
		this.account.deposit(quantia)

		then: "throw an exception"
		thrown(BankException)

		where:
		quantia | _
		0       | _
		-100    | _
	}

	def "oneAmount"() {
		expect:
		this.account.deposit(1)
	}

}
