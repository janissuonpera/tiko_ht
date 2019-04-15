package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;

public class InvoiceDialog extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public InvoiceDialog(Shell parent, int style) {
		super(parent, style);
		setText("Invoices");
	}

	/**
	 * Open the dialog.
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
		shell.setSize(450, 300);
		shell.setText(getText());
		DBHandler db = new DBHandler();
		shell.setLayout(null);
		
		Label invoiceLabel = new Label(shell, SWT.NONE);
		invoiceLabel.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		invoiceLabel.setBounds(10, 10, 114, 25);
		invoiceLabel.setText("Valitse lasku:");
		
		Combo invoice_dropdown = new Combo(shell, SWT.NONE);
		invoice_dropdown.setBounds(130, 12, 220, 23);
		db.connect();
		
	}
}
