CREATE TABLE asiakas(
asiakas_id INT,
nimi VARCHAR (40) NOT NULL,
osoite VARCHAR(50) NOT NULL,
yritys BOOLEAN NOT NULL,
hetu VARCHAR,
UNIQUE(hetu),
PRIMARY KEY(asiakas_id));

CREATE TABLE tyokohde(
tyokohde_id INT,
asiakas_id INT NOT NULL,
nimi VARCHAR (40) NOT NULL,
osoite VARCHAR(50) NOT NULL,
valmis BOOLEAN NOT NULL,
urakka BOOLEAN NOT NULL,
PRIMARY KEY (tyokohde_id),
FOREIGN KEY(asiakas_id) references asiakas);

CREATE TABLE urakkatarjous(
urakka_id INT,
tyokohde_id INT NOT NULL,
alennus_prosentti DECIMAL (9,2),
osissa INT NOT NULL,
PRIMARY KEY (urakka_id),
FOREIGN KEY (tyokohde_id) references tyokohde);

CREATE TABLE tarvike(
tarvike_id INT,
nimi VARCHAR (50) NOT NULL,
osto_hinta DECIMAL (9,2),
myynti_hinta DECIMAL(9,2),
yksikko VARCHAR(20) NOT NULL,
kirjallisuus BOOLEAN NOT NULL,
varasto_tilanne DECIMAL(9,2) NOT NULL,
PRIMARY KEY(tarvike_id));

CREATE TABLE urakkalista(
urakka_id INT NOT NULL,
tarvike_id INT NOT NULL,
maara DECIMAL (9,2) NOT NULL,
PRIMARY KEY(urakka_id,tarvike_id),
FOREIGN KEY(tarvike_id) REFERENCES tarvike,
FOREIGN KEY (urakka_id) REFERENCES urakkatarjous);

CREATE TABLE suoritus(
suoritus_id INT,
tyokohde_id INT NOT NULL,
pvm DATE NOT NULL,
tunnit INT NOT NULL,
tyyppi VARCHAR(15) NOT NULL,
hinta DECIMAL (9,2) NOT NULL,
PRIMARY KEY (suoritus_id),
FOREIGN KEY (tyokohde_id) REFERENCES tyokohde);

CREATE TABLE suoritus_tarvike(
tarvike_id INT NOT NULL,
suoritus_id INT NOT NULL,
maara DECIMAL (9,2) NOT NULL,
hinta DECIMAL (9,2) NOT NULL,
alennus_prosentti INT NOT NULL,
PRIMARY KEY(suoritus_id,tarvike_id),
FOREIGN KEY(tarvike_id) REFERENCES tarvike,
FOREIGN KEY(suoritus_id) REFERENCES suoritus);




CREATE TABLE lasku(
lasku_id INT,
tyokohde_id INT NOT NULL,
pvm DATE,
era_pvm DATE,
laskun_tyyppi VARCHAR(20) NOT NULL,
lasku_lkm INT NOT NULL,
tuntien_hinta DECIMAL(9,2),
hinta DECIMAL (9,2),
maksettu BOOLEAN NOT NULL,
PRIMARY KEY (lasku_id),
FOREIGN KEY (tyokohde_id) references tyokohde);



