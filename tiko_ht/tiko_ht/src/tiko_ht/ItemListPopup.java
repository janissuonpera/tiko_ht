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
import org.eclipse.wb.swt.SWTResourceManager;

//Class for creating a graphical dialog for showing the items used
//in a task.
public class ItemListPopup extends Dialog {

	//Attributes
	protected Object result;
	protected Shell shell;
	
	//Constructor
	public ItemListPopup(Shell parent, int style) {
		super(parent, style);
		setText("Tarvikelista");

	}

	//Open the dialog
	public Object open(java.util.List<String[]> selected_items) {
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
	
	//Create the contents of the dialog
	private void createContents(java.util.List<String[]> selected_items) {
		shell = new Shell(getParent(), getStyle());
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(245, 321);
		shell.setText(getText());
		List list = new List(shell, SWT.BORDER);
		list.setBounds(10, 31, 219, 224);
		// Populate the item list.
		for(String [] item : selected_items) {
			list.add(item[0] + " | " + item[1] + " | " + item[2] + "%");
		}

		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setBounds(77, 261, 75, 25);
		btnOk.setText("OK");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel.setBounds(28, 10, 181, 15);
		lblNewLabel.setText("Tarvikkeen nimi,m\u00E4\u00E4r\u00E4 ja alennus");

		//Listener for "Ok" button. Closes the dialog window.
		btnOk.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();

			}

		});

	}
}
