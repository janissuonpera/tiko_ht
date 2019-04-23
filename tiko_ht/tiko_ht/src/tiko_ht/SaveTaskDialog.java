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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;

public class SaveTaskDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	public List<String[]> jobs = new ArrayList<String[]>();
	// Contains user selected items
	// public List<String> selected_items = new ArrayList<String>();

	// Contains all items from database and their price.
	public List<String[]> itemList = new ArrayList<String[]>();

	public List<String[]> selected_items = new ArrayList<String[]>();
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public SaveTaskDialog(Shell parent, int style) {
		super(parent, style);
		setText("Tallenna ty\u00F6suoritus");
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
		shell.setLayout(null);

		Label queryResultLabel = new Label(shell, SWT.NONE);
		queryResultLabel.setBounds(259, 189, 178, 15);

		Label lblValitseKohde = new Label(shell, SWT.NONE);
		lblValitseKohde.setBounds(20, 9, 74, 15);
		lblValitseKohde.setText("Valitse kohde*");

		Combo job_dropdown = new Combo(shell, SWT.READ_ONLY);
		job_dropdown.setBounds(115, 5, 234, 23);
		
		// Populate the job dropdown list.
		jobs = db.getJobs(true);
		job_dropdown.add("");
		for(String [] job : jobs) {
			job_dropdown.add(job[0]);
		}

		Label lblAnnaPivmr = new Label(shell, SWT.NONE);
		lblAnnaPivmr.setBounds(9, 37, 97, 15);
		lblAnnaPivmr.setText("Anna p\u00E4iv\u00E4m\u00E4\u00E4r\u00E4*");

		DateTime date_time = new DateTime(shell, SWT.BORDER);
		date_time.setBounds(115, 33, 76, 24);

		Label lblAnnaTehdytTunnit = new Label(shell, SWT.NONE);
		lblAnnaTehdytTunnit.setBounds(5, 65, 105, 15);
		lblAnnaTehdytTunnit.setText("Anna tehdyt tunnit*");

		Spinner hour_spinner = new Spinner(shell, SWT.BORDER);
		hour_spinner.setBounds(115, 62, 47, 22);

		Label lblAnnaTynTyyppy = new Label(shell, SWT.NONE);
		lblAnnaTynTyyppy.setBounds(9, 93, 96, 15);
		lblAnnaTynTyyppy.setText("Anna ty\u00F6n tyyppi*");

		Combo worktype_dropdown = new Combo(shell, SWT.READ_ONLY);
		worktype_dropdown.setBounds(115, 89, 88, 23);
		worktype_dropdown.setItems(
				new String[]{"", "Ty\u00F6", "Suunnittelu", "Aputy\u00F6"});

		Label lblLisTarvike = new Label(shell, SWT.NONE);
		lblLisTarvike.setBounds(22, 127, 71, 15);
		lblLisTarvike.setText("Valitse tarvike");

		Combo item_dropdown = new Combo(shell, SWT.READ_ONLY);
		item_dropdown.setBounds(115, 123, 88, 23);
		
		// Populate the item dropdown list.
		item_dropdown.add("");
		itemList = db.getAllItems();
		for(String [] item : itemList) {
			item_dropdown.add(item[0]);
		}

		Button selectedItems_btn = new Button(shell, SWT.NONE);
		selectedItems_btn.setBounds(274, 121, 75, 26);
		selectedItems_btn.setText("Tarvikelista");

		Label lblMr = new Label(shell, SWT.NONE);
		lblMr.setBounds(41, 160, 33, 15);
		lblMr.setText("M\u00E4\u00E4r\u00E4");

		Spinner item_amount_spinner = new Spinner(shell, SWT.BORDER);
		item_amount_spinner.setBounds(115, 157, 87, 22);
		item_amount_spinner.setMaximum(9999);
		item_amount_spinner.setMinimum(1);

		Label lblAlennus = new Label(shell, SWT.NONE);
		lblAlennus.setBounds(208, 160, 61, 15);
		lblAlennus.setText("Alennus(%)");

		Spinner discount_spinner = new Spinner(shell, SWT.BORDER);
		discount_spinner.setBounds(274, 157, 47, 22);

		// Button for adding an item.
		Button addItem_btn = new Button(shell, SWT.NONE);
		addItem_btn.setBounds(115, 184, 76, 25);
		addItem_btn.setEnabled(false);

		addItem_btn.setText("Lis\u00E4\u00E4 tarvike");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(5, 219, 97, 15);
		lblNewLabel.setText("* Tarvittavat tiedot");

		Button cancel_btn = new Button(shell, SWT.NONE);
		cancel_btn.setBounds(296, 214, 53, 25);
		cancel_btn.setText("Sulje");
		cancel_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				shell.dispose();
			}
		});

		// Save data and add task.
		Button done_btn = new Button(shell, SWT.NONE);
		done_btn.setBounds(354, 214, 83, 25);
		done_btn.setText("Lis\u00E4\u00E4 suoritus");
		done_btn.setEnabled(false);

		// If job hasn't been selected, disable done button.
		job_dropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (job_dropdown.getText().equals("")) {
					done_btn.setEnabled(false);
				} else {
					done_btn.setEnabled(true);
				}
				queryResultLabel.setText("");
			}
		});

		// Listener for item dropdown menu. Sets the add item button disabled or
		// enabled.
		item_dropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (item_dropdown.getText().equals("")) {
					addItem_btn.setEnabled(false);
				} else {
					addItem_btn.setEnabled(true);
				}
			}
		});

		// Button listener to add an item to the task.
		addItem_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Add selected items into a list.
				String[] item = {item_dropdown.getText(),
						String.valueOf(item_amount_spinner.getSelection()),
						String.valueOf(discount_spinner.getSelection())};
				selected_items.add(item);
				item_dropdown.select(0);
				item_amount_spinner.setSelection(0);
				discount_spinner.setSelection(0);
			}
		});
		// Button listener for showing the selected items.
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
		// Saves inserted information into db.
		done_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				// Validate the selections
				if ((hour_spinner.getSelection() > 0
						&& !worktype_dropdown.getText().equals(""))
						|| item_amount_spinner.getSelection() > 0
								&& !item_dropdown.getText().equals("")
								&& !selected_items.isEmpty()) {
					
					// Create a new task with the selected information.
					db.createTask(job_dropdown.getText(),
							worktype_dropdown.getText(),
							hour_spinner.getSelection(), date_time,
							selected_items);
					clearLists();
					job_dropdown.select(0);
					item_dropdown.select(0);
					worktype_dropdown.select(0);
					queryResultLabel.setText("Lis\u00E4ys onnistui!");
				} else {
					queryResultLabel.setText("Virhe annetuissa tiedoissa!");
				}
			}
		});
	}
	public void clearLists() {
		selected_items.clear();
	}
}
