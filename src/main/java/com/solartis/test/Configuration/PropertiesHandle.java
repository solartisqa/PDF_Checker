package com.solartis.test.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Map.Entry;

import com.solartis.common.DatabaseOperation;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.PropertiesHandleException;

public class PropertiesHandle extends Properties

{
	private static final long serialVersionUID = 1L;
	protected String path = null;
	protected String Project;
	protected String PDFName;
	protected String UserName;
	protected String JDBC_DRIVER;
	protected String DB_URL;
	protected String USER;
	protected String password;
	protected String queryresult;
	protected String ExecutionName;
	static DatabaseOperation ConfigQuery = new DatabaseOperation();
			
	    public PropertiesHandle(String Project,String PDFName,  String UserName, String JDBC_DRIVER, String DB_URL, String USER, String password,String ExecutionName) throws DatabaseException, PropertiesHandleException
		{
			this.Project = Project;
			this.PDFName=PDFName;
			this.UserName=UserName;
			this.JDBC_DRIVER=JDBC_DRIVER;
			this.DB_URL=DB_URL;
			this.USER=USER;
			this.password=password;
			this.ExecutionName=ExecutionName;
			WriteProperty(UserName);
			
		}
		
		protected void WriteProperty(String UserName) throws DatabaseException, PropertiesHandleException
		{
			DatabaseOperation.ConnectionSetup(JDBC_DRIVER, DB_URL, USER, password);						
          /*  if(OutputChioce.equalsIgnoreCase("Output_Saved_in_DB"))
            {
				 this.ActualAndComparisonStatus("Y", "N");    
            }
		    else if(OutputChioce.equalsIgnoreCase("Get_Response_Only"))
		    {
		    	this.ActualAndComparisonStatus("N", "N");    
		    }
			if(OutputChioce.equalsIgnoreCase("Compared_Results"))
			{
				this.ActualAndComparisonStatus("Y", "Y");    
			}*/
			this.put("ProjectDBName", RdmsValue("ProjectDatabaseName"));
			this.put("FormsMappingTable", RdmsValue("FormsMappingTable"));
			this.put("jdbc_driver", JDBC_DRIVER);
			this.put("db_url", DB_URL);
			this.put("db_username", USER);
			this.put("db_password", password);
			this.put("inputOutputTableQuery", RdmsValue("Query"));
			this.put("ClassName", RdmsValue("ClassName"));
		    DatabaseOperation.CloseConn();		 
		}
		
		protected String RdmsValue(String OutputColoumn) throws PropertiesHandleException
		{
			try
			{
				LinkedHashMap<Integer, LinkedHashMap<String, String>> tableRdmsValue = ConfigQuery.GetDataObjects("SELECT * FROM `Forms_Config` WHERE Forms_Config.Project=\""+Project+"\" AND Forms_Config.PDFName=\""+PDFName+"\" ORDER BY Forms_Config.Version DESC LIMIT 1;");
				for (Entry<Integer, LinkedHashMap<String, String>> entry : tableRdmsValue.entrySet())	
				{
					LinkedHashMap<String, String> rowRdmsValue = entry.getValue();
					queryresult = rowRdmsValue.get(OutputColoumn);
				}
				return queryresult;
			}
			catch(DatabaseException e)
			{
				throw new PropertiesHandleException("ERROR IN RETRIVING DATA FROM -- " + OutputColoumn, e);
			}
		}

		public PropertiesHandle(String path) throws PropertiesHandleException
		{
			this.path = path;
			
			FileInputStream configuration = null;
			
			try 
			{
				configuration = new FileInputStream(path);
			} 
			catch (FileNotFoundException e) 
			{
				throw new PropertiesHandleException("CONFIGURATION FILE PATH DOES NOT CONTAINS CONFIG FILE", e);
			}
			try 
			{
				this.load(configuration);
			} 
			catch (IOException e) 
			{
				throw new PropertiesHandleException("ERROR IN LOADING A CONFIG FILE", e);
			}
		}
		
		public void store(String newpath) throws PropertiesHandleException
		{
			Writer writer = null;
			try 
			{
				 writer = new FileWriter(newpath);
			} 
			catch (IOException e) 
			{
				throw new PropertiesHandleException("ERROR IN WRITING A CONFIG FILE", e);
			}
			try 
			{
				this.store(writer, "File saved");
			} 
			catch (IOException e) 
			{
				throw new PropertiesHandleException("ERROR IN STORING A CONFIG FILE", e);
			};
		}
		
		public void store()
		{
			Writer writer = null;
			try 
			{
				 writer = new FileWriter(this.path);
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try 
			{
				this.store(writer, "File saved");
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
}