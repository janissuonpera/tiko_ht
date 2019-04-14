package tiko_ht;
import java.sql.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.widgets.DateTime;

public class DBHandler {

	// tietokannan ja kayttajan tiedot
	private static final String PROTOKOLLA = "jdbc:postgresql:";
	private static final String PALVELIN = "localhost";
	private static final int PORTTI = 5432;
	private static final String TIETOKANTA = "Janis";
	private static final String KAYTTAJA = "Janis";
	private static final String SALASANA = "";
	private final int REGULAR_WORK = 45;
	private final int PLANNING_WORK = 55;
	private final  int HELPING_WORK = 35;
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
			return customers;

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
	public void createJob(String customer_name,String job_name, String address, Boolean contract) {
		Connection con = getConnection();
		int customer_id = 0;
		int job_id = 0;
		String SQL = "INSERT INTO tyokohde(tyokohde_id,asiakas_id,nimi,osoite,valmis,urakka)"
				+ "VALUES(?,?,?,?,?,?)";
		try {
			
			prep_stmt = con.prepareStatement("SELECT asiakas_id from asiakas WHERE nimi = ?");
			// Find the customer id with customer name.'
			prep_stmt.setString(1, customer_name);
			result = prep_stmt.executeQuery();
			while (result.next()) {	
					customer_id = result.getInt(1);
			}
			prep_stmt.clearBatch(); 
			
			stmt = con.createStatement();
			result = stmt
					.executeQuery("SELECT tyokohde_id from tyokohde");
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
	
	public List<List<String>> getAllItems(){
		List<List<String>> items = new ArrayList<List<String>>();
		List<String> names = new ArrayList<String>();
		List<String> prices = new ArrayList<String>();
		
		Connection con = getConnection();
		try {
			stmt = con.createStatement();
			result = stmt.executeQuery("select nimi,myynti_hinta from tarvike WHERE varasto_tilanne > 0");
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
	
	public void createTask(String job_name,String work_type,int hours,DateTime date,List<String> items,List<Integer>item_counts,List<Integer> discount_pct) {
		int job_id = 0;
		int task_id = 0;
		// Price of hours * work type cost.
		double price = 0;
		
		// Format date into a correct format.
		Calendar cal = Calendar.getInstance();
		cal.set(date.getYear(),date.getMonth(),date.getDay());
		java.sql.Date sql_date = new java.sql.Date(cal.getTimeInMillis());
		
		// Get connection.
		Connection con = getConnection();
		
		// Initialize SQL statement strings.
		String taskSQL = "INSERT INTO suoritus "
				+ " VALUES(?,?,?,?,?,?)";
		
		String itemSQL = " INSERT INTO suoritus_tarvike VALUES(?,?,?,?,?)";
		
		
		// Find the job id with job name.
		try {
			prep_stmt = con.prepareStatement("SELECT tyokohde_id from tyokohde WHERE nimi = ?");
			// Find the customer id with customer name.'
			prep_stmt.setString(1, job_name);
			result = prep_stmt.executeQuery();
			while (result.next()) {	
					job_id = result.getInt(1);
			}
			prep_stmt.clearBatch(); 
			
			// Find next task id, that's free.
			stmt = con.createStatement();
			result = stmt
					.executeQuery("SELECT suoritus_id from suoritus");
			// Find next available task id.
			while (result.next()) {
				task_id = result.getInt(1);
			}
			// Add one into task id to make a new id.
			task_id++;
			
			// Calculating total price of hours.
			if(work_type.equals("Työ")) {
				price = hours*REGULAR_WORK;
			} else if(work_type.equals("Suunnittelu")) {
				price = hours*PLANNING_WORK;
			}else {
				price = hours*HELPING_WORK;
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
			prep_stmt = con.prepareStatement(itemSQL);
			
			for(int i = 0; i < items.size(); i++) {
				stmt = con.createStatement();
				result = stmt.executeQuery("SELECT tarvike_id,nimi,myynti_hinta from tarvike");
				while(result.next()) {
					if(items.get(i).equals(result.getString(2))) {
						item_id = result.getInt(1);
						item_price = result.getDouble(3);
						// Calculate total price percentage.
						total_item_price = item_price*item_counts.get(i);
						// Check if there is a discount percentage.
						if(discount_pct.get(i) > 0) {
							total_item_price *= (100-discount_pct.get(i))/100;
						}
						// Set parameters
						prep_stmt.setInt(1,item_id);
						prep_stmt.setInt(2, task_id);
						prep_stmt.setDouble(3, item_counts.get(i));
						prep_stmt.setDouble(4,total_item_price);
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
	public List<String> getJobs(){
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
	public List<List<String>> getJobItems(String job_name,boolean getOnlyPrices){
		// Contains items with names and their data 
		List<List<String>> items = new ArrayList<List<String>>();
		// Contains item names.
		List<String> item_names = new ArrayList<String>();
		// Contains item amount, unit and total price. 
		List<String> item_data = new ArrayList<String>();
		// Job identifier
		int job_id = 0;
		// SQL query string to get all items and their count and total price from the job
		String SQL = "SELECT tarvike.nimi, maara, tarvike.yksikko,stk.hinta " + 
				" FROM ((suoritus_tarvike as stk JOIN suoritus as st ON stk.suoritus_id = st.suoritus_id)JOIN tarvike ON stk.tarvike_id = tarvike.tarvike_id) "
				+ " JOIN tyokohde ON st.tyokohde_id = tyokohde.tyokohde_id" +
				" WHERE tyokohde.tyokohde_id = ? ";
		
		Connection con = getConnection();
		try {
			// Get the job id with the job name.
			prep_stmt = con.prepareStatement("SELECT tyokohde_id FROM tyokohde WHERE nimi = ?");
			prep_stmt.setString(1, job_name);
			result = prep_stmt.executeQuery();
			while(result.next()) {
				job_id = result.getInt(1);
			}
			prep_stmt.clearBatch();
			prep_stmt.close();
			result.close();
			
	
			prep_stmt = con.prepareStatement(SQL);
			prep_stmt.setInt(1,job_id);
			result = prep_stmt.executeQuery();
			while(result.next()) {
				item_names.add(result.getString(1));
				if(getOnlyPrices == true) {
					item_data.add(" "+String.valueOf(result.getDouble(4)));
				} else {
					item_data.add(" | " + String.valueOf(result.getDouble(2)) + " " + result.getString(3) + " | "+ String.valueOf(result.getDouble(4)));
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
		System.out.println(items);
		return items;
	}
	// Sets the job finished value into true.
	public void setJobFinished(String job_name) {
		Connection con = getConnection();
		try {
			prep_stmt = con.prepareStatement("UPDATE tyokohde SET valmis = true WHERE nimi = ?");
			prep_stmt.setString(1,job_name);
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
			prep_stmt = con.prepareStatement("SELECT valmis from tyokohde WHERE nimi = ?");
			prep_stmt.setString(1,job_name);
			result = prep_stmt.executeQuery();
			while(result.next()) {
				ready = result.getBoolean(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		closeConnection();
		return ready;
	}

	// Close connection
	public void closeConnection() {
		Connection con = getConnection();
		if (con != null)

			try { // jos yhteyden luominen ei onnistunut, con == null
				con.close();

			} catch (SQLException poikkeus) {
				System.out.println(
						"Yhteyden sulkeminen tietokantaan ei onnistunut. Lopetetaan ohjelman suoritus.");
				return;
			}
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

}
