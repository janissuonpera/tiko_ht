package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class JobCreationDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text jobAddress_field;
	private Text jobName_field;
	List<String> customers = new ArrayList<String>();
	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public JobCreationDialog(Shell parent, int style) {
		super(parent, style);
		setText("Luo työkohde");

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
		DBHandler db = new DBHandler();
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(439, 186);
		shell.setText(getText());
		shell.setLayout(new GridLayout(4, false));

		Label lblLuoUusiAsiakas = new Label(shell, SWT.NONE);
		lblLuoUusiAsiakas.setText("Luo uusi ty\u00F6kohde");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("Valitse asiakas");

		Combo customer_dropdown = new Combo(shell, SWT.READ_ONLY);
		GridData gd_customer_dropdown = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1);
		gd_customer_dropdown.widthHint = 169;
		customer_dropdown.setLayoutData(gd_customer_dropdown);
		db.connect();
		customers = db.getCustomers();

		for (int i = 0; i < customers.size(); i++) {
			customer_dropdown.add(customers.get(i));
		}

		new Label(shell, SWT.NONE);

		Label jobName_lbl = new Label(shell, SWT.NONE);
		jobName_lbl.setText("Kohteen nimi");

		jobName_field = new Text(shell, SWT.BORDER);
		GridData gd_jobName_field = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1);
		gd_jobName_field.widthHint = 171;
		jobName_field.setLayoutData(gd_jobName_field);
		new Label(shell, SWT.NONE);

		Label jobAddress_lbl = new Label(shell, SWT.NONE);
		jobAddress_lbl.setText("Kohteen osoite");

		jobAddress_field = new Text(shell, SWT.BORDER);
		GridData gd_jobAddress_field = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1);
		gd_jobAddress_field.widthHint = 231;
		jobAddress_field.setLayoutData(gd_jobAddress_field);
		new Label(shell, SWT.NONE);

		Label lblUrakka = new Label(shell, SWT.NONE);
		lblUrakka.setText("Urakka");

		Button radioButton_pos = new Button(shell, SWT.RADIO);
		radioButton_pos.setText("Kyllä");

		Button radiobutton_neg = new Button(shell, SWT.RADIO);
		radiobutton_neg.setSelection(true);
		radiobutton_neg.setText("Ei");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setLayoutData(
				new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText("Peruuta");
		btnCancel.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});

		Button btnCreateJob = new Button(shell, SWT.NONE);
		btnCreateJob.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				db.connect();
				db.createJob(customer_dropdown.getText(),
						jobName_field.getText(), jobAddress_field.getText(),
						radioButton_pos.getSelection());
				shell.dispose();
			}
		});
		btnCreateJob.setLayoutData(
				new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCreateJob.setText("Lis\u00E4\u00E4 kohde");

	}
	public List<String> getCustomers() {
		List<String> names = new ArrayList<String>();
		return names;
	}
}
