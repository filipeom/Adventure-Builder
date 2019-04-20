package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class BankInterfaceCancelPaymentSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account
  def target
	def reference

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
		account = new Account(bank, client)
    target = new Account(bank, client)
		reference = account.deposit(100000).getReference()
	}

	def 'success'() {
		when:
		def newReference = BankInterface.cancelPayment(reference)

		then:
		bank.getOperation(newReference) != null
	}

  def 'sucess with transfer'() {
    given:
    def transfer = account.transfer(target, 50000)

    when:
    BankInterface.cancelPayment(transfer.getReference())

    then:
    transfer.getTransactionSource() == "REVERT"
    account.getBalance() == 100000
  }
  
  def 'twice revert'() {
    given:
    def transfer = account.transfer(target, 50000)

    when:
    BankInterface.cancelPayment(transfer.getReference())

    then:
    transfer.getTransactionSource() == "REVERT"

    when:
    BankInterface.cancelPayment(transfer.getReference())

    then:
    thrown(BankException)

  }

	@Unroll('Cancel: #label')
	def 'problem cancel payment'() {
		when:
		BankInterface.cancelPayment(payConf)

		then:
		thrown(BankException)

		where:
		payConf | label
		null    | 'null reference'
		''      | 'empty reference'
		'XPTO'  | 'not exists reference'
	}
}
