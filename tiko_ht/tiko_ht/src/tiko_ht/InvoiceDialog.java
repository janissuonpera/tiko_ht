package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

/*
 * Creates a graphical dialog for viewing and deleting invoices.
 */
public class InvoiceDialog extends Dialog {

	//Attributes
	protected Object result;
	protected Shell shell;
	List<String> invoices_names;
	Invoice current_invoice;
	private Text id_text;
	private Text kohde_text;
	private Text pvm_text;
	private Text erapvm_text;
	private Text tyyppi_text;
	private Text lkm_text;
	private Text tunnit_text;
	private Text hinta_text;
	private Text maksettu_text;

	//Constructor
	public InvoiceDialog(Shell parent, int style) {
		super(parent, style);
		setText("Invoices");
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
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(365, 540);
		shell.setText(getText());	
		shell.setLayout(null);

		Label invoiceLabel = new Label(shell, SWT.NONE);
		invoiceLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		invoiceLabel.setFont(
				SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		invoiceLabel.setBounds(10, 10, 114, 25);
		invoiceLabel.setText("Valitse lasku:");

		Combo invoice_dropdown = new Combo(shell, SWT.NONE);
		invoice_dropdown.setBounds(130, 12, 220, 23);

		Label id_label = new Label(shell, SWT.NONE);
		id_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		id_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		id_label.setBounds(10, 41, 75, 25);
		id_label.setText("Laskun ID");

		Label tyokohde_label = new Label(shell, SWT.NONE);
		tyokohde_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		tyokohde_label.setText("Tyokohde");
		tyokohde_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		tyokohde_label.setBounds(10, 90, 75, 25);

		Label pvm_label = new Label(shell, SWT.NONE);
		pvm_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		pvm_label.setText("P\u00E4iv\u00E4m\u00E4\u00E4r\u00E4");
		pvm_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		pvm_label.setBounds(10, 139, 75, 25);

		Label erapvm_label = new Label(shell, SWT.NONE);
		erapvm_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		erapvm_label.setText("Er\u00E4p\u00E4iv\u00E4");
		erapvm_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		erapvm_label.setBounds(10, 188, 75, 25);

		Label tyyppi_label = new Label(shell, SWT.NONE);
		tyyppi_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		tyyppi_label.setText("Tyyppi");
		tyyppi_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		tyyppi_label.setBounds(10, 237, 75, 25);

		Label lkm_label = new Label(shell, SWT.NONE);
		lkm_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lkm_label.setText("Lukum\u00E4\u00E4r\u00E4");
		lkm_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		lkm_label.setBounds(10, 286, 75, 25);

		Label tunnit_label = new Label(shell, SWT.NONE);
		tunnit_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		tunnit_label.setText("Tuntien hinta");
		tunnit_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		tunnit_label.setBounds(10, 335, 94, 25);

		Label hinta_label = new Label(shell, SWT.NONE);
		hinta_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		hinta_label.setText("Kokonaishinta");
		hinta_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		hinta_label.setBounds(10, 384, 94, 25);

		Label maksettu_label = new Label(shell, SWT.NONE);
		maksettu_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		maksettu_label.setText("Maksettu");
		maksettu_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		maksettu_label.setBounds(10, 433, 94, 25);

		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label.setBounds(0, 72, 359, 12);

		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_1.setBounds(0, 121, 359, 12);

		Label label_2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_2.setBounds(0, 170, 359, 12);

		Label label_3 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_3.setBounds(0, 219, 359, 12);

		Label label_4 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_4.setBounds(0, 268, 359, 12);

		Label label_5 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_5.setBounds(0, 317, 359, 12);

		Label label_6 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_6.setBounds(0, 366, 359, 12);

		Label label_7 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_7.setBounds(0, 415, 359, 12);

		id_text = new Text(shell, SWT.BORDER);
		id_text.setEditable(false);
		id_text.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		id_text.setBounds(175, 42, 175, 21);

		kohde_text = new Text(shell, SWT.BORDER);
		kohde_text.setEditable(false);
		kohde_text.setBounds(175, 91, 175, 21);

		pvm_text = new Text(shell, SWT.BORDER);
		pvm_text.setEditable(false);
		pvm_text.setBounds(175, 140, 175, 21);

		erapvm_text = new Text(shell, SWT.BORDER);
		erapvm_text.setEditable(false);
		erapvm_text.setBounds(175, 189, 175, 21);

		tyyppi_text = new Text(shell, SWT.BORDER);
		tyyppi_text.setEditable(false);
		tyyppi_text.setBounds(175, 238, 175, 21);

		lkm_text = new Text(shell, SWT.BORDER);
		lkm_text.setEditable(false);
		lkm_text.setBounds(175, 287, 175, 21);

		tunnit_text = new Text(shell, SWT.BORDER);
		tunnit_text.setEditable(false);
		tunnit_text.setBounds(175, 336, 175, 21);

		hinta_text = new Text(shell, SWT.BORDER);
		hinta_text.setEditable(false);
		hinta_text.setBounds(175, 385, 175, 21);

		maksettu_text = new Text(shell, SWT.BORDER);
		maksettu_text.setEditable(false);
		maksettu_text.setBounds(175, 434, 175, 21);

		Button delete_btn = new Button(shell, SWT.NONE);
		delete_btn.setBounds(10, 476, 75, 25);
		delete_btn.setText("Poista lasku");

		Label status_label = new Label(shell, SWT.NONE);
		status_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		status_label.setBounds(172, 486, 166, 15);
		//=============================GUI ELEMENTS END HERE========================================
		
		//Listener for "Poista lasku" button. Deletes the invoice from database
		delete_btn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				
				boolean deleted = db
						.deleteInvoice(current_invoice.getLasku_id());
				if (deleted) {
					shell.dispose();
				} else {
					status_label.setText("Poisto ep\u00E4onnistui.");
				}
			}

		});

		//Get all invoice ids and the jobs name
		//Add both to the dropdown list
		invoices_names = db.getInvoicesIdAndName();
		for (int i = 0; i < invoices_names.size(); i++) {
			invoice_dropdown.add(invoices_names.get(i));
		}
		
		//Listener for the dropdown list. Choosing one of the invoices
		//shows that invoice's info on the labels and textboxes below
		invoice_dropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				String curr_invoice = invoice_dropdown.getText();
				int invoice_id = Integer.parseInt(curr_invoice.split(" ")[0]);
				
				current_invoice = db.getFullInvoice(invoice_id);
				updateTexts();
			}
		});
	}
	
	//Fetches all the invoice information from the invoice object using getters
	//and updates the textboxes
	public void updateTexts() {
		DBHandler db = new DBHandler();
		
		id_text.setText(Integer.toString(current_invoice.getLasku_id()));
		kohde_text.setText(
				db.getJobNameById(current_invoice.getTyokohde_id(), true));
		pvm_text.setText(current_invoice.getPvm().toString());
		erapvm_text.setText(current_invoice.getEra_pvm().toString());
		tyyppi_text.setText(current_invoice.getTyyppi());
		lkm_text.setText(Integer.toString(current_invoice.getLkm()));
		tunnit_text
				.setText(Double.toString(current_invoice.getTuntien_hinta()));
		hinta_text.setText(Double.toString(current_invoice.getHinta()));
		maksettu_text.setText(Boolean.toString(current_invoice.isMaksettu()));
	}
}
