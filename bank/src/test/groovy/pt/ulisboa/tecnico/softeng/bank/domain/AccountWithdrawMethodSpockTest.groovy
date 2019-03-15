package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountWithdrawMethodSpockTest extends SpockRollbackTestAbstractClass {

	def bank
	def account

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
		account.deposit(100)
	}

	def "success"() {
		when:
		def reference = account.withdraw(40).getReference()
		def operation = bank.getOperation(reference)

		then:
		with (account) {
			getBalance() == 60
		}

		with (operation) {
			operation != null
			getType() == Operation.Type.WITHDRAW
			getAccount() == account
			getValue() == 40
		}
	}

	@Unroll("Withdraw: #amount")
	def "exceptions"() {
		when:
		account.withdraw(amount)

		then:
		thrown(BankException)

		where:
		amount | _
		-20    | _
		0      | _
		101    | _
		150    | _
	}

	@Unroll("Withdraw: #amount || #result")
	def "withdrawAmount"() {
		when:
		account.withdraw(amount)

		then:
		account.getBalance() == result

		where:
		amount | result
		99     | 1
		100    | 0
	}
}
