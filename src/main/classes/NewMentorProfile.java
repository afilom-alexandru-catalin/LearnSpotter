package main.ui.newContent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import main.app.App;
import main.classes.Card;
import main.classes.Course;
import main.classes.Feedback;
import main.classes.Mentor;
import main.classes.MentoringProgram;
import main.classes.User;
import main.db.DBManager;
import main.db.DbConnection;
import main.ui.content.MainPanel;
import main.ui.content.PaymentPanel;
import main.ui.customComponents.RoundButton;
import main.ui.customComponents.RoundImagePanel;
import main.ui.customComponents.RoundPanel;
import main.ui.customUI.CheckChangeDocListener;
import main.ui.customUI.HintTextAreaUI;
import main.ui.customUI.HintTextFieldUI;
import main.ui.login.LoginData;
import main.utility.ImageLoader;
import main.utility.StaticUtilities;

import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Container;

public class NewMentorProfile extends JPanel {

	private JPanel profilePicPanel;
	private JButton btnUploadImage;
	private Image profilePic;
	private JTextField tFFirstName;
	private JTextField tFLastName;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JCheckBox checkBShowPassword;
	private PaymentPanel cardDetailsPanel;
	private JButton btnAddCard;
	private Card card;
	private JButton btnRegister;
	private JTextArea tAStudyFields;
	private JTextField tFLocation;
	private JTextField tFPhoneNumber;
	private JTextField tFEmail;
	
	//for edit panel
	private JButton bEditCard;
	private JButton bEditName;
	private JButton bEditField;
	private JButton bEditLocation;
	private JButton bEditPhoneNo;
	private JButton bEditPassword;
	
	private JPanel panel_1;//card panel support
	private JLabel lblNewLabel;//cards details label
	
	public NewMentorProfile(Boolean TODO) {
		this();
		profilePicPanel.add(new RoundImagePanel(ImageLoader.getInstance().getUserIcon(),new Dimension(220,200)));
	}
	
	/**
	 * for edit accound data
	 * @param mentor
	 */
	public NewMentorProfile(Mentor mentor) {
		this(true,true);
		//this();
		profilePicPanel.add(new RoundImagePanel(mentor.getProfilePic(),new Dimension(220,200)));
		
		
		//fill data
		profilePic = mentor.getProfilePic();
		tFFirstName.setText(mentor.getFirstName());
		tFLastName.setText(mentor.getLastName());
		tFPhoneNumber.setText(mentor.getPhoneNumber());
		profilePicPanel.add(new RoundImagePanel(mentor.getProfilePic(),new Dimension(220,200)));
		tFLocation.setText(mentor.getLocation());
		tAStudyFields.setText(mentor.getField());
		
		lblNewLabel.setVisible(false);
		tFFirstName.setBorder(new EmptyBorder(0,0,0,0));
		tFFirstName.setEditable(false);
		tFLastName.setBorder(new EmptyBorder(0,0,0,0));
		tFLastName.setEditable(false);
		tFPhoneNumber.setBorder(new EmptyBorder(0,0,0,0));
		tFPhoneNumber.setEditable(false);
		passwordField.setVisible(false);
		passwordField_1.setVisible(false);
		checkBShowPassword.setVisible(false);
		tFLocation.setBorder(new EmptyBorder(0,0,0,0));
		tFLocation.setEditable(false);
		tAStudyFields.setBorder(new EmptyBorder(0,0,0,0));
		tAStudyFields.setEditable(false);
		
		bEditName.addActionListener(editNameAction(mentor));
		bEditPhoneNo.addActionListener(editPhoneNoAction(mentor));
		bEditPassword.addActionListener(editPasswordAction());
		bEditField.addActionListener(editFieldAction(mentor));
		bEditLocation.addActionListener(editLocationAction(mentor));
		bEditCard.addActionListener(editCardAction(mentor));
		
		btnRegister.setText("Save");
		btnRegister.removeActionListener(btnRegister.getActionListeners()[0]);
		btnRegister.addActionListener(saveActionListener(mentor));
		
		/*tFFirstName.getDocument().addDocumentListener(new CheckChangeDocListener(btnRegister));
		tFLastName.getDocument().addDocumentListener(new CheckChangeDocListener(btnRegister));

		passwordField.getDocument().addDocumentListener(new CheckChangeDocListener(btnRegister));
		passwordField_1.getDocument().addDocumentListener(new CheckChangeDocListener(btnRegister));
		tFPhoneNumber.getDocument().addDocumentListener(new CheckChangeDocListener(btnRegister));*/
		
		 
	}
	
	private ActionListener saveActionListener(Mentor mentor) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String newFirstName = tFFirstName.getText();
				String newLastName = tFLastName.getText();
				String newPhoneNo = tFPhoneNumber.getText();
				String newLocation = tFLocation.getText();
				String newStudyField = tAStudyFields.getText();
				//profilepic
				Image newProfilePic = profilePic; //if the user change it, it will be stored here; update object and database anyway
				
