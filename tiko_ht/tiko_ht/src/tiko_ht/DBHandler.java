package tiko_ht;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.widgets.DateTime;

public class DBHandler {

	// tietokannan ja kayttajan tiedot
	private static final String PROTOKOLLA = "jdbc:postgresql:";
	private static final String PALVELIN = "localhost";
	private static final int PORTTI = 5432;
	private static final String TIETOKANTA = "postgres";
	private static final String KAYTTAJA = "postgres";
	private static final String SALASANA = "";
	private final int REGULAR_WORK = 45;
	private final int PLANNING_WORK = 55;
	private final int HELPING_WORK = 35;
	private Connection con = null;
	public PreparedStatement prep_stmt;
	public Statement stmt;
	public ResultSet result;

	// Getter
	public Connection getConnection() {
		return con;
	}
	// Setter
	public void setConnection(Connection new_con) {
		con = new_con;
	}
	// Vaihe 1: yhteyden ottaminen tietokantaan

	public void connect() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(PROTOKOLLA + "//" + PALVELIN + ":"
					+ PORTTI + "/" + TIETOKANTA, KAYTTAJA, SALASANA);
			setConnection(con);

			// Set proper schema.
			stmt = con.createStatement();

			stmt.execute("SET search_path TO tiko_ht");

			// Set auto commit to false to avoid mistakes.
			con.setAutoCommit(false);

		} catch (SQLException poikkeus) {

			// Vaihe 3.2: tahan toiminta mahdollisessa virhetilanteessa

			System.out.println(
					"Tapahtui seuraava virhe: " + poikkeus.getMessage());
		}

	}
	// Get customers.
	public List<String> getCustomers() {
		connect();
		List<String> customers = new ArrayList<String>();
		try {
			stmt = con.createStatement();
			result = stmt.executeQuery("select nimi from asiakas");
			while (result.next()) {
				customers.add(result.getString(1));
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		closeConnection();
		return customers;

	}
	public void createCustomer(String name, String address, boolean company,
			String ssn) {
		int customer_id = 0;
		String SQL = "INSERT INTO asiakas(asiakas_id,nimi,osoite,yritys,hetu) "
				+ "VALUES(?,?,?,?,?)";
		connect();
		try {
			prep_stmt = con.prepareStatement(SQL);
			stmt = getConnection().createStatement();
			ResultSet rts = stmt.executeQuery("SELECT asiakas_id from asiakas");
			while (rts.next()) {
				customer_id = rts.getInt(1);
			}
			customer_id++;
			prep_stmt.setInt(1, customer_id);
			prep_stmt.setString(2, name);
			prep_stmt.setString(3, address);
			prep_stmt.setBoolean(4, company);
			prep_stmt.setString(5, ssn);
			prep_stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
	}
	// Create a new job.
	public void createJob(String customer_name, String job_name, String address,
			Boolean contract) {
		connect();
		int customer_id = 0;
		int job_id = 0;
		String SQL = "INSERT INTO tyokohde(tyokohde_id,asiakas_id,nimi,osoite,valmis,urakka)"
				+ "VALUES(?,?,?,?,?,?)";
		try {
			prep_stmt = con.prepareStatement(
					"SELECT asiakas_id from asiakas WHERE nimi = ?");
			// Find the customer id with customer name.'
			prep_stmt.setString(1, customer_name);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				customer_id = result.getInt(1);
			}
			prep_stmt.clearBatch();

			stmt = con.createStatement();
			result = stmt.executeQuery(
					"SELECT tyokohde_id from tyokohde ORDER BY tyokohde_id");
			// Find next available job id.
			while (result.next()) {
				job_id = result.getInt(1);
			}
			// Add one into job id to make a new id.
			job_id++;
			prep_stmt = con.prepareStatement(SQL);
			prep_stmt.setInt(1, job_id);
			prep_stmt.setInt(2, customer_id);
			prep_stmt.setString(3, job_name);
			prep_stmt.setString(4, address);
			prep_stmt.setBoolean(5, false);
			prep_stmt.setBoolean(6, contract);
			prep_stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
	}
	// Gets all items from database and return two dimensional arraylist.
	public List<String[]> getAllItems() {
		List<String[]> items = new ArrayList<String[]>();
		connect();
		try {
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"select nimi,myynti_hinta,yksikko,kirjallisuus from tarvike WHERE varasto_tilanne > 0");
			while (result.next()) {
				String[] item = {result.getString(1), result.getString(2),
						result.getString(3), result.getString(4)};
				items.add(item);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		return items;
	}

	public void createTask(String job_name, String work_type, int hours,
			DateTime date, List<String[]> items) {

		
		// Get next available task id.
		int task_id = getNextTaskId();
		// Price of hours * work type cost.
		double price = 0;
		// Get connection.
		connect();
		// Get job id by its name
		int job_id = getJobIdByName(job_name);

		// Format date into a correct format.
		Calendar cal = Calendar.getInstance();
		cal.set(date.getYear(), date.getMonth(), date.getDay());
		java.sql.Date sql_date = new java.sql.Date(cal.getTimeInMillis());

		// Initialize SQL statement strings.
		String taskSQL = "INSERT INTO suoritus " + " VALUES(?,?,?,?,?,?)";

		String itemSQL = " INSERT INTO suoritus_tarvike VALUES(?,?,?,?,?)";

		try {
			// Calculating total price of hours.
			if (work_type.equals("Työ")) {
				price = hours * REGULAR_WORK;
			} else if (work_type.equals("Suunnittelu")) {
				price = hours * PLANNING_WORK;
			} else {
				price = hours * HELPING_WORK;
			}

			prep_stmt = con.prepareStatement(taskSQL);
			prep_stmt.setInt(1, task_id);
			prep_stmt.setInt(2, job_id);
			prep_stmt.setDate(3, sql_date);
			prep_stmt.setInt(4, hours);
			prep_stmt.setString(5, work_type);
			prep_stmt.setDouble(6, price);
			prep_stmt.executeUpdate();

			int item_id = 0;
			double item_price = 0;
			double total_item_price = 0;
			double pct = 0;
			prep_stmt = con.prepareStatement(itemSQL);

			for (int i = 0; i < items.size(); i++) {
				stmt = con.createStatement();
				result = stmt.executeQuery(
						"SELECT tarvike_id,nimi,myynti_hinta from tarvike");
				while (result.next()) {
					if (items.get(i)[0].equals(result.getString(2))) {
						item_id = result.getInt(1);
						item_price = result.getDouble(3);
						// Calculate total price percentage.
						total_item_price = item_price
								* Integer.parseInt(items.get(i)[1]);
						// Check if there is a discount percentage.
						if (Integer.parseInt(items.get(i)[2]) > 0) {
							pct = Double.valueOf(items.get(i)[2]);
							pct = (100 - pct) / 100;
							total_item_price *= pct;
						}
						// Set parameters
						prep_stmt.setInt(1, item_id);
						prep_stmt.setInt(2, task_id);
						prep_stmt.setDouble(3, Double.valueOf(items.get(i)[1]));
						prep_stmt.setDouble(4, total_item_price);
						prep_stmt.setInt(5, Integer.parseInt(items.get(i)[2]));
						prep_stmt.executeUpdate();
						prep_stmt.clearParameters();
					}
				}
			}
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();

	}
	// Fetch jobs and return them as array list.
	public List<String[]> getJobs(boolean exceptContracts) {
		List<String[]> jobs = new ArrayList<String[]>();
		connect();

		try {
			stmt = con.createStatement();
			if (exceptContracts) {
				// Return name only of jobs that aren't contracts.
				result = stmt.executeQuery(
						"select nimi from tyokohde WHERE urakka=false");
				while (result.next()) {
					String[] job = {result.getString(1)};
					jobs.add(job);
				}
			} else {
				// Return name and contract boolean value of all jobs.
				result = stmt.executeQuery("select nimi,urakka from tyokohde");
				while (result.next()) {
					String[] job = {result.getString(1),
							String.valueOf(result.getBoolean(2))};
					jobs.add(job);
				}
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		return jobs;
	}
	// Gets the used items for the chosen job.
	public List<String[]> getJobItems(String job_name, boolean getOnlyPrices,
			boolean closeConnection) {

		List<String[]> items = new ArrayList<String[]>();
		if(closeConnection){
			connect();
		}
		// Get job identifier by its name
		int job_id = getJobIdByName(job_name);
		// SQL query string to get item data
		// from the job
		String SQL = "SELECT tarvike.nimi, maara, tarvike.yksikko,stk.hinta,myynti_hinta,alennus_prosentti,kirjallisuus "
				+ " FROM ((suoritus_tarvike as stk JOIN suoritus as st ON stk.suoritus_id = st.suoritus_id)JOIN tarvike ON stk.tarvike_id = tarvike.tarvike_id) "
				+ " JOIN tyokohde ON st.tyokohde_id = tyokohde.tyokohde_id"
				+ " WHERE tyokohde.tyokohde_id = ? ";
	
		try {
			prep_stmt = con.prepareStatement(SQL);
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();

			while (result.next()) {
				String[] item = new String[7];
				// Insert name
				item[0] = result.getString(1);
				if (getOnlyPrices == true) {
					item[1] = String.valueOf(result.getDouble(4));
				} else {
					// Insert amount
					item[1] = String.valueOf(result.getDouble(2));
					// Insert unit
					item[2] = result.getString(3);
					// Insert price
					item[3] = String.valueOf(result.getDouble(4));
					// Insert selling price
					item[4] = String.valueOf(result.getDouble(5));
					// Insert discount pct
					item[5] = String.valueOf(result.getInt(6));
					// Insert tax identifier
					item[6] = result.getString(7);
				}
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (closeConnection) {
			closeConnection();
		}
		return items;
	}

	// Sets the job finished value into true.
	public void setJobFinished(String job_name) {
		connect();
		try {
			prep_stmt = con.prepareStatement(
					"UPDATE tyokohde SET valmis = true WHERE nimi = ?");
			prep_stmt.setString(1, job_name);
			int updated = prep_stmt.executeUpdate();
			if (updated > 0) {
				con.commit();
				closeConnection();
			}
			createInvoice(job_name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// Gets the job finished boolean value.
	public boolean getFinishedValue(String job_name) {
		connect();

		boolean ready = false;
		try {
			prep_stmt = con.prepareStatement(
					"SELECT valmis from tyokohde WHERE nimi = ?");
			prep_stmt.setString(1, job_name);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				ready = result.getBoolean(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		return ready;
	}

	// Returns the hours of done for a specific job.
	public List<String> getTaskHours(String job_name) {
		List<String> hoursAndTypes = new ArrayList<String>();
		connect();
		int job_id = getJobIdByName(job_name);
		try {
			prep_stmt = con.prepareStatement(
					"SELECT tyyppi,tunnit,hinta FROM suoritus WHERE tyokohde_id = ?");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				hoursAndTypes.add(result.getString(1) + " | "
						+ String.valueOf(result.getInt(2)) + "h | "
						+ String.valueOf(result.getDouble(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		return hoursAndTypes;
	}
	// Get job id with the job name.
	public int getJobIdByName(String job_name) {
		int job_id = 0;
		try {
			// Get the job id with the job name.
			prep_stmt = con.prepareStatement(
					"SELECT tyokohde_id FROM tyokohde WHERE nimi = ?");
			prep_stmt.setString(1, job_name);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				job_id = result.getInt(1);
			}
			prep_stmt.clearBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return job_id;
	}

	// Get job name with the job id.
	public String getJobNameById(int job_id, boolean connect) {
		if (connect) {
			connect();
		}
		String job_name = "";
		try {
			// Get the job id with the job name.
			prep_stmt = con.prepareStatement(
					"SELECT nimi FROM tyokohde WHERE tyokohde_id = ?");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				job_name = result.getString(1);
			}
			prep_stmt.clearBatch();
			prep_stmt.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (connect) {
			closeConnection();
		}
		return job_name;
	}
	// Adds a discount to the work type hours.
	public void addDiscount(String job_name, String work_type,
			int discount_pct) {
		connect();
		double pct = Double.valueOf(discount_pct);
		pct = (100 - pct) / 100;
		int job_id = getJobIdByName(job_name);
		

		String SQL = "UPDATE suoritus SET hinta = ? * hinta  WHERE tyokohde_id = ? AND tyyppi = ?";
		try {
			prep_stmt = con.prepareStatement(SQL);
			prep_stmt.setDouble(1, pct);
			prep_stmt.setInt(2, job_id);
			prep_stmt.setString(3, work_type);

			prep_stmt.executeUpdate();
			con.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
	}

	// Fetches name of all invoices, returns invoice id and name
	public List<String> getInvoicesIdAndName() {
		List<String> invoices = new ArrayList<String>();
		connect();
		try {
			stmt = con.createStatement();
			ResultSet new_result = stmt.executeQuery(
					"select lasku_id, tyokohde_id from lasku ORDER BY lasku_id");
			while (new_result.next()) {
				String lasku_id = (Integer.toString(new_result.getInt(1)));
				int tyokohde_id = new_result.getInt(2);
				String tyokohde_nimi = getJobNameById(tyokohde_id, false);
				invoices.add(lasku_id + " " + tyokohde_nimi);
			}
			stmt.close();
			new_result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
		return invoices;
	}

	// Search if there are unpaid and due date passed invoices. Create new
	// invoice if that's the case.
	public void checkInvoiceState() {
		connect();

		java.sql.Date currentDate = java.sql.Date
				.valueOf(LocalDateTime.now().toLocalDate());
		try {
			ResultSet prep_result = null;
			stmt = con.createStatement();
			// Select job_id's from invoices.
			result = stmt.executeQuery("SELECT tyokohde_id FROM lasku");
			// Selects the most recent invoice for the job.
			prep_stmt = con.prepareStatement("SELECT era_pvm,maksettu "
					+ " FROM  lasku "
					+ " WHERE lasku_lkm=(SELECT MAX(lasku_lkm) FROM lasku) AND tyokohde_id=?");

			while (result.next()) {
				// Save job id.
				int job_id = result.getInt(1);
				// Query the most recent invoice with the current job id.
				prep_stmt.setInt(1, job_id);
				prep_result = prep_stmt.executeQuery();
				while (prep_result.next()) {
					// If the most recent invoice hasn't been paid and the due
					// date has passed, make new invoice.
					if (prep_result.getBoolean(2) == false
							&& currentDate.after(prep_result.getDate(1))) {
						String job_name = getJobNameById(job_id, false);
						createInvoice(job_name);
					}
				}
				prep_stmt.clearParameters();
			}
			if (prep_result != null) {
				prep_result.close();
			}
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
	}

	// Returns all invoice data
	public Invoice getFullInvoice(int id) {
		Invoice invoice = null;
		connect();
		try {
			stmt = con.createStatement();
			prep_stmt = con
					.prepareStatement("select * from lasku where lasku_id = ?");
			prep_stmt.setInt(1, id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				int invoice_id = result.getInt(1);
				int job_id = result.getInt(2);
				Date date = result.getDate(3);
				Date due_date = result.getDate(4);
				String type = result.getString(5);
				int count = result.getInt(6);
				double hour_price = result.getDouble(7);
				double total_price = result.getDouble(8);
				boolean paid = result.getBoolean(9);
				invoice = new Invoice(invoice_id, job_id, date, due_date, type,
						count, hour_price, total_price, paid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
		return invoice;
	}

	public void createInvoice(String job_name) {
		connect();
		// Get job id by its name
		int job_id = getJobIdByName(job_name);
		// Get next free task id.
		int invoice_id = 0;
		int invoice_count = 0;
		double final_price = 0;
		double hours_price = 0;
		boolean contract = false;
		String invoice_type = "Normaali";
		// Get current date in sql format.
		java.sql.Date date = java.sql.Date
				.valueOf(LocalDateTime.now().toLocalDate());
		// Set due date to be 1 month after today.
		java.sql.Date dueDate = java.sql.Date
				.valueOf(LocalDateTime.now().plusMonths(1).toLocalDate());
		

		try {
			// Check if the job is a contract.
			prep_stmt = con.prepareStatement(
					"SELECT urakka FROM tyokohde WHERE tyokohde_id = ?");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				contract = result.getBoolean(1);
			}

			// Get the next available invoice_id.
			result = stmt.executeQuery(
					"SELECT lasku_id FROM lasku ORDER BY lasku_id");
			while (result.next()) {
				invoice_id = result.getInt(1);
			}
			invoice_id++;

			// Get invoice count and due date from all invoices of selected job.
			prep_stmt = con.prepareStatement(
					"SELECT lasku_lkm,era_pvm,hinta FROM lasku WHERE tyokohde_id = ?");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				invoice_count = result.getInt(1);
				final_price = result.getDouble(3);
				dueDate = result.getDate(2);
			}
			invoice_count++;
			// If the invoice hasn't been paid in time, set due date to be
			// in 5 days from the last due date.
			if (invoice_count > 1) {
				dueDate = java.sql.Date
						.valueOf(dueDate.toLocalDate().plusDays(5));
			}
			prep_stmt.clearBatch();

			// Get total price of hours.
			prep_stmt = con.prepareStatement("SELECT SUM(hinta) "
					+ " FROM suoritus " + " WHERE tyokohde_id = ? "
					+ " group by tyokohde_id");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				hours_price = result.getDouble(1);
			}

			// If it is the first invoice, then fetch the data from database.
			if (invoice_count == 1) {
				// If the job is a contract, get the item prices from
				// urakkalista-table.
				if (contract) {
					// Query to fetch total hour price
					prep_stmt = con.prepareStatement(
							"SELECT (myynti_hinta * maara),kirjallisuus"
									+ " FROM tarvike as tk JOIN urakkalista as ul ON tk.tarvike_id = ul.tarvike_id "
									+ " WHERE urakka_id IN(select urakka_id FROM urakkatarjous WHERE tyokohde_id = ?)");
					prep_stmt.setInt(1, job_id);
					result = prep_stmt.executeQuery();
					while (result.next()) {
						// If the item is literature, add 10% tax. Else, add 24%
						// tax.
						if (result.getBoolean(2)) {
							final_price += result.getDouble(1) * 1.10;
						} else {
							final_price += result.getDouble(1) * 1.24;
						}
					}
					final_price += hours_price;
					prep_stmt.clearParameters();

					// If the job is not a contract, get the item price from the
					// suoritus_tarvike-table.
				} else {
					// Query to fetch item prices and their tax value.
					prep_stmt = con
							.prepareStatement("SELECT (stk.hinta),kirjallisuus "
									+ " FROM suoritus_tarvike as stk JOIN suoritus ON stk.suoritus_id = suoritus.suoritus_id "
									+ "JOIN tarvike ON stk.tarvike_id = tarvike.tarvike_id "
									+ " WHERE tyokohde_id = ?");
					prep_stmt.setInt(1, job_id);
					result = prep_stmt.executeQuery();
					while (result.next()) {
						// If the item is literature, add 10% tax. Else, add 24%
						// tax.
						if (result.getBoolean(2)) {
							final_price += result.getDouble(1) * 1.10;
						} else {
							final_price += result.getDouble(1) * 1.24;
						}
					}
					final_price += hours_price;
				}
			}
			// Set invoice type depending on invoice count.
			if (invoice_count == 2) {
				invoice_type = "Muistutus";
				// Add billing fee.
				final_price += 5;
			} else if (invoice_count > 2) {
				invoice_type = "Karhu";
				// Add late payment fee and billing fee.
				final_price = final_price * 1.16 + 5;
			}

			prep_stmt = con.prepareStatement(
					"INSERT INTO lasku VALUES (?,?,?,?,?,?,?,?,?)");
			prep_stmt.setInt(1, invoice_id);
			prep_stmt.setInt(2, job_id);
			prep_stmt.setDate(3, date);
			prep_stmt.setDate(4, dueDate);
			prep_stmt.setString(5, invoice_type);
			prep_stmt.setInt(6, invoice_count);
			prep_stmt.setDouble(7, hours_price);
			prep_stmt.setDouble(8, final_price);
			prep_stmt.setBoolean(9, false);
			prep_stmt.executeUpdate();
			con.commit();
			closeConnection();
			printInvoice(invoice_id);
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
	}

	// Deletes the selected job.
	public boolean deleteJob(String job_name) {
		connect();
		int job_id = getJobIdByName(job_name);

		try {
			prep_stmt = con.prepareStatement(
					"DELETE FROM tyokohde WHERE tyokohde_id = ?");
			prep_stmt.setInt(1, job_id);
			int changedRows = prep_stmt.executeUpdate();
			if (changedRows > 0) {
				con.commit();
				closeConnection();
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
		return false;
	}
	// Writes all data from invoice to a text file.
	public void printInvoice(int invoice_id) {
		// Get the full invoice.
		Invoice invoice = getFullInvoice(invoice_id);
		// If true, the job is a contract. Opposite if false.
		boolean contract = false;

		// Get job id.
		int job_id = invoice.getTyokohde_id();
		// Get job name
		String job_name = getJobNameById(job_id, true);
		// Connect to database
		connect();
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("lasku_" + invoice_id + ".txt"),
				Charset.defaultCharset()))) {

			writer.write("Lasku (Tunnus: " + invoice_id + ")");
			writer.newLine();
			writer.newLine();

			// Get job info and print it.
			writer.write("Työkohteen tiedot:");
			writer.newLine();
			writer.write("-------------------------------------");
			writer.newLine();
			prep_stmt = con.prepareStatement(
					"SELECT nimi,osoite,urakka FROM tyokohde WHERE tyokohde_id = ?");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				// Get the contract value.
				contract = result.getBoolean(3);

				writer.write("Nimi: " + result.getString(1));
				writer.newLine();
				writer.write("Osoite: " + result.getString(2));
				writer.newLine();
				writer.write(
						"Urakka: " + ((result.getBoolean(3)) ? "Kyllä" : "Ei"));
				writer.newLine();
				writer.newLine();
			}
			prep_stmt.close();

			// Get customer info and print it.
			prep_stmt = con.prepareStatement(
					"SELECT * FROM asiakas WHERE asiakas_id =("
							+ "SELECT asiakas_id FROM tyokohde WHERE tyokohde_id = ?)");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			writer.write("Asiakkaan tiedot:");
			writer.newLine();
			writer.write("-------------------------------------");
			writer.newLine();
			while (result.next()) {
				writer.write("Kohde: " + result.getString(2));
				writer.newLine();
				writer.write("Osoite: " + result.getString(3));
				writer.newLine();
				writer.write("Status: " + ((result.getBoolean(4))
						? "Yritys"
						: "Yksityis-asiakas"));
				writer.newLine();
				writer.write("Henkilötunnus: " + (result.getString(5) == null
						? "Ei saatavilla"
						: result.getString(5)));
				writer.newLine();
				writer.newLine();
			}
			result.close();
			prep_stmt.close();
			
			
			// Get all items used for the job.
			writer.write("Käytetyt tarvikkeet");
			writer.newLine();
			writer.write("-------------------------");
			writer.newLine();
			double taxPct = 1.24;
			double taxlessPrice = 0;
			if (!contract) {
				List<String[]> items = getJobItems(job_name, false, false);
				for (String[] item : items) {
					taxlessPrice += Double.valueOf(item[3]);
					// Calculate tax
					if (Boolean.valueOf(item[4])) {
						taxPct = 1.1;
					} else {
						taxPct = 1.24;
					}
					writer.write("Nimi: " + item[0]);
					writer.newLine();
					writer.write("Määrä: " + item[1] + " " + item[2]);
					writer.newLine();
					writer.write("Yksikköhinta: "
							+ Double.parseDouble(item[4]) * taxPct + " euroa");
					writer.newLine();
					writer.write("Kokonaishinta: "
							+ String.valueOf(
									Double.parseDouble(item[3]) * taxPct)
							+ " euroa");
					writer.newLine();
					writer.write(
                            "Arvonlisäveroton-hinta: " + item[3] + " euroa");
					writer.newLine();
					writer.write("Alennusprosentti: " + item[5] + "%");
					writer.newLine();
					writer.write("Arvonlisävero %: " + ((taxPct - 1) * 100));
					writer.newLine();
					writer.newLine();
				}
			} else {
				List<String[]> items = getContractItems(job_name, false);
				for (String[] item : items) {
					taxlessPrice += Double.valueOf(item[3]);
					// Calculate tax.
					if (Boolean.valueOf(item[4])) {
						// If it's literature, tax is 10%
						taxPct = 1.1;
					} else {
						taxPct = 1.24;
					}
					writer.write("Nimi: " + item[0]);
					writer.newLine();
					writer.write("Määrä: " + item[1] + " " + item[2]);
					writer.newLine();
					writer.write("Kokonaishinta: "
							+ String.valueOf(
									(Double.parseDouble(item[3]) * taxPct))
							+ " euroa");
					writer.newLine();
					writer.write(
                            "Arvonlisäveroton-hinta: " + item[3] + " euroa");
					writer.newLine();
					writer.write("Arvonlisävero %: " + ((taxPct - 1) * 100));
					writer.newLine();
					writer.newLine();
				}
			}
			taxlessPrice += (invoice.getTuntien_hinta() * 0.80645);

			// Get all hours and their types and print.
			prep_stmt = con
					.prepareStatement("select tyyppi, SUM(tunnit), SUM(hinta) "
							+ " FROM suoritus " + " WHERE tyokohde_id=? "
							+ " GROUP BY 1");
			prep_stmt.setInt(1, job_id);
			
			writer.write("Tuntierittely: ");
			writer.newLine();
			writer.write("-------------------------");
			writer.newLine();
			result = prep_stmt.executeQuery();
			
			while (result.next()) {
				writer.write("Tyyppi: "+result.getString(1));
				writer.newLine();
				writer.write("Määrä: " + String.valueOf(result.getInt(2)) + " tuntia");
				writer.newLine();
				writer.write("Hinta: " + String.valueOf(result.getDouble(3)) + " euroa");
				writer.newLine();
				writer.write("Arvonlisävero: 24%");
				writer.newLine();
				writer.newLine();
			}
			
			writer.write("-------------------------");
			// Get basic info for the invoice and print.
			writer.newLine();
			writer.write("Tuntien hinta yhteensa: "
					+ String.valueOf(invoice.getTuntien_hinta()) + " euroa");
			writer.newLine();
			writer.write("Kokonaishinta: " + String.valueOf(invoice.getHinta())
					+ " euroa");
			writer.newLine();
			writer.write("Arvonlisäveroton-hinta: "
					+ String.valueOf(taxlessPrice) + " euroa");
			writer.newLine();
			writer.write("Laskun tyyppi: " + invoice.getTyyppi());
			writer.newLine();
			writer.newLine();
			writer.write("Eräpäivä: " + invoice.getEra_pvm());
			writer.newLine();
			writer.write(
					"Maksa lasku eräpäivään mennessä. Tilinumero: FI86 2139 2199 2938293");
			writer.newLine();
			writer.newLine();
			writer.write("Terveisin, Seppo Sähkötärsky Oy.");
			writer.newLine();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
	}
	// Deletes the selected invoice.
	public boolean deleteInvoice(int invoice_id) {
		connect();
		int success = 0;
		try {
			prep_stmt = con
					.prepareStatement("DELETE FROM lasku WHERE lasku_id = ?");
			prep_stmt.setInt(1, invoice_id);
			success = prep_stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
		
		// If there were deleted rows, return true. Else, return false.
		if (success >= 1) {
			return true;
		} else {
			return false;
		}
	}
	// Fetches all contracts from the database and returns their name as a list.
	public List<String> getContracts() {
		List<String> contracts = new ArrayList<String>();
		connect();

		try {
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"SELECT nimi FROM tyokohde WHERE urakka=true AND tyokohde_id NOT IN(SELECT tyokohde_id FROM urakkatarjous)");
			while (result.next()) {
				contracts.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
		return contracts;
	}
	
	// Returns the contracts which have offers made of as a list..
	public List<String> getContractOffers() {
		connect();
		List<String> contractOffers = new ArrayList<String>();
		try {
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"SELECT nimi FROM tyokohde WHERE tyokohde_id IN (SELECT tyokohde_id FROM urakkatarjous)");
			while (result.next()) {
				contractOffers.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
		return contractOffers;
	}

	// Creates a contract and adds it to the database (urakkatarjous table)
	public void createContract(String job_name, int instalments) {

		int contract_id = 0;
		connect();
		int job_id = getJobIdByName(job_name);
		try {
			stmt = con.createStatement();
			// Get the next available contract id.
			result = stmt.executeQuery(
					"SELECT urakka_id FROM urakkatarjous ORDER BY urakka_id ");
			while (result.next()) {
				contract_id = result.getInt(1);
			}
			contract_id++;

			prep_stmt = con.prepareStatement(
					"INSERT INTO urakkatarjous VALUES(?,?,?)");
			prep_stmt.setInt(1, contract_id);
			prep_stmt.setInt(2, job_id);
			prep_stmt.setInt(3, instalments);
			prep_stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
	}

	// Adds items to urakka_Tarvike table and hours and types to suoritus table.
	public boolean addToContract(String contract, List<String[]> items,
			List<String[]> hours) {

		
		// Identifier for the contract.
		int contract_id = 0;
		// Get next available task with the method.
		int task_id = getNextTaskId();
		// Connect to db.
		connect();
		// Get current date.
		Calendar cal = Calendar.getInstance();
		java.sql.Date sql_date = new java.sql.Date(cal.getTimeInMillis());
		// Get job id by its name.
		int job_id = getJobIdByName(contract);
		String taskSQL = "INSERT INTO suoritus VALUES(?,?,?,?,?,?)";
		String itemSQL = "INSERT INTO urakkalista VALUES (?,?,?)";
	

		try {

			// Get the contract id with the name of the contract.
			prep_stmt = con.prepareStatement(
					"SELECT urakka_id FROM urakkatarjous WHERE tyokohde_id = (SELECT tyokohde_id FROM tyokohde WHERE nimi = ?)");
			prep_stmt.setString(1, contract);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				contract_id = result.getInt(1);
			}
			prep_stmt.clearBatch();

			prep_stmt = con.prepareStatement(taskSQL);
			// Add data to suoritus-table.
			for (String[] hour : hours) {
				prep_stmt.setInt(1, task_id);
				prep_stmt.setInt(2, job_id);
				prep_stmt.setDate(3, sql_date);
				prep_stmt.setInt(4, Integer.parseInt(hour[1]));
				prep_stmt.setString(5, hour[0]);
				prep_stmt.setDouble(6, Double.valueOf(hour[2]));
				prep_stmt.executeUpdate();
				// Increase task id by one.
				task_id++;
			}
			prep_stmt.clearBatch();
			// Get item id for each item and then insert the item into
			// urakkalista table.
			prep_stmt = con.prepareStatement(
					"SELECT tarvike_id FROM tarvike WHERE nimi = ?");
			PreparedStatement insert_stmt = con.prepareStatement(itemSQL);
			int item_id = 0;
			// Loop through items.
			for (String[] item : items) {
				prep_stmt.setString(1, item[0]);
				result = prep_stmt.executeQuery();
				while (result.next()) {
					// Get item id
					item_id = result.getInt(1);
				}
				// Add item to urakkalista-table.
				insert_stmt.setInt(1, contract_id);
				insert_stmt.setInt(2, item_id);
				insert_stmt.setDouble(3, Double.valueOf(item[1]));
				insert_stmt.executeUpdate();
			}
			insert_stmt.close();
			// Commit the changes.
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
			return false;
		}
		closeConnection();
		return true;
	}
	// Returns all items that are used in the chosen contract.
	public List<String[]> getContractItems(String job_name,
			boolean closeConnection) {
		// List for fetched items.
		List<String[]> items = new ArrayList<String[]>();
		
		String itemSQL = "SELECT tarvike.nimi,maara,yksikko,(maara*myynti_hinta),kirjallisuus "
				+ "FROM tarvike,urakkalista WHERE tarvike.tarvike_id = urakkalista.tarvike_id "
				+ "AND urakka_id IN (SELECT urakka_id FROM urakkatarjous WHERE tyokohde_id = ?)";
		try {
			if (closeConnection || con.isClosed()) {
				connect();
			}
			int job_id = getJobIdByName(job_name);
			prep_stmt = con.prepareStatement(itemSQL);
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				// Add the fetched item into a string array and the add the
				// array into a list.
				String[] item = new String[6];
				// [0] name
				item[0] = result.getString(1);
				// [1] amount
				item[1] = String.valueOf(result.getDouble(2));
				// [2] unit
				item[2] = result.getString(3);
				// [3] total price
				item[3] = String.valueOf(result.getDouble(4));
				// [4] tax identifier
				item[4] = String.valueOf(result.getBoolean(5));
				// Add array to a list
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		if (closeConnection) {
			closeConnection();
		}
		return items;
	}

	// Returns next free task id.
	public int getNextTaskId() {
		int task_id = 0;
		connect();
		// Find next task id, that's free.
		try {
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"SELECT suoritus_id from suoritus ORDER BY suoritus_id");
			// Find next available task id.
			while (result.next()) {
				task_id = result.getInt(1);
			}
			// Add one into task id to make a new id.
			task_id++;
			stmt.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		closeConnection();
		return task_id;
	}

	// Close connection
	public void closeConnection() {
		if (con != null)
			try {
				con.close();
			} catch (SQLException poikkeus) {
				System.out.println(
						"Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");
				return;
			}
		if (prep_stmt != null) {
			try {
				prep_stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
