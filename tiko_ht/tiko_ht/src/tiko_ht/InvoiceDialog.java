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

public class InvoiceDialog extends Dialog {

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

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public InvoiceDialog(Shell parent, int style) {
		super(parent, style);
		setText("Invoices");
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
		shell.setSize(365, 540);
		shell.setText(getText());
		DBHandler db = new DBHandler();
		shell.setLayout(null);

		Label invoiceLabel = new Label(shell, SWT.NONE);
		invoiceLabel.setFont(
				SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		invoiceLabel.setBounds(10, 10, 114, 25);
		invoiceLabel.setText("Valitse lasku:");

		Combo invoice_dropdown = new Combo(shell, SWT.NONE);
		invoice_dropdown.setBounds(130, 12, 220, 23);

		Label id_label = new Label(shell, SWT.NONE);
		id_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		id_label.setBounds(10, 41, 75, 25);
		id_label.setText("Laskun ID");

		Label tyokohde_label = new Label(shell, SWT.NONE);
		tyokohde_label.setText("Tyokohde");
		tyokohde_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		tyokohde_label.setBounds(10, 90, 75, 25);

		Label pvm_label = new Label(shell, SWT.NONE);
		pvm_label.setText("P\u00E4iv\u00E4m\u00E4\u00E4r\u00E4");
		pvm_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		pvm_label.setBounds(10, 139, 75, 25);

		Label erapvm_label = new Label(shell, SWT.NONE);
		erapvm_label.setText("Er\u00E4p\u00E4iv\u00E4");
		erapvm_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		erapvm_label.setBounds(10, 188, 75, 25);

		Label tyyppi_label = new Label(shell, SWT.NONE);
		tyyppi_label.setText("Tyyppi");
		tyyppi_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		tyyppi_label.setBounds(10, 237, 75, 25);

		Label lkm_label = new Label(shell, SWT.NONE);
		lkm_label.setText("Lukum\u00E4\u00E4r\u00E4");
		lkm_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		lkm_label.setBounds(10, 286, 75, 25);

		Label tunnit_label = new Label(shell, SWT.NONE);
		tunnit_label.setText("Tuntien hinta");
		tunnit_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		tunnit_label.setBounds(10, 335, 94, 25);

		Label hinta_label = new Label(shell, SWT.NONE);
		hinta_label.setText("Kokonaishinta");
		hinta_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		hinta_label.setBounds(10, 384, 94, 25);

		Label maksettu_label = new Label(shell, SWT.NONE);
		maksettu_label.setText("Maksettu");
		maksettu_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		maksettu_label.setBounds(10, 433, 94, 25);

		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 72, 359, 12);

		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(0, 121, 359, 12);

		Label label_2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setBounds(0, 170, 359, 12);

		Label label_3 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_3.setBounds(0, 219, 359, 12);

		Label label_4 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setBounds(0, 268, 359, 12);

		Label label_5 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_5.setBounds(0, 317, 359, 12);

		Label label_6 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_6.setBounds(0, 366, 359, 12);

		Label label_7 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_7.setBounds(0, 415, 359, 12);

		id_text = new Text(shell, SWT.BORDER);
		id_text.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		id_text.setBounds(175, 42, 175, 21);

		kohde_text = new Text(shell, SWT.BORDER);
		kohde_text.setBounds(175, 91, 175, 21);

		pvm_text = new Text(shell, SWT.BORDER);
		pvm_text.setBounds(175, 140, 175, 21);

		erapvm_text = new Text(shell, SWT.BORDER);
		erapvm_text.setBounds(175, 189, 175, 21);

		tyyppi_text = new Text(shell, SWT.BORDER);
		tyyppi_text.setBounds(175, 238, 175, 21);

		lkm_text = new Text(shell, SWT.BORDER);
		lkm_text.setBounds(175, 287, 175, 21);

		tunnit_text = new Text(shell, SWT.BORDER);
		tunnit_text.setBounds(175, 336, 175, 21);

		hinta_text = new Text(shell, SWT.BORDER);
		hinta_text.setBounds(175, 385, 175, 21);

		maksettu_text = new Text(shell, SWT.BORDER);
		maksettu_text.setBounds(175, 434, 175, 21);

		Button delete_btn = new Button(shell, SWT.NONE);
		delete_btn.setBounds(10, 476, 75, 25);
		delete_btn.setText("Poista lasku");

		Label status_label = new Label(shell, SWT.NONE);
		status_label.setBounds(172, 486, 166, 15);
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

		
		invoices_names = db.getInvoicesIdAndName();
		for (int i = 0; i < invoices_names.size(); i++) {
			invoice_dropdown.add(invoices_names.get(i));
		}
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
