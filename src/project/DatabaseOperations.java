package project;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import prosjekt_del2.InsertIntoDatabase;
    
    public class DatabaseOperations {
        
    	
    	//KRAV 1/////////Legge til alt i Database///////////
    	
    	
    	//Legg til apparat
    	public static void addApparat(Connection connection, String navn, String beskrivelse) throws SQLException{
    		
    		//Gjør klar query, ? er det som skal legges til i etterkant av prepstat.
    		String queryStatement = "INSERT INTO apparat (navn, beskrivelse) VALUES(?,?)";
    		
    		//PreparedStatement er en klasse som kompilerer SQL statements. 
    		PreparedStatement prepStat = connection.prepareStatement(queryStatement);
    		
    		//setString fordi navn og beskrivelse er String
    		prepStat.setString(1, navn);
    		prepStat.setString(2, beskrivelse);
    		
    		//Execute for å kjøre kode.
    		prepStat.execute();
    		System.out.println("Apparat lagt til");
    		
    	}
    	
    	
    	
    	
    	//Legg til apparatøvelse
    	public static void addApparatOvelse(Connection connection, String ovelsesnavn, 
    			String antallKilo, String antallSett, String apparatNavn) throws SQLException{
    		String queryStatement = "INSERT INTO apparatovelse(ovelsesnavn, antallKilo, "
    				+ "antallSett, apparatNavn) VALUES(?,?,?,?)";
    		PreparedStatement prepStat = connection.prepareStatement(queryStatement);
    		
    		prepStat.setString(1, ovelsesnavn);
    		prepStat.setString(2, antallKilo);
    		prepStat.setString(3, antallSett);
    		prepStat.setString(4, apparatNavn);
    		prepStat.execute();
    		System.out.println("ApparatOvelse lagt til ");
    		
    	}
    	
    	
    	
    	
    	//Legg til friøvelse
    	public static void addFriOvelse(Connection connection, String ovelsesnavn, String beskrivelse) throws SQLException{
    		String queryStatement = "INSERT INTO friovelse(ovelsesnavn, beskrivelse) VALUES(?,?)";
    		PreparedStatement prepStat = connection.prepareStatement(queryStatement);
    		
    		prepStat.setString(1, ovelsesnavn);
    		prepStat.setString(2, beskrivelse);
    		prepStat.execute();
    		System.out.println("Fri Ovelse lagt til");
    		
    	}
    	
    	
    	
    	//Legg til treningsøkt
    	public static void addTreningsOkt(Connection connection, Date dato, Time time, 
    			int duration, int personligForm, int prestasjon, String notat ) throws SQLException{
    		String preQueryStatement = "INSERT INTO treningsokt(dato, tidspunkt, varighet, "
    				+ "personligform, prestasjon, notat) VALUES (?,?,?,?,?,?)";
    		PreparedStatement prepStat = connection.prepareStatement(preQueryStatement);
    		
    		prepStat.setDate(1,dato);
    		prepStat.setTime(2, time);
    		prepStat.setInt(3, duration);
    		prepStat.setInt(4, personligForm);
    		prepStat.setInt(5, prestasjon);
    		prepStat.setString(6, notat);
    		
    		prepStat.execute();
    		System.out.println("TreningsOkt lagt til");
    		
    	}
    	
    	
    	
    	//Legg til øvelse i treningsøkt
    	public static void addOvelseITreningsOkt(Connection connection, Date dato, 
    			Time tidspunkt, String ovelsesnavn) throws SQLException{
    		String queryStatement = "INSERT INTO ovelseITreningsokt(dato, tidspunkt, ovelsesnavn) VALUES (?,?,?)";
    		PreparedStatement prepStat = connection.prepareStatement(queryStatement);
    		
    		prepStat.setDate(1, dato);
    		prepStat.setTime(2, tidspunkt);
    		prepStat.setString(3, ovelsesnavn);
    		
    		prepStat.execute();
    		System.out.println("Ovelse lagt i treningsokt");
    	}
    	
    	
    	
    	//Legg til øvelse i øvelsesgruppe
    	public static void addOvelseIOvelsesgruppe(Connection connection, String ovelsesnavn, 
    			String ovelsesgruppe) throws SQLException{
    		String queryStatement = "INSERT INTO ovelseIOvelsesgruppe(ovelsesnavn, ovelsesgruppe) VALUES (?,?)";
    		PreparedStatement prepStat = connection.prepareStatement(queryStatement);
    		
    		prepStat.setString(1, ovelsesnavn);
    		prepStat.setString(2, ovelsesgruppe);
    		
    		prepStat.execute();
    		System.out.println("Ovelse lagt i ovelsesgruppe");
    	}
    	
    	
    	
    	//Legg til ovelsesgruppe
    	public void addOvelsegruppe(Connection connection, String Ovelsesgruppenavn) throws SQLException {
            String queryStatement = "insert into ovelsesgruppe (ovelsesgruppenavn) VALUES (?)";
            PreparedStatement prepStat = connection.prepareStatement(queryStatement);
            
            prepStat.setString(1, Ovelsesgruppenavn);
            
            prepStat.executeUpdate();
            System.out.println("Ovelsesgruppe lagt til");
        }
    	


    	//KRAV 2//////Hente de n siste Treningsøkter med all informasjon/////////
    	
    	public static List<TreningsOkt> getNSisteTreningsOkter(Connection connection, int n) throws SQLException{
    		
    		List<TreningsOkt> treningsOkter = new ArrayList<TreningsOkt>();
    		
    		//First find a list over the N last workouts
    		String stmt = "select * from treningsokt order by dato desc limit ?";
    		PreparedStatement prepStat = connection.prepareStatement(stmt);
    		prepStat.setInt(1, n);
    		ResultSet rs = prepStat.executeQuery();
    		
    		
    		
    		//Legger inn de n siste treningsøkter inn i en liste med treningsøkter.
    		while(rs.next()) {
    			TreningsOkt t = new TreningsOkt(rs.getDate("dato"), rs.getTime("tidspunkt"), 
    			rs.getInt("varighet"), rs.getInt("personligForm"), rs.getInt("prestasjon"), rs.getString("notat"));
    			treningsOkter.add(t);
    		}
    		
    		return treningsOkter;
    	}
    	
    	

    	//KRAV 3///////////////Hente ut øvelse x mellom to datoer y og z/////////////////
  	
    	@SuppressWarnings("resource")
		public static List<Ovelse> getInfoAboutOvelseInTimeInterval(Connection connection, String ovelsesnavn, Date startsDato, Date sluttDato) throws SQLException{
  		
    		List<Ovelse> ovelser = new ArrayList<>();
  		
    		//Sjekker først om det finnes noen ovelsesnavn i apparatøvelse. Hvis det gjør det så execute kode
    		String queryStatement = "select count(*) from apparatovelse where ovelsesnavn = ?";
    		PreparedStatement preStat = connection.prepareStatement(queryStatement);
    		preStat.setString(1, ovelsesnavn);
    		ResultSet rs = preStat.executeQuery();

    		rs.next();
    		System.out.println(rs);
    		if (rs.getInt(1) > 0){
    			/////SJUUUUUK SPØRRING//////
    			queryStatement = "select dato, tidspunkt, antallKilo, antallSett, apparatNavn from ovelseITreningsokt JOIN "
    					+ "apparatovelse on (apparatovelse.ovelsesnavn = ovelseITreningsokt.ovelsesnavn) "
    					+ "and dato >= ? and dato < ? and apparatovelse.ovelsesnavn = ?";
    			preStat = connection.prepareStatement(queryStatement);
    			preStat.setDate(1, startsDato);
    			preStat.setDate(2, sluttDato);
    			preStat.setString(3, ovelsesnavn);
    			
    			ResultSet resultat = preStat.executeQuery();
    			while (resultat.next()){
 
    				ApparatOvelse apparatovelse = new ApparatOvelse(ovelsesnavn, 
    				resultat.getInt("antallKilo"), resultat.getInt("antallSett"), new Apparat(resultat.getString("apparatNavn")));
    		  		ovelser.add(apparatovelse);
    			}
    			return ovelser;
    		}

    		queryStatement = "select count(*) from friovelse where ovelsesnavn = ?";
    		preStat = connection.prepareStatement(queryStatement);
    		preStat.setString(1, ovelsesnavn);
    		
    		rs = preStat.executeQuery();
    		rs.next();

    		if (rs.getInt(1) > 0){
    			queryStatement = "select dato, tidspunkt, beskrivelse from ovelseITreningsokt JOIN "
    					+ "friovelse on (friovelse.ovelsesnavn = ovelseITreningsokt.ovelsesnavn) "
    					+ "and dato >= ? and dato < ? and friovelse.ovelsesnavn = ?";
    			preStat = connection.prepareStatement(queryStatement);
    			preStat.setDate(1, startsDato);
    			preStat.setDate(2, sluttDato);
    			preStat.setString(3, ovelsesnavn);
    			
    			ResultSet resultat = preStat.executeQuery();
    			while (resultat.next()){
    				System.out.println("Da var vi inne her");
    				FriOvelse friOvelse= new FriOvelse(ovelsesnavn, resultat.getString("beskrivelse"));
    		  		ovelser.add(friOvelse);
    			}
    			return ovelser;
    		}
			return null;
  		
    	}
    	
    	
    	
    	
    	//KRAV 4////////////LAGE ØVELSESGRUPPER OG FINNE ØVELSER SOM ER I SAMME GRUPPE////////
    	
    	public static List<Ovelse> getOvelserIOvelsesgrupper(Connection connection, String ovelsesgruppe) throws SQLException{
    		
    		List<Ovelse> ovelser = new ArrayList<>();
    		
    		String queryStatement = "select ovelsesnavn from ovelseIOvelsesgruppe where ovelsesgruppe = ?";
    		PreparedStatement preStat = connection.prepareStatement(queryStatement);
    		preStat.setString(1, ovelsesgruppe);
    		ResultSet resultat = preStat.executeQuery();
    		
    		while (resultat.next()){
    			Ovelse ovelse = new Ovelse(resultat.getString("ovelsesnavn"));
    			if (ovelser.contains(ovelse)){
    				ovelse.getOvelsesnavn();
    			}
    			else{
    				ovelser.add(ovelse);
    			}
    		}
			return ovelser;
    		
    	}
    	
    	
    	//KRAV 5///////////HVOR MANGE TRENINGSØKTER TOTALT/////////
    	
    	public static int getTotalTreningsøkter(Connection conn) throws SQLException {
    		String stmt  = "select count(dato) as total from treningsokt";
    		PreparedStatement pr = conn.prepareStatement(stmt);
    		ResultSet rs = pr.executeQuery();
    		return rs.next() ? rs.getInt("total") : 0;
    	}
    	
    	
    	
    	
    	
    	
    	///////////////////MAIN ////////////////
    	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
			/*
			System.out.println(getNSisteTreningsøkter(DBConnection.createDBConnection(), 3));
    		DatabaseOperations.addApparatØvelse(DBConnection.createDBConnection(), "hei", "90", "5", "hola");
    		
    		Statement prepStatement = DBConnection.createDBConnection().createStatement();
    		ResultSet rs = prepStatement.executeQuery("select count(*) from apparatovelse where ovelsesnavn like 'hei'");
    		System.out.println(rs);
    		rs.next();
    		System.out.println(rs.getInt(1));
    		
    		DatabaseOperations.addApparat(DBConnection.createDBConnection(), "Hei", "Dette er en test");
    		System.out.println(DatabaseOperations.getApparater(DBConnection.createDBConnection()));
    		System.out.println(getInfoAboutOvelseInTimeInterval(DBConnection.createDBConnection(), "heioghopp", new Date(1990-1900, 10, 20), new Date(2000-1900, 10, 20)));
    		
    		DatabaseOperations.addOvelseIOvelsesgruppe(DBConnection.createDBConnection(), "Test", "JADA");
    		System.out.println(DatabaseOperations.getOvelserIOvelsesgrupper(DBConnection.createDBConnection(), "JADA"));
    		
    		System.out.println(DatabaseOperations.getTotalTreningsøkter(DBConnection.createDBConnection()));
    		*/
		}
    	
    }