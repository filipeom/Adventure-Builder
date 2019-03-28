package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException;

public class Processor extends Processor_Base {
	private static final String TRANSACTION_SOURCE = "ACTIVITY";
    private final BankInterface _bankInterface;
    private final TaxInterface _taxInterface;
    
    public Processor(BankInterface bankInterface, TaxInterface taxInterface){
        _bankInterface = bankInterface;
        _taxInterface = taxInterface;
    }
    
    public Processor(){
        _bankInterface = new BankInterface();
        _taxInterface = new TaxInterface();
    }
    
	public void delete() {
		setActivityProvider(null);

		for (Booking booking : getBookingSet()) {
			booking.delete();
		}

		deleteDomainObject();
	}

	public void submitBooking(Booking booking) {
		addBooking(booking);
		processInvoices();
	}

	private void processInvoices() {
		Set<Booking> failedToProcess = new HashSet<>();
		for (Booking booking : getBookingSet()) {
			if (!booking.isCancelled()) {
				if (booking.getPaymentReference() == null) {
					try {
						booking.setPaymentReference(_bankInterface.processPayment(new RestBankOperationData(
								booking.getIban(), booking.getAmount(), TRANSACTION_SOURCE, booking.getReference())));
					} catch (BankException | RemoteAccessException ex) {
						failedToProcess.add(booking);
						continue;
					}
				}
				RestInvoiceData invoiceData = new RestInvoiceData(booking.getProviderNif(), booking.getBuyerNif(),
						booking.getType(), booking.getAmount(), booking.getDate(), booking.getTime());
				try {
					booking.setInvoiceReference(_taxInterface.submitInvoice(invoiceData));
				} catch (TaxException | RemoteAccessException ex) {
					failedToProcess.add(booking);
				}
			} else {
				try {
					if (booking.getCancelledPaymentReference() == null) {
						booking.setCancelledPaymentReference(
								_bankInterface.cancelPayment(booking.getPaymentReference()));
					}
					_taxInterface.cancelInvoice(booking.getInvoiceReference());
					booking.setCancelledInvoice(true);
				} catch (BankException | TaxException | RemoteAccessException ex) {
					failedToProcess.add(booking);
				}

			}
		}

		for (Booking booking : getBookingSet()) {
			removeBooking(booking);
		}

		for (Booking booking : failedToProcess) {
			addBooking(booking);
		}
	}

}
