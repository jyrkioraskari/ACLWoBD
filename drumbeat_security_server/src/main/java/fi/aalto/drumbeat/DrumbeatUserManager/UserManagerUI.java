package fi.aalto.drumbeat.DrumbeatUserManager;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.drumbeat.DrumbeatUserManager.events.EventBusCommunication;

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


// Push(PushMode.AUTOMATIC)
@Theme("drumbeat")
public class UserManagerUI extends UI {
	private EventBusCommunication communication = EventBusCommunication.getInstance();
	private static final long serialVersionUID = 1L;


	@SuppressWarnings("serial")
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		communication.register(this);
		setPollInterval(2000);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		final VerticalLayout main_layout = new VerticalLayout();
		main_layout.setStyleName("drumbeat");
		main_layout.addComponent(tabsheet);

	}

	@WebServlet(urlPatterns = { "/ui/*", "/VAADIN/*" }, name = "UserManagerUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = UserManagerUI.class, productionMode = false)
	public static class UserManagerUIServlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
	}

}
