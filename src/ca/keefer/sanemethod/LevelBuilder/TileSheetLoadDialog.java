package ca.keefer.sanemethod.LevelBuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/** 
 * Extends the JDialog to get the string reference and integere cellwidth and cellheight of a tileSheet
 * @author Christopher Keefer
 * @version 1.1
 *
 */
public class TileSheetLoadDialog extends JDialog{

	static final long serialVersionUID = 1;
	JPanel textPanel;
	JTextField refField;
	JLabel refLabel;
	String ref;
	JTextField cellWidthField;
	int cellWidth;
	JTextField cellHeightField;
	int cellHeight;
	JLabel cWLabel;
	JLabel CHLabel;
	LayoutManager layout;
	JButton ok;
	JButton cancel;
	JDialog thisDialog;
	static final int D_WIDTH = 450;
	static final int D_HEIGHT = 150;
	
	// inner class for ok/cancel button in chooser dialog
	class buttonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (e.getActionCommand() == "OK"){
			// get size and text entered, get font, create shape from info
				
				ref = refField.getText();
				cellWidth = Integer.parseInt(cellWidthField.getText());
				cellHeight = Integer.parseInt(cellHeightField.getText());
			} else if (e.getActionCommand() == "Cancel"){
				// Do Nothing
			}
			thisDialog.dispose();
		}
	}
	
	
	public TileSheetLoadDialog(Frame owner){
		super(owner,"Load TileSheet",true);
		ref = "";
		cellWidth=0;
		cellHeight=0;
		// for reference
		thisDialog = this;
		this.setSize(D_WIDTH,D_HEIGHT);
		// Create layout manager
		layout = new BorderLayout();
		// set layout
		this.getContentPane().setLayout(layout);
		// create Jpanel and add to contentpane
		textPanel = new JPanel();
		this.getContentPane().add(textPanel);
		//textPanel.setLayout(layout);
		// create combobox and add to panel
		// create and add label for text
		refLabel = new JLabel("Enter Reference String:");
		textPanel.add(refLabel,BorderLayout.WEST);
		// Create the main text field and add to panel
		refField = new JTextField("",35);
		textPanel.add(refField,BorderLayout.EAST);
		// create and add label for size
		cWLabel = new JLabel("cellWidth:");
		textPanel.add(cWLabel,BorderLayout.NORTH);
		// Create the size text field and add to panel
		cellWidthField = new JTextField("64",4);
		textPanel.add(cellWidthField,BorderLayout.NORTH);
		// Create the size text field and add to panel
		cellHeightField = new JTextField("64",4);
		textPanel.add(cellHeightField,BorderLayout.NORTH);
		// create ok and cancel button and add to panel
		ok = new JButton("OK");
		cancel = new JButton("Cancel");
		textPanel.add(ok,BorderLayout.SOUTH);
		textPanel.add(cancel,BorderLayout.SOUTH);
		// Add listeners
		ActionListener textChosen = new buttonListener();
		ok.addActionListener(textChosen);
		cancel.addActionListener(textChosen);
		this.setAlwaysOnTop(true);
		// Center the screen
	    Component c = null;
	    setLocationRelativeTo(c);
	}
	
	
	
	public void showDialog(){
		this.setVisible(true);
		
	}
	
	public int getCellWidth(){
		return cellWidth;
	}
	
	public int getCellHeight(){
		return cellHeight;
	}
	
	public String getReference(){
		return ref;
	}
	
}
