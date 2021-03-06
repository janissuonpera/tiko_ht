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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

//Class for creating a graphical dialog for adding a new task into the database
//In the dialog you choose the customer who its for, the job's name, the job's address
//and whether it's a contract
public class JobCreationDialog extends Dialog {
	
	//Attributes
	protected Object result;
	protected Shell shell;
	private Text jobAddress_field;
	private Text jobName_field;
	List<String> customers = new ArrayList<String>();
	
	//Constructor
	public JobCreationDialog(Shell parent, int style) {
		super(parent, style);
		setText("Luo ty�kohde");
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
		shell.setSize(439, 185);
		shell.setText(getText());
		shell.setLayout(null);

		Label lblLuoUusiAsiakas = new Label(shell, SWT.NONE);
		lblLuoUusiAsiakas.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblLuoUusiAsiakas.setBounds(5, 5, 98, 15);
		lblLuoUusiAsiakas.setText("Luo uusi ty\u00F6kohde");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel.setBounds(5, 29, 73, 15);
		lblNewLabel.setText("Valitse asiakas");

		Combo customer_dropdown = new Combo(shell, SWT.READ_ONLY);
		customer_dropdown.setBounds(108, 25, 195, 23);

		Label jobName_lbl = new Label(shell, SWT.NONE);
		jobName_lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		jobName_lbl.setBounds(5, 56, 71, 15);
		jobName_lbl.setText("Kohteen nimi");

		jobName_field = new Text(shell, SWT.BORDER);
		jobName_field.setBounds(108, 53, 243, 21);

		Label jobAddress_lbl = new Label(shell, SWT.NONE);
		jobAddress_lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		jobAddress_lbl.setBounds(5, 82, 79, 15);
		jobAddress_lbl.setText("Kohteen osoite");

		jobAddress_field = new Text(shell, SWT.BORDER);
		jobAddress_field.setBounds(108, 79, 243, 21);

		Label lblUrakka = new Label(shell, SWT.NONE);
		lblUrakka.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblUrakka.setBounds(5, 105, 36, 15);
		lblUrakka.setText("Urakka");

		Button radioButton_pos = new Button(shell, SWT.RADIO);
		radioButton_pos.setBounds(108, 105, 45, 16);
		radioButton_pos.setText("Kyll�");

		Button radiobutton_neg = new Button(shell, SWT.RADIO);
		radiobutton_neg.setBounds(158, 105, 30, 16);
		radiobutton_neg.setSelection(true);
		radiobutton_neg.setText("Ei");

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(298, 126, 53, 25);
		btnCancel.setText("Peruuta");
		
		Button btnCreateJob = new Button(shell, SWT.NONE);
		btnCreateJob.setBounds(356, 126, 74, 25);
		btnCreateJob.setText("Lis\u00E4\u00E4 kohde");		
		//=============================GUI ELEMENTS END HERE========================================
		
		//Get all customers from database
		customers = db.getCustomers();

		//Populate the customer dropdown list
		for (int i = 0; i < customers.size(); i++) {
			customer_dropdown.add(customers.get(i));
		}
		
		//Listener for 'Peruuta' button. Exits the dialog window
		btnCancel.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});
		
		//Listener for "Lis�� kohde" button. Adds a new job into the database
		btnCreateJob.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if(customer_dropdown.getText().length()>0 && jobName_field.getText().length()>0 && jobAddress_field.getText().length()>0) {
					db.createJob(customer_dropdown.getText(),
							jobName_field.getText(), jobAddress_field.getText(),
							radioButton_pos.getSelection());
					shell.dispose();
				}
			}
		});
		

	}
}