				if(newFirstName.isEmpty()) {
					//if true check the content and refresh object
					JOptionPane.showMessageDialog(null, "Your first name is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(newLastName.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Your last name is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				/*
				if(field.isEmpty()) {
					JOptionPane.showMessageDialog(null, "At least one field is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				*/
				if(newLocation.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Location is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!isValidPhoneNumber(newPhoneNo)) { 
					JOptionPane.showMessageDialog(null, "Invalid phone number. Make sure the number is 10 digits long including the leading '0' and contains no spaces or dashes.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				/*
				if(email.isEmpty()) {
					JOptionPane.showMessageDialog(null, "An email is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!isValidEmail(email) ) { //Doesn't work
					JOptionPane.showMessageDialog(null, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}*/
				/*
				if(password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Setting a password is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!isStrongPassword(password)) {
					JOptionPane.showMessageDialog(null, "The password is too weak. It must contain at least one uppercase letter, one lowercase letter, one digit and one special character.", "Error", JOptionPane.ERROR_MESSAGE);
			        return;
				}
				
				System.out.println(password + " " + passwordVerify);
				if(password.compareTo(passwordVerify) != 0) {
					JOptionPane.showMessageDialog(null, "The passwords do not match. Watch out for Caps Lock, NumLock or the Shift key.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				*/
				Mentor updatedMentor = new Mentor(
					mentor.getId(),
					newFirstName,
					newLastName,
					mentor.getEmail(),
					mentor.getPassword(),
					mentor.getPhoneNumber(),
					mentor.getProfilePic(),
					newLocation,
					mentor.getDescription(),
					newStudyField,
					mentor.getProgramsNumber(),
					mentor.getRegisterDate(),
					mentor.getFeedbacks(),
					mentor.getCourses(),
					mentor.getMentoringPrograms(),
					mentor.getCard()
					);
				try {
					//upload changes
					DBManager.updateMentor(updatedMentor);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}			
		};
	}
	/**
	 * Method that search for a panel index inside the container.
	 * @param target the panel in interest
	 * @return the index of the panel, -1 if not found
	 */
	public int findComponentIndex(Container container,Object target) {
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i].equals(target)) {
				return i;
			}
		}
		return -1; // Component not found
	}
	private ActionListener editCardAction(Mentor user) {
		return new ActionListener() {

			private boolean flag = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!flag) {
					int index = findComponentIndex(panel_1, btnRegister.getParent());
					panel_1.add(cardDetailsPanel,index);
					lblNewLabel.setVisible(true);
					NewMentorProfile.this.revalidate();
					flag = true;
				}else {
					panel_1.remove(cardDetailsPanel);
					lblNewLabel.setVisible(false);
					panel_1.revalidate();
					panel_1.invalidate();
					panel_1.validate();
					flag = false;
				}
				
			}

		};
	}
	private ActionListener editLocationAction(Mentor user) {
		return new ActionListener() {

			private boolean flag = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!flag) {
					tFLocation.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
					tFLocation.setEditable(true);
					flag = true;
				}else {
					if(!user.getLocation().equals(tFLocation.getText()))
						tFLocation.setText(user.getLocation());
					tFLocation.setBorder(new EmptyBorder(0,0,0,0));
					tFLocation.setEditable(false);
					flag = false;
				}
				
			}

		};
	}
	private ActionListener editFieldAction(Mentor user) {
		return new ActionListener() {

			private boolean flag = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!flag) {
					tAStudyFields.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
					tAStudyFields.setEditable(true);
					flag = true;
				}else {
					if(!user.getField().equals(tAStudyFields.getText()))
						tAStudyFields.setText(user.getFirstName());
					tAStudyFields.setBorder(new EmptyBorder(0,0,0,0));
					tAStudyFields.setEditable(false);
					flag = false;
				}
				
			}

		};
	}
	private ActionListener editNameAction(User user) {
		return new ActionListener() {

			private boolean flag = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!flag) {
					tFFirstName.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
				tFLastName.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
				tFFirstName.setEditable(true);
				tFLastName.setEditable(true);
					flag = true;
				}else {
					if(!user.getFirstName().equals(tFFirstName.getText()))
						tFFirstName.setText(user.getFirstName());
					if(!user.getLastName().equals(tFLastName.getText()))
						tFLastName.setText(user.getLastName());
					tFFirstName.setBorder(new EmptyBorder(0,0,0,0));
					tFLastName.setBorder(new EmptyBorder(0,0,0,0));
					tFFirstName.setEditable(false);
					tFLastName.setEditable(false);
					flag = false;
				}
				
			}

		};
	}
	private ActionListener editPhoneNoAction(User user) {
		return new ActionListener() {

			private boolean flag = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!flag) {
					tFPhoneNumber.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
					tFPhoneNumber.setEditable(true);
					flag = true;
				}else {
					if(!user.getPhoneNumber().equals(tFPhoneNumber.getText()))
						tFPhoneNumber.setText(user.getPhoneNumber());
					
					tFPhoneNumber.setBorder(new EmptyBorder(0,0,0,0));
					tFPhoneNumber.setEditable(false);
					flag = false;
				}
				
			}

		};
	}
	private ActionListener editPasswordAction() {
		return new ActionListener() {

			private boolean flag = false;
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!flag) {
					passwordField.setUI(new main.ui.customUI.HintPasswordFieldUI("Old password",false,Color.GRAY));
					passwordField_1.setUI(new main.ui.customUI.HintPasswordFieldUI("New password",false,Color.GRAY));
					passwordField.setVisible(true);
					passwordField_1.setVisible(true);
					checkBShowPassword.setVisible(true);
					flag = true;
				}else {
					passwordField.setVisible(false);
					passwordField_1.setVisible(false);
					checkBShowPassword.setVisible(false);
					flag = false;
				}

			}

		};
	}
	/**
	 * Create the panel.
	 */
	public NewMentorProfile(Boolean forMentorSettings,Boolean fromWinBuilder) {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new RoundPanel();
		panel.setMinimumSize(new Dimension(600, 200));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(panel);
		
		 panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JPanel panel_2 = new JPanel();
		panel_2.setOpaque(false);
		panel_1.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		JPanel panel_3 = new JPanel();
		panel_3.setOpaque(false);
		panel_2.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
		profilePicPanel = new JPanel();
		profilePicPanel.setMaximumSize(new Dimension(220, 200));
		profilePicPanel.setOpaque(false);
		profilePicPanel.setPreferredSize(new Dimension(220, 200));
		panel_3.add(profilePicPanel);
		profilePicPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_5.setOpaque(false);
		panel_3.add(panel_5);
		
		btnUploadImage = new RoundButton("Upload image");
		btnUploadImage.setBackground(new Color(128, 128, 128));
		btnUploadImage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnUploadImage.setPreferredSize(new Dimension(120, 30));
		panel_5.add(btnUploadImage);
		
		JPanel panel_10 = new JPanel();
		panel_10.setOpaque(false);
		panel_3.add(panel_10);
		panel_10.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EmptyBorder(0, 10, 0, 0));
		panel_4.setMinimumSize(new Dimension(300, 200));
		panel_4.setOpaque(false);
		panel_2.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));
		
		JPanel panel_11 = new JPanel();
		panel_11.setPreferredSize(new Dimension(12, 10));
		panel_11.setOpaque(false);
		panel_4.add(panel_11);
		panel_11.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_6 = new JPanel();
		panel_6.setPreferredSize(new Dimension(300, 20));
		panel_6.setMaximumSize(new Dimension(32767, 30));
		panel_6.setOpaque(false);
		panel_4.add(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		tFFirstName = new JTextField();
		tFFirstName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFFirstName.setUI(new HintTextFieldUI("Your first name...", true, Color.GRAY));
		tFFirstName.setOpaque(false);
		tFFirstName.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_6.add(tFFirstName);
		tFFirstName.setColumns(10);
		
		JPanel panel_12 = new JPanel();
		panel_12.setPreferredSize(new Dimension(12, 10));
		panel_12.setOpaque(false);
		panel_4.add(panel_12);
		panel_12.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_7 = new JPanel();
		panel_7.setOpaque(false);
		panel_7.setMaximumSize(new Dimension(32767, 30));
		panel_4.add(panel_7);
		panel_7.setLayout(new BorderLayout(0, 0));
		
		tFLastName = new JTextField();
		tFLastName.setUI(new HintTextFieldUI("Your last name...", true, Color.GRAY));
		tFLastName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFLastName.setOpaque(false);
		tFLastName.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_7.add(tFLastName);
		tFLastName.setColumns(10);
		
		JPanel panel_13 = new JPanel();
		panel_13.setPreferredSize(new Dimension(12, 10));
		panel_13.setOpaque(false);
		panel_4.add(panel_13);
		panel_13.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_8 = new JPanel();
		panel_8.setMaximumSize(new Dimension(500, 500));
		panel_8.setOpaque(false);
		panel_4.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		tAStudyFields = new JTextArea();
		tAStudyFields.setLineWrap(true);
		tAStudyFields.setWrapStyleWord(true);
		tAStudyFields.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tAStudyFields.setUI(new HintTextAreaUI("Your study field(s)...", true, Color.GRAY));
		tAStudyFields.setMaximumSize(new Dimension(2147483647, 500));
		tAStudyFields.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		tAStudyFields.setOpaque(false);
		panel_8.add(tAStudyFields);
		
		JPanel panel_9 = new JPanel();
		panel_9.setPreferredSize(new Dimension(12, 10));
		panel_9.setOpaque(false);
		panel_4.add(panel_9);
		panel_9.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_22 = new JPanel();
		panel_22.setOpaque(false);
		panel_4.add(panel_22);
		panel_22.setLayout(new BorderLayout(0, 0));
		JPanel panel_221 = new JPanel();
		panel_221.setPreferredSize(new Dimension(12, 10));
		panel_221.setOpaque(false);
		panel_4.add(panel_221);
		panel_221.setLayout(new BorderLayout(0, 0));
		JPanel panel_222 = new JPanel();
		panel_222.setOpaque(false);
		panel_4.add(panel_222);
		panel_222.setLayout(new BorderLayout(0, 0));
		
		tFPhoneNumber = new JTextField();
		tFPhoneNumber.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		tFPhoneNumber.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFPhoneNumber.setUI(new HintTextFieldUI("Your phone number...", true, Color.GRAY));
		tFPhoneNumber.setOpaque(false);
		panel_222.add(tFPhoneNumber, BorderLayout.NORTH);
		tFPhoneNumber.setColumns(10);
		
		tFLocation = new JTextField();
		tFLocation.setUI(new HintTextFieldUI("Your location...", true, Color.GRAY));
		tFLocation.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		tFLocation.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFLocation.setOpaque(false);
		panel_22.add(tFLocation, BorderLayout.NORTH);
		tFLocation.setColumns(10);
		
		JPanel panel_14 = new JPanel();
		panel_14.setMaximumSize(new Dimension(32767, 30));
		panel_14.setOpaque(false);
		panel_4.add(panel_14);
		panel_14.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_15 = new JPanel();
		panel_15.setPreferredSize(new Dimension(12, 10));
		panel_15.setOpaque(false);
		panel_4.add(panel_15);
		panel_15.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_16 = new JPanel();
		panel_16.setMaximumSize(new Dimension(32767, 30));
		panel_16.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_16.setOpaque(false);
		panel_4.add(panel_16);
		panel_16.setLayout(new BorderLayout(0, 0));
		
		passwordField = new JPasswordField();
		passwordField.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		passwordField.setUI(new main.ui.customUI.HintPasswordFieldUI("Your password",false,Color.GRAY));
		passwordField.setOpaque(false);
		panel_16.add(passwordField, BorderLayout.CENTER);
		
		JPanel panel_17 = new JPanel();
		panel_17.setPreferredSize(new Dimension(12, 10));
		panel_17.setOpaque(false);
		panel_4.add(panel_17);
		panel_17.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_18 = new JPanel();
		panel_18.setOpaque(false);
		panel_4.add(panel_18);
		panel_18.setLayout(new BorderLayout(0, 0));
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setUI(new main.ui.customUI.HintPasswordFieldUI("Verify your password",false,Color.GRAY));
		passwordField_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		passwordField_1.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		passwordField_1.setOpaque(false);
		panel_18.add(passwordField_1, BorderLayout.CENTER);
		
		JPanel panel_19 = new JPanel();
		panel_19.setOpaque(false);
		panel_4.add(panel_19);
		panel_19.setLayout(new BorderLayout(0, 0));
		
		checkBShowPassword = new JCheckBox("Show password");
		checkBShowPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		checkBShowPassword.setOpaque(false);
		panel_19.add(checkBShowPassword, BorderLayout.WEST);
		
		JPanel panel_24 = new JPanel();
		panel_24.setOpaque(false);
		panel_2.add(panel_24);
		panel_24.setLayout(new GridLayout(0, 1, 0, 0));
		
		 bEditName = new RoundButton("Edit name");
		 bEditName.setBackground(new Color(207, 216, 167));
		bEditName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_24.add(bEditName);
		
		JPanel panel_25 = new JPanel();
		panel_25.setOpaque(false);
		panel_24.add(panel_25);
		
		 bEditField = new RoundButton("Edit fields");
		 bEditField.setBackground(new Color(207, 216, 167));
		bEditField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_24.add(bEditField);
		
		JPanel panel_26 = new JPanel();
		panel_26.setOpaque(false);
		panel_24.add(panel_26);
		
		 bEditLocation = new RoundButton("Edit location");
		 bEditLocation.setBackground(new Color(207, 216, 167));
		bEditLocation.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_24.add(bEditLocation);
		
		JPanel panel_27 = new JPanel();
		panel_27.setOpaque(false);
		panel_24.add(panel_27);
		
		 bEditPhoneNo = new RoundButton("Edit phone no.");
		 bEditPhoneNo.setBackground(new Color(207, 216, 167));
		bEditPhoneNo.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_24.add(bEditPhoneNo);
		
		JPanel panel_28 = new JPanel();
		panel_28.setOpaque(false);
		panel_24.add(panel_28);
		
		 bEditPassword = new RoundButton("Edit password");
		 bEditPassword.setBackground(new Color(207, 216, 167));
		bEditPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_24.add(bEditPassword);
		
		JPanel panel_20 = new JPanel();
		panel_20.setOpaque(false);
		panel_1.add(panel_20);
		panel_20.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panel_32 = new JPanel();
		panel_32.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) panel_32.getLayout();
		flowLayout.setVgap(20);
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_20.add(panel_32);
		
		 lblNewLabel = new JLabel("Card details ");
		lblNewLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panel_32.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JPanel panel_30 = new JPanel();
		panel_30.setOpaque(false);
		FlowLayout flowLayout_1 = (FlowLayout) panel_30.getLayout();
		flowLayout_1.setVgap(20);
		flowLayout_1.setHgap(0);
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		panel_20.add(panel_30);
		
		 bEditCard = new RoundButton("Edit card");
		 bEditCard.setBackground(new Color(207, 216, 167));
		bEditCard.setFont(new Font("Tahoma", Font.PLAIN, 12));
		bEditCard.setPreferredSize(new Dimension(107, 26));
		panel_30.add(bEditCard);
		
		cardDetailsPanel = new PaymentPanel(card);
		//panel_1.add(cardDetailsPanel);
		
		JPanel panel_21 = new JPanel();
		panel_21.setOpaque(false);
		panel_1.add(panel_21);
		
		btnRegister = new RoundButton("Register");
		btnRegister.setForeground(new Color(255, 255, 255));
		btnRegister.setBackground(new Color(44, 122, 20));
		btnRegister.setPreferredSize(new Dimension(150, 30));
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_21.add(btnRegister);
		
		
		btnUploadImage.addActionListener(uploadPicAction());
		checkBShowPassword.addActionListener(checkBShowPasswordAction());
		btnRegister.addActionListener(registerActionListener());
	}
	/**
	 * Create the panel.
	 */
	public NewMentorProfile() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new RoundPanel();
		panel.setMinimumSize(new Dimension(600, 200));
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JPanel panel_2 = new JPanel();
		panel_2.setOpaque(false);
		panel_1.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		JPanel panel_3 = new JPanel();
		panel_3.setOpaque(false);
		panel_2.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
		profilePicPanel = new JPanel();
		profilePicPanel.setMaximumSize(new Dimension(220, 200));
		profilePicPanel.setOpaque(false);
		profilePicPanel.setPreferredSize(new Dimension(220, 200));
		panel_3.add(profilePicPanel);
		profilePicPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_5.setOpaque(false);
		panel_3.add(panel_5);
		
		btnUploadImage = new RoundButton("Upload image");
		btnUploadImage.setBackground(new Color(128, 128, 128));
		btnUploadImage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnUploadImage.setPreferredSize(new Dimension(120, 30));
		panel_5.add(btnUploadImage);
		
		JPanel panel_10 = new JPanel();
		panel_10.setOpaque(false);
		panel_3.add(panel_10);
		panel_10.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EmptyBorder(0, 10, 0, 0));
		panel_4.setMinimumSize(new Dimension(300, 200));
		panel_4.setOpaque(false);
		panel_2.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));
		
		JPanel panel_11 = new JPanel();
		panel_11.setPreferredSize(new Dimension(12, 10));
		panel_11.setOpaque(false);
		panel_4.add(panel_11);
		panel_11.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_6 = new JPanel();
		panel_6.setPreferredSize(new Dimension(300, 20));
		panel_6.setMaximumSize(new Dimension(32767, 30));
		panel_6.setOpaque(false);
		panel_4.add(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		tFFirstName = new JTextField();
		tFFirstName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFFirstName.setUI(new HintTextFieldUI("Your first name...", true, Color.GRAY));
		tFFirstName.setOpaque(false);
		tFFirstName.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_6.add(tFFirstName);
		tFFirstName.setColumns(10);
		
		JPanel panel_12 = new JPanel();
		panel_12.setPreferredSize(new Dimension(12, 10));
		panel_12.setOpaque(false);
		panel_4.add(panel_12);
		panel_12.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_7 = new JPanel();
		panel_7.setOpaque(false);
		panel_7.setMaximumSize(new Dimension(32767, 30));
		panel_4.add(panel_7);
		panel_7.setLayout(new BorderLayout(0, 0));
		
		tFLastName = new JTextField();
		tFLastName.setUI(new HintTextFieldUI("Your last name...", true, Color.GRAY));
		tFLastName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFLastName.setOpaque(false);
		tFLastName.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		panel_7.add(tFLastName);
		tFLastName.setColumns(10);
		
		JPanel panel_13 = new JPanel();
		panel_13.setPreferredSize(new Dimension(12, 10));
		panel_13.setOpaque(false);
		panel_4.add(panel_13);
		panel_13.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_8 = new JPanel();
		panel_8.setMaximumSize(new Dimension(500, 500));
		panel_8.setOpaque(false);
		panel_4.add(panel_8);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		tAStudyFields = new JTextArea();
		tAStudyFields.setLineWrap(true);
		tAStudyFields.setWrapStyleWord(true);
		tAStudyFields.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tAStudyFields.setUI(new HintTextAreaUI("Your study field(s)...", true, Color.GRAY));
		tAStudyFields.setMaximumSize(new Dimension(2147483647, 500));
		tAStudyFields.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		tAStudyFields.setOpaque(false);
		panel_8.add(tAStudyFields);
		
		JPanel panel_9 = new JPanel();
		panel_9.setPreferredSize(new Dimension(12, 10));
		panel_9.setOpaque(false);
		panel_4.add(panel_9);
		panel_9.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_22 = new JPanel();
		panel_22.setOpaque(false);
		panel_4.add(panel_22);
		panel_22.setLayout(new BorderLayout(0, 0));
		JPanel panel_221 = new JPanel();
		panel_221.setPreferredSize(new Dimension(12, 10));
		panel_221.setOpaque(false);
		panel_4.add(panel_221);
		panel_221.setLayout(new BorderLayout(0, 0));
		JPanel panel_222 = new JPanel();
		panel_222.setOpaque(false);
		panel_4.add(panel_222);
		panel_222.setLayout(new BorderLayout(0, 0));
		
		tFPhoneNumber = new JTextField();
		tFPhoneNumber.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		tFPhoneNumber.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFPhoneNumber.setUI(new HintTextFieldUI("Your phone number...", true, Color.GRAY));
		tFPhoneNumber.setOpaque(false);
		panel_222.add(tFPhoneNumber, BorderLayout.NORTH);
		tFPhoneNumber.setColumns(10);
		
		tFLocation = new JTextField();
		tFLocation.setUI(new HintTextFieldUI("Your location...", true, Color.GRAY));
		tFLocation.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		tFLocation.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFLocation.setOpaque(false);
		panel_22.add(tFLocation, BorderLayout.NORTH);
		tFLocation.setColumns(10);
		JPanel panel_23 = new JPanel();
		panel_23.setOpaque(false);
		panel_23.setPreferredSize(new Dimension(12, 10));
		panel_4.add(panel_23);
		panel_23.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_14 = new JPanel();
		panel_14.setMaximumSize(new Dimension(32767, 30));
		panel_14.setOpaque(false);
		panel_4.add(panel_14);
		panel_14.setLayout(new BorderLayout(0, 0));
		
		tFEmail = new JTextField();
		tFEmail.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		tFEmail.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tFEmail.setUI(new HintTextFieldUI("Your e-mail...", true, Color.GRAY));
		tFEmail.setOpaque(false);
		panel_14.add(tFEmail, BorderLayout.CENTER);
		tFEmail.setColumns(10);
		
		JPanel panel_15 = new JPanel();
		panel_15.setPreferredSize(new Dimension(12, 10));
		panel_15.setOpaque(false);
		panel_4.add(panel_15);
		panel_15.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_16 = new JPanel();
		panel_16.setMaximumSize(new Dimension(32767, 30));
		panel_16.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_16.setOpaque(false);
		panel_4.add(panel_16);
		panel_16.setLayout(new BorderLayout(0, 0));
		
		passwordField = new JPasswordField();
		passwordField.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		passwordField.setUI(new main.ui.customUI.HintPasswordFieldUI("Your password",false,Color.GRAY));
		passwordField.setOpaque(false);
		panel_16.add(passwordField, BorderLayout.CENTER);
		
		JPanel panel_17 = new JPanel();
		panel_17.setPreferredSize(new Dimension(12, 10));
		panel_17.setOpaque(false);
		panel_4.add(panel_17);
		panel_17.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_18 = new JPanel();
		panel_18.setOpaque(false);
		panel_4.add(panel_18);
		panel_18.setLayout(new BorderLayout(0, 0));
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setUI(new main.ui.customUI.HintPasswordFieldUI("Verify your password",false,Color.GRAY));
		passwordField_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		passwordField_1.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
		passwordField_1.setOpaque(false);
		panel_18.add(passwordField_1, BorderLayout.CENTER);
		
		JPanel panel_19 = new JPanel();
		panel_19.setOpaque(false);
		panel_4.add(panel_19);
		panel_19.setLayout(new BorderLayout(0, 0));
		
		checkBShowPassword = new JCheckBox("Show password");
		checkBShowPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
		checkBShowPassword.setOpaque(false);
		panel_19.add(checkBShowPassword, BorderLayout.WEST);
		
		JPanel panel_24 = new JPanel();
		//panel_2.add(panel_24);
		
		JPanel panel_20 = new JPanel();
		panel_20.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) panel_20.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_20);
		
		JLabel lblNewLabel = new JLabel("Card details ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_20.add(lblNewLabel);
		
		cardDetailsPanel = new PaymentPanel(card);
		panel_1.add(cardDetailsPanel);
		
		JPanel panel_21 = new JPanel();
		panel_21.setOpaque(false);
		panel_1.add(panel_21);
		
		btnRegister = new RoundButton("Register");
		btnRegister.setForeground(new Color(255, 255, 255));
		btnRegister.setBackground(new Color(44, 122, 20));
		btnRegister.setPreferredSize(new Dimension(150, 30));
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_21.add(btnRegister);
		
		
		btnUploadImage.addActionListener(uploadPicAction());
		checkBShowPassword.addActionListener(checkBShowPasswordAction());
		btnRegister.addActionListener(registerActionListener());
	}
	
	private ActionListener registerActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int id = 1;//getLastUserId() + 1;
				String firstName = tFFirstName.getText();
				if(firstName.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Your first name is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String lastName = tFLastName.getText();
				if(lastName.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Your last name is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String field = tAStudyFields.getText();
				if(field.isEmpty()) {
					JOptionPane.showMessageDialog(null, "At least one field is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String location = tFLocation.getText();
				if(location.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Location is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String phoneNumber = tFPhoneNumber.getText();								//verify to be write corectly
				if(!isValidPhoneNumber(phoneNumber)) { 
					JOptionPane.showMessageDialog(null, "Invalid phone number. Make sure the number is 10 digits long including the leading '0' and contains no spaces or dashes.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}	
				String email = tFEmail.getText();											//verify email to be write correctly
				if(email.isEmpty()) {
					JOptionPane.showMessageDialog(null, "An email is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!isValidEmail(email) ) { //Doesn't work
					JOptionPane.showMessageDialog(null, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}	
				String password = String.valueOf(passwordField.getPassword());					//verify if passwords match and make a chanck to be strong
				String passwordVerify = String.valueOf(passwordField_1.getPassword());
				System.out.println(password);										//verify email to be write correctly			//TODO verify if passwords match and make a chanck to be strong
				if(password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Setting a password is required.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(!isStrongPassword(password)) {
					JOptionPane.showMessageDialog(null, "The password is too weak. It must contain at least one uppercase letter, one lowercase letter, one digit and one special character.", "Error", JOptionPane.ERROR_MESSAGE);
			        return;
				}
				
				System.out.println(password + " " + passwordVerify);
				if(password.compareTo(passwordVerify) != 0) {
					JOptionPane.showMessageDialog(null, "The passwords do not match. Watch out for Caps Lock, NumLock or the Shift key.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				
				String description = "";					//no description when account just created
				int programsNumber = 0;											//no programsNumber when account just created
				Date registerDate = new Date();
				
				List<Course> courses = new ArrayList<>();						//no buyed courses when account just created
				List<MentoringProgram> mentoringPrograms = new ArrayList<>();	//no mentoring program joined when account just created
				List<Feedback> feedback = new ArrayList<>();					//no feedback when account just created
				
				//check if the user added succesfylly a card
				if(cardDetailsPanel.getCard() != null) {
					card = cardDetailsPanel.getCard();
				}else {
					JOptionPane.showMessageDialog(null,"You need to add a card first","Eroare",JOptionPane.ERROR_MESSAGE);
				}
				if(profilePic == null) {
					profilePic = ImageLoader.getInstance().getUserIcon();
				}
				
				//if test passed
				Mentor mentor = new Mentor(id,firstName,lastName,email,password,phoneNumber,profilePic,location,description,field,programsNumber,registerDate,
						feedback,courses,mentoringPrograms,card);
				//TODO store new mentor
				Connection conn = DbConnection.conn;
				
				String sql = "INSERT INTO users(first_name, last_name, email, password, phone_number, description, field,"
						+ " programs_number, card_number, cvv, card_holder_name, expiration_month, expiration_year, "
						+ "profile_pic, type, location) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt;
				try {
					pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	 	            pstmt.setString(1, mentor.getFirstName());
	 	            pstmt.setString(2, mentor.getLastName());
					pstmt.setString(3, mentor.getEmail());
					pstmt.setString(4, mentor.getPassword());
					pstmt.setString(5, mentor.getPhoneNumber());
					pstmt.setString(6, mentor.getDescription());
					pstmt.setString(7, mentor.getField());
					pstmt.setInt(8, mentor.getProgramsNumber());
					pstmt.setString(9, mentor.getCard().getCardNumber());
					pstmt.setString(10, mentor.getCard().getCvvCvc());
					pstmt.setString(11, mentor.getCard().getCardHolderName());
					pstmt.setString(12, mentor.getCard().getExpirationMonth());
					pstmt.setString(13, mentor.getCard().getExpirationYear());
					pstmt.setBytes(14, imageToByteArray(profilePic));
					pstmt.setString(15, "mentor");
					pstmt.setString(16, mentor.getLocation());
					
					pstmt.executeUpdate();
					ResultSet rs = pstmt.getGeneratedKeys();
					Preferences prefs = Preferences.userNodeForPackage(LoginData.class);
					
					if(rs.next())
		                prefs.put("id", String.valueOf(rs.getInt(1)));
					mentor.setId(rs.getInt(1));
					App.getInstance().getFrame().getContentPane().removeAll();
	            	App.getInstance().getFrame().setContentPane(MainPanel.updateInstance(mentor));
	            	
	            	//update the frame
	            	App.getInstance().getFrame().invalidate();
	            	App.getInstance().getFrame().revalidate();
					System.out.println("Inserted user with id " + rs.getInt(1));
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		};
	}
	
	/**
	 * Check if phoneNumber reprezents a correct phone number (romanian phone number)
	 * @param phoneNumber
	 * @return true if phone number is correctly formatted.
	 */
	public static boolean isValidPhoneNumber(String phoneNumber) {
	    // regular expression for Romanian phone numbers 
	    String regex = "^07[0-9]{8}$|^\\+407[0-9]{8}$";
	    
	    // check if the phone number matches the regular expression
	    return phoneNumber.matches(regex);
	}
	
	/**
	 * Check if a string is in a correct email format.
	 * @param email
	 * @return true if the email is correctly formatted.
	 */
	public static boolean isValidEmail(String email) {
		// Regular expression pattern for email validation
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
				"[a-zA-Z0-9_+&*-]+)*@" +
				"(?:[a-zA-Z0-9-]+\\.)+[a-z" +
				"A-Z]{2,7}$";

		// Creating a pattern object and matching it with the email string
		Pattern pattern = Pattern.compile(emailRegex);
		if (email == null) {
			return false;
		}
		// Matching the pattern with the email string
		java.util.regex.Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	/**
	 * Checks if password has at least 8 characters,
	 * a lowercase letter, an uppercase letter, a number 
	 * and a special character
	 * @param password
	 * @return true if password is strong enough
	 */
	public static boolean isStrongPassword(String password) {
	    // Define the criteria for a strong password
	    String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
	    
	    // Check if the password matches the criteria
	    if (password.matches(regex)) {
	        return true;
	    } else {
	        return false;
	    }
	}

	public static byte[] imageToByteArray(Image image) {
	    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    bufferedImage.getGraphics().drawImage(image, 0, 0, null);
	    
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    try {
	        ImageIO.write(bufferedImage, "png", outputStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    return outputStream.toByteArray();
	}

	
	private ActionListener checkBShowPasswordAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkBShowPassword.isSelected()) {
                    passwordField.setEchoChar((char) 0); // Show password
                    passwordField_1.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar(StaticUtilities.bullet); // Hide password
                    passwordField_1.setEchoChar(StaticUtilities.bullet);
                }
			}
		};
	}
	
	private ActionListener uploadPicAction() {
		return new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            JFileChooser fileChooser = new JFileChooser();
	            int option = fileChooser.showOpenDialog(NewMentorProfile.this);
	            if (option == JFileChooser.APPROVE_OPTION) {
	                File selectedFile = fileChooser.getSelectedFile();
	                if (isImageFile(selectedFile)) {
	                    try {
	                    	BufferedImage image = ImageIO.read(selectedFile);
	                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                        ImageIO.write(image, "jpg", baos);
	                        byte[] imageData = baos.toByteArray();
	                        // Create Image from byte array
	                        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	                        profilePic = ImageIO.read(bais);
	                        // Use the newImage as needed
	                        profilePicPanel.removeAll();
	                        profilePicPanel.add(new RoundImagePanel(profilePic,new Dimension(220,200)));
	                        NewMentorProfile.this.validate();
	                        
	                    } catch (IOException ex) {
	                        ex.printStackTrace();
	                    }
	                } else {
	                    JOptionPane.showMessageDialog(null, "Invalid file format. Please select an image file.",
	                            "Error", JOptionPane.ERROR_MESSAGE);
	                }
	            }
	        }

	        private boolean isImageFile(File file) {
	            String extension = getFileExtension(file);
	            return extension != null && (extension.equalsIgnoreCase("jpg")
	                    || extension.equalsIgnoreCase("jpeg")
	                    || extension.equalsIgnoreCase("png"));
	        }

	        private String getFileExtension(File file) {
	            String name = file.getName();
	            int lastDotIndex = name.lastIndexOf('.');
	            if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
	                return name.substring(lastDotIndex + 1).toLowerCase();
	            }
	            return null;
	        }
	    };
	}

}