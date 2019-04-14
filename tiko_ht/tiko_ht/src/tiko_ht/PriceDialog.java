package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

public class PriceDialog extends Dialog{
	protected Object result;
	protected Shell shell;
	protected int style;
	java.util.List<java.util.List<String>> item_list = null;
	java.util.List<String> task_list = null;
	
	/**
	 * Constructor.
	 * Dialog for the current price. Tasks + items = price. 
	 */
	public PriceDialog(Shell parent, int style, Object itemList, Object task_list) {
		super(parent, style);
		setText("Hinta");
		this.item_list = (java.util.List<java.util.List<String>>) itemList;
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
		DBHandler db = new DBHandler();
		shell.setLayout(null);
		
		Label tasks = new Label(shell, SWT.NONE);
		tasks.setBounds(5, 5, 65, 15);
		tasks.setText("Suoritukset:");
		
		Label lblTarvikkeet = new Label(shell, SWT.NONE);
		lblTarvikkeet.setText("Tarvikkeet:");
		lblTarvikkeet.setBounds(5, 189, 65, 15);
		
		//Adds all the tasks to task_list list-element in dialog and sums their total price at the bottom.
		List taskList = new List(shell, SWT.BORDER);
		taskList.setBounds(75, 5, 250, 150);
		double task_sum = 0;
		for (int i = 0; i < task_list.size(); i++) {
			taskList.add(task_list.get(i));
			task_sum += Double.parseDouble(task_list.get(i).split(" \\| ")[2].split("€")[0]);
		}
		taskList.add("----------------------------------------------");
		taskList.add("Suoritusten hinta: " + task_sum + "€");
		
		//Adds all the items to item_list list-element in dialog and sums their total price at the bottom.
		double item_sum = 0;
		List itemList = new List(shell, SWT.BORDER);
		itemList.setBounds(75, 185, 250, 150);
		for (int i = 0; i < item_list.size(); i++) {
			itemList.add(item_list.get(0).get(i) + ": " + item_list.get(1).get(i) + "€");
			item_sum += Double.parseDouble(item_list.get(1).get(i).split(" \\| ")[2]);
		}
		itemList.add("----------------------------------------------");
		itemList.add("Tarvikkeiden hinta: " + item_sum + "€");
		
		Label lblKokoHinta = new Label(shell, SWT.NONE);
		lblKokoHinta.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		lblKokoHinta.setBounds(127, 341, 90, 24);
		lblKokoHinta.setText("Koko hinta\nhei");
		
		Label sumLabel = new Label(shell, SWT.NONE);
		sumLabel.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
		sumLabel.setBounds(137, 371, 65, 24);
		sumLabel.setText(task_sum + item_sum + "€");
	}
}
