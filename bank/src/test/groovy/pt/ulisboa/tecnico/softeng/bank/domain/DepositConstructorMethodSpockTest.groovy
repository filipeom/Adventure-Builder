package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class DepositConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def account

	@Override
	def populate4Test() {
		bank = new Bank('Money', 'BK01')
		def client = new Client(bank, 'António')
		account = new Account(bank, client)
	}

	def 'success'() {
		when: 'when creating an deposit'
		def deposit = new Deposit(account, 1000)

		then: 'the object should hold the proper values'
		with(deposit) {
			getReference().startsWith(bank.getCode())
			getReference().length() > Bank.CODE_SIZE
			getType() == Type.DEPOSIT
			getAccount() == account
			getValue() == 1000
			getTime() != null
			bank.getOperation(getReference()) == deposit
		}
	}


	@Unroll('deposit: #acc, #value')
	def 'exception'() {
		when: 'when creating an invalid deposit'
		new Deposit(acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		acc     | value
		null    | 1000
		account | 0
		account | -1000
	}

	def 'one amount'() {
		when:
		def deposit = new Deposit(account, 1)

		then:
		bank.getOperation(deposit.getReference()) == deposit 
	}
}
