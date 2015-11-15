package name.bauhan.sven.tools.addresshandler;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPControl;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.util.LDAPWriter;
import com.novell.ldap.util.LDIFWriter;
import com.novell.ldap.util.LDIFReader;
import java.util.List;
import java.util.regex.Pattern;
import ezvcard.VCard;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Telephone;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.Email;
import ezvcard.property.Organization;
import ezvcard.property.StructuredName;
import ezvcard.util.PartialDate;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.logging.Level;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.ldif.LdapLdifException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle LDIF address files.
 *
 * @author Sven
 */
public class LDIFAdresses extends AddressFile {

	/**
	 * Logger instance
	 */
	private static transient final Logger logger
					= LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	public LDIFAdresses(String filename) {
		super(filename);
	}

	static protected Pattern file_pattern() {
		final Pattern xlsExt = Pattern.compile(".*[.][lL][dD][iI][fF]");
		return xlsExt;
	}

	private VCard readEntry(LdifEntry entry) {
		VCard vCard = new VCard();
		StructuredName name = new StructuredName();
		try {
			Attribute attr;
			// name
			attr = entry.get(Fieldnames.GIVEN_NAME.getLdap_attribute());
			if (attr != null) {
				name.setGiven(attr.getString());
			}
			attr = entry.get(Fieldnames.FAMILY_NAME.getLdap_attribute());
			if (attr != null) {
				name.setFamily(attr.getString());
			}
			vCard.setStructuredName(name);
			// organization
			attr = entry.get(Fieldnames.ORGANIZATION.getLdap_attribute());
			if (attr != null) {
				vCard.setOrganization(attr.getString().split(", "));
			}
			// phone
			attr = entry.get(Fieldnames.HOME_PHONE.getLdap_attribute());
			if (attr != null) {
				vCard.addTelephoneNumber(attr.getString(), TelephoneType.HOME);
			}
			attr = entry.get(Fieldnames.CELL_PHONE.getLdap_attribute());
			if (attr != null) {
				vCard.addTelephoneNumber(attr.getString(), TelephoneType.CELL);
			}
			// email
			attr = entry.get(Fieldnames.EMAIL.getLdap_attribute());
			if (attr != null) {
				vCard.addEmail(attr.getString(), EmailType.HOME);
			}
			// adresses
			Address address = new Address();
			attr = entry.get(Fieldnames.STREET.getLdap_attribute());
			if (attr != null) {
				address.setStreetAddress(attr.getString());
			}
			attr = entry.get(Fieldnames.POSTAL_CODE.getLdap_attribute());
			if (attr != null) {
				address.setPostalCode(attr.getString());
			}
			attr = entry.get(Fieldnames.CITY.getLdap_attribute());
			if (attr != null) {
				address.setLocality(attr.getString());
			}
			vCard.addAddress(address);
			// birthday
			Attribute birth_day = entry.get(Fieldnames.BIRTHDAY.getLdap_attribute());
			Attribute birth_month = entry.get(Fieldnames.BIRTHMONTH.getLdap_attribute());
			Attribute birth_year = entry.get(Fieldnames.BIRTHYEAR.getLdap_attribute());
			if ((birth_day != null) && (birth_month != null)) {
				Birthday birthday;
				int day = Integer.parseInt(birth_day.getString());
				int month = Integer.parseInt(birth_month.getString());
				if (birth_year != null) {
					int year = Integer.parseInt(birth_year.getString());
					GregorianCalendar cal = new GregorianCalendar(year, month, day);
					birthday = new Birthday(cal.getTime());
				} else {
					PartialDate date = PartialDate.date(null, month, day);
					birthday = new Birthday(date);
				}
				vCard.setBirthday(birthday);
			}
		} catch (LdapInvalidAttributeValueException ex) {
			logger.error(ex.getLocalizedMessage());
		}
		return vCard;
	}

	/**
	 * Reading an LDIF file.
	 */
	@Override
	public void readFile() {
		addresses = new LinkedList<VCard>();
//		try {
//			FileInputStream is = new FileInputStream(file_name);
//			LDIFReader reader = new LDIFReader(is);
//			LDAPMessage message = reader.readMessage();
//			LDAPControl[] controls = message.getControls();
//			for (LDAPControl control : controls) {
//			}
//			is.close();
//		} catch (IOException ex) {
//		} catch (LDAPLocalException ex) {
//		} catch (LDAPException ex) {
//		}
		LdifReader reader = new LdifReader();
		try {
			FileInputStream is = new FileInputStream(file_name);
			BufferedReader buf_read = new BufferedReader(new InputStreamReader(is));
			List<LdifEntry> entries = reader.parseLdif(buf_read);
			for (LdifEntry entry : entries) {
				VCard vCard = readEntry(entry);
				addresses.add(vCard);
			}
		} catch (LdapLdifException ex) {
			logger.warn("Unable to read LDIF file: " + ex.getLocalizedMessage());
		} catch (FileNotFoundException ex) {
			logger.warn("Unable to read LDIF file: " + ex.getLocalizedMessage());
		} catch (LdapException ex) {
			logger.warn("Unable to read LDIF file: " + ex.getLocalizedMessage());
		}
	}

