package pt.ulisboa.tecnico.softeng.bank.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.BankInterface;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.*;
import pt.ulisboa.tecnico.softeng.bank.services.remote.dataobjects.*;

@Controller
@RequestMapping(value = "/banks/{code}/clients/{id}/accounts")
public class AccountController {
	private static Logger logger = LoggerFactory.getLogger(AccountController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String accountForm(Model model, @PathVariable String code, @PathVariable String id) {
		logger.info("accountForm bankCode:{}, id:{}", code, id);

		ClientData clientData = BankInterface.getClientDataById(code, id);

		if (clientData == null) {
			model.addAttribute("error",
					"Error: it does not exist a client with id " + id + " in bank with code " + code);
			model.addAttribute("bank", BankInterface.getBankDataByCode(code));
			model.addAttribute("banks", BankInterface.getBanks());
			return "banks";
		} else {
			model.addAttribute("client", clientData);
			return "accounts";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String accountSubmit(Model model, @PathVariable String code, @PathVariable String id) {
		logger.info("accountSubmit bankCode:{}, clientId:{}", code, id);

		try {
			BankInterface.createAccount(code, id);
			model.addAttribute("bank", BankInterface.getBankDataByCode(code));
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to create de account");
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			return "accounts";
		}

		return "redirect:/banks/" + code + "/clients/" + id + "/accounts";
	}

	@RequestMapping(value = "/{iban}/operations", method = RequestMethod.GET)
	public String accountOperations(Model model, @PathVariable String code, @PathVariable String id,
			@PathVariable String iban) {
		logger.info("accountOperations bankCode:{}, clientId:{}, iban:{}", code, id, iban);

		try {
			AccountData account = BankInterface.getAccountData(iban);
      model.addAttribute("bank", BankInterface.getBankDataByCode(code));
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			model.addAttribute("account", account);
      model.addAttribute("data", new BankOperationData());
			return "account";
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to move to do the operations");
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			return "accounts";
		}

	}

  @RequestMapping(value = "/{iban}/operations/{ref}/revert", method = RequestMethod.GET)
	public String operationRevert(Model model, @PathVariable String code, @PathVariable String id,
			@PathVariable String iban, @PathVariable String ref) {
		logger.info("operationRevert bankCode:{}, clientId:{}, iban:{}, reference:{}", code, id, iban, ref);

		try {
			BankInterface.cancelPayment(ref);
      model.addAttribute("bank", BankInterface.getBankDataByCode(code));
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
      model.addAttribute("account", BankInterface.getAccountData(iban));
			return "redirect:/banks/" + code + "/clients/" + id + "/accounts/" + iban + "/operations";
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to revert the operation");
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
      model.addAttribute("account", BankInterface.getAccountData(iban));
      model.addAttribute("bank", BankInterface.getBankDataByCode(code));
      model.addAttribute("data", new BankOperationData());
			return "account";
		}

	}

	@RequestMapping(value = "/{iban}/deposit", method = RequestMethod.POST)
	public String accountDeposit(Model model, @PathVariable String code, @PathVariable String id,
			@PathVariable String iban, @ModelAttribute AccountData account) {
		logger.info("accountDeposit bankCode:{}, clientId:{}, iban:{}, amount:{}", code, id, iban, account.getAmount());

		try {
			BankInterface.deposit(iban, account.getAmount() != null ? account.getAmountLong() : -1);
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			model.addAttribute("account", BankInterface.getAccountData(iban));
			return "redirect:/banks/" + code + "/clients/" + id + "/accounts/" + iban + "/operations";
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to execute the operation");
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			model.addAttribute("account", BankInterface.getAccountData(iban));
      model.addAttribute("bank", BankInterface.getBankDataByCode(code));
      model.addAttribute("data", new BankOperationData());
			return "account";
		}
	}

	@RequestMapping(value = "/{iban}/withdraw", method = RequestMethod.POST)
	public String accountWithdraw(Model model, @PathVariable String code, @PathVariable String id,
			@PathVariable String iban, @ModelAttribute AccountData account) {
		logger.info("accountWithdraw bankCode:{}, clientId:{}, iban:{}, amount:{}", code, id, iban,
				account.getAmount());

		try {
			BankInterface.withdraw(iban, account.getAmount() != null ? account.getAmountLong() : -1);
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			model.addAttribute("account", BankInterface.getAccountData(iban));
			return "redirect:/banks/" + code + "/clients/" + id + "/accounts/" + iban + "/operations";
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to execute the operation");
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			model.addAttribute("account", BankInterface.getAccountData(iban));
      model.addAttribute("bank", BankInterface.getBankDataByCode(code));
      model.addAttribute("data", new BankOperationData());
			return "account";
		}

	}

	@RequestMapping(value = "/{iban}/transfer", method = RequestMethod.POST)
	public String accountTransfer(Model model, @PathVariable String code, @PathVariable String id,
			@PathVariable String iban, @ModelAttribute BankOperationData data) {
		logger.info("accountTransfer sourceIban:{}, targetIban:{}, value:{}, source:{}, reference:{}",
        iban, data.getTargetIban(), data.getValue(), data.getTransactionSource(),
        data.getTransactionReference());

    RestBankOperationData restOperationData = new RestBankOperationData(
        iban, data.getTargetIban(), data.getValueLong(),
        data.getTransactionSource(), data.getTransactionReference());

		try {
      BankInterface.processPayment(restOperationData);
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			model.addAttribute("account", BankInterface.getAccountData(iban));
      model.addAttribute("data", data);
			return "redirect:/banks/" + code + "/clients/" + id + "/accounts/" + iban + "/operations";
		} catch (BankException be) {
			model.addAttribute("error", "Error: it was not possible to execute the operation");
			model.addAttribute("client", BankInterface.getClientDataById(code, id));
			model.addAttribute("account", BankInterface.getAccountData(iban));
      model.addAttribute("bank", BankInterface.getBankDataByCode(code));
      model.addAttribute("data", new BankOperationData());
			return "account";
		}
	}

}
