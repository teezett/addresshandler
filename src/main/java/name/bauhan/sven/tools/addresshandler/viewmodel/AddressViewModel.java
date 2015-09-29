package name.bauhan.sven.tools.addresshandler.viewmodel;

import ezvcard.VCard;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Email;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewModel for the display of a selected address.
 *
 * @author Sven Bauhan <sde@sven.bauhan.name>
 */
public class AddressViewModel {

	/**
	 * Logger instance
	 */
	private static transient final Logger logger
					= LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	private final StringProperty prefix = new SimpleStringProperty();
	private final StringProperty givenName = new SimpleStringProperty();
	private final StringProperty familyName = new SimpleStringProperty();
	private final StringProperty privatePhone = new SimpleStringProperty();
	private final StringProperty mobilePhone = new SimpleStringProperty();
	private final StringProperty email = new SimpleStringProperty();
	private final StringProperty extAddress = new SimpleStringProperty();
	private final StringProperty street = new SimpleStringProperty();
	private final StringProperty plz = new SimpleStringProperty();
	private final StringProperty city = new SimpleStringProperty();
	private final ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();

	/**
	 * Set values of given address data.
	 * 
	 * @param vCard given address data
	 */
	public void set(VCard vCard) {
		StructuredName name = vCard.getStructuredName();
		List<String> prefixes = name.getPrefixes();
		String prefixString = StringUtils.join(prefixes, " ");
		prefix.set(prefixString);
		givenName.set(name.getGiven());
		familyName.set(name.getFamily());
		List<Telephone> tel_numbers = vCard.getTelephoneNumbers();
		tel_numbers.stream().forEach((tel) -> {
			if (tel.getTypes().contains(TelephoneType.HOME)) {
				privatePhone.set(tel.getText());
			} else if (tel.getTypes().contains(TelephoneType.CELL)) {
				mobilePhone.set(tel.getText());
			}
		});
		List<Email> email_addresses = vCard.getEmails();
		String emailText = email_addresses.stream()
						.map(mail -> mail.getValue()).reduce("", (a, b) -> a.concat(b + "\n"));
		email.set(emailText);
		List<Address> address_list = vCard.getAddresses();
		if (!address_list.isEmpty()) {
			Address addr = address_list.get(0);
			extAddress.set(addr.getExtendedAddress());
			street.set(addr.getStreetAddress());
			plz.set(addr.getPostalCode());
			city.set(addr.getLocality());
		}
		Birthday birth = vCard.getBirthday();
		LocalDate date = LocalDate.MAX;  // to express as undefined
		if (birth != null) {
			date = birth.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		birthday.set(date);
	}

	/**
	 * @return the prefix
	 */
	public StringProperty prefixProperty() {
		return prefix;
	}

	/**
	 * @return the givenName
	 */
	public StringProperty givenNameProperty() {
		return givenName;
	}

	/**
	 * @return the familyName
	 */
	public StringProperty familyNameProperty() {
		return familyName;
	}

	/**
	 * @return the privatePhone
	 */
	public StringProperty privatePhoneProperty() {
		return privatePhone;
	}

	/**
	 * @return the mobilePhone
	 */
	public StringProperty mobilePhoneProperty() {
		return mobilePhone;
	}

	/**
	 * @return the email
	 */
	public StringProperty emailProperty() {
		return email;
	}

	/**
	 * @return the extAddress
	 */
	public StringProperty extAddressProperty() {
		return extAddress;
	}

	/**
	 * @return the street
	 */
	public StringProperty streetProperty() {
		return street;
	}

	/**
	 * @return the plz
	 */
	public StringProperty plzProperty() {
		return plz;
	}

	/**
	 * @return the city
	 */
	public StringProperty cityProperty() {
		return city;
	}

	/**
	 * @return the birthday
	 */
	public ObjectProperty<LocalDate> birthdayProperty() {
		return birthday;
	}
}
