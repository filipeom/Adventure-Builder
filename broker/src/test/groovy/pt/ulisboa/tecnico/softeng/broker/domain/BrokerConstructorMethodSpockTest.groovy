package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

import spock.lang.Unroll
import spock.lang.Shared

class BrokerConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
  @Shared def BROKER_CODE = "BR01"
  @Shared def BROKER_NAME = "WeExplore"
  @Shared def BROKER_IBAN = "BROKER_IBAN"
  @Shared def BROKER_NIF_AS_BUYER = "buyerNIF"
  @Shared def NIF_AS_BUYER = "buyerNIF"
  @Shared def BROKER_NIF_AS_SELLER = "sellerNIF"

  @Override
  def populate4Test() {}

  def 'success'() {
    when:
      def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)

    then:
      BROKER_CODE == broker.getCode()
      BROKER_NAME == broker.getName()
      broker.getAdventureSet().size() == 0
      FenixFramework.getDomainRoot().getBrokerSet().contains(broker) == true
  }

  @Unroll
  def 'exceptions'(){
    when:
      new Broker(code, name, nif_as_seller, nif_as_buyer, iban)

    then:
      thrown(BrokerException)

    and:
      FenixFramework.getDomainRoot().getBrokerSet().size() == 0

    where:
      code        | name        | nif_as_seller        | nif_as_buyer         | iban
      null        | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | BROKER_IBAN
      ""          | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | BROKER_IBAN
      "  "        | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | BROKER_IBAN
      BROKER_CODE | null        | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | BROKER_IBAN
      BROKER_CODE | ""          | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | BROKER_IBAN
      BROKER_CODE | "    "      | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | BROKER_IBAN
      BROKER_CODE | BROKER_NAME | null                 | NIF_AS_BUYER         | BROKER_IBAN
      BROKER_CODE | BROKER_NAME | "    "               | NIF_AS_BUYER         | BROKER_IBAN
      BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | null                 | BROKER_IBAN
      BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | "   "                | BROKER_IBAN
      BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | null
      BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | "    "
      BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | BROKER_NIF_AS_SELLER | BROKER_IBAN
  }

  def 'unique Code'() {
    given:
      def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)

    when:
      new Broker(BROKER_CODE, "WeExploreX", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)

    then:
      thrown(BrokerException)

    and:
      FenixFramework.getDomainRoot().getBrokerSet().size() == 1
      FenixFramework.getDomainRoot().getBrokerSet().contains(broker) == true
  }

  def 'unique Seller NIF'(){
    given:
      def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, "123456789", BROKER_IBAN)

    when:
      new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)

    then:
      thrown(BrokerException)

    and:
      FenixFramework.getDomainRoot().getBrokerSet().size() == 1
  }

  def 'unique Buyer NIF One'() {
    given:
      def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)

    when:
      new Broker(BROKER_CODE, BROKER_NAME, "123456789", NIF_AS_BUYER, BROKER_IBAN)

    then:
      thrown(BrokerException)

    and:
      FenixFramework.getDomainRoot().getBrokerSet().size() == 1
  }

  def 'unique Buyer Seller NIF Two'() {
    given:
      def broker = 	new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)

    when:
      new Broker(BROKER_CODE, BROKER_NAME, NIF_AS_BUYER, "123456789", BROKER_IBAN)

    then:
      thrown(BrokerException)

    and:
      FenixFramework.getDomainRoot().getBrokerSet().size() == 1
  }
}
