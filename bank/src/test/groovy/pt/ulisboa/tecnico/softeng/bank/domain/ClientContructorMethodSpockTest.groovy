package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class ClientContructorMethodSpockTest extends SpockRollbackTestAbstractClass {

	@Shared def CLIENT_NAME = "António"
	def bank

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
	}

	def "success"() {
		when:
		def client = new Client(this.bank, CLIENT_NAME)

		then:
		client.getName() == CLIENT_NAME
		client.getID().length() >= 1
		this.bank.getClientSet().contains(client)
	}

	@Unroll("Client: #banco, #nome")
	def "exceptions"() {
		when: "creating a Client with invalid arguments"
		new Client(banco, nome)

		then: "throws an exception"
		thrown(BankException)

		where:
		banco      | nome
		null       | CLIENT_NAME
		this.bank  | null
		this.bank  | "    "
		this.bank  | ""
	}

}
