package tiko_ht;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainMenu extends Composite {
	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		new MainMenu(shell, SWT.CLOSE | SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL | SWT.MIN);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MainMenu(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		DBHandler db = new DBHandler();
		
		db.checkInvoiceState();
		setLayout(null);

		Label createNew_lbl = new Label(this, SWT.NONE);
		createNew_lbl.setBounds(51, 20, 45, 15);
		createNew_lbl.setText("Luo uusi");

		Label edit_lbl = new Label(this, SWT.NONE);
		edit_lbl.setBounds(207, 20, 55, 15);
		edit_lbl.setText("Muokkaus");

		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 227, 337, 2);

		Button AddCustomer_btn = new Button(this, SWT.NONE);
		AddCustomer_btn.setBounds(38, 62, 78, 25);
		AddCustomer_btn.setText("Asiakas");

		Button SavePerf_btn = new Button(this, SWT.NONE);
		SavePerf_btn.setBounds(173, 62, 128, 25);

		SavePerf_btn.setText("Tallenna ty\u00F6suoritus");

		Button createJob_btn = new Button(this, SWT.NONE);
		createJob_btn.setBounds(38, 99, 78, 25);
		createJob_btn.setText("Ty\u00F6kohde");

		Button editJob_btn = new Button(this, SWT.NONE);
		editJob_btn.setBounds(173, 99, 128, 25);
		editJob_btn.setText("Muokkaa ty\u00F6kohdetta");

		// Price calculation dialog.
		Button pricecalc_btn = new Button(this, SWT.NONE);
		pricecalc_btn.setBounds(38, 130, 78, 25);
		pricecalc_btn.setText("Hinta-arvio");

		Button invoices_btn = new Button(this, SWT.NONE);
		invoices_btn.setBounds(173, 130, 128, 25);
		invoices_btn.setText("Laskut");

		Button newContract_btn = new Button(this, SWT.NONE);
		newContract_btn.setBounds(38, 161, 84, 25);

		newContract_btn.setText("Urakkatarjous");

		/* Button listener for dialogs. */

		invoices_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				InvoiceDialog dialog = new InvoiceDialog(shell, style);
				dialog.open();
			}
		});

		AddCustomer_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {

				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				AddCustomer dialog = new AddCustomer(shell, style);
				dialog.open();

			}
		});

		SavePerf_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				SaveTaskDialog dialog = new SaveTaskDialog(shell, style);
				dialog.open();
			}
		});

		newContract_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				ContractCreationDialog dialog = new ContractCreationDialog(
						shell, style);
				dialog.open();
			}
		});

		pricecalc_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				PriceEstimateDialog dialog = new PriceEstimateDialog(shell, style);
				dialog.open();
			}
		});

		editJob_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				EditJobDialog dialog = new EditJobDialog(shell, style);
				dialog.open();
			}

		});
		createJob_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				JobCreationDialog dialog = new JobCreationDialog(shell, style);
				dialog.open();
			}
		});

	}

}
