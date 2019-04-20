package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class WithdrawConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def account

	@Override
	def populate4Test() {
		bank = new Bank('Money', 'BK01')
		def client = new Client(bank, 'António')
		account = new Account(bank, client)
	}

	def 'success'() {
		when: 'when creating an withdraw'
		def withdraw = new Withdraw(account, 1000)

		then: 'the object should hold the proper values'
		with(withdraw) {
			getReference().startsWith(bank.getCode())
			getReference().length() > Bank.CODE_SIZE
			getType() == Type.WITHDRAW
			getAccount() == account
			getValue() == 1000
			getTime() != null
			bank.getOperation(getReference()) == withdraw
		}
	}


	@Unroll('withdraw: #acc, #value')
	def 'exception'() {
		when: 'when creating an invalid withdraw'
		new Withdraw(acc, value)

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
		def withdraw = new Withdraw(account, 1)

		then:
		bank.getOperation(withdraw.getReference()) == withdraw 
	}
}
