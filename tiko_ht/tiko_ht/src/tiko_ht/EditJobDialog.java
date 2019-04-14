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
	java.util.List<java.util.List<String>> itemList = new ArrayList<java.util.List<String>>();
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
		
		List work_list = new List(shell, SWT.BORDER);
		work_list.setBounds(5, 72, 160, 115);
		
		List items_list = new List(shell, SWT.BORDER);
		items_list.setBounds(216, 148, 172, 117);	
		
		Combo job_dropdown = new Combo(shell, SWT.NONE);
		job_dropdown.setBounds(5, 26, 201, 23);
		db.connect();
		job_list = db.getJobs();
		for (int i = 0; i < job_list.size(); i++) {
			job_dropdown.add(job_list.get(i));
		}
		job_dropdown.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				db.connect();
				task_list = db.getTaskHours(job_dropdown.getText());
				work_list.removeAll();
				for (int i = 0; i < task_list.size(); i++) {
					work_list.add(task_list.get(i));
				}
				db.connect();
				itemList = db.getJobItems(job_dropdown.getText(), false);
				items_list.removeAll();
				for (int i = 0; i < itemList.get(0).size(); i++) {
					items_list.add(
							itemList.get(0).get(i) + itemList.get(1).get(i) + "");
				}
			}
			
		});

		Button finishJob_btn = new Button(shell, SWT.NONE);
		finishJob_btn.setBounds(249, 26, 106, 25);
		finishJob_btn.setToolTipText(
				"Aseta kohde valmiiksi ja lis\u00E4\u00E4 lasku.");
		finishJob_btn.setText("Aseta valmiiksi");
		finishJob_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				db.connect();
				db.setJobFinished(job_dropdown.getText());
			}
		});

		Button deleteJob_btn = new Button(shell, SWT.NONE);
		deleteJob_btn.setBounds(249, 88, 107, 25);
		deleteJob_btn.setToolTipText("Poista kohde tietokannasta.");
		deleteJob_btn.setText("Poista kohde");

		Button getPrice_btn = new Button(shell, SWT.NONE);
		getPrice_btn.setBounds(249, 57, 106, 25);
		getPrice_btn.setToolTipText("N\u00E4yt\u00E4 kohteen kokonaishinta");
		getPrice_btn.setText("Kustannukset");
		
		getPrice_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				//Get list of items and tasks for the specific job
				System.out.println(itemList);
				System.out.println(task_list);
				PriceDialog price = new PriceDialog(shell, style, itemList, task_list);
				price.open();
			}
		});

		Label lblAnnaAlennustaTunneista = new Label(shell, SWT.NONE);
		lblAnnaAlennustaTunneista.setBounds(4, 55, 114, 15);
		lblAnnaAlennustaTunneista.setText("Suoritukset kohteessa");


		Label lblAnnaAlennusta = new Label(shell, SWT.NONE);
		lblAnnaAlennusta.setBounds(0, 193, 165, 15);
		lblAnnaAlennusta.setText("Anna alennusta tuntityyppiin");

		Combo workType_dropdown = new Combo(shell, SWT.NONE);
		workType_dropdown.setItems(new String[] {"Ty\u00F6", "Suunnittelu", "Aputy\u00F6"});
		workType_dropdown.setBounds(5, 214, 132, 23);

		Spinner taskDiscount_spinner = new Spinner(shell, SWT.BORDER);
		taskDiscount_spinner.setBounds(5, 259, 47, 22);
		taskDiscount_spinner.setToolTipText("%");

		Button addDiscount_btn = new Button(shell, SWT.NONE);
		addDiscount_btn.setBounds(5, 287, 82, 25);
		addDiscount_btn.setText("Lis\u00E4\u00E4 alennus");
		addDiscount_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				db.connect();
				db.addDiscount(job_dropdown.getText(),workType_dropdown.getText(),taskDiscount_spinner.getSelection());
				db.connect();
				task_list = db.getTaskHours(job_dropdown.getText());
				work_list.removeAll();
				for (int i = 0; i < task_list.size(); i++) {
					work_list.add(task_list.get(i));
				}
			}
			
		});
		
		Button close_btn = new Button(shell, SWT.NONE);
		close_btn.setBounds(341, 287, 75, 25);
		close_btn.setText("Sulje");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(5, 243, 90, 15);
		lblNewLabel.setText("Alennusprosentti");
		close_btn.addListener(SWT.Selection,new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();
			}
			
		});
		
	}
}

//New class that handles current price. Sums the price of tasks and items and gives the user the price in a new dialog
class PriceDialog extends Dialog{
	protected Object result;
	protected Shell shell;
	protected int style;
	java.util.List<java.util.List<String>> itemList = null;
	java.util.List<String> task_list = null;
	
	/**
	 * Constructor.
	 * Dialog for the current price. Tasks + items = price. 
	 */
	public PriceDialog(Shell parent, int style, Object itemList, Object task_list) {
		super(parent, style);
		setText("Hinta");
		this.itemList = (java.util.List<java.util.List<String>>) itemList;
		this.task_list = (java.util.List<String>) task_list;
	}
	
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
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(350, 200);
		shell.setText(getText());
		DBHandler db = new DBHandler();
		shell.setLayout(null);
		
		Label tasks = new Label(shell, SWT.NONE);
		tasks.setBounds(5, 5, 65, 15);
		tasks.setText("Suoritukset:");
		List task_list = new List(shell, SWT.BORDER);
		task_list.setBounds(75, 5, 250, 150);
	}
}
