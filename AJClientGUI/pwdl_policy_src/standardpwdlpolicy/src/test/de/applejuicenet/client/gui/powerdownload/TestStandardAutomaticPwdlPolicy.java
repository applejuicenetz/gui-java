
package test.de.applejuicenet.client.gui.powerdownload;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.powerdownload.StandardAutomaticPwdlPolicy;
import de.applejuicenet.client.gui.powerdownload.StandardAutomaticPwdlPolicy.Reihenfolge;

public class TestStandardAutomaticPwdlPolicy extends TestCase
{
	private ApplejuiceFassadeDummy fassade = null;
	private StandardAutomaticPwdlPolicy policy = null;
	private DownloadSourceDummy DUMMY = new DownloadSourceDummy();
	private DownloadSource[] EINE_QUELLE = new DownloadSource[]{DUMMY};
	private DownloadSource[] ZWEI_QUELLEN = new DownloadSource[]{DUMMY,DUMMY};
	private DownloadSource[] DREI_QUELLEN = new DownloadSource[]{DUMMY,DUMMY,DUMMY};
	private DownloadSource[] VIER_QUELLEN = new DownloadSource[]{DUMMY,DUMMY,DUMMY,DUMMY};
	private static Reihenfolge[] SOURCEN_PROZENT_GROESSE = 
		new Reihenfolge[]{ 
		Reihenfolge.SOURCEN, 
		Reihenfolge.PROZENT_GELADEN, 
		Reihenfolge.GROESSE, 
		Reihenfolge.ID 
	};
	private static Reihenfolge[] PROZENT_SOURCEN_GROESSE = 
		new Reihenfolge[]{ 
		Reihenfolge.PROZENT_GELADEN, 
		Reihenfolge.SOURCEN, 
		Reihenfolge.GROESSE, 
		Reihenfolge.ID 
	};
	private static Reihenfolge[] GROESSE_PROZENT_SOURCEN = 
		new Reihenfolge[]{ 
		Reihenfolge.GROESSE, 
		Reihenfolge.PROZENT_GELADEN, 
		Reihenfolge.SOURCEN, 
		Reihenfolge.ID 
	};
		
