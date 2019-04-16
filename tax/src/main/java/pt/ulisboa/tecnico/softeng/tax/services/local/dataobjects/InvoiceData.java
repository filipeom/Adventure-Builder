package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import pt.ulisboa.tecnico.softeng.tax.domain.Invoice;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class InvoiceData {
	private String reference;
	private String sellerNif;
	private String buyerNif;
	private String itemType;
	private Double value;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	private Double iva;
	private DateTime time;

	public InvoiceData() {
	}

	public InvoiceData(String reference, String sellerNif, String buyerNif, String itemType, Long value,
			LocalDate date, DateTime time) {
		if (reference == null) {
			throw new TaxException();
		}
		this.reference = reference;
		this.sellerNif = sellerNif;
		this.buyerNif = buyerNif;
		this.itemType = itemType;
		this.value = (double) (Math.round((value / 1000.0) * 1000) / 1000);
		this.date = date;
		this.time = time;
	}

	public InvoiceData(Invoice invoice) {
		this.reference = invoice.getReference();
		this.sellerNif = invoice.getSeller().getNif();
		this.buyerNif = invoice.getBuyer().getNif();
		this.itemType = invoice.getItemType().getName();
		this.value = (double) (Math.round((invoice.getValue() / 1000.0) * 1000) / 1000);
		this.date = invoice.getDate();
		this.iva = (double) (Math.round((invoice.getIva() / 1000.0) * 1000) / 1000);
		this.time = invoice.getTime();
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getSellerNif() {
		return this.sellerNif;
	}

	public void setSellerNif(String sellerNif) {
		this.sellerNif = sellerNif;
	}

	public String getBuyerNif() {
		return this.buyerNif;
	}

	public void setBuyerNif(String buyerNif) {
		this.buyerNif = buyerNif;
	}

	public String getItemType() {
		return this.itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Long getValue() {
		return Math.round(this.value.doubleValue() * 1000);
	}

	public void setValue(Long value) {
		this.value = (double) (Math.round((value / 1000.0) * 1000) / 1000);
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getIva() {
		return Math.round(this.iva.doubleValue() * 1000);
	}

	public void setIva(long iva) {
		this.iva = (double) (Math.round((iva / 1000.0) * 1000) / 1000);
	}

	public DateTime getTime() {
		return this.time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

}
