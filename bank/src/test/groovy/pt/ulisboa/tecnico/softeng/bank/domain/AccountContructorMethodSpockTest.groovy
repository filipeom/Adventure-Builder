package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll
import spock.lang.Shared

class AccountContructorMethodSpockTest extends SpockRollbackTestAbstractClass {

	@Shared def allien
	@Shared def bank
	@Shared def client

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		client = new Client(bank, "António")
	}

	def "success"() {
		when:
		def account = new Account(bank, client)

		then:
		with (account) {
			getBank() == bank
			getIBAN().startsWith(bank.getCode())
			getClient() == client
			getBalance() == 0
		}

		with (bank) {
			getAccountSet().size() == 1
		}

		with (client) {
			bank.getClientSet().contains(client)
		}
	}

	@Unroll("Account: #bank_new, #client_new")
	def "exceptions"() {
		when:
		new Account(banck_new, client_new)
		allien = new Client(new Bank("MoneyPlus", "BK02"), "António")

		then:
		thrown(BankException)

		where:
		banck_new | client_new
		null      | client
		bank      | null
		bank      | allien
	}

}
