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

//Driver class for the program. MainMenu of the graphical user interface.
public class MainMenu extends Composite {
	
	//Main method
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

	//Constructor
	public MainMenu(Composite parent, int style) {
		super(parent, style);
		setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		
		DBHandler db = new DBHandler();
		
		db.checkInvoiceState();
		setLayout(null);
		
		//===========================GUI ELEMENTS START HERE======================================
		Label createNew_lbl = new Label(this, SWT.BORDER | SWT.CENTER);
		createNew_lbl.setFont(SWTResourceManager.getFont("System", 11, SWT.NORMAL));
		createNew_lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		createNew_lbl.setBounds(32, 20, 84, 18);
		createNew_lbl.setText("Luo uusi");

		Label edit_lbl = new Label(this, SWT.BORDER | SWT.CENTER);
		edit_lbl.setForeground(SWTResourceManager.getColor(0, 0, 0));
		edit_lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		edit_lbl.setFont(SWTResourceManager.getFont("System", 12, SWT.NORMAL));
		edit_lbl.setBounds(173, 20, 128, 18);
		edit_lbl.setText("Muokkaus");

		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 227, 337, 2);

		Button AddCustomer_btn = new Button(this, SWT.NONE);
		AddCustomer_btn.setBounds(32, 62, 84, 25);
		AddCustomer_btn.setText("Asiakas");

		Button SavePerf_btn = new Button(this, SWT.NONE);
		SavePerf_btn.setBounds(173, 62, 128, 25);

		SavePerf_btn.setText("Tallenna ty\u00F6suoritus");

		Button createJob_btn = new Button(this, SWT.NONE);
		createJob_btn.setBounds(32, 93, 84, 25);
		createJob_btn.setText("Ty\u00F6kohde");

		Button editJob_btn = new Button(this, SWT.NONE);
		editJob_btn.setBounds(173, 93, 128, 25);
		editJob_btn.setText("Muokkaa ty\u00F6kohdetta");

		// Price calculation dialog.
		Button pricecalc_btn = new Button(this, SWT.NONE);
		pricecalc_btn.setBounds(32, 124, 84, 25);
		pricecalc_btn.setText("Hinta-arvio");

		Button invoices_btn = new Button(this, SWT.NONE);
		invoices_btn.setBounds(173, 155, 128, 25);
		invoices_btn.setText("Laskut");

		Button newContract_btn = new Button(this, SWT.NONE);
		newContract_btn.setBounds(32, 155, 84, 25);

		newContract_btn.setText("Urakkatarjous");
		
		Button items_btn = new Button(this, SWT.NONE);
		items_btn.setBounds(173, 124, 128, 25);
		items_btn.setText("Tarvikkeet");
		//=============================GUI ELEMENTS END HERE========================================
		
		/* Button listener for dialogs. */
		
		//Listener for "Laskut" button. Opens dialogue for viewing invoices
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

		//Listener for "Asiakas" button. Opens dialogue for creating a new customer
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
		
		//Listener for "Tallenna työsuoritus" button. Opens dialogue for creating a new task
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

		//Listener for "Urakkatarjous" button. Opens dialogue for creating a new contract
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

		//Listener for "Hinta-arvio" button. Opens dialogue for creating a new price evaluation
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

		//Listener for "Muokkaa työkohdetta" button. Opens dialogue for editing and finishing a job
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
		
		//Listener for "Työkohde" button. Opens dialogue for creating a new job.
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
		
		//Add listener to button that opens a dialog to inventory of items
		items_btn.addListener(SWT.Selection,  new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4, false));
				InventoryDialog dialog = new InventoryDialog(shell, style);
				dialog.open();
			}			
		});
		
	}
}
