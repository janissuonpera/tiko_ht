package tiko_ht;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;



public class MainMenu extends Composite {
	public static void main(String [] args) {
	
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1,false));
		new MainMenu(shell,SWT.NONE);
		shell.pack();
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainMenu(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label createNew_lbl = new Label(this, SWT.NONE);
		createNew_lbl.setText("Luo uusi");
		
		Label edit_lbl = new Label(this, SWT.NONE);
		edit_lbl.setText("Muokkaus");
		
		Button AddCustomer_btn = new Button(this, SWT.NONE);
		AddCustomer_btn.setText("Asiakas");
		AddCustomer_btn.addListener(SWT.Selection, new Listener(){
			
			@Override
			public void handleEvent(Event event) {
				
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4,false));
				AddCustomer dialog = new AddCustomer(shell,style);
				dialog.open();
				

			}
		});
			
		Button SavePerf_btn = new Button(this, SWT.NONE);
		SavePerf_btn.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4,false));
				SaveTaskDialog dialog = new SaveTaskDialog(shell,style);
				dialog.open();
			}
		});
		SavePerf_btn.setText("Tallenna tyosuoritus");
		
		Button createJob_btn = new Button(this, SWT.NONE);
		createJob_btn.setText("Tyokohde");
		createJob_btn.addListener(SWT.Selection,new Listener() {

			@Override
			public void handleEvent(Event event) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4,false));
				JobCreationDialog dialog = new JobCreationDialog(shell,style);
				dialog.open();
			}
		});
		
		
		Button editJob_btn = new Button(this, SWT.NONE);
		editJob_btn.setText("Muokkaa tyokohdetta");
		editJob_btn.addListener(SWT.Selection,new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Display display = getParent().getDisplay();
				Shell shell = new Shell(display);
				shell.setLayout(new GridLayout(4,false));
				EditJobDialog dialog = new EditJobDialog(shell,style);
				dialog.open();
			}
			
		});
		
		Button pricecalc_btn = new Button(this, SWT.NONE);
		pricecalc_btn.addListener(SWT.Selection,new Listener() {
			@Override
			public void handleEvent(Event e) {
				
			}
		});
		pricecalc_btn.setText("Hinta-arvio");
		new Label(this, SWT.NONE);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
