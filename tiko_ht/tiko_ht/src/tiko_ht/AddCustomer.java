package tiko_ht;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class AddCustomer extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text Hetu;
	private Text NameBox;
	private Text Address;
	private boolean Company;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public AddCustomer(Shell parent, int style) {
		super(parent, style);
		setText("Lis‰‰ asiakas");
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setSize(433, 169);
		shell.setText(getText());
		shell.setLayout(new GridLayout(10, false));

		Label lblNimi = new Label(shell, SWT.NONE);
		lblNimi.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNimi.setText("Nimi*");

		NameBox = new Text(shell, SWT.BORDER);
		NameBox.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("");

		Label lblOsoite = new Label(shell, SWT.NONE);
		lblOsoite.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblOsoite.setText("Osoite*");

		Address = new Text(shell, SWT.BORDER);
		GridData gd_Address = new GridData(SWT.FILL, SWT.FILL, true, false, 5,
				1);
		gd_Address.widthHint = 170;
		Address.setLayoutData(gd_Address);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblYritys = new Label(shell, SWT.NONE);
		lblYritys.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblYritys.setText("Yritys*");

		Button NoButton = new Button(shell, SWT.RADIO);
		NoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Company = false;
			}
		});
		GridData gd_NoButton = new GridData(SWT.CENTER, SWT.CENTER, true, false,
				2, 1);
		gd_NoButton.widthHint = 44;
		gd_NoButton.heightHint = 23;
		NoButton.setLayoutData(gd_NoButton);
		NoButton.setText("Ei");

		Button YesButton = new Button(shell, SWT.RADIO);
		YesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Company = true;
			}
		});

		GridData gd_YesButton = new GridData(SWT.CENTER, SWT.CENTER, true,
				false, 3, 1);
		gd_YesButton.widthHint = 122;
		YesButton.setLayoutData(gd_YesButton);
		YesButton.setText("Kyll‰");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblHetu = new Label(shell, SWT.NONE);
		lblHetu.setLayoutData(
				new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblHetu.setText("Hetu");

		Hetu = new Text(shell, SWT.BORDER);
		GridData gd_Hetu = new GridData(SWT.FILL, SWT.CENTER, true, false, 5,
				1);
		gd_Hetu.widthHint = 95;
		Hetu.setLayoutData(gd_Hetu);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label lblVaaditutTiedot = new Label(shell, SWT.NONE);
		lblVaaditutTiedot.setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblVaaditutTiedot.setText("* Vaaditut tiedot");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Button CancelAddCustomer = new Button(shell, SWT.NONE);
		CancelAddCustomer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				shell.dispose();
			}
		});
		GridData gd_CancelAddCustomer = new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1);
		gd_CancelAddCustomer.widthHint = 87;
		CancelAddCustomer.setLayoutData(gd_CancelAddCustomer);
		CancelAddCustomer.setText("Peruuta");

		Button AddCustomer = new Button(shell, SWT.NONE);
		AddCustomer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				sendData();
				shell.dispose();
			}
		});
		AddCustomer.setText("Lis‰‰ Asiakas");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		DragSource dragSource = new DragSource(shell, DND.DROP_MOVE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		ViewForm viewForm = new ViewForm(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

	}

	public boolean getCompany() {
		return this.Company;
	}

	public void sendData() {
		try {
			DBHandler db = new DBHandler();
			db.connect();
			db.createCustomer(this.NameBox.getText(), this.Address.getText(),
					this.Company, this.Hetu.getText());
		} catch (Exception e) {
			System.out.println("Failed to add a customer");
			System.out.println(e);
		}
	}

}
