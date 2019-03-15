package pt.ulisboa.tecnico.softeng.bank.domain

class OperationRevertMethodSpockTest extends SpockRollbackTestAbstractClass {

	def bank
	def account

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
	}

	def "revertDeposit"() {
		given:
		def reference = account.deposit(100).getReference()
		def operation = bank.getOperation(reference)

		when:
		def newReference = operation.revert()

		then:
		with (account) {
			getBalance() == 0
		}

		with (bank) {
			getOperation(newReference) != null
			getOperation(reference) != null
		}
	}

	def "revertWithdraw"() {
		given:
		account.deposit(1000)
		def reference = account.withdraw(100).getReference()
		def operation = bank.getOperation(reference)

		when:
		def newReference = operation.revert()

		then:
		with (account) {
			getBalance() == 1000
		}

		with (bank) {
			getOperation(newReference) != null
			getOperation(reference) != null
		}
	}

}
