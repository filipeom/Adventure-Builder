package pt.ulisboa.tecnico.softeng.activity.domain

import spock.lang.Shared
import spock.lang.Unroll
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityProviderConstructorSpockMethodTest extends SpockRollbackTestAbstractClass {
    @Shared def PROVIDER_CODE = "XtremX"
    @Shared def PROVIDER_NAME = "Adventure++"
    @Shared def IBAN = "IBAN"
    @Shared def NIF = "NIF"

    @Override
    def populate4Test() {
    }

    def 'success'() {
        when:
        def provider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN)

        then:
	with(provider) {
         getName() == PROVIDER_NAME
         getCode().length() == ActivityProvider.CODE_SIZE == true // triple comparation
         getActivitySet().size() == 0
	}
        FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1
     }

    @Unroll('ActivityProvider: #prov_code, #prov_name, #nif, #iban')
    def 'exception'() {
        when:
        new ActivityProvider(prov_code, prov_name, nif, iban)

        then:
        thrown(ActivityException)

        where:
        prov_code     | prov_name     | nif   | iban
        null          | PROVIDER_NAME | NIF   | IBAN
        "      "      | PROVIDER_NAME | NIF   | IBAN
        PROVIDER_CODE | null          | NIF   | IBAN
        PROVIDER_CODE | "    "        | NIF   | IBAN
        "12345"       | PROVIDER_NAME | NIF   | IBAN
        "1234567"     | PROVIDER_NAME | NIF   | IBAN
        PROVIDER_CODE | PROVIDER_NAME | null  | IBAN
        PROVIDER_CODE | PROVIDER_NAME | "   " | IBAN
    }
    
    @Unroll('ActivityProvider: #prov_code, #prov_name, #nif, #iban')
    def 'ActivityExceptions'() {
    	given:
        new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN)

	when:
	new ActivityProvider(prov_code, prov_name, nif, iban)

	then:
	thrown(ActivityException)

	and:
        FenixFramework.getDomainRoot().getActivityProviderSet().size() == 1

	where:
	prov_code     | prov_name     | nif       | iban
	PROVIDER_CODE | "Hello"       | NIF + "2" | IBAN 
	"123456"      | PROVIDER_NAME | NIF + "2" | IBAN
	"123456"      | "jdgdsk"      | NIF       | IBAN
    }
    
} 
