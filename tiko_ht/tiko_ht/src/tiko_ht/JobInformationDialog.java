package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

public class JobInformationDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text custName_txt;
	private Text custAdd_txt;
	private Text ssn_txt;
	private Text jobName_txt;
	private Text jobAdd_txt;
	private String job_name;
	private String [] customer_info = new String [4];
	private String [] job_info = new String [3];
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public JobInformationDialog(Shell parent, int style,String job_name) {
		super(parent, style);
		this.job_name = job_name;
		setText("Kohteen tiedot");
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
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(386, 198);
		shell.setText(getText());
		DBHandler db = new DBHandler();
		
		Label lblAsiakas = new Label(shell, SWT.NONE);
		lblAsiakas.setFont(SWTResourceManager.getFont("System", 9, SWT.NORMAL));
		lblAsiakas.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblAsiakas.setBounds(54, 11, 55, 15);
		lblAsiakas.setText("Asiakas");
		
		Label lblTykohde = new Label(shell, SWT.NONE);
		lblTykohde.setFont(SWTResourceManager.getFont("System", 9, SWT.NORMAL));
		lblTykohde.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblTykohde.setBounds(249, 11, 75, 15);
		lblTykohde.setText("Ty\u00F6kohde");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel.setBounds(10, 33, 38, 26);
		lblNewLabel.setText("Nimi");
		
		Label lblOsoite = new Label(shell, SWT.NONE);
		lblOsoite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblOsoite.setBounds(10, 66, 38, 15);
		lblOsoite.setText("Osoite");
		
		Label lblYritys = new Label(shell, SWT.NONE);
		lblYritys.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblYritys.setBounds(10, 99, 38, 15);
		lblYritys.setText("Yritys?");
		
		Label lblHetu = new Label(shell, SWT.NONE);
		lblHetu.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblHetu.setBounds(10, 132, 38, 15);
		lblHetu.setText("Hetu");
		
		custName_txt = new Text(shell, SWT.BORDER);
		custName_txt.setEditable(false);
		custName_txt.setBounds(54, 33, 128, 21);
		
		custAdd_txt = new Text(shell, SWT.BORDER);
		custAdd_txt.setEditable(false);
		custAdd_txt.setBounds(54, 66, 128, 21);
		
		Label company_label = new Label(shell, SWT.NONE);
		company_label.setFont(SWTResourceManager.getFont("System", 9, SWT.NORMAL));
		company_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		company_label.setBounds(54, 99, 38, 18);
		
		ssn_txt = new Text(shell, SWT.BORDER);
		ssn_txt.setEditable(false);
		ssn_txt.setBounds(54, 132, 128, 21);
		
		jobName_txt = new Text(shell, SWT.BORDER);
		jobName_txt.setEditable(false);
		jobName_txt.setBounds(249, 33, 128, 21);
		
		jobAdd_txt = new Text(shell, SWT.BORDER);
		jobAdd_txt.setEditable(false);
		jobAdd_txt.setBounds(249, 66, 128, 21);
		
		Label label = new Label(shell, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label.setText("Osoite");
		label.setBounds(205, 66, 38, 15);
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		label_1.setText("Nimi");
		label_1.setBounds(205, 33, 38, 26);
		
		Label lblValmis = new Label(shell, SWT.NONE);
		lblValmis.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblValmis.setBounds(205, 99, 45, 15);
		lblValmis.setText("Valmis?");
		
		Label ready_label = new Label(shell, SWT.NONE);
		ready_label.setFont(SWTResourceManager.getFont("System", 9, SWT.NORMAL));
		ready_label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		ready_label.setBounds(251, 99, 38, 18);
		
		Label vertical_border = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
		vertical_border.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		vertical_border.setBounds(188, 10, 10, 154);
		
		
		// Fetch info for customer and job.
		customer_info = db.getCustomerByJobName(job_name);
		job_info = db.getJobInformation(job_name);
		
		// Display customer information
		custName_txt.setText(customer_info[0]);
		custAdd_txt.setText(customer_info[1]);
		company_label.setText(customer_info[2].equals("f")?"Ei":"Kyllä");
		ssn_txt.setText(customer_info[3]==null? "Ei tietokannassa":customer_info[3]);
		
		
		
		// Display job information
		jobName_txt.setText(job_info[0]);
		jobAdd_txt.setText(job_info[1]);
		ready_label.setText(job_info[2].equals("f")?"Ei":"Kyllä");
		
		
		

	}
}
