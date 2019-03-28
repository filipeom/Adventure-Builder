package pt.ulisboa.tecnico.softeng.activity.services.local;

import java.util.List;
import java.util.stream.Collectors;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;
import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityOfferData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.activity.domain.Processor;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;

public class ActivityInterface {

	@Atomic(mode = TxMode.READ)
	public List<ActivityProviderData> getProviders() {
		return FenixFramework.getDomainRoot().getActivityProviderSet().stream()
				.sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).map(p -> new ActivityProviderData(p))
				.collect(Collectors.toList());
	}

	@Atomic(mode = TxMode.WRITE)
	public void createProvider(final ActivityProviderData provider) {
        final Processor processor = new Processor(new BankInterface(), new TaxInterface());
		new ActivityProvider(provider.getCode(), provider.getName(), provider.getNif(), provider.getIban(), processor);
	}

	@Atomic(mode = TxMode.READ)
	public ActivityProviderData getProviderDataByCode(final String code) {
		final ActivityProvider provider = getProviderByCode(code);
		if (provider == null) {
			return null;
		}

		return new ActivityProviderData(provider);
	}

	@Atomic(mode = TxMode.WRITE)
	public void createActivity(final String code, final ActivityData activity) {
		final ActivityProvider provider = getProviderByCode(code);
		if (provider == null) {
			throw new ActivityException();
		}

		new Activity(provider, activity.getName(), activity.getMinAge() != null ? activity.getMinAge() : -1,
				activity.getMaxAge() != null ? activity.getMaxAge() : -1,
				activity.getCapacity() != null ? activity.getCapacity() : -1);
	}

	@Atomic(mode = TxMode.WRITE)
	public ActivityData getActivityDataByCode(final String codeProvider, final String codeActivity) {
		final Activity activity = getActivityByCode(codeProvider, codeActivity);
		if (activity == null) {
			return null;
		}

		return new ActivityData(activity);
	}

	@Atomic(mode = TxMode.READ)
	public ActivityOfferData getActivityOfferDataByExternalId(final String externalId) {
		final ActivityOffer offer = FenixFramework.getDomainObject(externalId);

		if (offer == null) {
			return null;
		}

		return new ActivityOfferData(offer);
	}

	@Atomic(mode = TxMode.WRITE)
	public void createOffer(final String codeProvider, final String codeActivity, final ActivityOfferData offer) {
		final Activity activity = getActivityByCode(codeProvider, codeActivity);
		if (activity == null) {
			throw new ActivityException();
		}

		new ActivityOffer(activity, offer.getBegin(), offer.getEnd(),
				offer.getAmount() != null ? offer.getAmount() : -1);
	}

	@Atomic(mode = TxMode.WRITE)
	public RestActivityBookingData reserveActivity(final RestActivityBookingData activityBookingData) {
		final Booking booking = getBookingByAdventureId(activityBookingData.getAdventureId());
		if (booking != null) {
			return new RestActivityBookingData(booking);
		}

		List<ActivityOffer> offers;
		for (final ActivityProvider provider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			offers = provider.findOffer(activityBookingData.getBegin(), activityBookingData.getEnd(),
					activityBookingData.getAge());
			if (!offers.isEmpty()) {
				Booking newBooking = offers.get(0).book(provider, offers.get(0), activityBookingData.getAge(),
						activityBookingData.getNif(), activityBookingData.getIban(),
						activityBookingData.getAdventureId());
				return new RestActivityBookingData(newBooking);
			}
		}
		throw new ActivityException();
	}

	@Atomic(mode = TxMode.WRITE)
	public void reserveActivity(final String externalId, final RestActivityBookingData bookingData) {
		final ActivityOffer offer = FenixFramework.getDomainObject(externalId);

		if (offer == null) {
			throw new ActivityException();
		}

		new Booking(offer.getActivity().getActivityProvider(), offer, bookingData.getNif(), bookingData.getIban());
	}

	@Atomic(mode = TxMode.WRITE)
	public String cancelReservation(final String reference) {
		final Booking booking = getBookingByReference(reference);
		if (booking != null && booking.getCancel() == null) {
			return booking.cancel();
		}
		throw new ActivityException();
	}

	@Atomic(mode = TxMode.READ)
	public RestActivityBookingData getActivityReservationData(final String reference) {
		for (final ActivityProvider provider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			for (final Activity activity : provider.getActivitySet()) {
				for (final ActivityOffer offer : activity.getActivityOfferSet()) {
					final Booking booking = offer.getBooking(reference);
					if (booking != null) {
						return new RestActivityBookingData(booking);
					}
				}
			}
		}
		throw new ActivityException();
	}

	@Atomic(mode = TxMode.WRITE)
	public void deleteActivityProviders() {
		FenixFramework.getDomainRoot().getActivityProviderSet().stream().forEach(p -> p.delete());
	}

	private Booking getBookingByReference(final String reference) {
		for (final ActivityProvider provider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			final Booking booking = provider.getBooking(reference);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

	private Booking getBookingByAdventureId(final String adventureId) {
		for (final ActivityProvider provider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			final Booking booking = provider.getBookingByAdventureId(adventureId);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

	public  ActivityProvider getProviderByCode(final String code) {
		return FenixFramework.getDomainRoot().getActivityProviderSet().stream().filter(p -> p.getCode().equals(code))
				.findFirst().orElse(null);
	}

	private Activity getActivityByCode(final String codeProvider, final String codeActivity) {
		final ActivityProvider provider = getProviderByCode(codeProvider);
		if (provider == null) {
			return null;
		}

		return provider.getActivitySet().stream().filter(a -> a.getCode().equals(codeActivity)).findFirst()
				.orElse(null);
	}

}
