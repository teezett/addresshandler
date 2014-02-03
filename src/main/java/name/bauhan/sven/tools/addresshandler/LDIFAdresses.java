package name.bauhan.sven.tools.addresshandler;

import java.util.List;
import java.util.regex.Pattern;
import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Birthday;
import ezvcard.property.StructuredName;
import ezvcard.util.PartialDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.ldif.LdapLdifException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
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

	@Override
	public void writeFile() {
		try {
			Rdn rdn = new Rdn("cn", "org");
			Dn dn = new Dn("cn=test", "ou=example", "dn=address", "dn=org");
			LdifEntry entry = new LdifEntry(dn, "");
		} catch (LdapInvalidAttributeValueException ex) {
		} catch (LdapLdifException ex) {
		} catch (LdapInvalidDnException ex) {
		}
	}
}
