package fi.aalto.drumbeat.user_interface;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.util.Iterator;
import java.util.Random;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.drumbeat.user_interface.events.EventBusCommunication;

/*
* 
Jyrki Oraskari, Aalto University, 2017 

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

// Table example from: https://examples.javacodegeeks.com/enterprise-java/vaadin-table-example/

// Push(PushMode.AUTOMATIC)
@Title("Drumbear Security")
@Theme("valo")

public class UserInterface extends UI implements Table.ColumnGenerator {
	private EventBusCommunication communication = EventBusCommunication.getInstance();
	private static final long serialVersionUID = 1L;


	private Random rnd = new Random();
	private String[] races = { "Human", "Dwarf", "Elf", "Orc", "Halfling", "Durglor" };
	private String[] myClasses = { "Paladin", "Warrior", "Archer", "Wizard", "Cleric", "Thief" };
	private ClickListener clickListenerRemoveButton;
	private ClickListener clickListenerAddButton;
	private ValueChangeListener valueChangeListenrTable;
	private Table characters;
	private Button buttonRemove;
	private Button buttonAdd;
	private VerticalLayout verticalLayoutMain;
	private HorizontalLayout horizontalLayoutButtons;

	@SuppressWarnings("serial")
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		communication.register(this);
		setPollInterval(2000);
		setUpEventHandling();
		setUpGui();

		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		final VerticalLayout main_layout = new VerticalLayout();
		main_layout.setStyleName("drumbeat");
		main_layout.addComponent(tabsheet);

	}

	@WebServlet(urlPatterns = { "/ui/*", "/VAADIN/*" }, name = "UserManagerUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = UserInterface.class, productionMode = false)
	public static class UserManagerUIServlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
	}

	
	
	  private void setUpGui()
	  {
	    buttonRemove = new Button("Remove", clickListenerRemoveButton);
	    buttonAdd = new Button("Add", clickListenerAddButton);
	    
	    horizontalLayoutButtons = new HorizontalLayout();
	    horizontalLayoutButtons.addComponent(buttonAdd);
	    horizontalLayoutButtons.addComponent(buttonRemove);
	    
	    characters = new Table("Rising Swang Party");
	    characters.addContainerProperty("Race", String.class, null);
	    characters.addContainerProperty("Class", String.class, null);
	    characters.addContainerProperty("Level", Integer.class, null);
	    characters.addContainerProperty("Type", Label.class, null);
	    characters.addValueChangeListener(valueChangeListenrTable);
	    
	    for(int i = 0; i < 5 ; i++)
	    {
	      generateCharacter();
	    }
	    
	    characters.setPageLength(characters.size());
	    characters.setSelectable(true);
	    characters.setImmediate(true);
	    characters.setFooterVisible(true);
	    characters.setColumnFooter("Race", null);
	    characters.setColumnFooter("Class", "Sum of party levels");
	    
	    verticalLayoutMain = new VerticalLayout();
	    setContent(verticalLayoutMain);
	    verticalLayoutMain.addComponent(horizontalLayoutButtons);
	    verticalLayoutMain.addComponent(characters);

	    characters.setColumnFooter("Level", String.valueOf(calcSum()));
	  }
	  
	  private void setUpEventHandling()
	  {
	    clickListenerRemoveButton = new Button.ClickListener()
	    {
	      
	      private static final long serialVersionUID = 1L;

	      @Override
	      public void buttonClick(ClickEvent event)
	      {
	        Object selected = characters.getValue();
	        if(selected == null)
	        {
	          Notification.show("You must select an item");
	        }
	        else
	        {
	          Notification.show("Value : " + selected);
	          characters.removeItem(selected);
	          characters.setColumnFooter("Level", String.valueOf(calcSum()));
	        }
	      }
	    };
	    
	    clickListenerAddButton = new Button.ClickListener()
	    {
	      private static final long serialVersionUID = 1L;

	      @Override
	      public void buttonClick(ClickEvent event)
	      {
	        generateCharacter();
	        Notification.show("Added a row");
	        characters.setColumnFooter("Level", String.valueOf(calcSum()));
	      }
	    };
	    
	    valueChangeListenrTable = new ValueChangeListener()
	    {
	      private static final long serialVersionUID = 1L;

	      @Override
	      public void valueChange(ValueChangeEvent event)
	      {
	        
	        Notification.show("Selected item : " + characters.getValue());
	      }
	    };
	    
	  }
	  
	  private void generateCharacter()
	  {
	    Object newItemId = characters.addItem();
	    Item row1 = characters.getItem(newItemId);
	    row1.getItemProperty("Race").setValue(getRace());
	    row1.getItemProperty("Class").setValue(getMyClass());
	    row1.getItemProperty("Level").setValue(getLevel());
	    row1.getItemProperty("Type").setValue(generateCell(characters, newItemId, "Level"));
	  }
	  
	  private int calcSum()
	  {
	    int sum=0;
	    
	    for(Iterator<?> i = characters.getItemIds().iterator(); i.hasNext();)
	    {
	      int cID = (Integer) i.next();
	      Item item = characters.getItem(cID);
	      int level = (int) item.getItemProperty("Level").getValue();
	      sum += level;
	    }
	    
	    return sum;
	  }
	  
	  
	  private String getRace()
	  {
	    int iRace = rnd.nextInt(races.length);
	    return races[iRace];
	  }
	  
	  private String getMyClass()
	  {
	    int iClass = rnd.nextInt(myClasses.length);
	    return myClasses[iClass];
	  }
	  
	  private int getLevel()
	  {
	    return rnd.nextInt(19)+1;
	  }

	  @Override
	  public Component generateCell(Table source, Object itemId, Object columnId)
	  {
	    Property prop = source.getItem(itemId).getItemProperty(columnId);
	    if(prop.getType().equals(Integer.class) && prop != null)
	    {
	      int val = (int)prop.getValue();
	      Label customLabel = new Label();
	      if(val < 10)
	      {
	        customLabel.setValue("bad");
	      }
	      if(val >= 10 && val < 15)
	      {
	        customLabel.setValue("medium");
	      }
	      if(val >= 15 && val < 18)
	      {
	        customLabel.setValue("good");
	      }
	      if(val >= 18)
	      {
	        customLabel.setValue("best");
	      }
	      return customLabel;
	    }
	    return null;
	  }

}
