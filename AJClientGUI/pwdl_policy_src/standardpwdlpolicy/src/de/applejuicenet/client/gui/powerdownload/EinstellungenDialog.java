package de.applejuicenet.client.gui.powerdownload;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

	public EinstellungenDialog(Frame parent, int powerDownload, int anzahlDownloads, int sleeptime)
	{
		super(parent, true);
		this.powerDownload = powerDownload;
		this.anzahlDownloads = anzahlDownloads;
		this.sleeptime = sleeptime;
		initGui();
	}

	public EinstellungenDialog(Frame parent)
	{
		super(parent, true);
		initGui();
	}

	private void initGui()
	{
    NumberAndSpecialCharsInputVerifier verifier = new NumberAndSpecialCharsInputVerifier(",.");
    verifier.setSpecialPattern("^([1-4]?[0-9]|50$)([,.][0-9]{0,2})?$");
    tfPwdlWert.setDocument(verifier);
    tfPwdlCount.setDocument(new NumberInputVerifier(1, 99));
    tfSleeptime.setDocument(new NumberInputVerifier(0, Integer.MAX_VALUE/1000));

    JPanel abfrage = new JPanel(new GridLayout(3, 2));
    abfrage.add(new JLabel("Pwdl-Wert:"));
    abfrage.add(tfPwdlWert);
    abfrage.add(new JLabel("Anzahl Downloads:"));
    abfrage.add(tfPwdlCount);
    abfrage.add(new JLabel("Aktualisierungsintervall:"));
    abfrage.add(tfSleeptime);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(abfrage, BorderLayout.CENTER);
    JPanel panel = new JPanel();
    panel.add(btnOk);
    getContentPane().add(panel, BorderLayout.SOUTH);
    
    initListeners();
    pack();
	}

	private void initListeners()
	{
		btnOk.addActionListener(this);
		tfPwdlCount.addActionListener(this);
		tfPwdlWert.addActionListener(this);
		tfSleeptime.addActionListener(this);
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
			anzahlDownloads = new Integer(tfPwdlCount.getText());
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
}