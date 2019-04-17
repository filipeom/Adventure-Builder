package pt.ulisboa.tecnico.softeng.tax.domain;

import java.util.Map;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class TaxPayer extends TaxPayer_Base {

	private final static int PERCENTAGE = 5;

	protected TaxPayer() {
		// this is a FenixFramework artifact; if not present, compilation fails.
		// the empty constructor is used by the base class to materialize objects from
		// the database, and in this case the classes Seller_Base and Buyer_Base, which
		// extend this class, have the empty constructor, which need to be present in
		// their superclass
		super();
	}

	public TaxPayer(IRS irs, String NIF, String name, String address) {
		checkArguments(irs, NIF, name, address);

		setNif(NIF);
		setName(name);
		setAddress(address);

		irs.addTaxPayer(this);
	}

	public void delete() {

		for (Invoice invoice : getInvoiceOfSellerSet())
			invoice.delete();

		for (Invoice invoice : getInvoiceOfBuyerSet())
			invoice.delete();

		setIrs(null);
		deleteDomainObject();
	}

	protected void checkArguments(IRS irs, String NIF, String name, String address) {
		if (NIF == null || NIF.length() != 9) {
			throw new TaxException();
		}

		if (name == null || name.length() == 0) {
			throw new TaxException();
		}

		if (address == null || address.length() == 0) {
			throw new TaxException();
		}

		if (irs.getTaxPayerByNIF(NIF) != null) {
			throw new TaxException();
		}

	}

	//Buyer-----------------------------------------------------------------------


	public long taxReturn(int year) {
		if (year < 1970) {
			throw new TaxException();
		}

		long result = 0;
		for (Invoice invoice : getInvoiceOfBuyerSet()) {
			if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
				result = result + invoice.getIva() * PERCENTAGE / 100;
			}
		}
		return result;
	}

	public Map<Integer, Long> getTaxReturnPerYear() {
		return getInvoiceOfBuyerSet().stream().map(i -> i.getDate().getYear()).distinct()
				.collect(Collectors.toMap(y -> y, y -> taxReturn(y)));
	}

	public Invoice getBuyerInvoiceByReference(String invoiceReference) {
		if (invoiceReference == null || invoiceReference.isEmpty()) {
			throw new TaxException();
		}

		for (Invoice invoice : getInvoiceOfBuyerSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}
		return null;
	}

	//Seller----------------------------------------------------------------------

	public long toPay(int year) {
		if (year < 1970) {
			throw new TaxException();
		}

		long result = 0;
		for (Invoice invoice : getInvoiceOfSellerSet()) {
			if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
				result = result + invoice.getIva();
			}
		}
		return result;
	}

	public Map<Integer, Long> getToPayPerYear() {
		return getInvoiceOfSellerSet().stream().map(i -> i.getDate().getYear()).distinct()
				.collect(Collectors.toMap(y -> y, y -> toPay(y)));
	}

	public Invoice getSellerInvoiceByReference(String invoiceReference) {
		if (invoiceReference == null || invoiceReference.isEmpty()) {
			throw new TaxException();
		}

		for (Invoice invoice : getInvoiceOfSellerSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}
		return null;
	}

}
