package pt.ulisboa.tecnico.softeng.broker.services.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class BankInterface {
	private final Logger logger = LoggerFactory.getLogger(BankInterface.class);

	private final String ENDPOINT = "http://localhost:8082";

	public String processPayment(RestBankOperationData bankOperationData) {
		this.logger.info("processPayment iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
				bankOperationData.getIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
				bankOperationData.getTransactionReference());

		RestTemplate restTemplate = new RestTemplate();
		try {
			String result = restTemplate.postForObject(
					ENDPOINT + "/rest/banks/accounts/" + bankOperationData.getIban() + "/processPayment",
					bankOperationData, String.class);
			return result;
		} catch (HttpClientErrorException e) {
			this.logger.info(
					"processPayment HttpClientErrorException  iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
					bankOperationData.getIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
					bankOperationData.getTransactionReference());
			throw new BankException();
		} catch (Exception e) {
			this.logger.info("processPayment Exception iban:{}, amount:{}, transactionSource:{}, transactionReference:{}",
					bankOperationData.getIban(), bankOperationData.getValue(), bankOperationData.getTransactionSource(),
					bankOperationData.getTransactionReference());
			throw new RemoteAccessException();
		}
	}

	public String cancelPayment(String reference) {
		this.logger.info("cancelPayment reference:{}", reference);

		RestTemplate restTemplate = new RestTemplate();
		try {
			String result = restTemplate.postForObject(ENDPOINT + "/rest/banks/cancel?reference=" + reference, null,
					String.class);
			return result;
		} catch (HttpClientErrorException e) {
			this.logger.info("cancelPayment HttpClientErrorException reference:{}", reference);
			throw new BankException();
		} catch (Exception e) {
			this.logger.info("cancelPayment Exception reference:{}", reference);
			throw new RemoteAccessException();
		}
	}

	public RestBankOperationData getOperationData(String reference) {
		this.logger.info("getOperationData reference:{}", reference);

		RestTemplate restTemplate = new RestTemplate();
		try {
			RestBankOperationData result = restTemplate.getForObject(
					ENDPOINT + "/rest/banks/operation?reference=" + reference, RestBankOperationData.class);
			this.logger.info("getOperationData iban:{}", result.getIban());
			return result;
		} catch (HttpClientErrorException e) {
			this.logger.info("getOperationData HttpClientErrorException reference:{}", reference);
			throw new BankException();
		} catch (Exception e) {
			this.logger.info("getOperationData Exception reference:{}", reference);
			throw new RemoteAccessException();
		}
	}

}
