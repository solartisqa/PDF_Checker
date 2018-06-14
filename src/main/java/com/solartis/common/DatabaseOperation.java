package com.solartis.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;

public class DatabaseOperation
{
	public static Connection conn = null;
	private static String JDBC_DRIVER = null;
	private static String DB_URL =null;
	private static String USER=null;
	private static String PASS =null;
	protected String query = null;
	protected Statement stmt = null;
	protected ResultSet rs = null;
	protected int rs_row = 1;
	protected LinkedHashMap<Integer, LinkedHashMap<String, String>> table = null;
	protected ResultSetMetaData meta = null;
	
	public static Connection ConnectionSetup(PropertiesHandle config) throws DatabaseException 
	{
		JDBC_DRIVER =config.getProperty("jdbc_driver");
		DB_URL = config.getProperty("db_url");
		USER=config.getProperty("db_username");
		PASS =config.getProperty("db_password");
		if(conn == null)
		{
			//System.out.println(JDBC_DRIVER+DB_URL+USER+PASS);
			try 
			{
				Class.forName(JDBC_DRIVER);
			} 
			catch (ClassNotFoundException e) 
			{
				throw new DatabaseException("ERROR IN JDBC_DRIVER : " + JDBC_DRIVER, e);
			}
			try 
			{
				conn = DriverManager.getConnection(DB_URL,USER,PASS);
			} 
			catch (SQLException e) 
			{
				throw new DatabaseException("ERROR IN DB - URL / USERNAME / PASSWORD", e);	
			}	
		}	
		return conn;
	}
	
	public static Connection ConnectionSetup(String JDBC_DRIVER, String DB_URL, String USER, String password) throws DatabaseException 
	{
		if(conn == null)
		{
			
			try 
			{
				Class.forName(JDBC_DRIVER);
			} 
			catch (ClassNotFoundException e) 
			{
				throw new DatabaseException("ERROR IN JDBC_DRIVER : " + JDBC_DRIVER, e);
			}
			try 
			{
				conn = DriverManager.getConnection(DB_URL,USER,password);
			} 
			catch (SQLException e) 
			{
				throw new DatabaseException("ERROR IN DB - URL / USERNAME / PASSWORD", e);	
			}	
		}
		return conn;
	}
	
	public static void CloseConn() throws DatabaseException
	{
		try 
		{
			conn.close();
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH CLOSING DB-CONNECTION", e);
		}
		conn = null;
	}
	
	public void executeQuery(String query) throws  DatabaseException
	{
		try 
		{
			Statement stmt = null;
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
			stmt.executeUpdate(query);
		}
	    catch (Exception e) 
		{
			System.out.println(e);
			throw new DatabaseException("PROBLEM in Executing query",e);
			
		}
	}
	
	public LinkedHashMap<Integer, LinkedHashMap<String, String>> GetDataObjects(String query) throws DatabaseException
	{
		this.query = query;
		//System.out.println(this.query);
		LinkedHashMap<String, String> row = null;
		try 
		{
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
		    rs =    stmt.executeQuery(this.query);
		    table = new LinkedHashMap<Integer, LinkedHashMap<String, String>>();
	        meta = rs.getMetaData();        
	        while (rs.next())
	        {
	        	row = new LinkedHashMap<String, String>();
	            for (int columnIterator = 1; columnIterator <= meta.getColumnCount(); columnIterator++) 
	            {
	                String key = meta.getColumnName(columnIterator);
	                String value = rs.getString(key);
	                row.put(key, value);
	            }
	            table.put(rs_row, row);
	            rs_row = rs_row + 1;   
	        } 
	        return table;  
		} 
		catch (SQLException e) 
		{
			System.out.println(e);
			throw new DatabaseException("PROBLEM WITH RESULT-SET OBTAINED FROM DB",e);
			
		}		
	}
	
	public void UpdateRow(Integer rowNumber, LinkedHashMap<String, String> row) throws DatabaseException
	{
		
		try 
		{
			rs.first();
		    int rowIterator = 1;
			do
			{
				if(rowNumber == rowIterator)
			    {
					for (int i = 1; i <= meta.getColumnCount(); i++) 
					{  
				       rs.updateString(meta.getColumnName(i), row.get(meta.getColumnName(i)));     
				    }
					rs.updateRow();
			    } 
			 
			    rowIterator++;
			 }while (rs.next());	
		}	
		
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH UPDATE ROW IN DB", e);
		}
	}
	
	
	
	public void UpdateTable(LinkedHashMap<Integer, LinkedHashMap<String, String>> table) throws DatabaseException
	{
		this.table = table;
		LinkedHashMap<String, String> row = null;
		try 
		{
			rs.first();
		    int rowIterator = 1;
			do
			{
				for (int columnIterator = 1; columnIterator <= meta.getColumnCount(); columnIterator++) 
				{  
			       row = table.get(rowIterator);
			       rs.updateString(meta.getColumnName(columnIterator), row.get(meta.getColumnName(columnIterator)));
			    }
			 
			    rs.updateRow();
			    rowIterator++;
			 }while (rs.next());
		}	
		
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH UPDATE ROW IN DB", e);
		}
	}
	
	public ResultSet GetQueryResultsSet(String query) throws DatabaseException
	{
		this.query = query;
		try 
		{
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
		    rs =    stmt.executeQuery(this.query);
		    rs.first();
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH RESULT-SET OBTAINED FROM DB",e);
		}
		return rs;
	}
	
	public void createTable(String query) throws SQLException
	{
		this.query=query;
		 stmt = conn.createStatement();
		 //System.out.println(this.query);
		 stmt.execute(this.query);
	}
	
	public void switchDB(String db) throws DatabaseException
	{
		try
		{
			conn.setCatalog(db);
		}
		catch (SQLException e)
		{
			throw new DatabaseException("Error while switch DataBase", e);
		}
	}
	
	public  void truncateTable(String tablename) throws SQLException
	{
		stmt = conn.createStatement();
		String query="TRUNCATE "+tablename;
		stmt.executeUpdate(query);
	}
	
	public void insetRowWithSNO(String OutputTableName,String inputTableName) throws SQLException
	{
		stmt = conn.createStatement();
		String query1 ="INSERT INTO "+OutputTableName+" (`S_No`,`Testdata`,`Flag_for_execution`) SELECT `S_No`,`Testdata`,`Flag_for_execution` FROM "+inputTableName;
		stmt.executeUpdate(query1);
	}
	
	
	
}
