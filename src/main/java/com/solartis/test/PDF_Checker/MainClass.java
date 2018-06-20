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
		String BaseProjectFolder="E:\\RestFullAPIDeliverable\\Devolpement\\admin\\";
		String ProjectName=System.getProperty("Project");
		String PDFName=System.getProperty("PDFName");
		String ActualPDFPath = BaseProjectFolder+ProjectName+"\\PDFs\\"+PDFName+"\\Actual\\";
		String ExpectedPDFPath =BaseProjectFolder+ProjectName+"\\PDFs\\"+PDFName+"\\Expected\\";
		String ScreenShotPath = BaseProjectFolder+ProjectName+"\\PDFs\\"+PDFName+"\\Result\\ScreenShots\\";
		try 
		{
			//config = new PropertiesHandle("Starr-SGL","PolicyPDF","Admin", "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.84.225:3700/Starr_Config_Development","root","redhat","rerun");
			config = new PropertiesHandle(ProjectName, PDFName, System.getProperty("UserName"), System.getProperty("JDBC_DRIVER"), System.getProperty("DB_URL"), System.getProperty("USER"), System.getProperty("password"),System.getProperty("ExecutionName"));
			StarrGLPDFChecker checkGL = new StarrGLPDFChecker();
			DatabaseOperation DB = new DatabaseOperation();
			DatabaseOperation.ConnectionSetup(config);
			System.out.println(config.getProperty("ProjectDBName"));
			DB.switchDB(config.getProperty("ProjectDBName"));
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableOutputColVerify = DB.GetDataObjects(config.getProperty("inputOutputTableQuery"));		
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableOutputColVerify.entrySet())	
			{
				LinkedHashMap<String, String> inputOutputRow = entry.getValue();
				if(inputOutputRow.get("Flag_for_execution").equals("Y"))
				{
					LinkedHashMap<Integer,SheduleOfFormsList> files=checkGL.loadListofForms(config);
					checkGL.pumpDatatoForms(files, inputOutputRow,config);
					checkGL.mergeForms(files,ExpectedPDFPath+inputOutputRow.get("Testdata")+".pdf");
					checkGL.generateActualPDF(inputOutputRow,ActualPDFPath+inputOutputRow.get("Testdata")+".pdf");
					checkGL.checkPDFPageSizes(ActualPDFPath+inputOutputRow.get("Testdata")+".pdf", 8.5, 11);
					checkGL.comparePDFS(inputOutputRow,ActualPDFPath+inputOutputRow.get("Testdata")+".pdf",
							ExpectedPDFPath+inputOutputRow.get("Testdata")+".pdf", ScreenShotPath);
				}
			}
		} catch (DatabaseException | PropertiesHandleException | IOException | DocumentException e) {
			DatabaseOperation.CloseConn();
			e.printStackTrace();
		}
		
	}
}
