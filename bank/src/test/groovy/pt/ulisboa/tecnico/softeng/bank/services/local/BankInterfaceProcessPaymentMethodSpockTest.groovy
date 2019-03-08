package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData
import spock.lang.Unroll

class BankInterfaceProcessPaymentMethodSpockTest extends SpockRollbackTestAbstractClass {
  def TRANSACTION_SOURCE = "ADVENTURE"
  def TRANSACTION_REFERENCE = "REFERENCE"

  def bank
  def account
  def iban

  @Override
  def populate4Test() {
    this.bank = new Bank("Money", "BK01")
    def client = new Client(this.bank, "António")
    this.account = new Account(this.bank, client)
    this.iban = this.account.getIBAN()
    this.account.deposit(500)
  }

  def 'success'() {
    when:
    this.account.getIBAN();
    def newReference = BankInterface.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

    then:
    newReference != null
    newReference.startsWith("BK01") == true

    this.bank.getOperation(newReference) != null
    this.bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
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
    BankInterface.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"))

    then:
    otherAccount.getBalance() == 900
    this.account.getBalance() == 400
  }

  def 'redo an already payed'() {
    when:
    this.account.getIBAN()
    def firstReference = BankInterface.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
    def secondReference = BankInterface.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
    then:
    firstReference == secondReference
    this.account.getBalance() == 400
  }

  @Unroll("processPayment: #iban, #val")
  def 'exceptions'() {
    when:
    BankInterface.processPayment(new BankOperationData(iban, val, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
      
    then:
    thrown(BankException)

    where:
    iban      | val
    null      | 100
    "  "      | 100
    this.iban | 0
    "other"   | 0
  }

  def 'one ammount success'() {
    when:
    BankInterface.processPayment(new BankOperationData(this.iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

    then: 
    this.account.getBalance() == 499
  }
}
