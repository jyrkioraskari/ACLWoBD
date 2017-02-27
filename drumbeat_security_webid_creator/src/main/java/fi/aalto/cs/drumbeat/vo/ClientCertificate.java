package fi.aalto.cs.drumbeat.vo;

import fi.aalto.cs.drumbeat.Country;
import fi.aalto.cs.drumbeat.WebIDController;

/*
* 
Jyrki Oraskari, Aalto University, 2016 

This research has partly been carried out at Aalto University in DRUMBEAT 
“Web-Enabled Construction Lifecycle” (2014-2017) —funded by Tekes, 
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

public class ClientCertificate extends Certificate {

	private String modulus=null;
	private String exponent=null;
	
	public ClientCertificate()
	{
		super();
		this.common_name ="Jyrki Oraskari";
	}
	public void updateFrom(WebIDController p) {
		this.common_name = p.client_common_name_field.getText();
		this.organization = p.client_organization_field.getText();
		this.city = p.client_city_field.getText();
		this.country = p.client_country_field.getSelectionModel().getSelectedItem();
	}

	public void updateTo(WebIDController p) {
		p.client_common_name_field.setText(this.common_name);
		p.client_organization_field.setText(this.organization);
		p.client_city_field.setText(this.city);
		for (Country c : p.countries) {
			if (c.getCountry_Code().equals(this.country.getCountry_Code()))
				p.client_country_field.getSelectionModel().select(c);
		}

	}
	public String getModulus() {
		return modulus;
	}
	public void setModulus(String modulus) {
		this.modulus = modulus;
	}
	public String getExponent() {
		return exponent;
	}
	public void setExponent(String exponent) {
		this.exponent = exponent;
	}

	
	
}
