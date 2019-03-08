package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework

class BankPersistenceSpockTest extends SpockPersistenceTestAbstractClass {

	def BANK_NAME = "Money"
	def BANK_CODE = "BK01"
	def CLIENT_NAME = "João dos Anzóis"

	@Override
	def whenCreateInDatabase() {
		def bank = new Bank(BANK_NAME, BANK_CODE)

		def client = new Client(bank, CLIENT_NAME)

		def account = new Account(bank, client)

		account.deposit(100)
	}

	@Override
	def thenAssert() {
		assert FenixFramework.getDomainRoot().getBankSet().size() == 1

		def banks = new ArrayList<>(FenixFramework.getDomainRoot().getBankSet())
		def bank = banks.get(0)

		assert bank.getName() == BANK_NAME
		assert bank.getCode() == BANK_CODE
		assert bank.getClientSet().size() == 1
		assert bank.getAccountSet().size() == 1
		assert bank.getOperationSet().size() == 1

		def clients = new ArrayList<>(bank.getClientSet())
		def client = clients.get(0)

		assert client.getName() == CLIENT_NAME

		def accounts = new ArrayList<>(bank.getAccountSet())
		def account = accounts.get(0)

		assert account.getClient() == client
		assert account.getIBAN() != null
		assert account.getBalance() == 100

		def operations = new ArrayList<>(bank.getOperationSet())
		def operation = operations.get(0)

		assert operation.getType() == Operation.Type.DEPOSIT
		assert operation.getValue() == 100
		assert operation.getReference() != null
		assert operation.getTime() != null
	}

	@Override
	def deleteFromDatabase() {
		for (def bank : FenixFramework.getDomainRoot().getBankSet()) {
			bank.delete()
		}
	}

}
