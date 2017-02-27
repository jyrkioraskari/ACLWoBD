package fi.aalto.cs.drumbeat.vo;

import fi.aalto.cs.drumbeat.Country;
import fi.aalto.cs.drumbeat.WebIDController;

public class CACertificate extends Certificate{
	
	public void updateFrom(WebIDController p) {
		this.common_name = p.ca_common_name_field.getText();
		this.organization = p.ca_organization_field.getText();;
		this.city = p.ca_city_field.getText();
		this.country = p.ca_country_field.getSelectionModel().getSelectedItem();
	}
	
	public void updateTo(WebIDController p) {
		p.ca_common_name_field.setText(this.common_name);
		p.ca_organization_field.setText(this.organization);
		p.ca_city_field.setText(this.city);
		for (Country c : p.countries) {
			if (c.getCountry_Code().equals(this.country.getCountry_Code()))
				p.ca_country_field.getSelectionModel().select(c);
		}

	}

	
}
