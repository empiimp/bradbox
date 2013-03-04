package edu.segal.bradbox;

import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JLabel;
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
	final static private String[] keypadLabels = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"};
	final static private String[] numberStrings = {"", "", "[AaBbCc]", "[DdEeFf]", "[GgHhIi]", "[JjKkLl]", "[MmNnOo]", "[PpQqRrSs]", "[TtUuVv]", "[WwXxYyZz]"}; 
	final static private Map<String, String> keyCodeMap = new HashMap<String, String>();
	final private JTextField numberField = new JTextField(12);
	final private JLabel callingLabel = new JLabel();
	JButton callButton = new JButton("Call");
	private JPanel contactsPanel = new JPanel();
	private ContactModule[] contacts = new ContactModule[3];
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
		keyCodeMap.put("#", "KEYCODE_POUND");
		keyCodeMap.put("*", "KEYCODE_STAR");
		keyCodeMap.put("Delete", "KEYCODE_DEL");
		keyCodeMap.put("Call", "KEYCODE_CALL");
		keyCodeMap.put("End Call", "KEYCODE_ENDCALL");
	}
	
	public void initKeypad(){	
		// Set the orientation of the keypad Panel
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(0, 0, 0, 0));
		
		// Add a text field to the panel
		JPanel numberFieldPanel = new JPanel();
		numberFieldPanel.add(numberField);
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
				JButton delButton = new JButton("Delete");
				delButton.setPreferredSize(new Dimension(300,100));
				numberFieldPanel.add(delButton);
				
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
		
		this.add(numberFieldPanel);

		// Add the keypad
		JPanel keypad = new JPanel();
		this.add(keypad);
		keypad.setLayout(new GridLayout(4,3));
		keypad.setPreferredSize(new Dimension(1000,200));

		// Create all buttons with listeners attached
		for(int i = 0; i < 12; i++){
			JButton numberKey = new JButton(keypadLabels[i]);
			numberKey.setFont(numberFont);
			numberKey.setSize(WIDTH, HEIGHT);
			numberKey.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					JButton clickedButton = (JButton) event.getSource();
					numberField.setText(numberField.getText() + clickedButton.getText());
					monkey.press(keyCodeMap.get(clickedButton.getText()));
				}
			});
			keypad.add(numberKey);
		}
		
		// Create a panel for the calling button
		JPanel callingPanel = new JPanel();
		
		// Customize the calling panel font
		Font callingFont = new Font("SansSerif", Font.BOLD, 20);
		
		// Create a call button
		callButton.setFont(numberFont);
		callButton.setBackground(Constants.GREEN);
		
		delButton.setFont(callingFont);
		delButton.setBackground(Constants.RED);
		//delButton.setSize(200,100);
		
		callingPanel.add(callButton);
		this.add(callingPanel);
		callingPanel.setLayout(new GridLayout(1,0));
		
		// Create a calling Label to display when a call is initiated
		callingLabel.setFont(callingFont);
		callingPanel.add(callingLabel);
				
				
		// Create a listener for the callButton
		callButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JButton callButton = (JButton) event.getSource();
				if(callButton.getText().equals("Call")) {
					initiateCall();
				} else {
					endCall();
				}
			}
		});
		
		for(int i = 0; i < contacts.length; i++) contacts[i] = new ContactModule(this, "", "");
		initFavorites();
		contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
		for(int i = 0; i < contacts.length; i++) {
			contactsPanel.add(contacts[i]);
		}
		this.add(contactsPanel);
	}
	
	public void openEditContact(String nm, String no) {
		superframe.openEditContact(nm, no);
	}
	
	public void acceptButtonPush() {
		if(callButton.getText().equals("Call")) initiateCall();
		else endCall();
	}
	
	private void initiateCall() {
		callButton.setText("End Call");
		callButton.setBackground(Constants.RED);
		String number = numberField.getText();
		numberField.setText("");
		callingLabel.setText("    Calling " + number + " . . .");
		monkey.press("KEYCODE_CALL");	
	}
	
	public void initiateCall(String number) {
		if(callButton.getText().equals("Call")) {
			callButton.setText("End Call");
			callButton.setBackground(Constants.RED);
			String oldText = numberField.getText();
			numberField.setText("");
			callingLabel.setText("    Calling " + number + " . . .");
			System.out.println("old text: " + oldText.length());
			for(int i = 0; i < oldText.length() + 10; i++) {
				monkey.press("KEYCODE_DEL");
			}
			for(int i = 0; i < number.length(); i++) {
				monkey.press(keyCodeMap.get(number.substring(i, i + 1)));
			}
			monkey.press("KEYCODE_CALL");	
		}
	}
	
	private void endCall() {
		callButton.setText("Call");
		callButton.setBackground(Constants.GREEN);
		numberField.setText("");
		callingLabel.setText("");
		monkey.press("KEYCODE_ENDCALL");
	}
	
	public void initFavorites() {
		contacts[0].setName("Mom");
		contacts[0].setNumber("123");
		contacts[1].setName("Dad");
		contacts[1].setNumber("456");
		contacts[2].setName("Deb");
		contacts[2].setNumber("789");
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
		      statement.setQueryTimeout(30);  // set timeout to 30 sec.
		      
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
