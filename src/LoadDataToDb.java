import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class LoadDataToDb {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_LINK = "jdbc:mysql://localhost/soccer";

	public static void main(String[] args) {
		System.out.println("Please enter the path to the folder containing the csv files: \n");
		Scanner s = new Scanner(System.in);
		String path = s.nextLine();
		LoadDataToDb loader = new LoadDataToDb();

		loader.modifyPlayersCsv(path);
		loader.loadData(path);

	}

	private void modifyPlayersCsv(String path) {
		String modPath = createPath(path);
		try {
			CSVReader csvReader = new CSVReader(new FileReader(modPath + "Players.csv"), ',', '\'');
			List<String[]> values = csvReader.readAll();
			Iterator<String[]> iterator = values.iterator();
			while (iterator.hasNext()) {
				String[] currentLine = iterator.next();
				if (currentLine[currentLine.length - 1].equals("TRUE")) {
					currentLine[currentLine.length - 1] = "1";
				} else if (currentLine[currentLine.length - 1].equals("FALSE")) {
					currentLine[currentLine.length - 1] = "0";
				}
			}

			csvReader.close();

			CSVWriter csvWriter = new CSVWriter(new FileWriter(modPath + "Players.csv"), ',','\'');
			csvWriter.writeAll(values);
			csvWriter.flush();
			csvWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void loadData(String path) {
		String[] filenames = { "Country", "Players", "Match_results", "Player_Assists_Goals", "Player_Cards" };
		Connection c = null;
		Statement s = null;
		try {

			String modPath = createPath(path);
			Class.forName(JDBC_DRIVER);
			c = DriverManager.getConnection(DB_LINK, "root", "sanika");
			s = c.createStatement();

			String q_firstHalf = "LOAD DATA LOCAL INFILE ";
			String q_secondHalf = " INTO TABLE ";
			// String path = "'C:/Users/Sanika/SkyDrive/Documents/DB1/input/";

			String last = " fields terminated by ',' optionally enclosed by \"\\'\" lines terminated by";
			String termBy = null;

			for (int i = 0; i < filenames.length; i++) {
				if (i == 0 || i == 4) {
					termBy = " '\\r';";
				} else
					termBy = "'\\n';";
				s.executeQuery(q_firstHalf + "'" + modPath + filenames[i] + ".csv'" + q_secondHalf + filenames[i] + last
						+ termBy);
			}

			s.close();
			c.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException sqle) {
			// TODO Auto-generated catch block
			sqle.printStackTrace();
		} finally {
			try {
				if (s != null)
					s.close();
				if (c != null)
					c.close();
			} catch (SQLException sqle) {
			}
		}
	}

	private String createPath(String path) {
		String modPath = path;
		if (path.charAt(path.length() - 1) != '/') {
			modPath = path.replace("\\", "/");
			modPath = modPath.concat("/");
		}
		return modPath;
	}

}
