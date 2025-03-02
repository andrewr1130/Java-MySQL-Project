import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Scanner;

public class DBTest_Demo {

	static Scanner scan = new Scanner(System.in);

	public DBTest_Demo() {
	}


	public static void main(String[] args) {

		int n;

		System.out.println("hello User");
		System.out.println("Are you:");
		System.out.println("1. Specify a problem");
		System.out.println("2. contribute a SQL");
		System.out.println("3. display a list of runnable query");

		n = scan.nextInt();
		scan.nextLine(); // debug

		switch (n) {
		case 1:
			problemSpecfier();
			break;
		case 2:
			sqlDeveloper();
			break;
		case 3:
			solvedQuestion();
			break;
		default:
			System.out.println("Input doesn't exist");
		}

	}

	public static void problemSpecfier() {

		PreparedStatement stmt = null;
		Connection connect = null;

		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://18.206.156.104/chad?" + "user=connection&password=Penguin03!");

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			String q;
			System.out.println("What is your problem: ");

			q = scan.nextLine();

			String sql = "INSERT INTO Problems(description) VALUES (?);";
			stmt = connect.prepareStatement(sql);
			stmt.setString(1, q);

			stmt.executeUpdate();
			System.out.println("Problem successfully upload");

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void sqlDeveloper() {
		PreparedStatement preparedStatement = null;
		Statement stmt = null;
		Connection connect = null;
		ResultSet rs;

		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://18.206.156.104/chad?" + "user=connection&password=Penguin03!");

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB

			String unsolve = "select p.* " + " from Problems p"
					+ " left join SQL_Contributions sc on p.problem_id = sc.problem_id"
					+ " where sc.problem_id is null;";

			preparedStatement = connect.prepareStatement(unsolve);
			ResultSet r1 = preparedStatement.executeQuery();

			System.out.println("Unsolved Problems:");
			while (r1.next()) {
				int n1 = r1.getInt(1);
				String n2 = r1.getString(2);

				System.out.printf("(%d) %s", n1, n2);
				System.out.println();
			}
			r1.close();
			preparedStatement.close();

			int s;
			System.out.println("Please select a problem to solve: ");

			s = scan.nextInt();

			// only the problems that yet to be solved
			String selec = "select p.description" + " from Problems p"
					+ " left join SQL_Contributions sc on p.problem_id = sc.problem_id" + " where sc.problem_id is null"
					+ " and p.problem_id =" +

					String.valueOf(s) + ";";

			preparedStatement = connect.prepareStatement(selec);
			ResultSet r2 = preparedStatement.executeQuery();

			if (r2.next()) {
				String n2 = r2.getString(1);
				System.out.println(n2);
				sqlInsert(s); // sql developer input
			}
			r2.close();
			preparedStatement.close();

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public static void sqlInsert(int s) {
		Connection connect = null;
		PreparedStatement stmt = null;

		hibernateMapping();
		System.out.println("(Important!!!) Please Enter a valid working SQL, the system will parameterize your SQL for the seeker.");
		System.out.println("(Example working input SQL: SELECT QuestionS6, COUNT(QuestionS6) AS COUNT FROM chad_encoded_data GROUP BY QuestionS6 ORDER BY COUNT DESC LIMIT 7;)");
		System.out.println("Insert your SQL:");


		try {

			connect = DriverManager
					.getConnection("jdbc:mysql://18.206.156.104/chad?" + "user=connection&password=Penguin03!");

			String m;
			scan.nextLine(); // debug
			m = scan.nextLine();

			try (PreparedStatement stmtValidate = connect.prepareStatement(m)) {
				stmtValidate.execute();
				System.out.println("SQL is valid!");
			} catch (SQLException e) {
				if (m.contains("‘") || m.contains("’")) {
					System.out.println("Invalid quotes detected. Please use straight single quotes (').");
					return;
				}
				System.out.println("Invalid SQL: " + e.getMessage());
				return;
			}

			String n = convertSQL(m);

			String sql = "INSERT INTO SQL_Contributions(problem_id, sql_statement, parameter_sql_statement) VALUES (?, ?, ?);";
			stmt = connect.prepareStatement(sql);
			stmt.setInt(1, s);
			stmt.setString(2, m);
			stmt.setString(3, n);

			stmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String convertSQL(String s) {
		String[] uniqueValues = { "0", "1", "1024", "128", "16", "2", "20", "2048", "22", "256", "276", "3", "326",
				"336", "384", "4", "5", "64", "7", "Q12", "Q16", "Q17", "Q18", "Q19", "Q24", "Q28", "Q29b", "Q45",
				"192", "Q46", "Q8", "Q9", "S6", "Q47", "Q48", "N/A", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "yes", "No", "Not Sure", "Not Due Yet", "Prefer not to answer", "Employed full-time",
				"Employed part-time", "Retired", "Unemployed", "Disabled", "Student", "Other (please specify)",
				"Less than high school", "Some high school", "High school graduate (or equivalent)",
				"Some college, no degree", "Two-year associate degree from a college or university",
				"Four-year college or university degree/Bachelor degree (e.g., BS, BA, AB)",
				"Some postgraduate or professional schooling, no postgraduate degree (e.g., some graduate school)",
				"Postgraduate or professional degree (e.g., MA, MS, PhD, JD, graduate school)", "Do not know/Refused",
				"Less than $25,000", "$25,000-$49,999", "$50,000-$74,999", "$75,000-$99,999", "$100,000-$124,999",
				"$125,000-$149,999", "$150,000-$199,999", "$200,000 or more", "Never", "Rarely", "Sometimes", "Often",
				"Always", "1", "2", "3", "4", "5", "Do not know", "*" };
		String[] columnNames = { "Question28", "Question29b", "QuesionS6", "Quesion8", "Quesion9", "Quesion12",
				"Quesion16", "Quesion17", "Quesion18", "Question19", "Quesion24", "Quesion45", "Quesion46", "Quesion47",
				"Quesion48", "response_description_pk", "response_choice", "response_description", "data_dictionary_pk",
				"question_label", "response_encoded_value", "ref_response_description" };

		for (String value : uniqueValues) {
			s = s.replaceAll("(?!)(?!\\b(" + String.join("|", columnNames) + ")\\b\\s"
					+ java.util.regex.Pattern.quote(value) + "?!\\w)", "?");

		}

		for (String value : uniqueValues) {
			s = s.replaceAll("(?<==)" + java.util.regex.Pattern.quote(value) + "(?=\\b)", "?");
			s = s.replaceAll("\\(" + java.util.regex.Pattern.quote(value) + "\\b", "(?");
		}

		for (String value : uniqueValues) {
			s = s.replaceAll("\\(" + java.util.regex.Pattern.quote(value) + "\\)", "(?)");
			s = s.replaceAll("\\," + java.util.regex.Pattern.quote(value), ",?");
			s = s.replaceAll("\\=" + java.util.regex.Pattern.quote(value), "=?");

		}

		s = s.replaceAll("'[^']*'", "?");

		s = s.replaceAll("\\*", "?");

		s = s.replaceAll("(?<=\\s)\\d+", "?");

		System.out.println(s);
		return s;
	}

	private static int countPlaceholders(String sql) {
		int count = 0;
		for (char c : sql.toCharArray()) {
			if (c == '?')
				count++;
		}
		return count;
	}

	private static void hibernateMapping() {
		PreparedStatement preparedStatement = null;
		Connection connect = null;

		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://18.206.156.104/chad?" + "user=connection&password=Penguin03!");

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB

			String chadEncoded = "describe chad_encoded_data;";

			preparedStatement = connect.prepareStatement(chadEncoded);
			ResultSet r1 = preparedStatement.executeQuery();

			System.out.println("chad_encoded_data's Hibernate ORM Mapping:");
			while (r1.next()) {
				String n1 = r1.getString(1);
				String n2 = r1.getString(2);

				System.out.printf("-(%s): %s", n1, n2);
				System.out.println();
			}
			r1.close();
			preparedStatement.close();

			String dataDictionary = "describe data_dictionary;";

			preparedStatement = connect.prepareStatement(dataDictionary);
			ResultSet r11 = preparedStatement.executeQuery();

			System.out.println("data_dictionary's Hibernate ORM Mapping:");
			while (r11.next()) {
				String n1 = r11.getString(1);
				String n2 = r11.getString(2);

				System.out.printf("-(%s): %s", n1, n2);
				System.out.println();
			}
			r11.close();
			preparedStatement.close();

			String responseDescription = "describe response_description";

			preparedStatement = connect.prepareStatement(responseDescription);
			ResultSet r111 = preparedStatement.executeQuery();

			System.out.println("response_description's Hibernate ORM Mapping:");
			while (r111.next()) {
				String n1 = r111.getString(1);
				String n2 = r111.getString(2);

				System.out.printf("-(%s): %s", n1, n2);
				System.out.println();
			}

			System.out.println();

			r111.close();
			preparedStatement.close();

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void solvedQuestion() {
		PreparedStatement preparedStatement = null;
		Connection connect = null;

		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://18.206.156.104/chad?" + "user=connection&password=Penguin03!");

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB

			String solved = "select p.*" + " from Problems p"
					+ " left join SQL_Contributions sc on p.problem_id = sc.problem_id"
					+ " where sc.problem_id is not null;";

			preparedStatement = connect.prepareStatement(solved);
			ResultSet r1 = preparedStatement.executeQuery();

			System.out.println("Here is a list of already solved questons:");
			while (r1.next()) {
				int n1 = r1.getInt(1);
				String n2 = r1.getString(2);

				System.out.printf("(%d) %s", n1, n2);
				System.out.println();
			}
			r1.close();
			preparedStatement.close();
			int s;
			System.out.println("Please select a problem that has already been solved: ");

			s = scan.nextInt();

			// only the problems that has already been solved
			String selec = "select p.*" + " from Problems p"
					+ " left join SQL_Contributions sc on p.problem_id = sc.problem_id"
					+ " where sc.problem_id is not null" + " and p.problem_id = " + s + ";";

			preparedStatement = connect.prepareStatement(selec);
			ResultSet r2 = preparedStatement.executeQuery();

			if (r2.next()) {
				String n2 = r2.getString(2);
				System.out.println(n2);
			}
			r2.close();
			preparedStatement.close();

			String answer = "select parameter_sql_statement" + " from SQL_Contributions" + " where " + s
					+ " = problem_id;";
			preparedStatement = connect.prepareStatement(answer);
			r1 = preparedStatement.executeQuery(answer);

			if (r1.next()) {
				String n1 = r1.getString(1);
				System.out.println(n1);
				int placeholderCount = countPlaceholders(n1);
				System.out.println("The query has " + placeholderCount + " parameters to fill.");
				String[] parameters = new String[placeholderCount];

				String[] parameter_type = new String[placeholderCount];
				scan.nextLine();
				for (int i = 0; i < placeholderCount; i++) {
					System.out.println("Enter value type (String, Int, Double) for parameter " + (i + 1)
							+ " (Please do exactly as follow such as Int cant be input as int):");
					parameter_type[i] = scan.nextLine();
					System.out.println("Enter value for parameter " + (i + 1) + ":");
					parameters[i] = scan.nextLine();
				}
				PreparedStatement dynamicStatement = connect.prepareStatement(n1);
				for (int i = 0; i < parameters.length; i++) {
					if (parameter_type[i].equals("String")) {
						dynamicStatement.setString(i + 1, parameters[i]);
					} else if (parameter_type[i].equals("Int")) {
						dynamicStatement.setInt(i + 1, Integer.parseInt(parameters[i]));
					} else if (parameter_type[i].equals("Double")) {
						dynamicStatement.setDouble(i + 1, Double.parseDouble(parameters[i]));
					} else {
						System.out.println("Error not a valid type!");
					}
				}

				System.out.println("This works!!");
				ResultSet r21 = dynamicStatement.executeQuery();// changed (n1)
				ResultSetMetaData metaData = r21.getMetaData();
				int columnCount = metaData.getColumnCount();

				System.out.println("Query Result:");
				while (r21.next()) {
					for (int i = 1; i <= columnCount; i++) {
						System.out.print(metaData.getColumnName(i) + ":" + r21.getString(i) + "|");
					}
					System.out.println();
				}

				r21.close();
				dynamicStatement.close();
			}
			r1.close();

			preparedStatement.close();

		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
}
