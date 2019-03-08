package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankInterfaceGetOperationDataMethodSpockTest extends SpockRollbackTestAbstractClass {

	def AMOUNT = 100
	def bank
	def account
	def reference

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		def client = new Client(this.bank, "António")
		this.account = new Account(this.bank, client)
		this.reference = this.account.deposit(AMOUNT).getReference()
	}

	def "success"() {
		when:
		def data = BankInterface.getOperationData(this.reference)

		then:
		data.getReference() == this.reference
		data.getIban() == this.account.getIBAN()
		data.getType() == Type.DEPOSIT.name()
		data.getValue() == AMOUNT
		data.getTime() != null
	}

	@Unroll("Get Operation Data: #ref")
	def "exceptions"() {
		when: "getting an Operation's data with invalid reference"
		BankInterface.getOperationData(ref)

		then:
		thrown(BankException)

		where:
		ref 		 | _
		null         | _
		""           | _
		"XPTO"       | _
	}
	
}
