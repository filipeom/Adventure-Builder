package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountDepositMethodSpockTest extends SpockRollbackTestAbstractClass {

	def account
	def bank

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
	}

	def "success"() {
		when:
		def reference = account.deposit(50).getReference()
		def operation = bank.getOperation(reference)

		then:
		with (operation) {
			operation != null
			getType() == Operation.Type.DEPOSIT
			getAccount() == account
			getValue() == 50
		}

		with (account) {
			getBalance() == 50
		}
	}

	@Unroll("Deposit: #amount")
	def "exceptions"() {
		when:
		account.deposit(amount)

		then:
		thrown(BankException)

		where:
		amount  | _
		0       | _
		-100    | _
	}

	def "oneAmount"() {
		expect:
		account.deposit(1)
	}

}
