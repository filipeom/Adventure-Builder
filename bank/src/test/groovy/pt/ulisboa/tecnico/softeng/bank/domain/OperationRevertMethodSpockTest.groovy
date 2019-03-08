package pt.ulisboa.tecnico.softeng.bank.domain;

class OperationRevertMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account

	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		def client = new Client(this.bank, "António")
		this.account = new Account(this.bank, client)
	}

	def "revertDeposit"() {
		given:
		def reference = this.account.deposit(100).getReference()
		def operation = this.bank.getOperation(reference)

		when:
		def newReference = operation.revert()

		then:
		this.account.getBalance() == 0
		this.bank.getOperation(newReference) != null
		this.bank.getOperation(reference) != null
	}

	def "revertWithdraw"() {
		given:
		this.account.deposit(1000);
		def reference = this.account.withdraw(100).getReference();
		def operation = this.bank.getOperation(reference);

		when:
		def newReference = operation.revert()

		then:
		this.account.getBalance() == 1000
		this.bank.getOperation(newReference) != null
		this.bank.getOperation(reference) != null
	}
}
