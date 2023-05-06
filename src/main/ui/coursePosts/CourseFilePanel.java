package main.ui.coursePosts;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.awt.Color;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import main.app.App;
import main.ui.customComponents.RoundButton;
import main.utility.ImageLoader;

import java.awt.Component;

import javax.swing.Box;

/**
 * A panel that countains the course name and a button that let the user to see the course,
 * if the course was bought.
 * @author Daniel
 * @version 1.0
 */
public class CourseFilePanel extends JPanel {
	
	private ImageIcon iconButtonLocked;			//icon before payment
	private ImageIcon iconButtonUnlocked;		//related TODO: change icons after the payed was made
	private ImageIcon pdfFileIcon;				//pdf file icon
	private JPanel pdfViewPanel;				//the panel that disply the pdf			
	private JPanel courseDetailsParent;			//the panel with the course details
	
	//for action listeners
	private JButton btnViewCourse;				//the button the show the pdf
	private Boolean payed = false;				//indicate whatever the course was bought or not
	public Boolean getPayed() {
		return payed;
	}

	private Boolean displayed = false;			//indicate whatever the pdf is displayed or not

	/**
	 * Sets the payed status.
	 * @param payed new payed status.
	 */
	public void setPayed(Boolean payed) {
		this.payed = payed;
	}

	/**
	 * @return the button that display the course
	 */
	public JButton getBtnViewCourse() {
		return btnViewCourse;
	}

	/**
	 * Loads the images used by class.
	 */
	private void loadButtonsImageIcon() {
		iconButtonLocked = new ImageIcon(ImageLoader.getInstance().getLockedIcon());
		iconButtonUnlocked = new ImageIcon(ImageLoader.getInstance().getUnlockedIcon());
		pdfFileIcon = new ImageIcon(ImageLoader.getInstance().getPdfIcon());
		
	}
	
	/**
	 * Parametes contructor. It will fill the data with the proper values.		//TODO: fill missing data
	 * @param pane -?
	 */
	public CourseFilePanel(JPanel courseDetailsP) {
		this();
		courseDetailsParent = courseDetailsP;
		btnViewCourse.addActionListener(btnViewCourseActionListener());
	}
	
	/**
	 * Create the panel.
	 * UI components.
	 */
	public CourseFilePanel() {
		loadButtonsImageIcon();
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(Color.LIGHT_GRAY);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel courseFilePanel = new JPanel();
		courseFilePanel.setOpaque(false);
		courseFilePanel.setBorder(new EmptyBorder(10, 10, 20, 20));
		add(courseFilePanel);
		courseFilePanel.setLayout(new BoxLayout(courseFilePanel, BoxLayout.X_AXIS));
		
		JLabel lblIconPdfFile = new JLabel("");
		lblIconPdfFile.setBorder(new EmptyBorder(3, 3, 3, 3));
		lblIconPdfFile.setPreferredSize(new Dimension(32, 32));
		lblIconPdfFile.setMinimumSize(new Dimension(32, 32));
		lblIconPdfFile.setMaximumSize(new Dimension(32, 32));
		
		lblIconPdfFile.setIcon(pdfFileIcon);
		
		courseFilePanel.add(lblIconPdfFile);
		
		JLabel lblNewLabel = new JLabel("CourseName.pdf");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		courseFilePanel.add(lblNewLabel);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		horizontalStrut_1.setMaximumSize(new Dimension(20000, 10));
		courseFilePanel.add(horizontalStrut_1);
		
		btnViewCourse = new RoundButton("View course");
		btnViewCourse.setFocusable(false);
		btnViewCourse.setForeground(Color.BLACK);
		btnViewCourse.setBackground(Color.GRAY);
		btnViewCourse.setPreferredSize(new Dimension(200, 30));
		btnViewCourse.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnViewCourse.setMinimumSize(new Dimension(100, 30));
		btnViewCourse.setMaximumSize(new Dimension(200, 30));
		courseFilePanel.add(btnViewCourse);
		btnViewCourse.setIcon(iconButtonLocked);
		CourseFilePanel p = this;
		
		
		pdfViewPanel = new JPanel();
		pdfViewPanel.setOpaque(false);
		add(pdfViewPanel);

	}

	/**
	 * Action for view course button.
	 * It check if the payments was made and display or hide the pdf(depending on whatever the pdf is displayed or not)
	 * @return action listener for view course button
	 */
	private ActionListener btnViewCourseActionListener() {
		CourseFilePanel p = this;
		ActionListener act = new ActionListener() { 			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(payed) { 																			//action if the payment was made
					if(!displayed) {																	//need to display the pdf
						p.remove(pdfViewPanel);															//make and add the new pdf
						pdfViewPanel = new PDFViewPanel();
						btnViewCourse.setIcon(iconButtonUnlocked);										//set image icon as unlockde after the
						p.add(pdfViewPanel,1);															//payed was made
						btnViewCourse.setText("Hide course");
						courseDetailsParent.getComponent(0).setMaximumSize(new Dimension(1300,30000));	//reset size of panel
						App.getInstance().getFrame().invalidate();										//update ui
						displayed = true;
					}else {																				//need to hide the pdf
						p.remove(pdfViewPanel);															//remove pdf
						courseDetailsParent.getComponent(0).setMaximumSize(new Dimension(800,30000));	//reset size of panel
						btnViewCourse.setText("View course");											//reset button message
						displayed = false;
						App.getInstance().getFrame().invalidate();										//update ui
					}
				}else {	//display a popUp to inform the user that the course needs to be bought for this action
						//set it visible for 3 seconds
					JPopupMenu popupMenu = new JPopupMenu();
					popupMenu.setOpaque(false);
					popupMenu.setBorder(new EmptyBorder(0,0,0,0));
					
					JLabel mes = new JLabel("You need to buy the course to perform this action!");
					mes.setForeground(Color.red);
					mes.setFont(new Font("Tahoma", Font.PLAIN, 16));
			        popupMenu.add(mes);
			        
			        //get the position to display the popup menu
			        int xPos = 0;
			        int yPos = - btnViewCourse.getHeight();										//disply on top
			        popupMenu.show(btnViewCourse,xPos ,yPos );
			        
			        Timer timer = new Timer(3000, new ActionListener() {						//visible for 3 sec
	                    @Override
	                    public void actionPerformed(ActionEvent evt) {
	                        popupMenu.setVisible(false);
	                    }
	                });
	                timer.setRepeats(false);
	                timer.start();
				}
			}
		};

		return act;
	}

}
