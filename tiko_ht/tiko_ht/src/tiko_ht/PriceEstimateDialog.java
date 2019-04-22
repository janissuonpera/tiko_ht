package tiko_ht;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class PriceEstimateDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text ItemTextField;
	private Text PriceEstimateTextField;
	private final int REGULAR_WORK = 45;
	private final int PLANNING_WORK = 55;
	private final int HELPING = 35;
	private double totalPrice = 0;
	// Contains all items, their price and their units in the database.
	List<String[]> items = new ArrayList<String[]>();
	// Contains the user selected items and their amounts.
	List<String[]> selected_items = new ArrayList<String[]>();
	// Contains the user selected hours, types and total price.
	List<String[]> selected_hours = new ArrayList<String[]>();
	// Contains contract offers.
	List<String> contractOffers = new ArrayList<String>();

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public PriceEstimateDialog(Shell parent, int style) {
		super(parent, style);
		setText("Hinta-arvio");
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
		DBHandler db = new DBHandler();

		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setSize(565, 405);
		shell.setText(getText());
		shell.setLayout(null);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(5, 10, 51, 15);
		lblNewLabel.setText("Ty\u00F6tunnit");

		Combo workType_dropdown = new Combo(shell, SWT.READ_ONLY);
		workType_dropdown.setBounds(61, 5, 171, 23);
		workType_dropdown.setItems(
				new String[]{"", "Ty\u00F6", "Suunnittelu", "Aputy\u00F6"});

		Spinner workHours_spinner = new Spinner(shell, SWT.BORDER);
		workHours_spinner.setBounds(237, 5, 67, 25);
		workHours_spinner.setMaximum(99999);

		Button addWorkHours_btn = new Button(shell, SWT.NONE);
		addWorkHours_btn.setBounds(309, 5, 90, 25);

		addWorkHours_btn.setText("Lis\u00E4\u00E4 ty\u00F6tunnit");

		Label lblTarvike = new Label(shell, SWT.NONE);
		lblTarvike.setBounds(5, 40, 37, 15);
		lblTarvike.setText("Tarvike");

		Combo ItemName_dropdown = new Combo(shell, SWT.READ_ONLY);
		ItemName_dropdown.setBounds(61, 35, 171, 23);
		
		items = db.getAllItems();
		ItemName_dropdown.add("");
		for (String[] item : items) {
			ItemName_dropdown.add(item[0]);
		}

		Spinner itemAmount_spinner = new Spinner(shell, SWT.BORDER);
		itemAmount_spinner.setBounds(237, 36, 67, 22);
		itemAmount_spinner.setMaximum(99999);
		itemAmount_spinner.setToolTipText("Kpl");

		Button addItem_btn = new Button(shell, SWT.NONE);
		addItem_btn.setBounds(309, 35, 91, 25);

		addItem_btn.setText("Lis\u00E4\u00E4 tarvike");

		ItemTextField = new Text(shell,
				SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		ItemTextField.setBounds(309, 65, 245, 281);

		Label lblHintaArvio = new Label(shell, SWT.NONE);
		lblHintaArvio.setBounds(246, 354, 58, 15);
		lblHintaArvio.setText("Hinta arvio");

		PriceEstimateTextField = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		PriceEstimateTextField.setBounds(309, 351, 245, 21);

		Button close_btn = new Button(shell, SWT.NONE);
		close_btn.setBounds(10, 344, 75, 25);
		close_btn.setText("Sulje");

		Button addToContract_btn = new Button(shell, SWT.NONE);

		addToContract_btn.setBounds(91, 344, 98, 25);
		addToContract_btn.setText("Lis\u00E4\u00E4 urakkaan");

		Combo contracts_dropdown = new Combo(shell, SWT.NONE);
		contracts_dropdown.setToolTipText("Listassa n\u00E4kyy vain urakat, joihin ei ole viel\u00E4 lis\u00E4tty tarvikkeita tai tunteja.");
		contracts_dropdown.setBounds(10, 300, 179, 23);
		// Get the contract offers into a list.
		
		contractOffers = db.getContractOffers();
		for (String contract : contractOffers) {
			contracts_dropdown.add(contract);
		}

		Label lblUrakat = new Label(shell, SWT.NONE);
		lblUrakat.setBounds(10, 275, 55, 15);
		lblUrakat.setText("Urakat");

		Label lblLisListanSislt = new Label(shell, SWT.BORDER);
		lblLisListanSislt.setBounds(10, 244, 160, 15);
		lblLisListanSislt
				.setText("Lis\u00E4\u00E4 listan sis\u00E4lt\u00F6 urakkaan");
		
		Label result_label = new Label(shell, SWT.NONE);
		result_label.setBounds(10, 179, 232, 15);
		
		
		// Adds work hours into the list.
		addWorkHours_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Validate selections
				if (workHours_spinner.getSelection() > 0
						&& !workType_dropdown.getText().equals("")) {
				
					int worktype = 0;
					String hoursAndType = workType_dropdown.getText() + " "
							+ workHours_spinner.getSelection() + "h\n";
					
					if (workType_dropdown.getText().equals("Ty\u00F6")) {
						worktype = REGULAR_WORK;
					} else if (workType_dropdown.getText()
							.equals("Suunnittelu")) {
						worktype = PLANNING_WORK;
					} else {
						worktype = HELPING;
					}
					double price = worktype * workHours_spinner.getSelection();
					// Save selected values into an array.
					String [] hours = {workType_dropdown.getText(),String.valueOf(workHours_spinner.getSelection()),String.valueOf(price)};
					selected_hours.add(hours);
					ItemTextField.append(hoursAndType);
					totalPrice = totalPrice + price;
					PriceEstimateTextField.setText(totalPrice + "€");
					workHours_spinner.setSelection(0);
					workType_dropdown.select(0);
				}
			}
		});
		// Adds an item to the list.
		addItem_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Check if the selections are valid.
				if (itemAmount_spinner.getSelection() > 0
						&& !ItemName_dropdown.getText().equals("")) {
					try {
						String unit = "";
						// Find the corresponding unit for the item.
						for(String [] item : items ) {
							if(item[0].equals(ItemName_dropdown.getText())) {
								unit = item[2];
							}
						}

						String itemInfo = ItemName_dropdown.getText() + " "
								+ itemAmount_spinner.getSelection() + unit + "\n";
						// Insert the item and the amount into the list.
						ItemTextField.append(itemInfo);
						double itemPrice = 0;

						for (int i = 0; i < items.size(); i++) {
							if (items.get(i)[0]
									.equals(ItemName_dropdown.getText())) {
								itemPrice = Double.parseDouble(items.get(i)[1]);
								// Checks which tax to use
								if(Boolean.valueOf(items.get(i)[3])) { 
	                                   itemPrice=itemPrice*1.10;
	                                }else {
	                                   itemPrice=itemPrice*1.24;
	                                }
								// End the loop.
								i = items.size();
							}
						}
						String[] item = {ItemName_dropdown.getText(), String
								.valueOf(itemAmount_spinner.getSelection())};
						selected_items.add(item);

						totalPrice = totalPrice + (itemPrice
								* itemAmount_spinner.getSelection());
						PriceEstimateTextField.setText(totalPrice + "€");

					} catch (Exception ex) {
						ex.printStackTrace();
					}
					itemAmount_spinner.setSelection(0);
					ItemName_dropdown.select(0);
				}
			}
		});

		addToContract_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// If there is something to add and the contract selected to add
				// into, do it.
				if (!contracts_dropdown.getText().equals("")
						&& totalPrice > 0) {
					
					boolean result = db.addToContract(contracts_dropdown.getText(),selected_items,selected_hours);
					if(result) {
						shell.dispose();
					} else {
						result_label.setText("Lisäys epäonnistui");
					}
				} else {
					result_label.setText("Epäonnistui. Tarkista antamasi tiedot.");
				}
			}
		});

		close_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				shell.dispose();
			}
		});
	}
}
