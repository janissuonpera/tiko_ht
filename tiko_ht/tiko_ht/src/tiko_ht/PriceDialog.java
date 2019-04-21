package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;

public class PriceDialog extends Dialog {
	protected Object result;
	protected Shell shell;
	protected int style;
	java.util.List<String[]> item_list = null;
	java.util.List<String> task_list = null;

	/**
	 * Constructor. Dialog for the current price. Tasks + items = price.
	 */
	public PriceDialog(Shell parent, int style, java.util.List<String[]> itemList,
			 java.util.List<String> task_list) {
		super(parent, style);
		setText("Hinta");
		this.item_list = (java.util.List<String[]>) itemList;
		this.task_list = (java.util.List<String>) task_list;
	}

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
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(350, 434);
		shell.setText(getText());
		shell.setLayout(null);

		Label tasks = new Label(shell, SWT.NONE);
		tasks.setBounds(5, 5, 65, 15);
		tasks.setText("Suoritukset:");

		Label lblTarvikkeet = new Label(shell, SWT.NONE);
		lblTarvikkeet.setText("Tarvikkeet:");
		lblTarvikkeet.setBounds(5, 189, 65, 15);

		// Adds all the tasks to task_list list-element in dialog and sums their
		// total price at the bottom.
		List taskList = new List(shell, SWT.BORDER);
		taskList.setBounds(75, 5, 250, 150);
		double task_sum = 0;
		for (int i = 0; i < task_list.size(); i++) {
			taskList.add(task_list.get(i) + " e");
			task_sum += Double.parseDouble(
					task_list.get(i).split(" \\| ")[2]);
		}
		taskList.add("----------------------------------------------");
		taskList.add("Suoritusten hinta: " + task_sum + " euroa");

		// Adds all the items to item_list list-element in dialog and sums their
		// total price at the bottom.
		double item_sum = 0;
		List itemList = new List(shell, SWT.BORDER);
		itemList.setBounds(75, 185, 250, 150);
		// Go through every item and add their name and price to the list.
		for(String[] item : item_list) {
			itemList.add(item[0] + ": "
					+ item[3] + " e");
			item_sum += Double
					.parseDouble(item[3]);
		}
		itemList.add("----------------------------------------------");
		itemList.add("Tarvikkeiden hinta: " + item_sum + " euroa");

		Label lblKokoHinta = new Label(shell, SWT.NONE);
		lblKokoHinta.setFont(
				SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		lblKokoHinta.setBounds(127, 341, 90, 24);
		lblKokoHinta.setText("Koko hinta\nhei");

		Label sumLabel = new Label(shell, SWT.NONE);
		sumLabel.setFont(
				SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		sumLabel.setBounds(137, 371, 165, 24);
		sumLabel.setText(task_sum + item_sum + " euroa");
	}
}
