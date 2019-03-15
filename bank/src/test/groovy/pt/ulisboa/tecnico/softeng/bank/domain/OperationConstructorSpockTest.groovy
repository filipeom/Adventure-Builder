package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.exception.BankException


import spock.lang.*

class OperationConstructorSpockTest extends SpockRollbackTestAbstractClass{
  def bank
  @Shared def account
  def client

  def populate4Test(){
    bank = new Bank("Money", "BK01")
    client = new Client(bank, "António")
    account = new Account(bank, client)
  }

  def "success"(){
    when:
    def operation = new Operation(Type.DEPOSIT, account, 1000)

    then:
    operation.getReference().startsWith(bank.getCode()) == true
    operation.getReference().length() > bank.CODE_SIZE
    operation.getType() == Type.DEPOSIT
    operation.getAccount() == account
    operation.getValue() == 1000
    operation.getTime() != null
    operation == bank.getOperation(operation.getReference())

  }

  @Unroll("Operation: #type, #account_u, #amount")
  def "exceptions"(){
    when:
    new Operation(type, account_u, amount)
    then:
    thrown(BankException)

    where:

    type          | account_u        | amount
    null          | account        | 1000
    Type.WITHDRAW | null           | 1000
    Type.DEPOSIT  | account        | 0
    Type.WITHDRAW | account        | -1000
  }

  def "oneAmount"(){
    when:
    def operation = new Operation(Type.DEPOSIT, account, 1)

    then:
    operation == bank.getOperation(operation.getReference())
  }

}
