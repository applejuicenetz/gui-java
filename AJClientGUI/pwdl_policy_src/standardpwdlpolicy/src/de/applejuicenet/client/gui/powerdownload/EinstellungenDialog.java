package de.applejuicenet.client.gui.powerdownload;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import de.applejuicenet.client.gui.powerdownload.StandardAutomaticPwdlPolicy.Reihenfolge;
import de.applejuicenet.client.shared.NumberAndSpecialCharsInputVerifier;
import de.applejuicenet.client.shared.NumberInputVerifier;

public class EinstellungenDialog extends JDialog implements ActionListener
{
	private int powerDownload = 12;
	private int anzahlDownloads = 2;
	private int sleeptime = 30;
	private JTextField tfPwdlWert = new JTextField();
	private JTextField tfPwdlCount = new JTextField();
	private JTextField tfSleeptime = new JTextField();
	private JButton btnOk = new JButton("Ok");
	private JList listSorting = new JList();
	private DefaultListModel listSortingModel = new DefaultListModel();
	private JButton btnUp = new JButton("Hoch");
	private JButton btnDown = new JButton("Runter");
	private static String PROZENT_GELADEN = "prozentGeladen";
	private static String GROESSE = "groesse";
	private static String QUELLENANZAHL = "quellenAnzahl";
	
	public static void main(String args[]) {
		EinstellungenDialog e = new EinstellungenDialog(null);
		e.setVisible(true);
	}
	
	public EinstellungenDialog(Frame parent, int powerDownload, int anzahlDownloads, int sleeptime)
	{
		super(parent, true);
		this.powerDownload = powerDownload;
		this.anzahlDownloads = anzahlDownloads;
		this.sleeptime = sleeptime;
		listSorting.setModel(listSortingModel);
		initGui();
		initData();
	}

	public EinstellungenDialog(Frame parent)
	{
		super(parent, true);
		listSorting.setModel(listSortingModel);
		initGui();
		initData();
	}

	private void initData()
	{
		listSortingModel.addElement("prozentGeladen");
		listSortingModel.addElement("groesse");
		listSortingModel.addElement("quellenAnzahl");
	}

	private void initGui()
	{
		setResizable(false);
		setTitle("Powerdownload - Einstellungen");
    NumberAndSpecialCharsInputVerifier verifier = new NumberAndSpecialCharsInputVerifier(",.");
    verifier.setSpecialPattern("^([1-4]?[0-9]|50$)([,.][0-9]{0,2})?$");
    tfPwdlWert.setDocument(verifier);
    tfPwdlCount.setDocument(new NumberInputVerifier(1, 99));
    tfSleeptime.setDocument(new NumberInputVerifier(0, Integer.MAX_VALUE/1000));

    getContentPane().setLayout(new GridBagLayout());
    GridBagConstraints constr = new GridBagConstraints();
    constr.anchor = GridBagConstraints.NORTHWEST;
    constr.insets = new Insets(5,5,5,5);
    constr.fill = GridBagConstraints.BOTH;
    constr.gridheight = 1;
    constr.gridwidth = 1;
    constr.gridx = 0;
    constr.gridy = 0;
    getContentPane().add(new JLabel("Pwdl-Wert:"), constr);
    constr.gridy++;
    getContentPane().add(new JLabel("Anzahl Downloads:"), constr);
    constr.gridy++;
    getContentPane().add(new JLabel("Aktualisierungsintervall:"), constr);
    constr.gridy++;
    constr.gridheight = 2;
    getContentPane().add(listSorting, constr);
    constr.gridheight = 1;
    constr.gridy++;
    constr.gridy++;
    constr.gridwidth = 2;
    getContentPane().add(btnOk, constr);
    constr.gridwidth = 1;

    constr.gridy = 0;
    constr.gridx++;
    getContentPane().add(tfPwdlWert, constr);
    constr.gridy++;
    getContentPane().add(tfPwdlCount, constr);
    constr.gridy++;
    getContentPane().add(tfSleeptime, constr);
    constr.gridy++;
    getContentPane().add(btnUp, constr);
    constr.gridy++;
    getContentPane().add(btnDown, constr);
    
    
    listSorting.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    initListeners();
    pack();
	}

	private void initListeners()
	{
		btnOk.addActionListener(this);
		tfPwdlCount.addActionListener(this);
		tfPwdlWert.addActionListener(this);
		tfSleeptime.addActionListener(this);
		btnUp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int index = listSorting.getSelectedIndex();
				if (index > 0) {
					moveListEntry(index, index-1);
				}
			}});
		btnDown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int index = listSorting.getSelectedIndex();
				if (index < listSorting.getModel().getSize()-1 && index != -1) {
					moveListEntry(index, index+1);
				}
			}});
	}
	
	private void moveListEntry(int von, int nach) {
		Object objVon = listSortingModel.getElementAt(von);
		Object objNach = listSortingModel.getElementAt(nach);
		listSortingModel.setElementAt(objVon, nach);
		listSortingModel.setElementAt(objNach, von);
		listSorting.setSelectedIndex(nach);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (checkInput()) {
			dispose();
		}
	}

	private boolean checkInput() {
		boolean returnValue = true;
		String result = tfPwdlWert.getText();
		result = result.replace(',', '.');
		double wert = Double.parseDouble(result) - 1;
		wert = ((double) Math.round(wert * 100.0))/100.0;
		int tempValue = (int)((wert) * 10);
		if (tempValue<12 || tempValue>490){
			returnValue = false;
			tfPwdlWert.setText(Integer.toString(powerDownload));
		}
		else {
			powerDownload = tempValue;
		}
		try {
			anzahlDownloads = Integer.parseInt(tfPwdlCount.getText());
		}
		catch (NumberFormatException e) {
			returnValue = false;
		}
		try {
			int slt = Integer.parseInt(tfSleeptime.getText());
			if (slt >= 30) {
				sleeptime = slt;
			}
			else {
				tfSleeptime.setText(Integer.toString(sleeptime));
				returnValue = false;
			}
		}
		catch (NumberFormatException e) {
			tfSleeptime.setText(Integer.toString(sleeptime));
			returnValue = false;
		}
		return returnValue;
	}
		
	public int getAnzahlDownloads()
	{
		return anzahlDownloads;
	}

	public int getPowerDownload()
	{
		return powerDownload;
	}

	public int getSleeptime()
	{
		return sleeptime*1000;
	}
	
	public void setVisible(boolean visible) {
		Dimension appDimension = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - appDimension.width) / 2,
				(screenSize.height - appDimension.height) / 2);
		super.setVisible(visible);
	}

	public Reihenfolge[] getReihenfolge()
	{
		Reihenfolge[] reihenfolge = new Reihenfolge[4];
		for (int i=0; i<listSortingModel.size(); i++) {
			String cur = (String) listSortingModel.elementAt(i);
			if (cur == PROZENT_GELADEN) {
				reihenfolge[i] = Reihenfolge.PROZENT_GELADEN; 
			}
			else if (cur == GROESSE) {
				reihenfolge[i] = Reihenfolge.GROESSE; 
			}
			else if (cur == QUELLENANZAHL) {
				reihenfolge[i] = Reihenfolge.SOURCEN; 
			}
		}
		reihenfolge[3] = Reihenfolge.ID;
		return reihenfolge;
	}
}