package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;

public class Activity extends Activity_Base {
	private static final int MIN_AGE = 18;
	private static final int MAX_AGE = 100;

	public Activity(final ActivityProvider provider, final String name, final int minAge, final int maxAge, final int capacity) {
		checkArguments(provider, name, minAge, maxAge, capacity);

		setCode(provider.getCode() + Integer.toString(provider.getCounter()));
		setName(name);
		setMinAge(minAge);
		setMaxAge(maxAge);
		setCapacity(capacity);

		setActivityProvider(provider);
	}

	public void delete() {
		setActivityProvider(null);

		for (final ActivityOffer offer : getActivityOfferSet()) {
			offer.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(ActivityProvider provider, final String name, final int minAge, final int maxAge, final int capacity) {
		if (provider == null || name == null || name.trim().equals("")) {
			throw new ActivityException();
		}

		if (minAge < MIN_AGE || maxAge >= MAX_AGE || minAge > maxAge) {
			throw new ActivityException();
		}

		if (capacity < 1) {
			throw new ActivityException();
		}

	}

	Set<ActivityOffer> getOffers(final LocalDate begin, final LocalDate end, final int age) {
		final Set<ActivityOffer> result = new HashSet<>();
		for (final ActivityOffer offer : getActivityOfferSet()) {
			if (matchAge(age) && offer.available(begin, end)) {
				result.add(offer);
			}
		}
		return result;
	}

	boolean matchAge(final int age) {
		return age >= getMinAge() && age <= getMaxAge();
	}

	public Booking getBooking(final String reference) {
		for (final ActivityOffer offer : getActivityOfferSet()) {
			final Booking booking = offer.getBooking(reference);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

}
