package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

public class ItemListPopup extends Dialog {

	protected Object result;
	protected Shell shell;
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ItemListPopup(Shell parent, int style) {
		super(parent, style);
		setText("Tarvikelista");

	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open(java.util.List<String> selected_items) {
		createContents(selected_items);
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
	private void createContents(java.util.List<String> selected_items) {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(202, 315);
		shell.setText(getText());
		List list = new List(shell, SWT.BORDER);
		list.setBounds(21, 31, 148, 224);
		// Populate the item list.
		for (int i = 0; i < selected_items.size(); i++) {
			list.add(selected_items.get(i));
		}

		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setBounds(57, 261, 75, 25);
		btnOk.setText("OK");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(21, 10, 148, 15);
		lblNewLabel.setText("Tarvikkeen nimi ja m\u00E4\u00E4r\u00E4");

		btnOk.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();

			}

		});

	}
}
