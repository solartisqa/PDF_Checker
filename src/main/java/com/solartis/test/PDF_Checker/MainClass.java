package com.solartis.test.PDF_Checker;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.itextpdf.text.DocumentException;
import com.solartis.common.DatabaseOperation;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.PDFException;
import com.solartis.test.exception.PropertiesHandleException;

public class MainClass 
{
	public static PropertiesHandle config; 
	public static void main(String args[]) throws DatabaseException, PDFException
	{
		System.setProperty("jsse.enableSNIExtension", "false");

		try 
		{
			config = new PropertiesHandle("Starr-SGL","PolicyPDF","Admin", "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.84.225:3700/Starr_Config_Development","root","redhat","rerun");
			//config = new PropertiesHandle(System.getProperty("Project"), System.getProperty("PDFName"), System.getProperty("UserName"), System.getProperty("JDBC_DRIVER"), System.getProperty("DB_URL"), System.getProperty("USER"), System.getProperty("password"),System.getProperty("ExecutionName"));
			StarrGLPDFChecker checkGL = new StarrGLPDFChecker();
			DatabaseOperation DB = new DatabaseOperation();
			DatabaseOperation.ConnectionSetup(config);
			System.out.println(config.getProperty("ProjectDBName"));
			DB.switchDB(config.getProperty("ProjectDBName"));
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableOutputColVerify = DB.GetDataObjects("Select * from INPUT_Quote_GL_V6");		
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableOutputColVerify.entrySet())	
			{
				LinkedHashMap<String, String> inputOutputRow = entry.getValue();
				if(inputOutputRow.get("Flag_for_execution").equals("Y"))
				{
					LinkedHashMap<Integer,SheduleOfFormsList> files=checkGL.loadListofForms(config);
					checkGL.pumpDatatoForms(files, inputOutputRow,config);
					checkGL.mergeForms(files, "E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\Expected\\"+inputOutputRow.get("Testdata")+".pdf");
				}
			}
		} catch (DatabaseException | PropertiesHandleException | IOException | DocumentException e) {
			DatabaseOperation.CloseConn();
			e.printStackTrace();
		}
		
	}
}
