package com.solartis.test.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	protected String Api;
	protected String Env;
	protected String OutputChioce;
	protected String UserName;
	protected String JDBC_DRIVER;
	protected String DB_URL;
	protected String USER;
	protected String password;
	protected String priority;
	protected String queryresult;
	protected String ExecutionName;
    protected String ModeofExecution;
	static DatabaseOperation ConfigQuery = new DatabaseOperation();
			
	    public PropertiesHandle(String Project,String Api, String Env ,String OutputChioce, String UserName, String JDBC_DRIVER, String DB_URL, String USER, String password, String priority,String ExecutionName,String ModeofExecution) throws DatabaseException, PropertiesHandleException
		{
			this.Project = Project;
			this.Api=Api;
			this.Env=Env;
			this.OutputChioce=OutputChioce;
			this.UserName=UserName;
			this.JDBC_DRIVER=JDBC_DRIVER;
			this.DB_URL=DB_URL;
			this.USER=USER;
			this.password=password;
			this.priority=priority;
			this.ExecutionName=ExecutionName;
			this.ModeofExecution=ModeofExecution;
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
			this.put("Execution_Flag",OutputChioce);
			this.put("output_in_same_table", this.RdmsValue("OutputInInputTable"));
			this.put("sample_request", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/SampleRequest/SampleRequest" + this.RdmsValue("Version") + "/");
			this.put("request_location", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/Results/Request/");
			this.put("response_location", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/Results/Response/");
			this.put("Samplepath", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api + "/SampleRatingModel/SampleRating" + this.RdmsValue("Version") + "/");
			this.put("TargetPath", this.RdmsValue("RootFolder") + "/" + Project + "/" + Api +  "/Results/RatingModelResult/");
			this.put("config_query", this.RdmsQuery("MacroMappingTable"));
			this.put("lookup_query", this.RdmsQuery("MacroTranslationTable"));
			this.put("test_url", this.RdmsValue("URL"));
			this.put("AuthenticationURL", this.RdmsValue("AuthenticationURL"));
			this.put("OwnerID", this.RdmsValue("OwnerID"));
			this.put("Userneme", this.RdmsValue("Userneme"));
			this.put("Password", this.RdmsValue("Password"));
			this.put("AuthenticationToken", this.RdmsValue("AuthenticationToken"));
			this.put("content_type", "application/"+this.RdmsValue("ServiceType"));
			this.put("token", this.RdmsValue("Token"));
		    this.put("EventName", this.RdmsValue("EventName"));
		    this.put("EventVersion", this.RdmsValue("EventVersion"));
			this.put("output_query", this.RdmsQuery("OutputTable"));
			this.put("InputColQuery",this.RdmsQuery("InputConditonTable"));
			this.put("OutputColQuery",this.RdmsQuery("OutputConditionTable"));
			this.put("ClassName", this.RdmsValue("ClassName"));
			this.put("InputJsonPath", "InputJsonPath");
		    this.put("OutputJsonPath", "OutputJsonPath");
			this.put("InputColumn", "InputColumn");
			this.put("OutputColumn", "OutputColumn");
			this.put("InputCondColumn", "InputColumnCondtn");
			this.put("OutputCondColumn", "OutputColumnCondtn");
			this.put("ExpectedColumn", "ExpectedColumn");
			this.put("StatusColumn", "StatusColumn");
			//this.put("request_query", "SELECT * FROM ConditionInputTable_LDWC_Rating");
			this.put("jdbc_driver", this.RdmsValue("JDCDriver"));
			this.put("db_url", this.RdmsValue("DB_URL") + "/" + this.RdmsValue("ProjectDBName") + "_" + this.RdmsValue("UserDBName"));
			this.put("db_username", this.RdmsValue("DB_UserName"));
			this.put("db_password", this.RdmsValue("DB_Password"));
			this.put("report_location", this.RdmsValue("RootFolder") + "/" + Project + "/" +  Api + "/Results/AnalysisReport/");		 
			this.put("report_template_location", this.RdmsValue("RootFolder") + "/ReportTemplate/");
			this.put("Project",Project );
			this.put("API",Api );
			this.put("ExecutionName", ExecutionName);
			this.put("TestcaseQuery", "SELECT * FROM "+this.RdmsValue("InputTable"));
			this.put("resultQuery", "SELECT * FROM "+this.RdmsValue("OutputTable"));

			this.put("inputTable", this.RdmsValue("InputTable"));
			this.put("outputTable", this.RdmsValue("OutputTable"));
			this.put("TestdataPath", this.RdmsValue("RootFolder") + "/" + Project + "/"+ Api + "/Testdata/"+this.getProperty("ExecutionName")+".xls");
			this.put("ZipFolderPath", this.RdmsValue("RootFolder") + "/" + Project + "/" +  Api + "/Results/");
			Date date = new Date();
			String DateandTime = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(date);
			this.put("OverallResults", this.RdmsValue("RootFolder") + "/" + Project + "/" +  Api + "/Reports/"+this.getProperty("ExecutionName")+"_"+Env+"_"+DateandTime+".zip");
			this.InputQuery();
			this.put("ModeofExecution", ModeofExecution);
		    DatabaseOperation.CloseConn();		 
		}
		
		protected String RdmsQuery(String OutputColoumn) throws PropertiesHandleException
		{
			try
			{
				LinkedHashMap<Integer, LinkedHashMap<String, String>> tableRdmsQuery =  ConfigQuery.GetDataObjects("SELECT UserFolder_CONFIG.RootFolder,UserFolder_CONFIG.JDCDriver,UserFolder_CONFIG.DB_URL,UserFolder_CONFIG.DB_UserName,UserFolder_CONFIG.DB_Password,UserFolder_CONFIG.UserDBName,Version_CONFIG.Version,Project_CONFIG.ProjectDBName,Project_CONFIG.ServiceType,Environment_CONFIG.URL,Project_CONFIG.Token,VersionDetail_CONFIG.EventName,VersionDetail_CONFIG.EventVersion,API_CONFIG.OutputInInputTable,VersionDetail_CONFIG.ClassName,VersionDetail_CONFIG.InputConditonTable,VersionDetail_CONFIG.InputTable,VersionDetail_CONFIG.OutputConditionTable,VersionDetail_CONFIG.OutputTable,VersionDetail_CONFIG.MacroMappingTable,VersionDetail_CONFIG.MacroTranslationTable FROM Project_CONFIG INNER JOIN UserFolder_CONFIG INNER JOIN API_CONFIG ON Project_CONFIG.ProjectID = API_CONFIG.ProjectID INNER JOIN Environment_CONFIG ON API_CONFIG.APIID = Environment_CONFIG.APIID INNER JOIN Version_CONFIG ON Environment_CONFIG.Env_ID = Version_CONFIG.Env_ID INNER JOIN VersionDetail_CONFIG ON (VersionDetail_CONFIG.Verision = Version_CONFIG.Version and VersionDetail_CONFIG.APIID = API_CONFIG.APIID)  WHERE Project_CONFIG.ProjectName ='" + Project +"' AND API_CONFIG.APIName = '" + Api + "' AND Environment_CONFIG.Env_Name = '" + Env + "' AND UserFolder_CONFIG.User_ID = '" + UserName + "' ORDER BY Version_CONFIG.Version DESC LIMIT 1");
				for (Entry<Integer, LinkedHashMap<String, String>> entry : tableRdmsQuery.entrySet())	
				{
					LinkedHashMap<String, String> rowRdmsQuery = entry.getValue();
					queryresult =  rowRdmsQuery.get(OutputColoumn);
				}
				return "SELECT * FROM " + queryresult;		
			}
			catch(DatabaseException e)
			{
				throw new PropertiesHandleException("ERROR IN SQL - QUERY -- " + OutputColoumn, e);
			}
		}
		
		protected String RdmsQueryWithPriority(String OutputColoumn, String priority) throws PropertiesHandleException
		{
			try 
			{
				LinkedHashMap<Integer, LinkedHashMap<String, String>> tableRdmsQueryWithPriority =  ConfigQuery.GetDataObjects("SELECT UserFolder_CONFIG.RootFolder,UserFolder_CONFIG.JDCDriver,UserFolder_CONFIG.DB_URL,UserFolder_CONFIG.DB_UserName,UserFolder_CONFIG.DB_Password,UserFolder_CONFIG.UserDBName,Version_CONFIG.Version,Project_CONFIG.ProjectDBName,Project_CONFIG.ServiceType,Environment_CONFIG.URL,Project_CONFIG.Token,VersionDetail_CONFIG.EventName,VersionDetail_CONFIG.EventVersion,API_CONFIG.OutputInInputTable,VersionDetail_CONFIG.ClassName,VersionDetail_CONFIG.InputConditonTable,VersionDetail_CONFIG.InputTable,VersionDetail_CONFIG.OutputConditionTable,VersionDetail_CONFIG.OutputTable,VersionDetail_CONFIG.MacroMappingTable,VersionDetail_CONFIG.MacroTranslationTable FROM Project_CONFIG INNER JOIN UserFolder_CONFIG INNER JOIN API_CONFIG ON Project_CONFIG.ProjectID = API_CONFIG.ProjectID INNER JOIN Environment_CONFIG ON API_CONFIG.APIID = Environment_CONFIG.APIID INNER JOIN Version_CONFIG ON Environment_CONFIG.Env_ID = Version_CONFIG.Env_ID INNER JOIN VersionDetail_CONFIG ON (VersionDetail_CONFIG.Verision = Version_CONFIG.Version and VersionDetail_CONFIG.APIID = API_CONFIG.APIID)  WHERE Project_CONFIG.ProjectName ='" + Project +"' AND API_CONFIG.APIName = '" + Api + "' AND Environment_CONFIG.Env_Name = '" + Env + "' AND UserFolder_CONFIG.User_ID = '" + UserName + "' ORDER BY Version_CONFIG.Version DESC LIMIT 1");
				for (Entry<Integer, LinkedHashMap<String, String>> entry : tableRdmsQueryWithPriority.entrySet())	
				{
					LinkedHashMap<String, String> rowRdmsQueryWithPriority = entry.getValue();
					queryresult = rowRdmsQueryWithPriority.get(OutputColoumn);
				}	
				return "SELECT * FROM " + queryresult + " WHERE " + queryresult + ".Priority = '" + priority + "'";	
			} 
			catch (DatabaseException e)
			{
				throw new PropertiesHandleException("ERROR IN SQL - PRIORITY QUERY -- " + OutputColoumn, e);
			}
		}
		
		protected String RdmsQueryWithCustomSortPriority(String OutputColoumn) throws PropertiesHandleException
		{
			try 
			{
				LinkedHashMap<Integer, LinkedHashMap<String, String>> tableRdmsQueryWithCustomSortPriority = ConfigQuery.GetDataObjects("SELECT UserFolder_CONFIG.RootFolder,UserFolder_CONFIG.JDCDriver,UserFolder_CONFIG.DB_URL,UserFolder_CONFIG.DB_UserName,UserFolder_CONFIG.DB_Password,UserFolder_CONFIG.UserDBName,Version_CONFIG.Version,Project_CONFIG.ProjectDBName,Project_CONFIG.ServiceType,Environment_CONFIG.URL,Project_CONFIG.Token,VersionDetail_CONFIG.EventName,VersionDetail_CONFIG.EventVersion,API_CONFIG.OutputInInputTable,VersionDetail_CONFIG.ClassName,VersionDetail_CONFIG.InputConditonTable,VersionDetail_CONFIG.InputTable,VersionDetail_CONFIG.OutputConditionTable,VersionDetail_CONFIG.OutputTable,VersionDetail_CONFIG.MacroMappingTable,VersionDetail_CONFIG.MacroTranslationTable FROM Project_CONFIG INNER JOIN UserFolder_CONFIG INNER JOIN API_CONFIG ON Project_CONFIG.ProjectID = API_CONFIG.ProjectID INNER JOIN Environment_CONFIG ON API_CONFIG.APIID = Environment_CONFIG.APIID INNER JOIN Version_CONFIG ON Environment_CONFIG.Env_ID = Version_CONFIG.Env_ID INNER JOIN VersionDetail_CONFIG ON (VersionDetail_CONFIG.Verision = Version_CONFIG.Version and VersionDetail_CONFIG.APIID = API_CONFIG.APIID)  WHERE Project_CONFIG.ProjectName ='" + Project +"' AND API_CONFIG.APIName = '" + Api + "' AND Environment_CONFIG.Env_Name = '" + Env + "' AND UserFolder_CONFIG.User_ID = '" + UserName + "' ORDER BY Version_CONFIG.Version DESC LIMIT 1");
			    for (Entry<Integer, LinkedHashMap<String, String>> entry : tableRdmsQueryWithCustomSortPriority.entrySet())	
				{
					LinkedHashMap<String, String> rowRdmsQueryWithCustomSortPriority = entry.getValue();
					queryresult = rowRdmsQueryWithCustomSortPriority.get(OutputColoumn);
				}
				return "SELECT * FROM " + queryresult + " WHERE (" + queryresult + ".Priority = 'high' OR " + queryresult + ".Priority = 'low' or " + queryresult + ".Priority = 'medium') ORDER BY ( " + queryresult + ".`S_No`)";//".Priority ,'high','medium','low')";	
			} 
			catch (DatabaseException e)
			{
				throw new PropertiesHandleException("ERROR IN SQL - CUSTOM SORT PRIORITY QUERY -- " + OutputColoumn, e);
			}
		}
		
		protected String RdmsValue(String OutputColoumn) throws PropertiesHandleException
		{
			try
			{
				LinkedHashMap<Integer, LinkedHashMap<String, String>> tableRdmsValue = ConfigQuery.GetDataObjects("SELECT UserFolder_CONFIG.RootFolder,UserFolder_CONFIG.JDCDriver,UserFolder_CONFIG.DB_URL,UserFolder_CONFIG.DB_UserName,UserFolder_CONFIG.DB_Password,UserFolder_CONFIG.UserDBName,Version_CONFIG.Version,Project_CONFIG.ProjectDBName,Project_CONFIG.ServiceType,Environment_CONFIG.URL,Environment_CONFIG.AuthenticationURL,Environment_CONFIG.AuthenticationToken,Environment_CONFIG.OwnerID,Environment_CONFIG.Userneme,Environment_CONFIG.Password,Project_CONFIG.Token,VersionDetail_CONFIG.EventName,VersionDetail_CONFIG.EventVersion,API_CONFIG.OutputInInputTable,VersionDetail_CONFIG.ClassName,VersionDetail_CONFIG.InputConditonTable,VersionDetail_CONFIG.InputTable,VersionDetail_CONFIG.OutputConditionTable,VersionDetail_CONFIG.OutputTable,VersionDetail_CONFIG.MacroMappingTable,VersionDetail_CONFIG.MacroTranslationTable FROM Project_CONFIG INNER JOIN UserFolder_CONFIG INNER JOIN API_CONFIG ON Project_CONFIG.ProjectID = API_CONFIG.ProjectID INNER JOIN Environment_CONFIG ON API_CONFIG.APIID = Environment_CONFIG.APIID INNER JOIN Version_CONFIG ON Environment_CONFIG.Env_ID = Version_CONFIG.Env_ID INNER JOIN VersionDetail_CONFIG ON (VersionDetail_CONFIG.Verision = Version_CONFIG.Version and VersionDetail_CONFIG.APIID = API_CONFIG.APIID)  WHERE Project_CONFIG.ProjectName ='" + Project +"' AND API_CONFIG.APIName = '" + Api + "' AND Environment_CONFIG.Env_Name = '" + Env + "' AND UserFolder_CONFIG.User_ID = '" + UserName + "' ORDER BY Version_CONFIG.Version DESC LIMIT 1");
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

	
		protected void ActualAndComparisonStatus(String Actual, String Comparison)// FUNCTION FOR ACTUAL AND STATUS OCCURANCE
		{
			this.put("actualFlag", Actual);
			this.put("ComparisonFlag", Comparison);
		}	
		
		
	
		protected void InputQuery() throws PropertiesHandleException// FUNCTION FOR INPUTQUERY
		{
				if(priority.equalsIgnoreCase("all"))
				{
					this.put("input_query",  this.RdmsQueryWithCustomSortPriority("InputTable") );
				}
				else if(priority.equalsIgnoreCase("high") || priority.equalsIgnoreCase("low"))
				{
					this.put("input_query",  this.RdmsQueryWithPriority("InputTable", priority));
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