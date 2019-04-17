package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class TaxPayerConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def ADDRESS = 'Somewhere'
	@Shared def NAME = 'José Vendido'
	@Shared def NIF = '123456789'
	def irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
	}

	def 'success'() {
		when:
		def taxPayer = new TaxPayer(irs, NIF, NAME, ADDRESS)

		then:
		with(taxPayer) {
			getNif() == NIF
			getName() == NAME
			getAddress() == ADDRESS
		}
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == taxPayer
	}

	def 'unique nif'() {
		given: "a buyer"
		def taxPayer = new TaxPayer(irs, NIF, NAME, ADDRESS)

		when: "another buyer with the same info"
		new TaxPayer(irs, NIF, NAME, ADDRESS)

		then: "an exception is thrown"
		def error = thrown(TaxException)
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == taxPayer
	}

	@Unroll('testing exceptions: #nif, #name, #address')
	def 'testing exceptions'() {
		when:
		new TaxPayer(irs, nif, name, address)

		then:
		thrown(TaxException)

		where:
		nif        | name | address
		null       | NAME | ADDRESS
		''         | NAME | ADDRESS
		'12345678' | NAME | ADDRESS
		NIF        | null | ADDRESS
		NIF        | ''   | ADDRESS
		NIF        | NAME | null
		NIF        | NAME | ''
	}
}
