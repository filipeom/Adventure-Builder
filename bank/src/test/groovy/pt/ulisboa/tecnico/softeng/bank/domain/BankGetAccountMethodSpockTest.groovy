package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankGetAccountMethodSpockTest extends SpockRollbackTestAbstractClass {

	def bank
	def client

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		this.client = new Client(this.bank, "António")
	}

	def "success"() {
		given:
		def account = new Account(this.bank, this.client)

		when:
		def result = this.bank.getAccount(account.getIBAN())

		then:
		account == result
	}

	@Unroll("Bank: #account")
	def "exceptions"() {
		when: "retrieving an Account with invalid name"
		this.bank.getAccount(account)

		then: "throws an exception"
		thrown(BankException)

		where:
		account    | _
		null       | _
		""         | _
		"    "     | _
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
