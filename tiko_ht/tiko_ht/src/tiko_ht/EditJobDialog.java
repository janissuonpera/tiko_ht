package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;

public class EditJobDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	// Makes item list visible or not.
	public boolean itemList_visible = false;
	// Contains items and their amount.
	java.util.List<java.util.List<String>> itemList = new ArrayList<java.util.List<String>>();
	// Job list containing all jobs.
	java.util.List<String> job_list = new ArrayList<String>();
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public EditJobDialog(Shell parent, int style) {
		super(parent, style);
		setText("Muokkaa työkohdetta");
	}
	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {

		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(432, 351);
		shell.setText(getText());
		DBHandler db = new DBHandler();
		shell.setLayout(null);

		Label lblValitseTykohde = new Label(shell, SWT.NONE);
		lblValitseTykohde.setBounds(5, 5, 113, 15);
		lblValitseTykohde.setText("Valitse ty\u00F6kohde");

		Label lblTykohteenValinnat = new Label(shell, SWT.NONE);
		lblTykohteenValinnat.setBounds(249, 5, 107, 15);
		lblTykohteenValinnat.setText("Ty\u00F6kohteen valinnat");

		Combo job_dropdown = new Combo(shell, SWT.NONE);
		job_dropdown.setBounds(5, 26, 201, 23);
		db.connect();
		job_list = db.getJobs();
		for (int i = 0; i < job_list.size(); i++) {
			job_dropdown.add(job_list.get(i));
		}

		Button finishJob_btn = new Button(shell, SWT.NONE);
		finishJob_btn.setBounds(249, 26, 106, 25);
		finishJob_btn.setToolTipText(
				"Aseta kohde valmiiksi ja lis\u00E4\u00E4 lasku.");
		finishJob_btn.setText("Aseta valmiiksi");

		Button deleteJob_btn = new Button(shell, SWT.NONE);
		deleteJob_btn.setBounds(249, 88, 107, 25);
		deleteJob_btn.setToolTipText("Poista kohde tietokannasta.");
		deleteJob_btn.setText("Poista kohde");

		Button getPrice_btn = new Button(shell, SWT.NONE);
		getPrice_btn.setBounds(249, 57, 106, 25);
		getPrice_btn.setToolTipText("N\u00E4yt\u00E4 kohteen kokonaishinta");
		getPrice_btn.setText("Kustannukset");
		List items_list = new List(shell, SWT.BORDER);
		items_list.setBounds(215, 119, 172, 117);
		
		

		Label lblAnnaAlennustaTunneista = new Label(shell, SWT.NONE);
		lblAnnaAlennustaTunneista.setBounds(4, 55, 114, 15);
		lblAnnaAlennustaTunneista.setText("Suoritukset kohteessa");

		Button getItems_btn = new Button(shell, SWT.NONE);
		getItems_btn.setBounds(246, 242, 110, 25);
		getItems_btn.setToolTipText(
				"N\u00E4yt\u00E4 kohteeseen k\u00E4ytetyt tarvikkeet");
		getItems_btn.setText("Kohteen tarvikkeet");
		getItems_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				// Set item list visible.
				db.connect();
				itemList = db.getJobItems(job_dropdown.getText(), false);
				// Get all items and their amount from the list.
				if(!itemList.isEmpty())
					items_list.removeAll();
					for (int i = 0; i < itemList.get(0).size(); i++) {
						items_list.add(
								itemList.get(0).get(i) + itemList.get(1).get(i) + "€");
					}
			}

		});

		List work_list = new List(shell, SWT.BORDER);
		work_list.setBounds(5, 72, 119, 115);

		Label lblAnnaAlennusta = new Label(shell, SWT.NONE);
		lblAnnaAlennusta.setBounds(0, 193, 165, 15);
		lblAnnaAlennusta.setText("Anna alennusta tuntityyppiin");

		Combo workType_dropdown = new Combo(shell, SWT.NONE);
		workType_dropdown.setItems(new String[] {"Ty\u00F6", "Suunnittelu", "Aputy\u00F6"});
		workType_dropdown.setBounds(5, 214, 132, 23);

		Spinner taskDiscount_spinner = new Spinner(shell, SWT.BORDER);
		taskDiscount_spinner.setBounds(5, 243, 47, 22);
		taskDiscount_spinner.setToolTipText("%");

		Button addDiscount_btn = new Button(shell, SWT.NONE);
		addDiscount_btn.setBounds(5, 271, 82, 25);
		addDiscount_btn.setText("Lis\u00E4\u00E4 alennus");
		
		Button close_btn = new Button(shell, SWT.NONE);
		close_btn.setBounds(341, 287, 75, 25);
		close_btn.setText("Sulje");
		close_btn.addListener(SWT.Selection,new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();
			}
			
		});
		
	}
}

