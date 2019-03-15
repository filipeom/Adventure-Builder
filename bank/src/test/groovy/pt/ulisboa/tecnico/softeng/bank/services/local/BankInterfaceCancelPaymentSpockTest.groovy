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
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
		reference = account.deposit(100).getReference()
	}

	def "success"() {
		when:
		def newReference = BankInterface.cancelPayment(reference)

		then:
		with (bank) {
			getOperation(newReference) != null
		}
	}

	@Unroll("Cancel Payment: #confirmation")
	def "exceptions"() {
		when:
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
