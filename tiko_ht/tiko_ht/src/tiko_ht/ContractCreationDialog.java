package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;

/*
 * Class for creating a graphical dialog for creating and adding a new contract
 *
 */
public class ContractCreationDialog extends Dialog {

	//Attributes
	protected Object result;
	protected Shell shell;
	List<String> contracts = new ArrayList<String>();

	//Constructor
	public ContractCreationDialog(Shell parent, int style) {
		super(parent, style);
		setText("Luo urakkatarjous");
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

	//Create contents of dialog
	private void createContents() {
		DBHandler db = new DBHandler();
		
		//===========================GUI ELEMENTS START HERE======================================
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(407, 172);
		shell.setText("Luo uusi urakka");
		shell.setLayout(null);
		

		Label lblValitseTykohde = new Label(shell, SWT.NONE);
		lblValitseTykohde.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblValitseTykohde.setBounds(10, 10, 90, 15);
		lblValitseTykohde.setText("Valitse ty\u00F6kohde");

		//Fetches all the contracts from the database into a List<String>
		contracts = db.getContracts();

		Combo contract_dropdown = new Combo(shell, SWT.READ_ONLY);
		contract_dropdown.setToolTipText("Listassa n\u00E4kyy vain ty\u00F6kohteet, joista ei ole viel\u00E4 tehty urakkatarjousta.");
		contract_dropdown.setBounds(106, 7, 191, 23);
		// Insert contracts into the dropdown list.
		for (String contract : contracts) {
			contract_dropdown.add(contract);
		}


		Label lblMaksuOsissa = new Label(shell, SWT.NONE);
		lblMaksuOsissa.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblMaksuOsissa.setBounds(10, 51, 75, 15);
		lblMaksuOsissa.setText("Maksu osissa?");

		Button positive_radio = new Button(shell, SWT.RADIO);

		positive_radio.setBounds(94, 50, 45, 16);
		positive_radio.setText("Kyll\u00E4");

		Button negative_radio = new Button(shell, SWT.RADIO);
		negative_radio.setSelection(true);
		negative_radio.setBounds(155, 50, 45, 16);
		negative_radio.setText("Ei");

		Button done_btn = new Button(shell, SWT.NONE);
		done_btn.setBounds(318, 114, 75, 25);
		done_btn.setText("Luo urakka");

		Button close_btn = new Button(shell, SWT.NONE);
		close_btn.setBounds(237, 114, 75, 25);
		close_btn.setText("Sulje");

		Spinner instalments_spinner = new Spinner(shell, SWT.BORDER);
		instalments_spinner.setMaximum(5);
		instalments_spinner.setMinimum(1);
		instalments_spinner.setBounds(48, 79, 47, 22);
		instalments_spinner.setEnabled(false);

		Label instalments_label = new Label(shell, SWT.NONE);
		instalments_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		instalments_label.setBounds(10, 82, 32, 15);
		instalments_label.setText("Osat");

		Button insertItems_btn = new Button(shell, SWT.NONE);
		//=============================GUI ELEMENTS END HERE========================================
		
		//Listener for button "Lis�� tarvikkeita", for adding items to the offer
		insertItems_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				PriceEstimateDialog price = new PriceEstimateDialog(shell,
						getStyle());
				price.open();
			}
		});

		insertItems_btn.setBounds(10, 114, 90, 25);
		insertItems_btn.setText("Lis\u00E4\u00E4 tarvikkeita");

		// Sends the selected options to DBHandler and creates a new contract.
		done_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				if (positive_radio.getSelection()) {
					int instalments = instalments_spinner.getSelection();
					db.createContract(contract_dropdown.getText(), instalments);
				} else {
					db.createContract(contract_dropdown.getText(), 1);
				}
				shell.dispose();
			}
		});
		// Listens for radio button press, and sets the pay in instalments
		// spinner enabled.
		positive_radio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instalments_spinner.setEnabled(true);
			}
		});
		// Listens for radio button presses, and sets the pay in instalments
		// spinner disabled.
		negative_radio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				instalments_spinner.setEnabled(false);
			}
		});
		// Button listener to close the window.
		close_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();
			}
		});

		insertItems_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

			}
		});

	}
}
