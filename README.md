# Uusimmat muokkaukset

Updatetkaa vaikka tätä kun teette isoja muokkauksia ja minkä parissa aiotte työskennellä seuraavaks.

## Janis
- Viimeistelin työkohteen kustannukset napin dialogin
- Loin tietoluokan laskuille: Invoice.java
- Loin dialogiluokan laskuille: InvoiceDialog.java
- Lisäsin laskuja koskevia metodeja DBHandleriin
## Tuomas


## Oskari
- Invoice will be created when job is set as finished.
- New invoice will be created if payment is late.
- Bug fixes

- To do:
  - ALV-tavaroihin
   - Urakkatarjous ja sen lasku
   - Laskun tulostus missä näkyy tunnit ja kaikki muutkin tiedot alveineen.
   
- Done:
  -Messed up commits in some way.
   
Muutokset tauluihin:
---------------------
ALTER TABLE urakkatarjous DROP COLUMN alennus_prosentti;

ALTER TABLE tiko_ht.suoritus DROP CONSTRAINT suoritus_tyokohde_id_fkey, ADD CONSTRAINT suoritus_tyokohde_id_fkey FOREIGN KEY(tyokohde_id) REFERENCES tyokohde ON DELETE CASCADE;

ALTER TABLE tiko_ht.suoritus_tarvike DROP CONSTRAINT suoritus_tarvike_suoritus_id_fkey, ADD CONSTRAINT suoritus_tarvike_suoritus_id_fkey FOREIGN KEY(suoritus_id) REFERENCES tiko_ht.suoritus ON DELETE CASCADE;

ALTER TABLE tiko_ht.lasku DROP CONSTRAINT lasku_tyokohde_id_fkey, ADD CONSTRAINT lasku_tyokohde_id_fkey FOREIGN KEY(tyokohde_id) REFERENCES tiko_ht.tyokohde ON DELETE CASCADE;
