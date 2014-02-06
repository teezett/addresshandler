package name.bauhan.sven.tools.addresshandler;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.util.LDAPWriter;
import com.novell.ldap.util.LDIFWriter;
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
import ezvcard.property.StructuredName;
import ezvcard.util.PartialDate;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import org.apache.directory.api.ldap.model.entry.Attribute;
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
			attr = entry.get("givenName");
			if (attr != null) {
				name.setGiven(attr.getString());
			}
			attr = entry.get("sn");
			if (attr != null) {
				name.setFamily(attr.getString());
			}
			vCard.setStructuredName(name);
			attr = entry.get("homePhone");
			if (attr != null) {
				vCard.addTelephoneNumber(attr.getString(), TelephoneType.HOME);
			}
			attr = entry.get("mobile");
			if (attr != null) {
				vCard.addTelephoneNumber(attr.getString(), TelephoneType.CELL);
			}
			attr = entry.get("mail");
			if (attr != null) {
				vCard.addEmail(attr.getString(), EmailType.HOME);
			}
			Address address = new Address();
			attr = entry.get("streetaddress");
			if (attr != null) {
				address.setStreetAddress(attr.getString());
			}
			attr = entry.get("postalcode");
			if (attr != null) {
				address.setPostalCode(attr.getString());
			}
			attr = entry.get("l");
			if (attr != null) {
				address.setLocality(attr.getString());
			}
			vCard.addAddress(address);
			Attribute birth_day = entry.get("birthday");
			Attribute birth_month = entry.get("birthmonth");
			Attribute birth_year = entry.get("birthyear");
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
	protected void readFile() {
		addresses = new LinkedList<VCard>();
		LdifReader reader = new LdifReader();
		try {
			List<LdifEntry> entries = reader.parseLdifFile(file_name);
			for (LdifEntry entry : entries) {
				String name = entry.getDn().getName();
				AddressHandler.logger.info("Name: " + name);
				VCard vCard = readEntry(entry);
				addresses.add(vCard);
			}
		} catch (LdapLdifException ex) {
			logger.warn("Unable to read LDIF file: " + ex.getLocalizedMessage());
		}
	}

	private void writeEntry(LDAPWriter writer, VCard vCard) throws IOException, LDAPException {
		LDAPAttributeSet attrSet = new LDAPAttributeSet();
		LDAPAttribute attr;
		StructuredName name = vCard.getStructuredName();
		attr = new LDAPAttribute("givenName", name.getGiven());
		attrSet.add(attr);
		attr = new LDAPAttribute("sn", name.getFamily());
		attrSet.add(attr);
		List<Address> addressList = vCard.getAddresses();
		for (Address addr : addressList) {
			if (addr.getTypes().contains(AddressType.HOME)) {
				String street = addr.getStreetAddress();
				if (street != null) {
					attr = new LDAPAttribute("streetaddress", street);
					attrSet.add(attr);
				}
				String postalCode = addr.getPostalCode();
				if (postalCode != null) {
					attr = new LDAPAttribute("postalcode", postalCode);
					attrSet.add(attr);
				}
				String location = addr.getLocality();
				if (location != null) {
					attr = new LDAPAttribute("l", location);
					attrSet.add(attr);
				}
			}
		}
		List<Telephone> phones = vCard.getTelephoneNumbers();
		for (Telephone tel : phones) {
			if (tel.getTypes().contains(TelephoneType.HOME)) {
				attr = new LDAPAttribute("homePhone", tel.getText());
				attrSet.add(attr);
			} else if (tel.getTypes().contains(TelephoneType.CELL)) {
				attr = new LDAPAttribute("mobile", tel.getText());
				attrSet.add(attr);
			}
		}
		List<Email> emailList = vCard.getEmails();
		for (Email email : emailList) {
			if (email.getTypes().contains(EmailType.HOME)) {
				attr = new LDAPAttribute("email", email.getValue());
				attrSet.add(attr);
			}
		}
		Birthday birth = vCard.getBirthday();
		if (birth != null) {
			PartialDate partial = birth.getPartialDate();
			if (partial != null) {
				Integer year = partial.getYear();
				if (year != null) {
					attr = new LDAPAttribute("birthyear", year.toString());
					attrSet.add(attr);
				}
				Integer month = partial.getMonth();
				if (month != null) {
					attr = new LDAPAttribute("birthmonth", month.toString());
					attrSet.add(attr);
				}
				Integer day = partial.getDate();
				if (day != null) {
					attr = new LDAPAttribute("birthday", day.toString());
					attrSet.add(attr);
				}
			}
			Date date = birth.getDate();
			if (date != null) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(date);
				Integer year = cal.get(Calendar.YEAR);
				attr = new LDAPAttribute("birthyear", year.toString());
				attrSet.add(attr);
				Integer month = cal.get(Calendar.MONTH);
				attr = new LDAPAttribute("birthmonth", month.toString());
				attrSet.add(attr);
				Integer day = cal.get(Calendar.DATE);
				attr = new LDAPAttribute("birthday", day.toString());
				attrSet.add(attr);
			}
		}
		LDAPEntry entry = new LDAPEntry(vCard.getStructuredName().toString(), attrSet);
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
