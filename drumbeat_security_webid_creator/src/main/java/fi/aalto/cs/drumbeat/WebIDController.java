package fi.aalto.cs.drumbeat;

import java.net.URL;
import java.security.Security;
import java.util.ResourceBundle;

import fi.aalto.cs.drumbeat.vo.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/*
 * The GNU Affero General Public License
 * 
 * Copyright (c) 2016 Jyrki Oraskari (Jyrki.Oraskari@aalto.fi / jyrki.oraskari@aalto.fi)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

public class WebIDController implements Initializable {
	Data data_store = Data.Singleton.INSTANCE.getSingleton();
	
	@FXML
	MenuBar myMenuBar;

	// ================= CA
	@FXML
	TextField ca_keystore_file;
	
	@FXML
	PasswordField ca_keystore_password;
	
	@FXML
	public TextField ca_common_name_field;
	@FXML
	public TextField ca_organization_field;
	@FXML
	public TextField ca_city_field;
	
	@FXML
	public ComboBox<Country> ca_country_field;
	
	
	// ================= CLIENT
	
	@FXML
	Tab client_tab;	
	@FXML
	public TextField client_keystore_file;

	@FXML
	PasswordField client_keystore_password;

	@FXML
	public TextField client_common_name_field;
	@FXML
	public TextField client_organization_field;
	@FXML
	public TextField client_city_field;
	
	@FXML
	TextField client_subject_alt_name_uri;	
	@FXML
	public ComboBox<Country> client_country_field;
	
	// ================= WEB	
	@FXML
	Tab web_tab;
	
	@FXML
	TextArea textarea_foaf;
	
	public ObservableList<Country> countries=FXCollections.observableArrayList();
	FileChooser fc;
	

	@FXML
	private void createCACertificate()
	{
		data_store.setCERT_AUTH_KEYSTORE_PASSWORD(ca_keystore_password.getText());
		data_store.getCa_certificate().updateFrom(this);
		new  CACertificateCreator();
		client_tab.setDisable(false);
	}
	

	@FXML
	private void createClientCertificate()
	{
		data_store.setCLIENT_KEYSTORE_PASSWORD(client_keystore_password.getText());
		data_store.getClient_certificate().updateFrom(this);
		data_store.setCLIENT_SUBJECT_ALT_NAME_URI(client_subject_alt_name_uri.getText());
		new  ClientCertificateCreator();
		web_tab.setDisable(false);
		FOAFCreator foaf_creator=new FOAFCreator();
        textarea_foaf.setText(foaf_creator.create());
	}
	
	@FXML
	private void aboutAction() {
		Stage stage = (Stage) myMenuBar.getScene().getWindow();
		new About(stage).show();
	}

	@FXML
	private void closeApplicationAction() {
		Stage stage = (Stage) myMenuBar.getScene().getWindow();
		stage.close();
	}

	
	private void subjectAlternativeName_Changed() {
	    String uri=client_subject_alt_name_uri.getText();
	    client_subject_alt_name_uri.setDisable(true);
	    
		
		if(!uri.endsWith("#me"))
		{
			int pos=uri.indexOf("#");			
			if(pos!=-1)
			{
				   uri=uri.substring(0, pos);
			}
			
			client_subject_alt_name_uri.setText(uri+"#me");
		}
	    
	    client_subject_alt_name_uri.setDisable(false);
	}

	private boolean inField=false; 
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		ca_keystore_file.setText(data_store.getCERT_AUTH_KEYSTORE_FILE());
		client_keystore_file.setText(data_store.getCLIENT_KEYSTORE_FILE());
		
		ca_keystore_password.setText(data_store.getCERT_AUTH_KEYSTORE_PASSWORD());
		client_keystore_password.setText(data_store.getCLIENT_KEYSTORE_PASSWORD());
		client_subject_alt_name_uri.setText(data_store.getCLIENT_SUBJECT_ALT_NAME_URI());
		Country.insert_countries(countries);
		ca_country_field.getItems().addAll(countries);
		client_country_field.getItems().addAll(countries);
		data_store.getCa_certificate().updateTo(this);
		data_store.getClient_certificate().updateTo(this);
		
		client_subject_alt_name_uri.focusedProperty().addListener((a,b,c) -> {
			inField=!inField;
			if(!inField)
				subjectAlternativeName_Changed();
        });
		FOAFCreator foaf_creator=new FOAFCreator();
        textarea_foaf.setText(foaf_creator.create());		
	}
}
