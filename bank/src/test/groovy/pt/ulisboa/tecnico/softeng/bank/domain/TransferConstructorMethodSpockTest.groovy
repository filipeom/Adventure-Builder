package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class TransferConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def source
	@Shared def target

	@Override
	def populate4Test() {
		bank = new Bank('Money', 'BK01')
		def client = new Client(bank, 'António')
		source = new Account(bank, client)
		target = new Account(bank, client)
    source.deposit(100000)
	}

	def 'success'() {
		when: 'when creating an transfer'
		def transfer = new Transfer(source, target, 10000)

		then: 'the object should hold the proper values'
		with(transfer) {
			getReference().startsWith(bank.getCode())
			getReference().length() > Bank.CODE_SIZE
			getType() == Type.TRANSFER
			getSourceAccount() == source 
			getTargetAccount() == target
			getValue() == 10000
			getTime() != null
			bank.getOperation(getReference()) == transfer
		}
	}


	@Unroll('transfer: #src, #dst, #value')
	def 'exception'() {
		when: 'when creating an invalid transfer'
		new Transfer(src, dst, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		src    | dst    | value
		null   | target | 1000
    source | null   | 1000
		source | target | 0
		source | target | -1000
	}

	def 'one amount'() {
		when:
		def transfer = new Transfer(source, target, 1000)

		then:
		bank.getOperation(transfer.getReference()) == transfer 
	}
}