	protected void setUp() throws Exception
	{
		super.setUp();
		fassade = new ApplejuiceFassadeDummy();
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3, PROZENT_SOURCEN_GROESSE);
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
		fassade = null;
		policy = null;
	}

	public void testDoSimpleAction() throws IllegalArgumentException
	{
		try
		{
			policy.doAction();
		}
		catch (Exception e)
		{
			fail("Fehler: " + e.getMessage());
			e.printStackTrace();
		}
		fassade.verify();
	}

	public void testDoOneAction() throws IllegalArgumentException
	{
		Map<String,Download> downloads = new HashMap<String,Download>();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 1.1, Download.SUCHEN_LADEN, 1));
		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3, PROZENT_SOURCEN_GROESSE);

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 1.1, Download.SUCHEN_LADEN, 3));
		try
		{
			policy.doAction();
		}
		catch (Exception e)
		{
			fail("Fehler: " + e.getMessage());
			e.printStackTrace();
		}
		fassade.verify();
	}

	public void testDoFourAction() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy(new HashMap<String,Download>());
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3, PROZENT_SOURCEN_GROESSE);

		fassade.addDownload(new DownloadDummy(1, 0.0, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(2, 0.0, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(3, 1.3, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(4, 1.4, Download.SUCHEN_LADEN, 1));
		fassade.addExpectedDownload(new DownloadDummy(1, 0.0, Download.PAUSIERT, 1));
		fassade.addExpectedDownload(new DownloadDummy(2, 0.0, Download.PAUSIERT, 1)); 
		fassade.addExpectedDownload(new DownloadDummy(3, 1.3, Download.PAUSIERT, 1)); 
		fassade.addExpectedDownload(new DownloadDummy(4, 1.4, Download.SUCHEN_LADEN, 3)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testDoActionGroesse() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy();
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3, PROZENT_SOURCEN_GROESSE);

		fassade.addDownload(new DownloadDummy(1, 10.0, Download.SUCHEN_LADEN, 1, 10));
		fassade.addDownload(new DownloadDummy(2, 10.0, Download.SUCHEN_LADEN, 1, 20));
		fassade.addDownload(new DownloadDummy(3, 1.3, Download.SUCHEN_LADEN, 1, 30));
		fassade.addDownload(new DownloadDummy(4, 1.4, Download.SUCHEN_LADEN, 1, 40));
		fassade.addDownload(new DownloadDummy(5, 10.0, Download.SUCHEN_LADEN, 1, 15));
		fassade.addExpectedDownload(new DownloadDummy(1, 10.0, Download.PAUSIERT, 1, 10));
		fassade.addExpectedDownload(new DownloadDummy(2, 10.0, Download.SUCHEN_LADEN, 3, 20)); 
		fassade.addExpectedDownload(new DownloadDummy(3, 1.3, Download.PAUSIERT, 1, 30)); 
		fassade.addExpectedDownload(new DownloadDummy(4, 1.4, Download.PAUSIERT, 1, 40)); 
		fassade.addExpectedDownload(new DownloadDummy(5, 10.0, Download.PAUSIERT, 1, 15)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testDoActionGroesseProzentSourcen() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy();
		policy = new StandardAutomaticPwdlPolicy(fassade, 3, 3, GROESSE_PROZENT_SOURCEN);

		fassade.addDownload(new DownloadDummy(1, 20.0, Download.SUCHEN_LADEN, 1, 30, EINE_QUELLE));
		fassade.addDownload(new DownloadDummy(2, 99.0, Download.SUCHEN_LADEN, 1, 30, EINE_QUELLE));
		fassade.addDownload(new DownloadDummy(3, 20.0, Download.SUCHEN_LADEN, 1, 30, ZWEI_QUELLEN));
		fassade.addDownload(new DownloadDummy(4, 20.0, Download.SUCHEN_LADEN, 1, 40, EINE_QUELLE));
		fassade.addDownload(new DownloadDummy(5, 20.0, Download.SUCHEN_LADEN, 1, 30, EINE_QUELLE));
		
		fassade.addExpectedDownload(new DownloadDummy(1, 20.0, Download.PAUSIERT, 1, 30, EINE_QUELLE));
		fassade.addExpectedDownload(new DownloadDummy(2, 99.0, Download.SUCHEN_LADEN, 3, 30, EINE_QUELLE)); 
		fassade.addExpectedDownload(new DownloadDummy(3, 20.0, Download.SUCHEN_LADEN, 3, 30, ZWEI_QUELLEN)); 
		fassade.addExpectedDownload(new DownloadDummy(4, 20.0, Download.SUCHEN_LADEN, 3, 40, EINE_QUELLE)); 
		fassade.addExpectedDownload(new DownloadDummy(5, 20.0, Download.PAUSIERT, 1, 30, EINE_QUELLE)); 
		
		policy.doAction();
		fassade.verify();
	}
	
	public void testDoActionProzentUndSourcen() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy();
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3, PROZENT_SOURCEN_GROESSE);

		fassade.addDownload(new DownloadDummy(1, 10.0, Download.SUCHEN_LADEN, 1, 100, EINE_QUELLE));
		fassade.addDownload(new DownloadDummy(2, 10.0, Download.SUCHEN_LADEN, 1, 20, VIER_QUELLEN));
		fassade.addDownload(new DownloadDummy(3, 1.3, Download.SUCHEN_LADEN, 1, 30, ZWEI_QUELLEN));
		fassade.addDownload(new DownloadDummy(4, 1.4, Download.SUCHEN_LADEN, 1, 40, DREI_QUELLEN));
		fassade.addExpectedDownload(new DownloadDummy(1, 10.0, Download.PAUSIERT, 1, 100, EINE_QUELLE));
		fassade.addExpectedDownload(new DownloadDummy(2, 10.0, Download.SUCHEN_LADEN, 3, 20, VIER_QUELLEN));
		fassade.addExpectedDownload(new DownloadDummy(3, 1.3, Download.PAUSIERT, 1, 30, ZWEI_QUELLEN));
		fassade.addExpectedDownload(new DownloadDummy(4, 1.4, Download.PAUSIERT, 1, 40, DREI_QUELLEN));
		
		policy.doAction();
		fassade.verify();
	}
	
	public void testDoActionQuellen() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy();
		policy = new StandardAutomaticPwdlPolicy(fassade, 2, 3, SOURCEN_PROZENT_GROESSE);

		fassade.addDownload(new DownloadDummy(1, 90.0, Download.SUCHEN_LADEN, 1, 30, ZWEI_QUELLEN));
		fassade.addDownload(new DownloadDummy(2, 90.0, Download.SUCHEN_LADEN, 1, 30, EINE_QUELLE));
		fassade.addDownload(new DownloadDummy(3, 30.0, Download.SUCHEN_LADEN, 1, 10, VIER_QUELLEN));
		fassade.addDownload(new DownloadDummy(4, 40.0, Download.SUCHEN_LADEN, 1, 10, DREI_QUELLEN));
		fassade.addExpectedDownload(new DownloadDummy(1, 90.0, Download.PAUSIERT, 1, 30, ZWEI_QUELLEN));
		fassade.addExpectedDownload(new DownloadDummy(2, 90.0, Download.PAUSIERT, 1, 30, EINE_QUELLE)); 
		fassade.addExpectedDownload(new DownloadDummy(3, 30.0, Download.SUCHEN_LADEN, 3, 10, VIER_QUELLEN)); 
		fassade.addExpectedDownload(new DownloadDummy(4, 40.0, Download.SUCHEN_LADEN, 3, 10, DREI_QUELLEN)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testDoAction() throws Exception
	{
			fassade = new ApplejuiceFassadeDummy();
			policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3, PROZENT_SOURCEN_GROESSE);

			fassade.addDownload(new DownloadDummy(1, Download.SUCHEN_LADEN, 1, 10));
			fassade.addDownload(new DownloadDummy(2, Download.SUCHEN_LADEN, 1, 20));
			fassade.addExpectedDownload(new DownloadDummy(1, Download.PAUSIERT, 1, 10));
			fassade.addExpectedDownload(new DownloadDummy(2, Download.SUCHEN_LADEN, 3, 20)); 

			policy.doAction();
			fassade.verify();

			fassade.addDownload(new DownloadDummy(3, 0.0, Download.SUCHEN_LADEN, 1));
			fassade.addExpectedDownload(new DownloadDummy(3, 0.0, Download.PAUSIERT, 1)); 
			
			policy.doAction();
			fassade.verify();	
	}
	
	public void testDoNextAction() throws Exception
	{
		Map<String,Download> downloads = new HashMap<String,Download>();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 1.1, Download.SUCHEN_LADEN, 1));
		downloads.put(Integer.toString(2), new DownloadDummy(new Integer(2), "bla2", 1.2, Download.SUCHEN_LADEN, 1));
		downloads.put(Integer.toString(3), new DownloadDummy(new Integer(3), "bla3", 1.3, Download.PAUSIERT, 3));
		downloads.put(Integer.toString(4), new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1));

		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 2, 3, PROZENT_SOURCEN_GROESSE);

		policy.doAction();

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 1.1, Download.PAUSIERT, 1));
		fassade.addExpectedDownload(new DownloadDummy(new Integer(2), "bla2", 1.2, Download.SUCHEN_LADEN, 3)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(3), "bla3", 1.3, Download.SUCHEN_LADEN, 3)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1)); 
		
		fassade.verify();
	}

	public void testDoEndeAction() throws Exception
	{
		Map<String,Download> downloads = new HashMap<String,Download>();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 1.1, Download.FERTIG, 1));
		downloads.put(Integer.toString(2), new DownloadDummy(new Integer(2), "bla2", 1.2, Download.FERTIG, 1));
		downloads.put(Integer.toString(3), new DownloadDummy(new Integer(3), "bla3", 1.3, Download.FERTIG, 3));
		downloads.put(Integer.toString(4), new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1));

		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 2, 3, PROZENT_SOURCEN_GROESSE);

		policy.doAction();

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 1.1, Download.FERTIG, 1));
		fassade.addExpectedDownload(new DownloadDummy(new Integer(2), "bla2", 1.2, Download.FERTIG, 1)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(3), "bla3", 1.3, Download.FERTIG, 3)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1)); 
		
		fassade.verify();
	}

	public void testDoActionOneAdded() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy(new HashMap<String,Download>());
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3, PROZENT_SOURCEN_GROESSE);

		fassade.addDownload(new DownloadDummy(1, 10.0, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(2, 10.0, Download.SUCHEN_LADEN, 1));
		fassade.addExpectedDownload(new DownloadDummy(1, 10.0, Download.SUCHEN_LADEN, 3));
		fassade.addExpectedDownload(new DownloadDummy(2, 10.0, Download.PAUSIERT, 1)); 

		policy.doAction();
		fassade.verify();

		fassade.addDownload(new DownloadDummy(3, 0.0, Download.SUCHEN_LADEN, 1));
		fassade.addExpectedDownload(new DownloadDummy(3, 0.0, Download.PAUSIERT, 1)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testGetVersion()
	{
		assertNotNull("Version ist nicht gesetzt!", policy.getVersion());
		assertTrue("Version ist \"\"!", policy.getVersion().length()>0);
	}

	public void testGetDescription()
	{
		assertNotNull("Description ist nicht gesetzt!", policy.getDescription());
		assertTrue("Description ist \"\"!", policy.getDescription().length()>0);
	}

	public void testGetAuthor()
	{
		assertNotNull("Autor ist nicht gesetzt!", policy.getAuthor());
		assertTrue("Autor ist \"\"!", policy.getAuthor().length()>0);
	}

}
