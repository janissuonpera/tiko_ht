package tiko_ht;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.wb.swt.SWTResourceManager;

/*
 * Class that creates a graphical dialog for creating and adding
 * a new customer to the database.
 * 
 */
public class AddCustomer extends Dialog {

	//Attributes
	protected Object result;
	protected Shell shell;
	private Text ssn_text;
	private Text name_text;
	private Text address_text;
	private boolean Company;

	//Constructor
	public AddCustomer(Shell parent, int style) {
		super(parent, style);
		setText("Lis‰‰ asiakas");
	}

	//Opens the dialog
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

	//Creates contents of the dialog
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(381, 169);
		shell.setText(getText());
		shell.setLayout(null);

		Label lblNimi = new Label(shell, SWT.NONE);
		lblNimi.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNimi.setBounds(9, 8, 31, 15);
		lblNimi.setText("Nimi*");

		name_text = new Text(shell, SWT.BORDER);
		name_text.setBounds(49, 5, 150, 21);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(342, 8, 0, 15);
		lblNewLabel.setText("");

		Label lblOsoite = new Label(shell, SWT.NONE);
		lblOsoite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblOsoite.setBounds(5, 34, 39, 15);
		lblOsoite.setText("Osoite*");

		address_text = new Text(shell, SWT.BORDER);
		address_text.setBounds(49, 31, 150, 21);

		Label lblYritys = new Label(shell, SWT.NONE);
		lblYritys.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblYritys.setBounds(8, 61, 33, 15);
		lblYritys.setText("Yritys*");

		//Radio button for deciding if the customer is a company or not
		Button negative_radio = new Button(shell, SWT.RADIO);
		negative_radio.setSelection(true);
		negative_radio.setBounds(65, 57, 31, 23);
		negative_radio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Company = false;
			}
		});
		negative_radio.setText("Ei");

		//Radio button for deciding if the customer is a company or not
		Button positive_radio = new Button(shell, SWT.RADIO);
		positive_radio.setBounds(110, 57, 45, 23);
		positive_radio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Company = true;
			}
		});
		positive_radio.setText("Kyll‰");

		Label lblHetu = new Label(shell, SWT.NONE);
		lblHetu.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblHetu.setBounds(11, 88, 26, 15);
		lblHetu.setText("Hetu");

		ssn_text = new Text(shell, SWT.BORDER);
		ssn_text.setBounds(49, 85, 150, 21);

		Label lblVaaditutTiedot = new Label(shell, SWT.NONE);
		lblVaaditutTiedot.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblVaaditutTiedot.setBounds(5, 116, 85, 15);
		lblVaaditutTiedot.setText("* Vaaditut tiedot");

		Button cancel_btn = new Button(shell, SWT.NONE);
		cancel_btn.setBounds(194, 111, 87, 25);
		
		cancel_btn.setText("Peruuta");

		Button done_btn = new Button(shell, SWT.NONE);
		done_btn.setBounds(287, 111, 80, 25);
		
		done_btn.setText("Lis‰‰ Asiakas");
		
		//Listener for the button "Lis‰‰ asiakas"
		done_btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				sendData();
				shell.dispose();
			}
		});
		
		//Listener for the button "Peruuta"
		cancel_btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shell.dispose();
			}
		});
	}

	//Getter for company attribute
	public boolean getCompany() {
		return this.Company;
	}

	//Sends the customer data to DBHandler object which adds the customer to the database
	public void sendData() {
		try {
			DBHandler db = new DBHandler();
			
			db.createCustomer(this.name_text.getText(), this.address_text.getText(),
					this.Company, this.ssn_text.getText());
		} catch (Exception e) {
			System.out.println("Failed to add a customer");
			System.out.println(e);
		}
	}

}
