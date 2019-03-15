package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

import spock.lang.Unroll
import spock.lang.Shared

class ClientConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {

  def BROKER_IBAN = "BROKER_IBAN"
  def NIF_AS_BUYER = "buyerNIF"
  def BROKER_NIF_AS_SELLER = "sellerNIF"
  def IBAN_BUYER = "IBAN"
  def OTHER_NIF = "987654321"
  @Shared def CLIENT_NIF = "123456789"
  @Shared def DRIVING_LICENSE = "IMT1234"
  @Shared def AGE = 20
  @Shared def CLIENT_IBAN = "BK011234567"

  protected Broker broker
  protected Client client

  @Override
  def populate4Test() {
    this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
  }

  def 'success'() {
    when:
      def client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

    then:
      assert CLIENT_IBAN == client.getIban()
      assert CLIENT_NIF == client.getNif()
      assert AGE == client.getAge()
  }

  @Unroll
  def 'exceptions'() {
    when:
      new Client(broker, iban, nif, drivingLicense, age)

    then:
      thrown(BrokerException)

    where:
	    broker      | iban        | nif        | drivingLicense  | age
	    null        | CLIENT_IBAN | CLIENT_NIF | DRIVING_LICENSE | AGE    
	    this.broker | null        | CLIENT_NIF | DRIVING_LICENSE | AGE
	    this.broker | ""          | CLIENT_NIF | DRIVING_LICENSE | AGE
	    this.broker | CLIENT_IBAN | null       | DRIVING_LICENSE | AGE
	    this.broker | CLIENT_IBAN | ""         | DRIVING_LICENSE | AGE
	    this.broker | CLIENT_IBAN | CLIENT_NIF | DRIVING_LICENSE | -1
	    this.broker | CLIENT_IBAN | CLIENT_NIF | ""              | AGE
  }

  def 'client Exists With Same IBAN'() {
    given:
      new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
      new Client(this.broker, CLIENT_IBAN, "OTHER_NIF", DRIVING_LICENSE + "1", AGE)

    expect:
      this.broker.getClientByNIF(CLIENT_NIF).getIban() == this.broker.getClientByNIF("OTHER_NIF").getIban()


  }

  def 'client Exists With Same NIF'() {
    given:
      def client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

    when:
      new Client(this.broker, "OTHER_IBAN", CLIENT_NIF, DRIVING_LICENSE + "1", AGE)

    then:
      thrown(BrokerException)

    and:
      client == this.broker.getClientByNIF(CLIENT_NIF)
  }

  def 'null Driving License'() {
    given:
      def client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, null, AGE)

    expect:
  		CLIENT_IBAN == client.getIban()
  		CLIENT_NIF == client.getNif()
  		AGE == client.getAge()
  		client.getDrivingLicense() == null
	}

}
