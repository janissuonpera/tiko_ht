package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;


public class SaveTaskDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	public List<String> jobs = new ArrayList<String>();
	// Contains user selected items
	public List<String> selected_items = new ArrayList<String>();
	// Contains user selected item count.
	public List<Integer> item_amount = new ArrayList<Integer>();
	// Contains user selected discount percentages for each item.
	public List<Integer> discount_pct = new ArrayList<Integer>();
	// Contains all items from database and their price.
	public List<List<String>> itemList = new ArrayList<List<String>>();
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public SaveTaskDialog(Shell parent, int style) {
		super(parent, style);
		setText("Tallenna työsuoritus");
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
		// Initialize database handler.
		DBHandler db = new DBHandler();
		
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(448, 272);
		shell.setText(getText());
		shell.setLayout(new GridLayout(5, false));

		Label lblValitseKohde = new Label(shell, SWT.NONE);
		lblValitseKohde.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblValitseKohde.setText("Valitse kohde*");

		Combo job_dropdown = new Combo(shell, SWT.READ_ONLY);
		db.connect();
		jobs = db.getJobs();
		for (int i = 0; i < jobs.size(); i++) {
			job_dropdown.add(jobs.get(i));
		}

		GridData gd_job_dropdown = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1);
		gd_job_dropdown.widthHint = 195;
		job_dropdown.setLayoutData(gd_job_dropdown);
		new Label(shell, SWT.NONE);

		Label lblAnnaPivmr = new Label(shell, SWT.NONE);
		lblAnnaPivmr.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAnnaPivmr.setText("Anna p\u00E4iv\u00E4m\u00E4\u00E4r\u00E4*");

		DateTime date_time = new DateTime(shell, SWT.BORDER);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblAnnaTehdytTunnit = new Label(shell, SWT.NONE);
		lblAnnaTehdytTunnit.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAnnaTehdytTunnit.setText("Anna tehdyt tunnit*");

		Spinner hour_spinner = new Spinner(shell, SWT.BORDER);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblAnnaTynTyyppy = new Label(shell, SWT.NONE);
		lblAnnaTynTyyppy.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAnnaTynTyyppy.setText("Anna ty\u00F6n tyyppi*");

		Combo worktype_dropdown = new Combo(shell, SWT.READ_ONLY);
		worktype_dropdown.setItems(
				new String[]{"Ty\u00F6", "Suunnittelu", "Aputy\u00F6"});
		GridData gd_worktype_dropdown = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_worktype_dropdown.widthHint = 177;
		worktype_dropdown.setLayoutData(gd_worktype_dropdown);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblLisTarvike = new Label(shell, SWT.NONE);
		lblLisTarvike.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
		lblLisTarvike.setText("Valitse tarvike");

		Combo item_dropdown = new Combo(shell, SWT.READ_ONLY);
		
		GridData gd_item_dropdown = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 2);
		gd_item_dropdown.widthHint = 136;
		item_dropdown.setLayoutData(gd_item_dropdown);
		new Label(shell, SWT.NONE);
		db.connect();
		itemList = db.getAllItems();
		for (int i = 0; i < itemList.get(0).size(); i++) {
			item_dropdown.add(itemList.get(0).get(i));
		}
		

		Button selectedItems_btn = new Button(shell, SWT.NONE);

		GridData gd_selectedItems_btn = new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 2);
		gd_selectedItems_btn.heightHint = 26;
		gd_selectedItems_btn.widthHint = 75;

		selectedItems_btn.setLayoutData(gd_selectedItems_btn);
		selectedItems_btn.setText("Tarvikelista");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		selectedItems_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				ItemListPopup popup = new ItemListPopup(shell, SWT.NONE);
				popup.open(selected_items);

			}
		});

		Label lblMr = new Label(shell, SWT.NONE);
		lblMr.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblMr.setText("M\u00E4\u00E4r\u00E4");

		Spinner item_amount_spinner = new Spinner(shell, SWT.BORDER);
		GridData gd_item_amount_spinner = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_item_amount_spinner.widthHint = 58;
		item_amount_spinner.setLayoutData(gd_item_amount_spinner);
		
		Label lblAlennus = new Label(shell, SWT.NONE);
		lblAlennus.setText("Alennus(%)");
		
		Spinner discount_spinner = new Spinner(shell, SWT.BORDER);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		// Button for adding an item.
		Button addItem_btn = new Button(shell, SWT.NONE);

		// Add item button listener.
		addItem_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Add selected items into a list.
				selected_items.add(item_dropdown.getText());
				item_amount.add(item_amount_spinner.getSelection());
				discount_pct.add(discount_spinner.getSelection());
				item_dropdown.select(0);
				item_amount_spinner.setSelection(0);
				discount_spinner.setSelection(0);
			}
		});
		addItem_btn.setText("Lis\u00E4\u00E4 tarvike");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("* Tarvittavat tiedot");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Button cancel_btn = new Button(shell, SWT.NONE);
		cancel_btn.setLayoutData(
				new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		cancel_btn.setText("Peruuta");
		cancel_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				shell.dispose();
			}
		});

		// Save data and add task.
		Button done_btn = new Button(shell, SWT.NONE);
		done_btn.setText("Lis\u00E4\u00E4 suoritus");
		// Saves inserted information into db.
		done_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				System.out.println("Saving task");
				db.connect();
				db.createTask(job_dropdown.getText(),
						worktype_dropdown.getText(),
						hour_spinner.getSelection(), date_time, selected_items,
						item_amount,discount_pct);
			}
		});

	}

}
