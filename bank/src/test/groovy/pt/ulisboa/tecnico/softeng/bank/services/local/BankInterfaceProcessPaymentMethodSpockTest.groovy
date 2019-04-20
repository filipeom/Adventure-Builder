package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData
import spock.lang.Shared
import spock.lang.Unroll

class BankInterfaceProcessPaymentMethodSpockTest extends SpockRollbackTestAbstractClass {
	def TRANSACTION_SOURCE='ADVENTURE'
	def TRANSACTION_REFERENCE='REFERENCE'
	def bank
	def sourceAccount 
	def targetAccount 
	@Shared def sourceIban
	@Shared def targetIban

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
		sourceAccount = new Account(bank, client)
    targetAccount = new Account(bank, client)
		sourceAccount.deposit(500000)
		sourceIban = sourceAccount.getIBAN()
		targetIban = targetAccount.getIBAN()
	}

	def 'success'() {
		when: 'a payment is processed for this account'
		def newReference = BankInterface.processPayment(new BankOperationData(sourceIban, targetIban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'the operation occurs and a reference is generated'
		newReference != null
		newReference.startsWith('BK01')
		bank.getOperation(newReference) != null
		bank.getOperation(newReference).getType() == Operation.Type.TRANSFER
		bank.getOperation(newReference).getValue() == 100000
		sourceAccount.getBalance() == 400000
		targetAccount.getBalance() == 100000
	}

	def 'success two banks'() {
		given:
		def otherBank = new Bank('Money','BK02')
		def otherClient = new Client(otherBank,'Manuel')
		def otherAccount = new Account(otherBank,otherClient)
		def otherIban = otherAccount.getIBAN()
		otherAccount.deposit(1000000)

		when:
		BankInterface.processPayment(new BankOperationData(otherIban, targetIban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then:
		otherAccount.getBalance() == 900000
    targetAccount.getBalance() == 100000

		when:
		BankInterface.processPayment(new BankOperationData(targetIban, sourceIban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + 'PLUS'))

		then:
		sourceAccount.getBalance() == 600000
    targetAccount.getBalance() == 0
	}

	def 'redo an already payed'() {
		given: 'a payment to the account'
		def firstReference = BankInterface.processPayment(new BankOperationData(sourceIban, targetIban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		when: 'when there is a second payment for the same reference'
		def secondReference = BankInterface.processPayment(new BankOperationData(sourceIban, targetIban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'the operation is idempotent'
		secondReference == firstReference
		and: 'does not transfer twice'
		sourceAccount.getBalance() == 400000
	}

	def 'one amount'() {
		when: 'a payment of 1'
		BankInterface.processPayment(new BankOperationData(sourceIban, targetIban, 1000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then:
		sourceAccount.getBalance() == 499000
	}


	@Unroll('bank operation data, process payment: #sibn, #tibn, #val')
	def 'problem process payment'() {
		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(sibn, tibn , val, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'throw exception'
		thrown(BankException)

		where: 'for incorrect arguments'
		sibn       | tibn       | val    | label
		null       | targetIban | 100000 | 'null source iban'
		'  '       | targetIban | 100000 | 'blank source iban'
		''         | targetIban | 100000 | 'empty source iban'
		sourceIban | null       | 100000 | 'null target iban'
		sourceIban | '  '       | 100000 | 'blank target iban'
		sourceIban | ''         | 100000 | 'empty target iban'
		sourceIban | 'other'    | 0      | 'account does not exist for other iban'
		'other'    | targetIban | 0      | 'account does not exist for other iban'
		sourceIban | targetIban | 0      | '0 amount'
	}

  def 'reverted transaction'() {
    when:
		BankInterface.processPayment(new BankOperationData(sourceIban, targetIban, 100000, "REVERT", TRANSACTION_REFERENCE))

    then:
    thrown(BankException)
  }

	def 'no banks'() {
		given: 'remove all banks'
		bank.delete()

		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(sourceIban, targetIban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'an exception is thrown'
		thrown(BankException)
	}
}
