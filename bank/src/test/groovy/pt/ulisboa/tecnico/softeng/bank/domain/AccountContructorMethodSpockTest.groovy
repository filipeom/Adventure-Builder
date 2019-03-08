package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll
import spock.lang.Shared

class AccountContructorMethodSpockTest extends SpockRollbackTestAbstractClass {

	@Shared def bank
	@Shared def client
	@Shared def allien

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		this.client = new Client(this.bank, "António")
	}

	def "success"() {
		when:
		def account = new Account(this.bank, this.client)

		then:
		this.bank == account.getBank()
		account.getIBAN().startsWith(this.bank.getCode())
		this.client == account.getClient()
		account.getBalance() == 0
		this.bank.getAccountSet().size() == 1
		this.bank.getClientSet().contains(this.client)
	}

	@Unroll("Account: #banco, #cliente")
	def "exceptions"() {
		when: "creating an Account with invalid arguments"
		new Account(banco, cliente)
		allien = new Client(new Bank("MoneyPlus", "BK02"), "António")

		then: "throws an exception"
		thrown(BankException)

		where:
		banco      | cliente
		null       | this.client
		this.bank  | null
		this.bank  | allien
	}

}
