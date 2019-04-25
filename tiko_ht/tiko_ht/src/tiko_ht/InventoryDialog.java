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
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;

/*
 * Class for creating a graphical dialog for viewing inventory status.
 * Also allows user to a new catalogue given by the vendor
 *
 */
public class InventoryDialog extends Dialog {

	//Attributes
	protected Object result;
	protected Shell shell;
	java.util.List<String[]> all_items = new ArrayList<String[]>();
	
	//Constructor
	public InventoryDialog(Shell parent, int style) {
		super(parent, style);
		setText("Tarvikkeet");
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
		shell = new Shell(getParent(), getStyle());
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		shell.setSize(422, 339);
		shell.setText(getText());
		
		List itemname_list = new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		itemname_list.setBounds(10, 59, 141, 202);
		
		Label itemName_lbl = new Label(shell, SWT.NONE);
		itemName_lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		itemName_lbl.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		itemName_lbl.setBounds(60, 23, 50, 30);
		itemName_lbl.setText("Nimi\r\n");
		
		Label itemPrice_lbl = new Label(shell, SWT.NONE);
		itemPrice_lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		itemPrice_lbl.setText("Yksikk\u00F6hinta");
		itemPrice_lbl.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		itemPrice_lbl.setBounds(303, 23, 88, 30);
		
		Label itemQuantity_lbl = new Label(shell, SWT.NONE);
		itemQuantity_lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		itemQuantity_lbl.setText("Varastotilanne");
		itemQuantity_lbl.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		itemQuantity_lbl.setBounds(167, 23, 101, 30);
		
		List itemquantity_list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		itemquantity_list.setBounds(157, 59, 120, 202);
		
		List itemprice_list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		itemprice_list.setBounds(294, 59, 112, 202);
		
		Button newCatalogue_btn = new Button(shell, SWT.NONE);
		newCatalogue_btn.setBounds(10, 281, 100, 25);
		newCatalogue_btn.setText("Uusi hinnasto");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel.setBounds(314, 267, 55, 15);
		lblNewLabel.setText("Sis. alv");
		
		DBHandler db = new DBHandler();
		//Fetches all the items from the database that have more than 0 in stock
		all_items = db.getAllItems();
		//Adds item name, selling price and stock to lists for viewing.
		for(int i=0; i<all_items.size(); i++) {
			itemname_list.add("["+i +"] "+ all_items.get(i)[0]);
			itemquantity_list.add("["+i +"] "+ all_items.get(i)[4] + " " + all_items.get(i)[2]);
			if(Boolean.parseBoolean(all_items.get(i)[3])) {
				itemprice_list.add("["+i +"] "+ Math.round(Double.parseDouble(all_items.get(i)[1])*1.10 * 100.0) / 100.0 + " e");
			}else {
				itemprice_list.add("["+i +"] "+  Math.round(Double.parseDouble(all_items.get(i)[1])*1.24 * 100.0) / 100.0 + " e");
			}
		}
		
		//Listener for button "Uusi hinnasto". Updates item prices and adds new items to the database according
		//to the vendors new catalogue.
		//Requires a text file "uusi_hinnasto.txt" in the local directory.
		//Each item in "uusi_hinnasto.txt" should be in a new line and 
		//formatted this way: Pistorasia,10.00,kpl,false
		//Each values is separated by a comma. The first value is the name of the product.
		//Second value is requisition price. Third value is physical quantity, for example 'kpl' or 'cm'.
		//Last value is whether the item is literature or not.
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
								//Else add the item into the db
								if(db.itemInDB(nimi)) {
									db.updateItem(nimi, req_price);
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
								itemname_list.add("["+i +"] "+ all_items.get(i)[0]);
								itemquantity_list.add("["+i +"] "+ all_items.get(i)[4] + " " + all_items.get(i)[2]);
								if(Boolean.parseBoolean(all_items.get(i)[3])) {
									itemprice_list.add("["+i +"] "+ Math.round(Double.parseDouble(all_items.get(i)[1])*1.10 * 100.0) / 100.0 + " e");
								}else {
									itemprice_list.add("["+i +"] "+ Math.round(Double.parseDouble(all_items.get(i)[1])*1.24 * 100.0) / 100.0 + " e");
								}
							}
							//The archived file is renamed after the date when it was archived
							Date date = new Date();
							SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
							String stored_inventory = "./historia/" + formatter.format(date) + "_hinnasto.txt";
							
							//Rename and move files
							Files.move(Paths.get("./nykyinen_hinnasto.txt"), Paths.get(stored_inventory), StandardCopyOption.REPLACE_EXISTING);
							Files.move(Paths.get("./uusi_hinnasto.txt"), Paths.get("./nykyinen_hinnasto.txt"), StandardCopyOption.REPLACE_EXISTING);
							
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
