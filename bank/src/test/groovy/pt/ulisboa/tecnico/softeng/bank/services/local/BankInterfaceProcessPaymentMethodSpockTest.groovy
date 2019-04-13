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
	def account
	@Shared def iban

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
		account = new Account(bank, client)
		account.deposit(500000)
		iban = account.getIBAN()
	}

	def 'success'() {
		when: 'a payment is processed for this account'
		def newReference = BankInterface.processPayment(new BankOperationData(iban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'the operation occurs and a reference is generated'
		newReference != null
		newReference.startsWith('BK01')
		bank.getOperation(newReference) != null
		bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
		bank.getOperation(newReference).getValue() == 100000
		account.getBalance() == 400000
	}

	def 'success two banks'() {
		given:
		def otherBank = new Bank('Money','BK02')
		def otherClient = new Client(otherBank,'Manuel')
		def otherAccount = new Account(otherBank,otherClient)
		def otherIban = otherAccount.getIBAN()
		otherAccount.deposit(1000000)

		when:
		BankInterface.processPayment(new BankOperationData(otherIban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then:
		otherAccount.getBalance() == 900000

		when:
		BankInterface.processPayment(new BankOperationData(iban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + 'PLUS'))

		then:
		account.getBalance() == 400000
	}

	def 'redo an already payed'() {
		given: 'a payment to the account'
		def firstReference = BankInterface.processPayment(new BankOperationData(iban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		when: 'when there is a second payment for the same reference'
		def secondReference = BankInterface.processPayment(new BankOperationData(iban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'the operation is idempotent'
		secondReference == firstReference
		and: 'does not withdraw twice'
		account.getBalance() == 400000
	}

	def 'one amount'() {
		when: 'a payment of 1'
		BankInterface.processPayment(new BankOperationData(this.iban, 1000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then:
		account.getBalance() == 499000
	}


	@Unroll('bank operation data, process payment: #ibn, #val')
	def 'problem process payment'() {
		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(ibn, val, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'throw exception'
		thrown(BankException)

		where: 'for incorrect arguments'
		ibn     | val    | label
		null    | 100000 | 'null iban'
		'  '    | 100000 | 'blank iban'
		''      | 100000 | 'empty iban'
		iban    | 0      | '0 amount'
		'other' | 0      | 'account does not exist for other iban'
	}

	def 'no banks'() {
		given: 'remove all banks'
		bank.delete()

		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(iban, 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'an exception is thrown'
		thrown(BankException)
	}
}
