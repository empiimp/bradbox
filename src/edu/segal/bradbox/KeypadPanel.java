package edu.segal.bradbox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class KeypadPanel extends JPanel{
	SuperFrame superframe;
	JavaMonkey monkey;
	final static private String[] keypadLabels = {"<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 1</font></b>\n<br>\n&nbsp;\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 2</font></b>\n<br>\nABC\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 3</font></b>\n<br>\nDEF\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 4</font></b>\n<br>\nGHI\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 5</font></b>\n<br>\nJKL\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 6</font></b>\n<br>\nMNO\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 7</font></b>\n<br>\nPQRS\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 8</font></b>\n<br>\nTUV\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 9</font></b>\n<br>\nWXYZ\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> *</font></b>\n<br>\n&nbsp;\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 0</font></b>\n<br>\n&nbsp;\n</p></html>", "<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> #</font></b>\n<br>\n&nbsp;\n</p></html>"};
	final static private String[] numberStrings = {"", "", "[AaBbCc]", "[DdEeFf]", "[GgHhIi]", "[JjKkLl]", "[MmNnOo]", "[PpQqRrSs]", "[TtUuVv]", "[WwXxYyZz]"}; 
	final static private Map<String, String> keyCodeMap = new HashMap<String, String>();
	final static private Map<String, String> keyStringMap = new HashMap<String, String>();
	final private JTextField numberField = new JTextField(9);
	JButton callButton = new JButton(Constants.CALL_STRING);
	private JPanel contactsPanel = new JPanel();
	private ContactModule[] contacts = new ContactModule[6];
	/**
	 *  Required for a JPanel
	 */
	private static final long serialVersionUID = 1L;
	
	public KeypadPanel(SuperFrame sf) {
		superframe = sf;
		initJavaMonkey(sf.monkey);
	    initKeyCodeMap();
	    initLookandFeel();
	    initKeypad();
	}

	private final void initJavaMonkey(JavaMonkey m) {
		// Initialize the JavaMonkey
	    if ( m == null ) {
            throw new IllegalStateException("JavaMonkey is not initialized in KeypadPanel.");
	    }
	    monkey = m;
	}
	
	private final void initLookandFeel() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	private final void initKeyCodeMap() {
		keyCodeMap.put("0", "KEYCODE_0");	
		keyCodeMap.put("1", "KEYCODE_1");
		keyCodeMap.put("2", "KEYCODE_2");
		keyCodeMap.put("3", "KEYCODE_3");
		keyCodeMap.put("4", "KEYCODE_4");
		keyCodeMap.put("5", "KEYCODE_5");
		keyCodeMap.put("6", "KEYCODE_6");
		keyCodeMap.put("7", "KEYCODE_7");
		keyCodeMap.put("8", "KEYCODE_8");
		keyCodeMap.put("9", "KEYCODE_9");
		keyCodeMap.put("#", "18");
		keyCodeMap.put("*", "17");
		keyCodeMap.put("Delete", "KEYCODE_DEL");
		keyCodeMap.put(Constants.CALL_STRING, "KEYCODE_CALL");
		keyCodeMap.put(Constants.END_CALL_STRING, "KEYCODE_ENDCALL");
		
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 0</font></b>\n<br>\n&nbsp;\n</p></html>", "0");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 1</font></b>\n<br>\n&nbsp;\n</p></html>", "1");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 2</font></b>\n<br>\nABC\n</p></html>", "2");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 3</font></b>\n<br>\nDEF\n</p></html>", "3");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 4</font></b>\n<br>\nGHI\n</p></html>", "4");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 5</font></b>\n<br>\nJKL\n</p></html>", "5");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 6</font></b>\n<br>\nMNO\n</p></html>", "6");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 7</font></b>\n<br>\nPQRS\n</p></html>", "7");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 8</font></b>\n<br>\nTUV\n</p></html>", "8");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> 9</font></b>\n<br>\nWXYZ\n</p></html>", "9");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> *</font></b>\n<br>\n&nbsp;\n</p></html>", "*");
		keyStringMap.put("<html>\n<p style=\"text-align: center;\">\n<b><font size=\"20\"> #</font></b>\n<br>\n&nbsp;\n</p></html>", "#");
	}
	
	public void initKeypad(){	
		// Set the orientation of the keypad Panel
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(900, 650));
		GridBagConstraints leftConstraints = new GridBagConstraints();
		GridBagConstraints rightConstraints = new GridBagConstraints();
		leftConstraints.fill = GridBagConstraints.HORIZONTAL;
		leftConstraints.gridx = 0;
		leftConstraints.gridy = 0;
		leftConstraints.weightx = 1;
		leftConstraints.anchor = GridBagConstraints.PAGE_START;
		rightConstraints.fill = GridBagConstraints.HORIZONTAL;
		rightConstraints.gridx = 2;
		rightConstraints.gridy = 0;
		rightConstraints.weightx = .5;
		rightConstraints.anchor = GridBagConstraints.PAGE_START;
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		JPanel numberFieldPanel = new JPanel();
		
		
		numberField.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent arg0) {
            }
            
            public void insertUpdate(DocumentEvent arg0) {
                searchContacts();
            }

            public void removeUpdate(DocumentEvent arg0) {
                searchContacts();
            }
        });
		
		// Customize the text field font
		Font numberFont = new Font("SansSerif", Font.BOLD, 40);
		numberField.setFont(numberFont);
		
		// Create backspace button
		JButton delButton = new JButton("Backspace");
		delButton.setPreferredSize(new Dimension(150,60));
				
		// Create a listener for the backspace button
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				monkey.press("KEYCODE_DEL");	
				String number = numberField.getText();
				if(number.length() > 0) {
					number = number.substring(0, number.length() - 1);
					numberField.setText(number);
				}
			}
		});
		
		// Add the keypad
		JPanel keypad = new JPanel();
		keypad.setPreferredSize(new Dimension(500, 430));
		
		keypad.setLayout(new GridLayout(4,3));

		// Create all buttons with listeners attached
		for(int i = 0; i < 12; i++){
			JButton numberKey = new JButton(keypadLabels[i]);
			//numberKey.setFont(numberFont);
			numberKey.setSize(WIDTH, HEIGHT);
			numberKey.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					JButton clickedButton = (JButton) event.getSource();
					String keyNumber = keyStringMap.get(clickedButton.getText());
					numberField.setText(numberField.getText() + keyNumber);
					if(callButton.getText().equals(Constants.END_CALL_STRING)) {
						monkey.press(keyCodeMap.get(keyNumber));
					}
				}
			});
			keypad.add(numberKey);
		}
		
		// Customize the calling panel font
		Font callingFont = new Font("SansSerif", Font.BOLD, 20);
		
		// Create a call button
		callButton.setFont(numberFont);
		callButton.setBackground(Constants.GREEN);
		callButton.setPreferredSize(new Dimension(250, 80));
		
		delButton.setFont(callingFont);
		delButton.setBackground(Constants.RED);

				
		// Create a listener for the callButton
		callButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JButton callButton = (JButton) event.getSource();
				if(callButton.getText().equals(Constants.CALL_STRING)) {
					initiateCall(numberField.getText());
				} else {
					endCall();
				}
			}
		});
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(0, 1));
		
		// Create options button
		JButton optionsButton = new JButton("Options");
		optionsButton.setPreferredSize(new Dimension(Constants.CONTACTS_WIDTH, 60));
		optionsButton.setFont(callingFont);
		optionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				superframe.showOptions();
			}
		});
		
		JButton callLogButton = new JButton("Call Log");
		callLogButton.setPreferredSize(new Dimension(Constants.CONTACTS_WIDTH, 60));
		callLogButton.setFont(callingFont);
		
		callLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				monkey.unlock();
			}
		});
		
		for(int i = 0; i < contacts.length; i++) contacts[i] = new ContactModule(this, "", "");
		initFavorites();
		contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
		for(int i = 0; i < contacts.length; i++) {
			contactsPanel.add(contacts[i]);
		}
		
		// Arrange all the panels
		this.add(leftPanel, leftConstraints);
			leftPanel.add(numberFieldPanel, BorderLayout.PAGE_START);
				numberFieldPanel.add(numberField);
				numberFieldPanel.add(delButton);
			leftPanel.add(keypad, BorderLayout.CENTER);
			leftPanel.add(callButton, BorderLayout.PAGE_END);
		this.add(rightPanel, rightConstraints);
			rightPanel.add(optionsPanel, BorderLayout.PAGE_START);
				optionsPanel.add(optionsButton);
				optionsPanel.add(callLogButton);
			rightPanel.add(contactsPanel, BorderLayout.CENTER);
	}
	
	public void openEditContact(String nm, String no) {
		superframe.openEditContact(nm, no);
	}
	
	public void acceptButtonPush() {
		if(callButton.getText().equals(Constants.CALL_STRING)) initiateCall(numberField.getText());
		else endCall();
	}
	
	public void initiateCall(String number) {
		monkey.shell("am start -a android.intent.action.DIAL");
		if(callButton.getText().equals(Constants.CALL_STRING)) {
			monkey.unlock();
			callButton.setText(Constants.END_CALL_STRING);
			callButton.setBackground(Constants.RED);
			for(int i = 0; i < numberField.getText().length() + 10; i++) {
				monkey.press("KEYCODE_DEL");
			}
			numberField.setText("");
			for(int i = 0; i < number.length(); i++) {
				monkey.press(keyCodeMap.get(number.substring(i, i + 1)));
			}
			monkey.press("KEYCODE_CALL");	
		}
	}
	
	private void endCall() {
		callButton.setText(Constants.CALL_STRING);
		callButton.setBackground(Constants.GREEN);
		numberField.setText("");
		monkey.press("KEYCODE_ENDCALL");
	}
	
	public void initFavorites() {
		for(int i = 0; i < 6; i++) {
			contacts[i].setName("Favorite " + Integer.toString(i + 1));
			contacts[i].setNumber("");
			contacts[i].setVisible(true);
		}
	}
	
	public void searchContacts() {
		String searchTerm = numberField.getText();
		if (searchTerm.equals("")) {
			initFavorites();
			return;
		}
		
		String globAlphaTerm = "";
		for (int i = 0; i < searchTerm.length(); i++) {
			globAlphaTerm += numberStrings[((int) searchTerm.charAt(i)) - 48];
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;
		    try {
		      // create a database connection
		      connection = DriverManager.getConnection("jdbc:sqlite:/platform-tools/contacts2.db");
		      Statement statement = connection.createStatement();
		      statement.setQueryTimeout(30);  // set timeout t0o 30 sec.
		      
		      ResultSet rs = statement.executeQuery("select * from contact_entities_view where (display_name glob '*" + globAlphaTerm + "*' or data1 glob '*" + searchTerm + "*') and mimetype = 'vnd.android.cursor.item/phone_v2'");
		      // put a for loop here that iterates for each search result panel available
		      for(int i = 0; i < contacts.length; i++) {
			      if (rs.next()) {  
			          // read the result set
			    	  contacts[i].setVisible(true);
			    	  contacts[i].setName(rs.getString("display_name"));
			    	  contacts[i].setNumber(rs.getString("data1"));
			    	  
			          System.out.println("name = " + rs.getString("display_name"));
			          System.out.println("number = " + rs.getString("data1"));
			      }
			      else {
			    	  contacts[i].setVisible(false);
			      }
		      }
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

}
