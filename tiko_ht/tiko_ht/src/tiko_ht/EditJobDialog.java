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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class EditJobDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	protected int style;
	// Makes item list visible or not.
	public boolean itemList_visible = false;
	// Contains items and their amount.
	java.util.List<String[]> itemList = new ArrayList<String[]>();
	// Job list containing all jobs.
	java.util.List<String> job_list = new ArrayList<String>();
	// Task list containing all task for the selected job.
	java.util.List<String> task_list = new ArrayList<String>();
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public EditJobDialog(Shell parent, int style) {
		super(parent, style);
		setText("Muokkaa ty\u00F6kohdetta");
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

		List work_list = new List(shell, SWT.BORDER);
		work_list.setBounds(5, 72, 160, 115);

		List items_list = new List(shell, SWT.BORDER);
		items_list.setBounds(219, 152, 172, 117);

		Combo job_dropdown = new Combo(shell, SWT.NONE);
		job_dropdown.setBounds(5, 26, 201, 23);
		db.connect();
		job_list = db.getJobs();
		job_dropdown.add("");
		for (int i = 0; i < job_list.size(); i++) {
			job_dropdown.add(job_list.get(i));
		}

		Label resultLabel = new Label(shell, SWT.NONE);
		resultLabel.setBounds(93, 292, 235, 15);

		Button finishJob_btn = new Button(shell, SWT.NONE);
		finishJob_btn.setBounds(249, 26, 106, 25);
		finishJob_btn.setToolTipText(
				"Aseta kohde valmiiksi ja lis\u00E4\u00E4 lasku.");
		finishJob_btn.setText("Aseta valmiiksi");
		finishJob_btn.setEnabled(false);

		Button deleteJob_btn = new Button(shell, SWT.NONE);
		deleteJob_btn.setBounds(249, 88, 107, 25);
		deleteJob_btn.setToolTipText("Poista kohde tietokannasta.");
		deleteJob_btn.setText("Poista kohde");
		deleteJob_btn.setEnabled(false);

		Button getPrice_btn = new Button(shell, SWT.NONE);
		getPrice_btn.setBounds(249, 57, 106, 25);
		getPrice_btn.setToolTipText("N\u00E4yt\u00E4 kohteen kokonaishinta");
		getPrice_btn.setText("Kustannukset");
		getPrice_btn.setEnabled(false);

		Label lblAnnaAlennustaTunneista = new Label(shell, SWT.NONE);
		lblAnnaAlennustaTunneista.setBounds(4, 55, 114, 15);
		lblAnnaAlennustaTunneista.setText("Suoritukset kohteessa");

		Label lblAnnaAlennusta = new Label(shell, SWT.NONE);
		lblAnnaAlennusta.setBounds(0, 193, 165, 15);
		lblAnnaAlennusta.setText("Anna alennusta tuntityyppiin");

		Combo workType_dropdown = new Combo(shell, SWT.NONE);
		workType_dropdown.setItems(
				new String[]{"", "Ty\u00F6", "Suunnittelu", "Aputy\u00F6"});
		workType_dropdown.setBounds(5, 214, 132, 23);

		Spinner taskDiscount_spinner = new Spinner(shell, SWT.BORDER);
		taskDiscount_spinner.setBounds(5, 259, 47, 22);
		taskDiscount_spinner.setToolTipText("%");

		Button addDiscount_btn = new Button(shell, SWT.NONE);
		addDiscount_btn.setBounds(5, 287, 82, 25);
		addDiscount_btn.setText("Lis\u00E4\u00E4 alennus");
		addDiscount_btn.setEnabled(false);

		Button close_btn = new Button(shell, SWT.NONE);
		close_btn.setBounds(341, 287, 75, 25);
		close_btn.setText("Sulje");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(5, 243, 90, 15);
		lblNewLabel.setText("Alennusprosentti");
		
		Label lblKohteeseenKytetytTarvikkeet = new Label(shell, SWT.NONE);
		lblKohteeseenKytetytTarvikkeet.setBounds(219, 131, 182, 15);
		lblKohteeseenKytetytTarvikkeet.setText("Kohteeseen k\u00E4ytetyt tarvikkeet");

		/*
		 * Click listener methods
		 * 
		 */
		// Listens for changes in work type dropdown-list and sets discount
		// button enabled or not..
		workType_dropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (!workType_dropdown.getText().equals("")
						&& taskDiscount_spinner.getSelection() > 0) {
					addDiscount_btn.setEnabled(true);
				} else {
					addDiscount_btn.setEnabled(false);
				}
			}
		});
		// Changes the enabled value of discount button
		taskDiscount_spinner.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (!workType_dropdown.getText().equals("")
						&& taskDiscount_spinner.getSelection() > 0) {
					addDiscount_btn.setEnabled(true);
				} else {
					addDiscount_btn.setEnabled(false);
				}
			}
		});
		// Gets the tasks and items for selected job.
		job_dropdown.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				db.connect();
				task_list = db.getTaskHours(job_dropdown.getText());
				work_list.removeAll();
				for (int i = 0; i < task_list.size(); i++) {
					work_list.add(task_list.get(i) + " e");
				}
				db.connect();
				itemList = db.getJobItems(job_dropdown.getText(), false);

				items_list.removeAll();
				for (String[] itemObj : itemList) {
					items_list.add(itemObj[0] + " " + itemObj[3] + "e");
				}

				if (job_dropdown.getText().equals("")) {
					deleteJob_btn.setEnabled(false);
					getPrice_btn.setEnabled(false);
					finishJob_btn.setEnabled(false);
				} else {
					deleteJob_btn.setEnabled(true);
					getPrice_btn.setEnabled(true);
					finishJob_btn.setEnabled(true);
				}
			}
		});
		// Closes the dialog window.
		close_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();
			}
		});
		// Adds a discount to hour type.
		addDiscount_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				db.connect();
				db.addDiscount(job_dropdown.getText(),
						workType_dropdown.getText(),
						taskDiscount_spinner.getSelection());
				db.connect();
				task_list = db.getTaskHours(job_dropdown.getText());
				work_list.removeAll();
				for (int i = 0; i < task_list.size(); i++) {
					work_list.add(task_list.get(i));
				}
			}
		});
		// Spawns price dialog window to show total price.
		getPrice_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				// Get list of items and tasks for the specific job
				PriceDialog price = new PriceDialog(shell, style, itemList,
						task_list);
				price.open();
			}
		});
		// Deletes the selected from database completely.
		deleteJob_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				db.connect();
				boolean deleted = db.deleteJob(job_dropdown.getText());
				resultLabel.setText((deleted)
						? "Työ poistettu."
						: "Virhe työn poistamisessa.");

				// Remove everything about the deleted job.
				work_list.removeAll();
				items_list.removeAll();
				job_dropdown.remove(job_dropdown.getText());
				job_dropdown.select(0);
			}
		});
		// Sets the job finished and creates invoice.
		finishJob_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				db.connect();
				db.setJobFinished(job_dropdown.getText());
				resultLabel
						.setText("Työ asetettu valmistuneeksi ja lasku luotu.");
			}
		});

	}
}
