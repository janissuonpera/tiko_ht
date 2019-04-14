package tiko_ht;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PriceEstimate extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text ItemTextField;
	private Text PriceEstimateTextField;
	private final int REGULAR_WORK = 45;
	private final int PLANNING_WORK = 55;
	private final int HELPING = 35;
	private double totalPrice = 0;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public PriceEstimate(Shell parent, int style) {
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
		db.connect();
		List<String> items = new ArrayList<String>();

		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setSize(565, 405);
		shell.setText(getText());
		shell.setLayout(new GridLayout(4, false));

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("Ty\u00F6tunnit");

		Combo WorkType = new Combo(shell, SWT.READ_ONLY);
		GridData gd_WorkType = new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1);
		gd_WorkType.widthHint = -6;
		WorkType.setLayoutData(gd_WorkType);
		WorkType.setItems(
				new String[]{"", "Ty\u00F6", "Suunnittelu", "Aputy\u00F6"});

		Spinner WorkHoursSpinner = new Spinner(shell, SWT.BORDER);
		WorkHoursSpinner.setMaximum(99999);
		GridData gd_WorkHoursSpinner = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		gd_WorkHoursSpinner.widthHint = 38;
		WorkHoursSpinner.setLayoutData(gd_WorkHoursSpinner);

		Button addWorkHours_btn = new Button(shell, SWT.NONE);
		addWorkHours_btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		addWorkHours_btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (WorkHoursSpinner.getSelection() > 0 && !WorkType.getText().equals("")) {
					int worktype = 0;
					String itemString = WorkType.getText() + " "
							+ WorkHoursSpinner.getSelection() + "h\n";
					if (WorkType.getText().equals("Työ")) {
						worktype = REGULAR_WORK;
					} else if (WorkType.getText().equals("Suunnittelu")) {
						worktype = PLANNING_WORK;
					} else {
						worktype = HELPING;
					}
					double price = worktype * WorkHoursSpinner.getSelection();
					
					ItemTextField.append(itemString);
					totalPrice = totalPrice + price;
					PriceEstimateTextField.setText(totalPrice + "€");
					WorkHoursSpinner.setSelection(0);
					WorkType.select(0);
				}
			}
		});
		addWorkHours_btn.setText("Lis\u00E4\u00E4 ty\u00F6tunnit");

		Label lblTarvike = new Label(shell, SWT.NONE);
		lblTarvike.setText("Tarvike");

		Combo ItemName_dropdown = new Combo(shell, SWT.READ_ONLY);
		GridData gd_ItemName_dropdown = new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1);
		gd_ItemName_dropdown.widthHint = -6;
		ItemName_dropdown.setLayoutData(gd_ItemName_dropdown);
		items = db.getAllItems().get(0);
		ItemName_dropdown.add("");
		for (String i : items) {
			ItemName_dropdown.add(i);
		}

		Spinner ItemAmountSpinner = new Spinner(shell, SWT.BORDER);
		ItemAmountSpinner.setMaximum(99999);
		ItemAmountSpinner.setToolTipText("Kpl");
		GridData gd_ItemAmountSpinner = new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1);
		gd_ItemAmountSpinner.widthHint = 38;
		ItemAmountSpinner.setLayoutData(gd_ItemAmountSpinner);

		Button addItem_btn = new Button(shell, SWT.NONE);
		GridData gd_addItem_btn = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_addItem_btn.widthHint = 91;
		addItem_btn.setLayoutData(gd_addItem_btn);
		addItem_btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (ItemAmountSpinner.getSelection() > 0 && !ItemName_dropdown.getText().equals("")) {
					try {
						DBHandler db = new DBHandler();
						db.connect();
						String itemString = ItemName_dropdown.getText() + " "
								+ ItemAmountSpinner.getSelection() + " kpl\n";
						ItemTextField.append(itemString);

						List<List<String>> items = db.getAllItems();
						List<String> itemList1 = items.get(0);
						List<String> itemList2 = items.get(1);
						double itemPrice = 0;

						for (int i = 0; i < itemList1.size(); i++) {
							if (itemList1.get(i).equals(ItemName_dropdown.getText())) {
								itemPrice = Double
										.parseDouble(itemList2.get(i));
								System.out.println(itemPrice);

								i = itemList1.size();
							}
						}

						totalPrice = totalPrice + (itemPrice
								* ItemAmountSpinner.getSelection());
						PriceEstimateTextField.setText(totalPrice + "€");

					} catch (Exception e2) {
						System.out.println("Failed to add an Item");
						System.out.println(e2);
					}
					ItemAmountSpinner.setSelection(0);
					ItemName_dropdown.select(0);
					
				}
			}
		});
		addItem_btn.setText("Lis\u00E4\u00E4 tarvike");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		ItemTextField = new Text(shell,
				SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		GridData gd_ItemTextField = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_ItemTextField.heightHint = 275;
		ItemTextField.setLayoutData(gd_ItemTextField);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblHintaArvio = new Label(shell, SWT.NONE);
		lblHintaArvio.setLayoutData(
				new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHintaArvio.setText("Hinta arvio");

		PriceEstimateTextField = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		PriceEstimateTextField.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}
}
