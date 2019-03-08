package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankInterfaceCancelPaymentSpockTest extends SpockRollbackTestAbstractClass {

	def bank
	def account
	def reference

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		def client = new Client(this.bank, "António")
		this.account = new Account(this.bank, client)
		this.reference = this.account.deposit(100).getReference()
	}

	def "success"() {
		given:
		def newReference = BankInterface.cancelPayment(this.reference)

		expect:
		this.bank.getOperation(newReference) != null
	}

	@Unroll("Cancel Payment: #confirmation")
	def "exceptions"() {
		when: "cancelling a Payment with invalid reference"
		BankInterface.cancelPayment(confirmation)

		then:
		thrown(BankException)

		where:
		confirmation | _
		null         | _
		""           | _
		"XPTO"       | _
	}

}
