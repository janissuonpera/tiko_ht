package tiko_ht;

import java.sql.Date;

/*
 * Class for an invoice object.
 * All attributes are values from 'lasku' table in the database.
 */
public class Invoice {
	
	//Attributes
	int lasku_id;
	int tyokohde_id;
	Date pvm;
	Date era_pvm;
	String tyyppi;
	int lkm;
	double tuntien_hinta;
	double hinta;
	boolean maksettu;
	

	//Constructor
	public Invoice(int lasku_id, int tyokohde_id, Date pvm, Date era_pvm, String tyyppi, int lkm, double tuntien_hinta,
			double hinta, boolean maksettu) {
		super();
		this.lasku_id = lasku_id;
		this.tyokohde_id = tyokohde_id;
		this.pvm = pvm;
		this.era_pvm = era_pvm;
		this.tyyppi = tyyppi;
		this.lkm = lkm;
		this.tuntien_hinta = tuntien_hinta;
		this.hinta = hinta;
		this.maksettu = maksettu;
	}

	//Only getters and setters below
	
	public int getLasku_id() {
		return lasku_id;
	}

	public void setLasku_id(int lasku_id) {
		this.lasku_id = lasku_id;
	}

	public int getTyokohde_id() {
		return tyokohde_id;
	}

	public void setTyokohde_id(int tyokohde_id) {
		this.tyokohde_id = tyokohde_id;
	}

	public Date getPvm() {
		return pvm;
	}

	public void setPvm(Date pvm) {
		this.pvm = pvm;
	}

	public Date getEra_pvm() {
		return era_pvm;
	}

	public void setEra_pvm(Date era_pvm) {
		this.era_pvm = era_pvm;
	}

	public String getTyyppi() {
		return tyyppi;
	}

	public void setTyyppi(String tyyppi) {
		this.tyyppi = tyyppi;
	}

	public int getLkm() {
		return lkm;
	}

	public void setLkm(int lkm) {
		this.lkm = lkm;
	}

	public double getTuntien_hinta() {
		return tuntien_hinta;
	}

	public void setTuntien_hinta(double tuntien_hinta) {
		this.tuntien_hinta = tuntien_hinta;
	}

	public double getHinta() {
		return hinta;
	}

	public void setHinta(double hinta) {
		this.hinta = hinta;
	}

	public boolean isMaksettu() {
		return maksettu;
	}

	public void setMaksettu(boolean maksettu) {
		this.maksettu = maksettu;
	}
	
	
	
}
