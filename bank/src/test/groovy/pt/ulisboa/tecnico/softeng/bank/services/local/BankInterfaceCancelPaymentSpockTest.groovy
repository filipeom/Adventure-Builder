package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException

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

	def "nullReference"() {
		when:
		BankInterface.cancelPayment(null)

		then:
		thrown(BankException)
	}

	def "emptyReference"() {
		when:
		BankInterface.cancelPayment("")

		then:
		thrown(BankException)
	}

	def "notExistsReference"() {
		when:
		BankInterface.cancelPayment("XPTO")

		then:
		thrown(BankException)
	}
}
