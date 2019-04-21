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
		Connection con = getConnection();
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
			stmt.close();
			prep_stmt.clearParameters();
			closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Create a new job.
	public void createJob(String customer_name, String job_name, String address,
			Boolean contract) {
		Connection con = getConnection();
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
			stmt.close();
			prep_stmt.close();
			closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<List<String>> getAllItems() {
		List<List<String>> items = new ArrayList<List<String>>();
		List<String> names = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();

		Connection con = getConnection();
		try {
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"select nimi,myynti_hinta from tarvike WHERE varasto_tilanne > 0");
			while (result.next()) {
				names.add(result.getString(1));
				prices.add(result.getString(2));
			}
			items.add(names);
			items.add(prices);
			stmt.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		closeConnection();
		return items;
	}

	public void createTask(String job_name, String work_type, int hours,
			DateTime date, List<String> items, List<Integer> item_counts,
			List<Integer> discount_pct) {
		int job_id = 0;
		int task_id = 0;
		// Price of hours * work type cost.
		double price = 0;

		// Format date into a correct format.
		Calendar cal = Calendar.getInstance();
		cal.set(date.getYear(), date.getMonth(), date.getDay());
		java.sql.Date sql_date = new java.sql.Date(cal.getTimeInMillis());

		// Get connection.
		Connection con = getConnection();

		// Initialize SQL statement strings.
		String taskSQL = "INSERT INTO suoritus " + " VALUES(?,?,?,?,?,?)";

		String itemSQL = " INSERT INTO suoritus_tarvike VALUES(?,?,?,?,?)";

		// Find the job id with job name.
		try {
			prep_stmt = con.prepareStatement(
					"SELECT tyokohde_id from tyokohde WHERE nimi = ?");
			// Find the customer id with customer name.'
			prep_stmt.setString(1, job_name);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				job_id = result.getInt(1);
			}
			prep_stmt.clearBatch();

			// Find next task id, that's free.
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"SELECT suoritus_id from suoritus ORDER BY suoritus_id");
			// Find next available task id.
			while (result.next()) {
				task_id = result.getInt(1);
			}
			// Add one into task id to make a new id.
			task_id++;

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
					if (items.get(i).equals(result.getString(2))) {
						item_id = result.getInt(1);
						item_price = result.getDouble(3);
						// Calculate total price percentage.
						total_item_price = item_price * item_counts.get(i);
						// Check if there is a discount percentage.
						if (discount_pct.get(i) > 0) {
							pct = Double.valueOf(discount_pct.get(i));
							pct = (100 - pct) / 100;
							System.out.println(pct);
							total_item_price *= pct;
							System.out.println(total_item_price);
						}
						// Set parameters
						prep_stmt.setInt(1, item_id);
						prep_stmt.setInt(2, task_id);
						prep_stmt.setDouble(3, item_counts.get(i));
						prep_stmt.setDouble(4, total_item_price);
						prep_stmt.setInt(5, discount_pct.get(i));
						prep_stmt.executeUpdate();
						prep_stmt.clearParameters();
					}
				}
			}
			con.commit();
			closeConnection();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	// Fetch jobs and return them as array list.
	public List<String> getJobs() {
		List<String> jobs = new ArrayList<String>();
		Connection con = getConnection();
		try {
			stmt = con.createStatement();
			result = stmt.executeQuery("select nimi from tyokohde");
			while (result.next()) {
				jobs.add(result.getString(1));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		return jobs;
	}
	// Gets the used items for the chosen job.
	public List<List<String>> getJobItems(String job_name,
			boolean getOnlyPrices) {
		// Contains items with names and their data
		List<List<String>> items = new ArrayList<List<String>>();
		// Contains item names.
		List<String> item_names = new ArrayList<String>();
		// Contains item amount, unit and total price.
		List<String> item_data = new ArrayList<String>();
		// Job identifier
		int job_id = 0;
		// SQL query string to get all items and their count and total price
		// from the job
		String SQL = "SELECT tarvike.nimi, maara, tarvike.yksikko,stk.hinta "
				+ " FROM ((suoritus_tarvike as stk JOIN suoritus as st ON stk.suoritus_id = st.suoritus_id)JOIN tarvike ON stk.tarvike_id = tarvike.tarvike_id) "
				+ " JOIN tyokohde ON st.tyokohde_id = tyokohde.tyokohde_id"
				+ " WHERE tyokohde.tyokohde_id = ? ";

		Connection con = getConnection();
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
			prep_stmt.close();
			result.close();

			prep_stmt = con.prepareStatement(SQL);
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				item_names.add(result.getString(1));
				if (getOnlyPrices == true) {
					item_data.add(" " + String.valueOf(result.getDouble(4)));
				} else {
					item_data.add(" | " + String.valueOf(result.getDouble(2))
							+ " " + result.getString(3) + " | "
							+ String.valueOf(result.getDouble(4)));
				}
			}
			prep_stmt.close();
			result.close();
			items.add(item_names);
			items.add(item_data);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		// System.out.println(items);
		return items;
	}
	// Sets the job finished value into true.
	public void setJobFinished(String job_name) {
		Connection con = getConnection();
		try {
			prep_stmt = con.prepareStatement(
					"UPDATE tyokohde SET valmis = true WHERE nimi = ?");
			prep_stmt.setString(1, job_name);
			int did_update = prep_stmt.executeUpdate();
			if (did_update == 0) {
				System.out.println("Update failed, returned 0 affected rows.");
			} else {
				con.commit();
			}
			createInvoice(job_name);
			prep_stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
	}
	// Gets the job finished boolean value.
	public boolean getFinishedValue(String job_name) {
		Connection con = getConnection();
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

	public List<String> getTaskHours(String job_name) {
		con = getConnection();
		List<String> hoursAndTypes = new ArrayList<String>();
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
		con = getConnection();
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
			prep_stmt.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return job_id;
	}

	// Get job name with the job id.
	public String getJobNameById(int job_id, boolean closeConnection) {
		con = getConnection();
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
		if (closeConnection)
			closeConnection();
		return job_name;
	}
	// Adds a discount to the work type hours.
	public void addDiscount(String job_name, String work_type,
			int discount_pct) {
		con = getConnection();
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
		Connection con = getConnection();
		try {
			stmt = con.createStatement();
			ResultSet new_result = stmt
					.executeQuery("select lasku_id, tyokohde_id from lasku");
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
		}
		closeConnection();
		return invoices;
	}

	// Search if there are unpaid and due date passed invoices. Create new
	// invoice if that's the case.
	public void checkInvoiceState() {
		con = getConnection();
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
			if (prep_result != null)
				prep_result.close();
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
		con = getConnection();
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
			stmt.close();
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();

		return invoice;
	}

	public void createInvoice(String job_name) {
		int job_id = getJobIdByName(job_name);
		int invoice_id = 0;
		int invoice_count = 0;
		double finalPrice = 0;
		double hoursPrice = 0;
		String invoice_type = "Normaali";
		// Get current date in sql format.
		java.sql.Date date = java.sql.Date
				.valueOf(LocalDateTime.now().toLocalDate());
		// Set due date to be 1 month after today.
		java.sql.Date dueDate = java.sql.Date
				.valueOf(LocalDateTime.now().plusMonths(1).toLocalDate());
		con = getConnection();

		try {
			// Get the next available invoice_id.
			result = stmt.executeQuery("SELECT lasku_id FROM lasku");
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
				finalPrice = result.getDouble(3);
				// If the invoice hasn't been paid in time, set due date to be
				// in 5 days from the last due date.
				dueDate = result.getDate(2);
			}
			invoice_count++;
			if (invoice_count > 1) {
				dueDate = java.sql.Date
						.valueOf(dueDate.toLocalDate().plusDays(5));
			}
			prep_stmt.clearBatch();

			// Get total price of hours.
			prep_stmt = con
					.prepareStatement("SELECT SUM(hinta) as kokonais_hinta "
							+ " FROM suoritus " + " WHERE tyokohde_id = ? "
							+ " group by tyokohde_id");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				hoursPrice = result.getDouble(1);
			}

			if (invoice_count == 1) {
				// Query to fetch total hour price + item price.
				prep_stmt = con.prepareStatement("SELECT SUM(hinta) as hinnat "
						+ " FROM suoritus " + " WHERE tyokohde_id = ? "
						+ " group by tyokohde_id " + " UNION "
						+ " SELECT SUM(stk.hinta) as hinnat2 "
						+ " FROM suoritus_tarvike as stk JOIN suoritus ON stk.suoritus_id = suoritus.suoritus_id "
						+ " WHERE tyokohde_id = ?");
				prep_stmt.setInt(1, job_id);
				prep_stmt.setInt(2, job_id);
				result = prep_stmt.executeQuery();
				while (result.next()) {
					// Get total price of hours from the second row of query.
					finalPrice += result.getDouble(1);
				}
				prep_stmt.clearParameters();
			}

			// Set invoice type depending on invoice count.
			if (invoice_count == 2) {
				invoice_type = "Muistutus";
				// Add billing fee.
				finalPrice += 5;
			} else if (invoice_count > 2) {
				invoice_type = "Karhu";
				// Add late payment fee and billing fee.
				finalPrice = finalPrice * 1.16 + 5;
			}

			prep_stmt = con.prepareStatement(
					"INSERT INTO lasku VALUES (?,?,?,?,?,?,?,?,?)");
			prep_stmt.setInt(1, invoice_id);
			prep_stmt.setInt(2, job_id);
			prep_stmt.setDate(3, date);
			prep_stmt.setDate(4, dueDate);
			prep_stmt.setString(5, invoice_type);
			prep_stmt.setInt(6, invoice_count);
			prep_stmt.setDouble(7, hoursPrice);
			prep_stmt.setDouble(8, finalPrice);
			prep_stmt.setBoolean(9, false);
			prep_stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		printInvoice(invoice_id);
		closeConnection();
	}
	public boolean deleteJob(String job_name) {
		con = getConnection();
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
		}
		closeConnection();
		return false;
	}
	// Writes all data from invoice to a text file.
	public void printInvoice(int invoice_id) {
		Invoice invoice = getFullInvoice(invoice_id);
		connect();
		con = getConnection();
		// Get job id.
		int job_id = invoice.getTyokohde_id();
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
				writer.write("Nimi: " + result.getString(1));
				writer.newLine();
				writer.write("Osoite: " + result.getString(2));
				writer.newLine();
				writer.write(
						"Urakka: " + ((result.getBoolean(3)) ? "Kyllä" : "Ei"));
				writer.newLine();
				writer.newLine();
			}
			prep_stmt.clearBatch();

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
				writer.write("Henkilötunnus: " + (result.getString(5).isEmpty()
						? "Ei tietokannassa"
						: result.getString(5)));
				writer.newLine();
				writer.newLine();
			}
			prep_stmt.clearBatch();

			// Get all items used for the job.
			writer.write("Käytetyt tarvikkeet");
			writer.newLine();
			writer.write("-------------------------");
			writer.newLine();
			prep_stmt = con.prepareStatement(
					"SELECT tarvike.nimi, maara,yksikko, tarvike.myynti_hinta, stk.hinta, stk.alennus_prosentti "
							+ " FROM ((suoritus_tarvike as stk JOIN suoritus as st ON stk.suoritus_id = st.suoritus_id) "
							+ " JOIN tarvike ON stk.tarvike_id = tarvike.tarvike_id)JOIN tyokohde ON st.tyokohde_id = tyokohde.tyokohde_id "
							+ " WHERE tyokohde.tyokohde_id = ?");
			prep_stmt.setInt(1, job_id);
			result = prep_stmt.executeQuery();
			while (result.next()) {
				writer.write("Nimi: " + result.getString(1));
				writer.newLine();
				writer.write("Määrä: " + result.getDouble(2) + " "
						+ result.getString(3));
				writer.newLine();
				writer.write("Yksikköhinta: " + result.getDouble(4) + " euroa");
				writer.newLine();
				writer.write(
						"Kokonaishinta: " + result.getDouble(5) + " euroa");
				writer.newLine();
				writer.write("Alennusprosentti: " + result.getInt(6) + "%");
				writer.newLine();
				writer.newLine();
			}
			prep_stmt.clearBatch();

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
				writer.write(result.getString(1));
				writer.newLine();
				writer.write(String.valueOf(result.getInt(2)) + " tuntia");
				writer.newLine();
				writer.write(String.valueOf(result.getDouble(3)) + " euroa");
				writer.newLine();
				writer.newLine();
			}
			prep_stmt.clearBatch();
			writer.write("-------------------------");
			// Get basic info for the invoice and print.
			writer.newLine();
			writer.write("Tuntien hinta yhteensa: "
					+ String.valueOf(invoice.getTuntien_hinta()) + " euroa");
			writer.newLine();
			writer.write("Kokonaishinta: " + String.valueOf(invoice.getHinta())
					+ " euroa");
			writer.newLine();
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
		}
	}
	public boolean deleteInvoice(int invoice_id) {
		con = getConnection();
		int success = 0;
		try {
			prep_stmt = con
					.prepareStatement("DELETE FROM lasku WHERE lasku_id = ?");
			prep_stmt.setInt(1, invoice_id);
			success = prep_stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		if (success >= 1) {
			return true;
		} else {
			return false;
		}
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
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
