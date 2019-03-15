package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class BankConstructorSpockTest extends SpockRollbackTestAbstractClass {

	@Shared def BANK_CODE = "BK01"
	@Shared def BANK_NAME = "Money"

	@Override
	def populate4Test() {
	}

	def "success"() {
		when:
		def bank = new Bank(BANK_NAME, BANK_CODE)

		then:
		with (bank) {
			getName() == BANK_NAME
			getCode() == BANK_CODE
			getAccountSet().size() == 0
			getClientSet().size() == 0
		}

		with (FenixFramework) {
			getDomainRoot().getBankSet().size() == 1
		}
	}

	@Unroll("Bank: #name, #code")
	def "exceptions"() {
		when:
		new Bank(name, code)

		then:
		thrown(BankException)

		where:
		name       | code
		null       | BANK_CODE
		"    "     | BANK_CODE
		BANK_NAME  | null
		BANK_NAME  | "    "
		BANK_NAME  | "BK0"
		BANK_NAME  | "BK011"
	}

	def "notUniqueCode"() {
		given:
		new Bank(BANK_NAME, BANK_CODE)

		when:
		new Bank(BANK_NAME, BANK_CODE)

		then:
		thrown(BankException)

		and:
		FenixFramework.getDomainRoot().getBankSet().size() == 1
	}

}
