package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountWithdrawMethodSpockTest extends SpockRollbackTestAbstractClass {

	def bank
	def account

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		def client = new Client(this.bank, "António")
		this.account = new Account(this.bank, client)
		this.account.deposit(100)
	}

	def "success"() {
		when:
		def reference = this.account.withdraw(40).getReference();

		then:
		this.account.getBalance() == 60
		def operation = this.bank.getOperation(reference)
		operation != null
		operation.getType() == Operation.Type.WITHDRAW
		operation.getAccount() == this.account
		operation.getValue() == 40
	}

	@Unroll("Withdraw: #quantia")
	def "exceptions"() {
		when: "making a withdraw with invalid amounts"
		this.account.withdraw(quantia)

		then: "throw an exception"
		thrown(BankException)

		where:
		quantia | _
		-20     | _
		0       | _
		101     | _
		150     | _
	}

	def "oneAmount"() {
		when:
		this.account.withdraw(1)

		then:
		this.account.getBalance() == 99
	}

	def "equalToBalance"() {
		when:
		this.account.withdraw(100)

		then:
		this.account.getBalance() == 0
	}

}