	private void writeEntry(LDAPWriter writer, VCard vCard) throws IOException, LDAPException {
		LDAPAttributeSet attrSet = new LDAPAttributeSet();
		LDAPAttribute attr;
		// name
		StructuredName name = vCard.getStructuredName();
		attr = new LDAPAttribute(Fieldnames.GIVEN_NAME.getLdap_attribute(), name.getGiven());
		attrSet.add(attr);
		attr = new LDAPAttribute(Fieldnames.FAMILY_NAME.getLdap_attribute(), name.getFamily());
		attrSet.add(attr);
		attr = new LDAPAttribute("cn", name.getFamily() + ", " + name.getGiven());
		attrSet.add(attr);
		// organization
		Organization organization = vCard.getOrganization();
		if ((organization != null) && (organization.getValues() != null) && !organization.getValues().isEmpty()) {
			String organizations = String.join(", ", organization.getValues());
			attr = new LDAPAttribute(Fieldnames.ORGANIZATION.getLdap_attribute(), organizations);
			attrSet.add(attr);
		}
		// addresses
		List<Address> addressList = vCard.getAddresses();
		for (Address addr : addressList) {
			if (addr.getTypes().contains(AddressType.HOME)) {
				String street = addr.getStreetAddress();
				if (street != null) {
					attr = new LDAPAttribute(Fieldnames.STREET.getLdap_attribute(), street);
					attrSet.add(attr);
				}
				String postalCode = addr.getPostalCode();
				if (postalCode != null) {
					attr = new LDAPAttribute(Fieldnames.POSTAL_CODE.getLdap_attribute(), postalCode);
					attrSet.add(attr);
				}
				String location = addr.getLocality();
				if (location != null) {
					attr = new LDAPAttribute(Fieldnames.CITY.getLdap_attribute(), location);
					attrSet.add(attr);
				}
			}
		}
		// phones
		List<Telephone> phones = vCard.getTelephoneNumbers();
		for (Telephone tel : phones) {
			if (tel.getTypes().contains(TelephoneType.HOME)) {
				attr = new LDAPAttribute(Fieldnames.HOME_PHONE.getLdap_attribute(), tel.getText());
				attrSet.add(attr);
			} else if (tel.getTypes().contains(TelephoneType.CELL)) {
				attr = new LDAPAttribute(Fieldnames.CELL_PHONE.getLdap_attribute(), tel.getText());
				attrSet.add(attr);
			}
		}
		// Email
		List<Email> emailList = vCard.getEmails();
		for (Email email : emailList) {
			if (email.getTypes().contains(EmailType.HOME)) {
				attr = new LDAPAttribute(Fieldnames.EMAIL.getLdap_attribute(), email.getValue());
				attrSet.add(attr);
			}
		}
		// Birthday
		Birthday birth = vCard.getBirthday();
		if (birth != null) {
			PartialDate partial = birth.getPartialDate();
			if (partial != null) {
				Integer year = partial.getYear();
				if (year != null) {
					attr = new LDAPAttribute(Fieldnames.BIRTHYEAR.getLdap_attribute(), year.toString());
					attrSet.add(attr);
				}
				Integer month = partial.getMonth();
				if (month != null) {
					attr = new LDAPAttribute(Fieldnames.BIRTHMONTH.getLdap_attribute(), month.toString());
					attrSet.add(attr);
				}
				Integer day = partial.getDate();
				if (day != null) {
					attr = new LDAPAttribute(Fieldnames.BIRTHDAY.getLdap_attribute(), day.toString());
					attrSet.add(attr);
				}
			}
			Date date = birth.getDate();
			if (date != null) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(date);
				Integer year = cal.get(Calendar.YEAR);
				attr = new LDAPAttribute(Fieldnames.BIRTHYEAR.getLdap_attribute(), year.toString());
				attrSet.add(attr);
				Integer month = cal.get(Calendar.MONTH);
				attr = new LDAPAttribute(Fieldnames.BIRTHMONTH.getLdap_attribute(), month.toString());
				attrSet.add(attr);
				Integer day = cal.get(Calendar.DATE);
				attr = new LDAPAttribute(Fieldnames.BIRTHDAY.getLdap_attribute(), day.toString());
				attrSet.add(attr);
			}
		}
		Path path = FileSystems.getDefault().getPath(file_name);
		String dn = "uid=" + name.getGiven() + name.getFamily() + ", ou=" + path.getFileName()
						+ ", dc=de";
		LDAPEntry entry = new LDAPEntry(dn, attrSet);
		writer.writeEntry(entry);
	}

	@Override
	public void writeFile() {
		try {
			FileOutputStream fileStream = new FileOutputStream(file_name);
			LDIFWriter writer = new LDIFWriter(fileStream);
			for (VCard vcard : addresses) {
				writeEntry(writer, vcard);
			}
			writer.finish();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getLocalizedMessage());
		} catch (IOException ex) {
			logger.error(ex.getLocalizedMessage());
		} catch (LDAPException ex) {
			logger.error(ex.getLocalizedMessage());
		}
	}
}
