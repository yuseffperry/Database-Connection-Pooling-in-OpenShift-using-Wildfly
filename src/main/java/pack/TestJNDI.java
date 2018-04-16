package pack;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class TestJndi
 */
@WebServlet("/TestJNDI")
public class TestJNDI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestJNDI() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.println("<h1> Data... </h1>");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM technical_editors");
			if (rs.next()) {
				writer.println("<h1> Data From DB ID - " + rs.getString(1) + "</h1>");
				writer.println("<h1> Data From DB Name - " + rs.getString(2) + "</h1>");
			} else {
				writer.println("<h1> No Data... </h1>");
			}
			rs.close();
			stmt.close();
			conn.close();
			writer.flush();
		} catch (Exception err) {
			writer.println("<h1> Error - " + err.toString() + "</h1>");
		} finally {
			if (null != conn)
				try {
					conn.close();
					writer.close();
				} catch (Exception err) {
					Logger.getLogger(TestJNDI.class.getName()).log(Level.SEVERE, null, err);
				}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	public Connection getConnection() throws Exception {
		Connection conn = null;
		InitialContext context = new InitialContext();
		dataSource = (DataSource) context.lookup("java:jboss/datasources/JdbcPool");

		conn = dataSource.getConnection();

		return conn;
	}
}