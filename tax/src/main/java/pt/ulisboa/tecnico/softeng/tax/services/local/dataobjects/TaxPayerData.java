package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer;

public class TaxPayerData {

	private String nif;
	private String name;
	private String address;
	private Map<Integer, Long> sellerTaxes = new TreeMap<Integer, Long>();
	private Map<Integer, Long> buyerTaxes = new TreeMap<Integer, Long>();

	public TaxPayerData() {
	}

	public TaxPayerData(TaxPayer taxPayer) {
		this.nif = taxPayer.getNif();
		this.name = taxPayer.getName();
		this.address = taxPayer.getAddress();
		this.sellerTaxes = taxPayer.getToPayPerYear();
		this.buyerTaxes = taxPayer.getTaxReturnPerYear();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Map<Integer, Long> getSellerTaxes() {
		return this.sellerTaxes;
	}

	public void setSellerTaxes(Map<Integer, Long> taxes) {
		this.sellerTaxes = taxes;
	}

	public Map<Integer, Long> getBuyerTaxes() {
		return this.buyerTaxes;
	}

	public void setBuyerTaxes(Map<Integer, Long> taxes) {
		this.buyerTaxes = taxes;
	}

}
