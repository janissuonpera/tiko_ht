package tiko_ht;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;

public class InventoryDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	java.util.List<String[]> all_items = new ArrayList<String[]>();
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public InventoryDialog(Shell parent, int style) {
		super(parent, style);
		setText("Tarvikkeet");
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(333, 334);
		shell.setText(getText());
		
		List itemname_list = new List(shell, SWT.BORDER);
		itemname_list.setBounds(10, 59, 98, 202);
		
		Label itemName_lbl = new Label(shell, SWT.NONE);
		itemName_lbl.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		itemName_lbl.setBounds(33, 18, 50, 30);
		itemName_lbl.setText("Nimi\r\n");
		
		Label itemPrice_lbl = new Label(shell, SWT.NONE);
		itemPrice_lbl.setText("Hinta");
		itemPrice_lbl.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		itemPrice_lbl.setBounds(239, 18, 56, 30);
		
		Label itemQuantity_lbl = new Label(shell, SWT.NONE);
		itemQuantity_lbl.setText("M\u00E4\u00E4r\u00E4");
		itemQuantity_lbl.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.NORMAL));
		itemQuantity_lbl.setBounds(132, 18, 67, 30);
		
		List itemquantity_list = new List(shell, SWT.BORDER);
		itemquantity_list.setBounds(114, 59, 98, 202);
		
		List itemprice_list = new List(shell, SWT.BORDER);
		itemprice_list.setBounds(220, 59, 98, 202);
		
		Button newCatalogue_btn = new Button(shell, SWT.NONE);
		newCatalogue_btn.setBounds(10, 270, 100, 25);
		newCatalogue_btn.setText("Uusi hinnasto");
		
		DBHandler db = new DBHandler();
		all_items = db.getAllItems();
		for(int i=0; i<all_items.size(); i++) {
			itemname_list.add(all_items.get(i)[0]);
			itemquantity_list.add(all_items.get(i)[4] + " " + all_items.get(i)[2]);
			if(Boolean.parseBoolean(all_items.get(i)[3])) {
				itemprice_list.add(Double.parseDouble(all_items.get(i)[1])*1.10 + " e");
			}else {
				itemprice_list.add(Double.parseDouble(all_items.get(i)[1])*1.24 + " e");
			}
		}
		
		//Button listeners
		newCatalogue_btn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				File inventory_file = new File("./uusi_hinnasto.txt");
				//Updating inventory happens by checking if a file named uusi_hinnasto.txt is in the local directory
				//If it exists, it's read and the database is updated. The file is renamed to nykyinen_hinnasto and the old file
				//is transfered to historia directory
				if(inventory_file.exists()) {
					MessageBox dialog =
						    new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
					dialog.setText("Ota käyttöön uusi hinnasto?");
					dialog.setMessage("Kansiostasi löytyi tiedosto nimeltä uusi_hinnasto.txt\nHaluatko varmasti ottaa uuden hinnaston käyttöön?");

					// open dialog and await user selection
					int user_selection = dialog.open();
					if(user_selection == SWT.OK) {
						try {
							//Read the uusi_hinnasto.txt file into a string, split it by lines
							String[] new_items = new String(Files.readAllBytes(Paths.get("./uusi_hinnasto.txt")), "UTF-8").split("[\\r\\n]+");
							for(String item : new_items) {
								String nimi = item.split(",")[0];
								double req_price = Double.parseDouble(item.split(",")[1]);
								String units = item.split(",")[2];
								boolean literature = Boolean.parseBoolean(item.split(",")[3]);
								double quantity = 0;
								if(item.split(",").length>4) {
									quantity = Double.parseDouble(item.split(",")[4]);
								}
								//If read item from uusi_hinnasto.txt is not a completely new item, update its new price into the db
								if(itemname_list.indexOf(item.split(",")[0])!=-1) {
									db.updateItem(item.split(",")[0], Double.parseDouble(item.split(",")[1]));
								}else {
									db.addItem(nimi, req_price, units, literature, quantity);
								}
							}
							//Refresh UI
							all_items = db.getAllItems();
							itemname_list.removeAll();
							itemquantity_list.removeAll();
							itemprice_list.removeAll();
							for(int i=0; i<all_items.size(); i++) {
								itemname_list.add(all_items.get(i)[0]);
								itemquantity_list.add(all_items.get(i)[4] + " " + all_items.get(i)[2]);
								if(Boolean.parseBoolean(all_items.get(i)[3])) {
									itemprice_list.add(Double.parseDouble(all_items.get(i)[1])*1.10 + " e");
								}else {
									itemprice_list.add(Double.parseDouble(all_items.get(i)[1])*1.24 + " e");
								}
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else {
					MessageBox dialog =
						    new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("Ei tiedostoa!");
					dialog.setMessage("Kansiostasi ei löydy tiedostoa nimeltä uusi_hinnasto.txt. Varmista onko tiedosto varmasti oikeassa paikassa.");
					dialog.open();
				}
			}
			
		});
		
	}
}
