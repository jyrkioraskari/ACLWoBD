package fi.aalto.cs.drumbeat;

import javafx.collections.ObservableList;

/*
* 
Jyrki Oraskari, Aalto University, 2016 

This research has partly been carried out at Aalto University in DRUMBEAT 
Web-Enabled Construction Lifecycle (2014-2017) funded by Tekes, 
Aalto University, and the participating companies.

The MIT License (MIT)
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public class Country {
  private final String Country_Name;
	private final String Country_Code;

	public Country(String country_Code, String country_Name) {
		super();
		Country_Name = country_Name;
		Country_Code = country_Code;
	}

	public String getCountry_Name() {
		return Country_Name;
	}

	public String getCountry_Code() {
		return Country_Code;
	}

	@Override
	public String toString() {
		return Country_Name;
	}
	
	
	static public void insert_countries(ObservableList<Country> countries)
	{
		countries.add(new Country("US","United States of America")); 
		countries.add(new Country("CA","Canada")); 
		countries.add(new Country("AX","�land Islands")); 
		countries.add(new Country("AD","Andorra")); 
		countries.add(new Country("AE","United Arab Emirates")); 
		countries.add(new Country("AF","Afghanistan")); 
		countries.add(new Country("AG","Antigua and Barbuda")); 
		countries.add(new Country("AI","Anguilla")); 
		countries.add(new Country("AL","Albania")); 
		countries.add(new Country("AM","Armenia")); 
		countries.add(new Country("AN","Netherlands Antilles")); 
		countries.add(new Country("AO","Angola")); 
		countries.add(new Country("AQ","Antarctica")); 
		countries.add(new Country("AR","Argentina")); 
		countries.add(new Country("AS","American Samoa")); 
		countries.add(new Country("AT","Austria")); 
		countries.add(new Country("AU","Australia")); 
		countries.add(new Country("AW","Aruba")); 
		countries.add(new Country("AZ","Azerbaijan")); 
		countries.add(new Country("BA","Bosnia and Herzegovina")); 
		countries.add(new Country("BB","Barbados")); 
		countries.add(new Country("BD","Bangladesh")); 
		countries.add(new Country("BE","Belgium")); 
		countries.add(new Country("BF","Burkina Faso")); 
		countries.add(new Country("BG","Bulgaria")); 
		countries.add(new Country("BH","Bahrain")); 
		countries.add(new Country("BI","Burundi")); 
		countries.add(new Country("BJ","Benin")); 
		countries.add(new Country("BM","Bermuda")); 
		countries.add(new Country("BN","Brunei Darussalam")); 
		countries.add(new Country("BO","Bolivia")); 
		countries.add(new Country("BR","Brazil")); 
		countries.add(new Country("BS","Bahamas")); 
		countries.add(new Country("BT","Bhutan")); 
		countries.add(new Country("BV","Bouvet Island")); 
		countries.add(new Country("BW","Botswana")); 
		countries.add(new Country("BZ","Belize")); 
		countries.add(new Country("CA","Canada")); 
		countries.add(new Country("CC","Cocos (Keeling) Islands")); 
		countries.add(new Country("CF","Central African Republic")); 
		countries.add(new Country("CH","Switzerland")); 
		countries.add(new Country("CI","Cote D'Ivoire (Ivory Coast)")); 
		countries.add(new Country("CK","Cook Islands")); 
		countries.add(new Country("CL","Chile")); 
		countries.add(new Country("CM","Cameroon")); 
		countries.add(new Country("CN","China")); 
		countries.add(new Country("CO","Colombia")); 
		countries.add(new Country("CR","Costa Rica")); 
		countries.add(new Country("CS","Czechoslovakia (former)")); 
		countries.add(new Country("CV","Cape Verde")); 
		countries.add(new Country("CX","Christmas Island")); 
		countries.add(new Country("CY","Cyprus")); 
		countries.add(new Country("CZ","Czech Republic")); 
		countries.add(new Country("DE","Germany")); 
		countries.add(new Country("DJ","Djibouti")); 
		countries.add(new Country("DK","Denmark")); 
		countries.add(new Country("DM","Dominica")); 
		countries.add(new Country("DO","Dominican Republic")); 
		countries.add(new Country("DZ","Algeria")); 
		countries.add(new Country("EC","Ecuador")); 
		countries.add(new Country("EE","Estonia")); 
		countries.add(new Country("EG","Egypt")); 
		countries.add(new Country("EH","Western Sahara")); 
		countries.add(new Country("ER","Eritrea")); 
		countries.add(new Country("ES","Spain")); 
		countries.add(new Country("ET","Ethiopia")); 
		countries.add(new Country("FI","Finland")); 
		countries.add(new Country("FJ","Fiji")); 
		countries.add(new Country("FK","Falkland Islands (Malvinas)")); 
		countries.add(new Country("FM","Micronesia")); 
		countries.add(new Country("FO","Faroe Islands")); 
		countries.add(new Country("FR","France")); 
		countries.add(new Country("FX","France, Metropolitan")); 
		countries.add(new Country("GA","Gabon")); 
		countries.add(new Country("GB","Great Britain (UK)")); 
		countries.add(new Country("GD","Grenada")); 
		countries.add(new Country("GE","Georgia")); 
		countries.add(new Country("GF","French Guiana")); 
		countries.add(new Country("GG","Guernsey")); 
		countries.add(new Country("GH","Ghana")); 
		countries.add(new Country("GI","Gibraltar")); 
		countries.add(new Country("GL","Greenland")); 
		countries.add(new Country("GM","Gambia")); 
		countries.add(new Country("GN","Guinea")); 
		countries.add(new Country("GP","Guadeloupe")); 
		countries.add(new Country("GQ","Equatorial Guinea")); 
		countries.add(new Country("GR","Greece")); 
		countries.add(new Country("GS","S. Georgia and S.")); 
		countries.add(new Country("GT","Guatemala")); 
		countries.add(new Country("GU","Guam")); 
		countries.add(new Country("GW","Guinea-Bissau")); 
		countries.add(new Country("GY","Guyana")); 
		countries.add(new Country("HK","Hong Kong")); 
		countries.add(new Country("HM","Heard and McDonald Islands")); 
		countries.add(new Country("HN","Honduras")); 
		countries.add(new Country("HR","Croatia (Hrvatska)")); 
		countries.add(new Country("HT","Haiti")); 
		countries.add(new Country("HU","Hungary")); 
		countries.add(new Country("ID","Indonesia")); 
		countries.add(new Country("IE","Ireland")); 
		countries.add(new Country("IL","Israel")); 
		countries.add(new Country("IM","Isle of Man")); 
		countries.add(new Country("IN","India")); 
		countries.add(new Country("IO","British Indian Ocean Territory")); 
		countries.add(new Country("IS","Iceland")); 
		countries.add(new Country("IT","Italy")); 
		countries.add(new Country("JE","Jersey")); 
		countries.add(new Country("JM","Jamaica")); 
		countries.add(new Country("JO","Jordan")); 
		countries.add(new Country("JP","Japan")); 
		countries.add(new Country("KE","Kenya")); 
		countries.add(new Country("KG","Kyrgyzstan")); 
		countries.add(new Country("KH","Cambodia")); 
		countries.add(new Country("KI","Kiribati")); 
		countries.add(new Country("KM","Comoros")); 
		countries.add(new Country("KN","Saint Kitts and Nevis")); 
		countries.add(new Country("KR","Korea (South)")); 
		countries.add(new Country("KW","Kuwait")); 
		countries.add(new Country("KY","Cayman Islands")); 
		countries.add(new Country("KZ","Kazakhstan")); 
		countries.add(new Country("LA","Laos")); 
		countries.add(new Country("LC","Saint Lucia")); 
		countries.add(new Country("LI","Liechtenstein")); 
		countries.add(new Country("LK","Sri Lanka")); 
		countries.add(new Country("LS","Lesotho")); 
		countries.add(new Country("LT","Lithuania")); 
		countries.add(new Country("LU","Luxembourg")); 
		countries.add(new Country("LV","Latvia")); 
		countries.add(new Country("LY","Libya")); 
		countries.add(new Country("MA","Morocco")); 
		countries.add(new Country("MC","Monaco")); 
		countries.add(new Country("MD","Moldova")); 
		countries.add(new Country("ME","Montenegro")); 
		countries.add(new Country("MG","Madagascar")); 
		countries.add(new Country("MH","Marshall Islands")); 
		countries.add(new Country("MK","Macedonia")); 
		countries.add(new Country("ML","Mali")); 
		countries.add(new Country("MM","Myanmar")); 
		countries.add(new Country("MN","Mongolia")); 
		countries.add(new Country("MO","Macau")); 
		countries.add(new Country("MP","Northern Mariana Islands")); 
		countries.add(new Country("MQ","Martinique")); 
		countries.add(new Country("MR","Mauritania")); 
		countries.add(new Country("MS","Montserrat")); 
		countries.add(new Country("MT","Malta")); 
		countries.add(new Country("MU","Mauritius")); 
		countries.add(new Country("MV","Maldives")); 
		countries.add(new Country("MW","Malawi")); 
		countries.add(new Country("MX","Mexico")); 
		countries.add(new Country("MY","Malaysia")); 
		countries.add(new Country("MZ","Mozambique")); 
		countries.add(new Country("NA","Namibia")); 
		countries.add(new Country("NC","New Caledonia")); 
		countries.add(new Country("NE","Niger")); 
		countries.add(new Country("NF","Norfolk Island")); 
		countries.add(new Country("NG","Nigeria")); 
		countries.add(new Country("NI","Nicaragua")); 
		countries.add(new Country("NL","Netherlands")); 
		countries.add(new Country("NO","Norway")); 
		countries.add(new Country("NP","Nepal")); 
		countries.add(new Country("NR","Nauru")); 
		countries.add(new Country("NT","Neutral Zone")); 
		countries.add(new Country("NU","Niue")); 
		countries.add(new Country("NZ","New Zealand (Aotearoa)")); 
		countries.add(new Country("OM","Oman")); 
		countries.add(new Country("PA","Panama")); 
		countries.add(new Country("PE","Peru")); 
		countries.add(new Country("PF","French Polynesia")); 
		countries.add(new Country("PG","Papua New Guinea")); 
		countries.add(new Country("PH","Philippines")); 
		countries.add(new Country("PK","Pakistan")); 
		countries.add(new Country("PL","Poland")); 
		countries.add(new Country("PM","St. Pierre and Miquelon")); 
		countries.add(new Country("PN","Pitcairn")); 
		countries.add(new Country("PR","Puerto Rico")); 
		countries.add(new Country("PS","Palestinian Territory")); 
		countries.add(new Country("PT","Portugal")); 
		countries.add(new Country("PW","Palau")); 
		countries.add(new Country("PY","Paraguay")); 
		countries.add(new Country("QA","Qatar")); 
		countries.add(new Country("RE","Reunion")); 
		countries.add(new Country("RO","Romania")); 
		countries.add(new Country("RS","Serbia")); 
		countries.add(new Country("RU","Russian Federation")); 
		countries.add(new Country("RW","Rwanda")); 
		countries.add(new Country("SA","Saudi Arabia")); 
		countries.add(new Country("SB","Solomon Islands")); 
		countries.add(new Country("SC","Seychelles")); 
		countries.add(new Country("SE","Sweden")); 
		countries.add(new Country("SG","Singapore")); 
		countries.add(new Country("SH","St. Helena")); 
		countries.add(new Country("SI","Slovenia")); 
		countries.add(new Country("SJ","Svalbard and Jan Mayen")); 
		countries.add(new Country("SK","Slovak Republic")); 
		countries.add(new Country("SL","Sierra Leone")); 
		countries.add(new Country("SM","San Marino")); 
		countries.add(new Country("SN","Senegal")); 
		countries.add(new Country("SR","Suriname")); 
		countries.add(new Country("ST","Sao Tome and Principe")); 
		countries.add(new Country("SU","USSR (former)")); 
		countries.add(new Country("SV","El Salvador")); 
		countries.add(new Country("SZ","Swaziland")); 
		countries.add(new Country("TC","Turks and Caicos Islands")); 
		countries.add(new Country("TD","Chad")); 
		countries.add(new Country("TF","French Southern Territories")); 
		countries.add(new Country("TG","Togo")); 
		countries.add(new Country("TH","Thailand")); 
		countries.add(new Country("TJ","Tajikistan")); 
		countries.add(new Country("TK","Tokelau")); 
		countries.add(new Country("TM","Turkmenistan")); 
		countries.add(new Country("TN","Tunisia")); 
		countries.add(new Country("TO","Tonga")); 
		countries.add(new Country("TP","East Timor")); 
		countries.add(new Country("TR","Turkey")); 
		countries.add(new Country("TT","Trinidad and Tobago")); 
		countries.add(new Country("TV","Tuvalu")); 
		countries.add(new Country("TW","Taiwan")); 
		countries.add(new Country("TZ","Tanzania")); 
		countries.add(new Country("UA","Ukraine")); 
		countries.add(new Country("UG","Uganda")); 
		countries.add(new Country("UM","US Minor Outlying Islands")); 
		countries.add(new Country("US","United States")); 
		countries.add(new Country("UY","Uruguay")); 
		countries.add(new Country("UZ","Uzbekistan")); 
		countries.add(new Country("VA","Vatican City State (Holy")); 
		countries.add(new Country("VC","Saint Vincent and the")); 
		countries.add(new Country("VE","Venezuela")); 
		countries.add(new Country("VG","Virgin Islands (British)")); 
		countries.add(new Country("VI","Virgin Islands (U.S.)")); 
		countries.add(new Country("VN","Viet Nam")); 
		countries.add(new Country("VU","Vanuatu")); 
		countries.add(new Country("WF","Wallis and Futuna Islands")); 
		countries.add(new Country("WS","Samoa")); 
		countries.add(new Country("YE","Yemen")); 
		countries.add(new Country("YT","Mayotte")); 
		countries.add(new Country("ZA","South Africa")); 
		countries.add(new Country("ZM","Zambia")); 
		countries.add(new Country("COM","US Commercial")); 
		countries.add(new Country("EDU","US Educational")); 
		countries.add(new Country("GOV","US Government")); 
		countries.add(new Country("INT","International")); 
		countries.add(new Country("MIL","US Military")); 
		countries.add(new Country("NET","Network")); 
		countries.add(new Country("ORG","Non-Profit Organization")); 
		countries.add(new Country("ARPA","Old style Arpanet")); 
	}
	
}
