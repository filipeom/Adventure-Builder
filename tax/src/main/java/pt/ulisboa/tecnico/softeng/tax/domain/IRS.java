package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;


public class IRS extends IRS_Base {
    public static final int SCALE = 1000;

    public static IRS getIRSInstance() {
        if (FenixFramework.getDomainRoot().getIrs() == null) {
            return createIrs();
        }
        return FenixFramework.getDomainRoot().getIrs();
    }

    @Atomic(mode = TxMode.WRITE)
    private static IRS createIrs() {
        return new IRS();
    }

    private IRS() {
        setRoot(FenixFramework.getDomainRoot());
    }

    public void delete() {
        setRoot(null);

        clearAll();

        deleteDomainObject();
    }

    public TaxPayer getTaxPayerByNif(String nif) {
        for (TaxPayer taxPayer : getTaxPayerSet()) {
            if (taxPayer.getNif().equals(nif)) {
                return taxPayer;
            }
        }
        return null;
    }

    public ItemType getItemTypeByName(String name) {
        for (ItemType itemType : getItemTypeSet()) {
            if (itemType.getName().equals(name)) {
                return itemType;
            }
        }
        return null;
    }

    private void clearAll() {
        for (ItemType itemType : getItemTypeSet()) {
            itemType.delete();
        }

        for (TaxPayer taxPayer : getTaxPayerSet()) {
            taxPayer.delete();
        }

        for (Invoice invoice : getInvoiceSet()) {
            invoice.delete();
        }

    }

    @Override
    public int getCounter() {
        int counter = super.getCounter() + 1;
        setCounter(counter);
        return counter;
    }

    public Map<Integer, Long> getTaxesPerYear() {
      Map<Integer, Long> taxes = new TreeMap<>();

      for (TaxPayer taxPayer : getTaxPayerSet()) {
        for(Integer year : taxPayer.getToPayPerYear().keySet()) {
          if(taxes.containsKey(year))
            taxes.replace(year, taxes.get(year) + taxPayer.getToPayPerYear().get(year));
          else
            taxes.put(year, taxPayer.getToPayPerYear().get(year));
        }
      }

      return taxes;
    }

    public Map<Integer, Long> getTaxesReturnPerYear() {
      Map<Integer, Long> taxes = new TreeMap<>();

      for (TaxPayer taxPayer : getTaxPayerSet()) {
        for(Integer year : taxPayer.getTaxReturnPerYear().keySet()) {
          if(taxes.containsKey(year))
            taxes.replace(year, taxes.get(year) + taxPayer.getTaxReturnPerYear().get(year));
          else
            taxes.put(year, taxPayer.getTaxReturnPerYear().get(year));
        }
      }

      return taxes;
    }

}
