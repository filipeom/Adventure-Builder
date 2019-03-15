package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankGetAccountMethodSpockTest extends SpockRollbackTestAbstractClass {

	def bank
	def client

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		client = new Client(bank, "António")
	}

	def "success"() {
		given:
		def account = new Account(bank, client)

		when:
		def result = bank.getAccount(account.getIBAN())

		then:
		account == result
	}

	@Unroll("Bank: #account")
	def "exceptions"() {
		when:
		bank.getAccount(account)

		then:
		thrown(BankException)

		where:
		account    | _
		null       | _
		""         | _
		"    "     | _
	}

	def "emptySetOfAccounts"() {
		expect:
		bank.getAccount("XPTO") == null
	}

	def "severalAccountsDoNoMatch"() {
		given:
		new Account(bank, client)

		when:
		new Account(bank, client)

		then:
		bank.getAccount("XPTO") == null
	}

}
