package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class ClientContructorMethodSpockTest extends SpockRollbackTestAbstractClass {

	@Shared def CLIENT_NAME = "António"
	@Shared def bank

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
	}

	def "success"() {
		when:
		def client = new Client(bank, CLIENT_NAME)

		then:
		with (client) {
			getName() == CLIENT_NAME
			getID().length() >= 1
			bank.getClientSet().contains(client)
		}
	}

	@Unroll("Client: #bank_new, #client")
	def "exceptions"() {
		when:
		new Client(bank_new, client)

		then:
		thrown(BankException)

		where:
		bank_new | client
		null     | CLIENT_NAME
		bank     | null
		bank     | "    "
		bank     | ""
	}

}
