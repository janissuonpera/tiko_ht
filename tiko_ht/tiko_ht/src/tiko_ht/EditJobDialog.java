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
import org.eclipse.wb.swt.SWTResourceManager;

/*
 * Class for creating a graphical dialog for editing a job and viewing information about it.
 * Allows the user to finish the job which creates an invoice as well and delete the job from the database.
 */
public class EditJobDialog extends Dialog {

	//Attributes
	protected Object result;
	protected Shell shell;
	protected int style;
	String jobType = "";
	// Contains items and their amount.
	java.util.List<String[]> itemList = new ArrayList<String[]>();
	// Job list containing all jobs.
	java.util.List<String[]> job_list = new ArrayList<String[]>();
	// Task list containing all task for the selected job.
	java.util.List<String> task_list = new ArrayList<String>();

	//Constructor
	public EditJobDialog(Shell parent, int style) {
		super(parent, style);
		setText("Muokkaa ty\u00F6kohdetta");
	}

	//Open the dialog
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
	
	//Create the contents of the dialog
	private void createContents() {
		DBHandler db = new DBHandler();
		
		//===========================GUI ELEMENTS START HERE======================================
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(488, 377);
		shell.setText(getText());		
		shell.setLayout(null);

		Label lblValitseTykohde = new Label(shell, SWT.NONE);
		lblValitseTykohde.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblValitseTykohde.setBounds(5, 5, 113, 15);
		lblValitseTykohde.setText("Valitse ty\u00F6kohde");

		Label lblTykohteenValinnat = new Label(shell, SWT.NONE);
		lblTykohteenValinnat.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblTykohteenValinnat.setBounds(284, 5, 107, 15);
		lblTykohteenValinnat.setText("Ty\u00F6kohteen valinnat");

		List work_list = new List(shell,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		work_list.setBounds(5, 72, 160, 115);

		List items_list = new List(shell,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		items_list.setBounds(253, 169, 172, 117);

		Combo job_dropdown = new Combo(shell, SWT.NONE);
		job_dropdown.setBounds(5, 26, 201, 23);

		//Get all the jobs from the database and add them to the job dropdown list
		job_list = db.getJobs(false, false);
		job_dropdown.add("");
		for (String[] job : job_list) {
			job_dropdown.add(job[0]);
		}

		Label resultLabel = new Label(shell, SWT.NONE);
		resultLabel.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		resultLabel.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		resultLabel.setBounds(96, 318, 256, 20);

		Button finishJob_btn = new Button(shell, SWT.NONE);
		finishJob_btn.setBounds(285, 24, 106, 25);
		finishJob_btn.setToolTipText(
				"Aseta kohde valmiiksi ja lis\u00E4\u00E4 lasku.");
		finishJob_btn.setText("Aseta valmiiksi");
		finishJob_btn.setEnabled(false);

		Button deleteJob_btn = new Button(shell, SWT.NONE);
		deleteJob_btn.setBounds(284, 117, 107, 25);
		deleteJob_btn.setToolTipText("Poista kohde tietokannasta.");
		deleteJob_btn.setText("Poista kohde");
		deleteJob_btn.setEnabled(false);

		Button getPrice_btn = new Button(shell, SWT.NONE);
		getPrice_btn.setBounds(285, 55, 106, 25);
		getPrice_btn.setToolTipText("N\u00E4yt\u00E4 kohteen kokonaishinta");
		getPrice_btn.setText("Kustannukset");
		getPrice_btn.setEnabled(false);

		Label lblAnnaAlennustaTunneista = new Label(shell, SWT.NONE);
		lblAnnaAlennustaTunneista.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblAnnaAlennustaTunneista.setBounds(4, 55, 114, 15);
		lblAnnaAlennustaTunneista.setText("Suoritukset kohteessa");

		Label lblAnnaAlennusta = new Label(shell, SWT.NONE);
		lblAnnaAlennusta.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
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
		close_btn.setBounds(397, 313, 75, 25);
		close_btn.setText("Sulje");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel.setBounds(5, 243, 90, 15);
		lblNewLabel.setText("Alennusprosentti");

		Label lblKohteeseenKytetytTarvikkeet = new Label(shell, SWT.NONE);
		lblKohteeseenKytetytTarvikkeet.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblKohteeseenKytetytTarvikkeet.setBounds(253, 148, 182, 15);
		lblKohteeseenKytetytTarvikkeet
				.setText("Kohteeseen k\u00E4ytetyt tarvikkeet");

		Label lblKohteenTyyppi = new Label(shell, SWT.BORDER);
		lblKohteenTyyppi.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblKohteenTyyppi.setBounds(173, 55, 82, 25);
		lblKohteenTyyppi.setText("Kohteen tyyppi");

		Label jobtype_label = new Label(shell, SWT.NONE);
		jobtype_label.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		jobtype_label.setBounds(171, 89, 55, 15);

		Button information_btn = new Button(shell, SWT.NONE);
		information_btn.setEnabled(false);
		information_btn.setBounds(284, 86, 107, 25);
		information_btn.setText("Tiedot");

		Label lblEiSisllAlv = new Label(shell, SWT.NONE);
		lblEiSisllAlv.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblEiSisllAlv.setBounds(253, 292, 82, 15);
		lblEiSisllAlv.setText("Ei sisällä alv.");
		//=============================GUI ELEMENTS END HERE========================================
		
		/*
		 * 
		 * 
		 * Click listener methods
		 * 
		 */

		// Listens for changes in work type dropdownlist and
		// sets discount button enabled or disabled..
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
				jobType = "";
				// Check that the selection is not the first null value.
				if (job_dropdown.getSelectionIndex() > 0) {
					// Check if the job is a contract or not.
					boolean contractBool = Boolean.valueOf(job_list
							.get(job_dropdown.getSelectionIndex() - 1)[1]);
					// Rename the label depending of the contract bool value.
					if (contractBool) {
						jobType = "Urakka";
					} else {
						jobType = "Tuntityö";
					}
					// Get task hours
					task_list = db.getTaskHours(job_dropdown.getText());
					work_list.removeAll();
					for (int i = 0; i < task_list.size(); i++) {
						work_list.add(task_list.get(i) + " e");
					}

					if (jobType.equals("Tuntityö")) {
						itemList = db.getJobItems(job_dropdown.getText(), false,
								true);
					} else if (jobType.equals("Urakka")) {
						itemList = db.getContractItems(job_dropdown.getText(),
								true);
					}
					// Clear the items list of previous items.
					items_list.removeAll();
					for (String[] item : itemList) {
						items_list.add(item[0] + " | " + item[1] + item[2]
								+ " | " + item[3] + "e");
					}
				}

				jobtype_label.setText(jobType);
				// Disable buttons if job selections is the empty character.
				if (job_dropdown.getText().equals("")) {
					deleteJob_btn.setEnabled(false);
					getPrice_btn.setEnabled(false);
					finishJob_btn.setEnabled(false);
					information_btn.setEnabled(false);
				} else {
					deleteJob_btn.setEnabled(true);
					getPrice_btn.setEnabled(true);
					information_btn.setEnabled(true);
					// Check if job is finished or not, and disable button
					// accordingly
					if (db.getFinishedValue(job_dropdown.getText())) {
						finishJob_btn.setEnabled(false);
					} else {
						finishJob_btn.setEnabled(true);
					}
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

				db.addDiscount(job_dropdown.getText(),
						workType_dropdown.getText(),
						taskDiscount_spinner.getSelection());

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

		information_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				JobInformationDialog dialog = new JobInformationDialog(shell,
						style, job_dropdown.getText());
				dialog.open();
			}
		});

		// Deletes the selected job from database completely.
		deleteJob_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

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
				db.setJobFinished(job_dropdown.getText());
				resultLabel
						.setText("Työ asetettu valmistuneeksi ja lasku luotu.");
				finishJob_btn.setEnabled(false);
			}
		});

	}
}
