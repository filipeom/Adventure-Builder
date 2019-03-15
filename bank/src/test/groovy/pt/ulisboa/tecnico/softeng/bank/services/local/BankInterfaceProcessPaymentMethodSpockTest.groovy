package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData
import spock.lang.Unroll
import spock.lang.Shared

class BankInterfaceProcessPaymentMethodSpockTest extends SpockRollbackTestAbstractClass {
  def TRANSACTION_SOURCE = "ADVENTURE"
  def TRANSACTION_REFERENCE = "REFERENCE"

  def bank
  def account
  @Shared def iban

  @Override
  def populate4Test() {
    bank = new Bank("Money", "BK01")
    def client = new Client(bank, "António")
    account = new Account(bank, client)
    iban = account.getIBAN()
    account.deposit(500)
  }

  def 'success'() {
    when:
    account.getIBAN();
    def newReference = BankInterface.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

    then:
    newReference != null
    newReference.startsWith("BK01") == true

    bank.getOperation(newReference) != null
    bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
  }

  def 'success two banks'() {
    given: 'another bank'
    def otherBank = new Bank("Money", "BK02")
    def otherClient = new Client(otherBank, "Manuel")
    def otherAccount = new Account(otherBank, otherClient)
    def otherIban = otherAccount.getIBAN()
    otherAccount.deposit(1000)

    when: 
    BankInterface.processPayment(new BankOperationData(otherIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
    BankInterface.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"))

    then:
    otherAccount.getBalance() == 900
    account.getBalance() == 400
  }

  def 'redo an already payed'() {
    when:
    account.getIBAN()
    def firstReference = BankInterface.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
    def secondReference = BankInterface.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
    then:
    firstReference == secondReference
    account.getBalance() == 400
  }

  @Unroll("processPayment: #iba, #val")
  def 'exceptions'() {
    when:
    BankInterface.processPayment(new BankOperationData(iba, val, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
      
    then:
    thrown(BankException)

    where:
    iba       | val
    null      | 100
    "  "      | 100
    iban | 0
    "other"   | 0
  }

  def 'one ammount success'() {
    when:
    BankInterface.processPayment(new BankOperationData(iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

    then: 
    account.getBalance() == 499
  }
}
