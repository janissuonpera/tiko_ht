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
	private boolean contract = false;

	/**
	 * Constructor. Dialog for the current price. Tasks + items = price.
	 */
	public PriceDialog(Shell parent, int style,
			java.util.List<String[]> itemList,
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
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(350, 465);
		shell.setText(getText());
		shell.setLayout(null);

		Label tasks = new Label(shell, SWT.NONE);
		tasks.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		tasks.setBounds(5, 5, 65, 15);
		tasks.setText("Suoritukset:");

		Label lblTarvikkeet = new Label(shell, SWT.NONE);
		lblTarvikkeet.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblTarvikkeet.setText("Tarvikkeet:");
		lblTarvikkeet.setBounds(5, 189, 65, 15);

		// Adds all the tasks to task_list list-element in dialog and sums their
		// total price at the bottom.
		List taskList = new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		taskList.setBounds(75, 5, 250, 150);
		double task_sum = 0;
		double taxless_task_sum = 0;
		for (int i = 0; i < task_list.size(); i++) {
			taskList.add(task_list.get(i) + " e" + " | Alv: 24%");
			task_sum += Double.parseDouble(task_list.get(i).split(" \\| ")[2]);
			taxless_task_sum += Double
					.parseDouble(task_list.get(i).split(" \\| ")[2]) * 0.80645;
		}
		taskList.add("----------------------------------------------");
		taskList.add("Suoritusten hinta: " + task_sum + " euroa");
		taskList.add("Suoritusten veroton hinta: "
				+ Math.round(taxless_task_sum * 100.0) / 100.0 + " euroa");

		// Adds all the items to item_list list-element in dialog and sums their
		// total price at the bottom.
		double item_sum = 0;
		double taxless_item_sum = 0;
		List itemList = new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		itemList.setBounds(75, 185, 250, 150);

		// If the item_list's array length is 6, then it is a contract. Hourly
		// job array size is 7.
		if (item_list.get(0).length == 6) {
			contract = true;
		}

		// Tax percentage.
		double tax_pct = 0.24;

		// Go through every item and add their name, amount, unit and price to
		// the list.
		for (String[] item : item_list) {
			double item_price = 0;
			// If the job is a contract, add with a different structured array.
			if (contract) {

				// If the item is literature, set tax to 10%.
				if (item[4].equals("true")) {
					tax_pct = 0.10;
				} else {
					tax_pct = 0.24;
				}

				item_price = Double.parseDouble(item[3])*(1+tax_pct);
				itemList.add(item[0] + " | " + item[1] + item[2] + " | "
						+ Math.round(item_price * 100.0) / 100.0 + "e"
						+ " | Alv: " + Math.round(tax_pct * 100) + "%");

			} else {

				// If the item is literature, set tax to 10%.
				if (item[6].equals("true")) {
					tax_pct = 0.10;
				} else {
					tax_pct = 0.24;
				}

				item_price = Double.parseDouble(item[3])*(1+tax_pct);
				itemList.add(item[0] + " | " + item[1] + item[2] + " | "
						+ Math.round(item_price * 100.0) / 100.0 + "e"
						+ " | Alv: " + Math.round(tax_pct * 100) + "%");

			}
			item_sum += item_price;
			taxless_item_sum += Double.parseDouble(item[3]);
		}
		itemList.add("----------------------------------------------");
		itemList.add("Tarvikkeiden hinta: " + Math.round(item_sum*100.0)/100.0 + " euroa");
		itemList.add(
				"Tarvikkeiden veroton hinta: " + (taxless_item_sum) + " euroa");

		

		Label lblKokoHinta = new Label(shell, SWT.NONE);
		lblKokoHinta.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblKokoHinta.setFont(
				SWTResourceManager.getFont("System", 16, SWT.NORMAL));
		lblKokoHinta.setBounds(137, 341, 90, 15);
		lblKokoHinta.setText("Koko hinta");

		Label sumLabel = new Label(shell, SWT.NONE);
		sumLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		sumLabel.setFont(
				SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		sumLabel.setBounds(137, 371, 165, 24);
		sumLabel.setText(task_sum + item_sum + " euroa");

		Label taxed_label = new Label(shell, SWT.NONE);
		taxed_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		taxed_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		taxed_label.setBounds(58, 375, 65, 15);
		taxed_label.setText("Verollinen");

		Label taxless_label = new Label(shell, SWT.NONE);
		taxless_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		taxless_label.setFont(
				SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
		taxless_label.setBounds(58, 405, 54, 15);
		taxless_label.setText("Veroton");
		
		// Taxless sum calculation
		double taxless_sum = taxless_task_sum + taxless_item_sum;
		Label taxless_sumLabel = new Label(shell, SWT.NONE);
		taxless_sumLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		taxless_sumLabel
				.setText(Math.round(taxless_sum * 100.0) / 100.0 + " euroa");
		taxless_sumLabel.setFont(
				SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		taxless_sumLabel.setBounds(137, 401, 165, 24);
	}
}
