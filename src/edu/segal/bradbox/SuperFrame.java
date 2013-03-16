package edu.segal.bradbox;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;


public class SuperFrame extends JFrame{
	public JavaMonkey monkey;
	public Serialio serialio;
	public KeypadPanel keypadPanel;
	//public TextingPanel textingPanel;
	public ContactPanel addContactPanel;
	public ContactPanel editContactPanel;
	public CallLogPanel callLogPanel;
	public VolumePanel volumePanel;
	JPanel displayedPanel = new JPanel();

	/**
	 *  Required field for extending the JFrame, set to default value
	 */
	private static final long serialVersionUID = 1L;

	// Constructor function
	public SuperFrame(JavaMonkey m, Serialio s) {
		initIO(m, s);
		initPanels();
	    initWindow();

		
		setSize(950, 550);
		BorderLayout layout = new BorderLayout();
		layout.setVgap(0);
		setLayout(layout);
		add(displayedPanel, BorderLayout.CENTER);
		keypadPanel.setBackground(Constants.BACKGROUND_GRAY);
		displayedPanel.add(keypadPanel);
		//displayedPanel.add(textingPanel);
		displayedPanel.add(addContactPanel);
		displayedPanel.add(volumePanel);
		displayedPanel.add(callLogPanel);
		displayedPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		((FlowLayout)displayedPanel.getLayout()).setVgap(0);
		((FlowLayout)displayedPanel.getLayout()).setHgap(0);

		hidePanels();
		showKeypad();
		pack();
	}
	
	public final void openEditContact(String nm, String no, boolean fav, int r) {
		editContactPanel = new ContactPanel(this, nm, no, fav, r);
		displayedPanel.add(editContactPanel);
		hidePanels();
		setTitle("Edit Contact " + nm);
		editContactPanel.setVisible(true);
	}
	
	private final void initIO(JavaMonkey m, Serialio s) {
		// Initialize the JavaMonkey and Serialio
	    if ( m == null ) {
            throw new IllegalStateException("JavaMonkey is not initialized in SuperFrame.");
	    }
	    this.monkey = m;
	    if ( s == null ) {
            throw new IllegalStateException("Serialio is not initialized in SuperFrame.");
	    }
	    this.serialio = s;
	}
	
	private final void initPanels() {
		keypadPanel = new KeypadPanel(this);
		//textingPanel = new TextingPanel(monkey);
		addContactPanel = new ContactPanel(this);
		editContactPanel = new ContactPanel(this, "", "", false, 0);
		volumePanel = new VolumePanel(this);
		callLogPanel = new CallLogPanel(this);
	}
	
	private final void initWindow() { 
		 // Initialize the UI look and feel
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			//UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setTitle("Brad Box");
		setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	

	public final JavaMonkey getMonkey() {
		return monkey;
	}
	
	public final KeypadPanel getKeypad() {
		return keypadPanel;
	}
	
	public final Serialio getSerialio() {
		return serialio;
	}
	
	private final void hidePanels() {
		keypadPanel.setVisible(false);
		//textingPanel.setVisible(false);
		addContactPanel.setVisible(false);
		editContactPanel.setVisible(false);
		volumePanel.setVisible(false);
		callLogPanel.setVisible(false);
	}
	
	public final void showAddContact() {
		hidePanels();
		setTitle("Add Contact");
		addContactPanel.setVisible(true);
	}
	
	public final void showVolume() {
		hidePanels();
		setTitle("Volume Options");
		volumePanel.setVisible(true);
	}
	
	public final void showKeypad() {
		hidePanels();
		setTitle("Keypad");
		keypadPanel.clearField();
		keypadPanel.refreshContactList();
		keypadPanel.setVisible(true);
	}
	
	public final void showCallLog() {
		hidePanels();
		setTitle("Call Log");
		callLogPanel.updateCallLog();
		callLogPanel.setVisible(true);
		
	}
	
	
	public final void updateFavorite(String newName, String rank) {
		String queryString = "update names set name = '" + newName +"' where rank = " + rank;
		updateDatabase("favorites.db", queryString);
	}
	
	
	public void updateDatabase(String db, String queryString) {
		System.out.println("update string: " + queryString);
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;
			try {
			  // create a database connection
			  connection = DriverManager.getConnection("jdbc:sqlite:/platform-tools/" + db);
			  Statement statement = connection.createStatement();
			  statement.setQueryTimeout(30);  // set timeout to 30 sec.
			  statement.executeUpdate(queryString);
			  showKeypad();

			}
		catch(SQLException e) {
		  // if the error message is "out of memory", 
		  // it probably means no database file is found
		  System.err.println(e.getMessage());
		}
		finally {
		  try {
		    if(connection != null)
		      connection.close();
		  }
		  catch(SQLException e) {
		    // connection close failed.
		    System.err.println(e);
		  }
		}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	public void runExecutable(String path) {
		System.out.println("attempting to run " + path);
		try {
			Process proc = Runtime.getRuntime().exec(path);
			try {
				proc.waitFor();
				proc.destroy();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			System.out.println("Error: IOException when calling " + path);
			e.printStackTrace();
		}
	}
	
	public String prettifyNumber(String number) {
		int len = number.length();
		if(len == 0) return "";
		String out = "(";
		out = out + number.substring(0, Math.min(3, len));
		if(len >=3) out = out + ") ";
		if (len <= 3) return out;
		out = out + number.substring(3, Math.min(6, len));
		if (len <= 6) return out;
		out = out + "-" + number.substring(6, len);
		return out;
	}
	
	public String uglifyNumber(String number) {
		System.out.println("uglified: " + number.replaceAll( "[^\\d]", "" ));
		return number.replaceAll( "[^\\d]", "" );
	}
}
