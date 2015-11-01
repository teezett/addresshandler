package name.bauhan.sven.tools.addresshandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Names of the attribute fields.
 * 
 * @author Sven
 */
enum Fieldnames {

	PREFIX("", "Titel", 0),
	GIVEN_NAME("givenName", "Vorname", 1),
	FAMILY_NAME("sn", "Nachname", 2),
	ORGANIZATION("organization", "Organisation", 11),
	HOME_PHONE("homePhone", "Festnetz", 3),
	CELL_PHONE("mobile", "Mobil", 4),
	EMAIL("mail", "Email", 5),
	EXT_ADDR("extAddress", "Adresszusatz", 6),
	STREET("streetaddress", "Straße", 7),
	POSTAL_CODE("postalcode", "PLZ", 8),
	CITY("l", "Ort", 9),
	BIRTHDAY("birthday", "Geburtstag", 10),
	BIRTHMONTH("birthmonth", "", -1),
	BIRTHYEAR("birthyear", "", -1);
	private final String ldap_attribute;
	private final String excel_title;
	private final int column;
	/**
	 * Mapping from field title to enum value.
	 */
	public static final Map<String, Fieldnames> TITLE2FIELD = new HashMap<String, Fieldnames>();

	static {
		for (Fieldnames field : Fieldnames.values()) {
			TITLE2FIELD.put(field.getExcel(), field);
		}
	}

	Fieldnames(String ldap, String excel, int count) {
		ldap_attribute = ldap;
		excel_title = excel;
		column = count;
	}

	/**
	 * @return the title
	 */
	public String getExcel() {
		return excel_title;
	}

	/**
	 * @return the index
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @return the ldap_attribute
	 */
	public String getLdap_attribute() {
		return ldap_attribute;
	}

}
