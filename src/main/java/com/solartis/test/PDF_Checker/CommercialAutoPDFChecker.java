package com.solartis.test.PDF_Checker;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.itextpdf.text.DocumentException;
import com.solartis.common.DBColoumnVerify;
import com.solartis.common.DatabaseOperation;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;

public class CommercialAutoPDFChecker  extends DBColoumnVerify 
{
	public LinkedHashMap<Integer,SheduleOfFormsList> formsList;
	public PDFUtilities pdf;
	protected PropertiesHandle config = null;
	protected DatabaseOperation DB;
	
    public CommercialAutoPDFChecker()
    {
    	pdf = new PDFUtilities();
    	DB = new DatabaseOperation();
    }
    
    public LinkedHashMap<Integer,SheduleOfFormsList> loadListofForms(PropertiesHandle config,LinkedHashMap<String, String> inputOutputRow) throws DatabaseException
    {
    	int i=0;
    	formsList = new LinkedHashMap<Integer,SheduleOfFormsList>();
    	DB.switchDB(config.getProperty("ProjectDBName"));
    	LinkedHashMap<Integer, LinkedHashMap<String, String>> ConfigTable = DB.GetDataObjects("SELECT * FROM `FormsNameFileNameMaping`");//config.getProperty("OutputColQuery"));		
		for (Entry<Integer, LinkedHashMap<String, String>> entry : ConfigTable.entrySet())	
		{
			LinkedHashMap<String, String> ConfigTableRow = entry.getValue();
			if(ConfigTableRow.get("FlagForExecution").equalsIgnoreCase("Y")&&ConditionReading(ConfigTableRow.get("Condition"),inputOutputRow))
			{
				i+=1;
				formsList.put(i, new SheduleOfFormsList(ConfigTableRow.get("FormNumber"),ConfigTableRow.get("FormEdition"),ConfigTableRow.get("FormDescription"),
						ConfigTableRow.get("SheduleofFormsFlag"),ConfigTableRow.get("FormNature"),ConfigTableRow.get("FormID"),ConfigTableRow.get("FormFileName")));
			}
		}
		return formsList;
		
    }
    
    public void pumpDatatoForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList,LinkedHashMap<String, String> inputOutputrow, PropertiesHandle config, String SampleLocation, String TempLocation) throws Exception
    {
    	for (Entry<Integer,SheduleOfFormsList> entryy : formsList.entrySet())	
		{
    		SheduleOfFormsList formlist=entryy.getValue();
    		if(formlist.getFormNature().equalsIgnoreCase("dynamic"))
    		{
	    		pdf.openPDF(SampleLocation+formlist.getFormFileName()+".pdf", TempLocation+formlist.getFormFileName()+".pdf");
		    	
	    		LinkedHashMap<Integer, LinkedHashMap<String, String>> ConfigTable = DB.GetDataObjects("Select * from `"+config.getProperty("FormsMappingTable")+"` where FormID = '"+formlist.getFormID()+"'");		
				for (Entry<Integer, LinkedHashMap<String, String>> entry : ConfigTable.entrySet())	
				{
					LinkedHashMap<String, String> ConfigTableRow = entry.getValue();
				/*	//System.out.println(ConfigTableRow.get("PDFPageNumbertoFill"));
					int PageNumber = Integer.parseInt(ConfigTableRow.get("PDFPageNumbertoFill"));
					String[] coordinates = ConfigTableRow.get("PDFCoordinatestoFill").split(",");
					
					int llx=Integer.parseInt(coordinates[0]);	int lly=Integer.parseInt(coordinates[1]);				
					int urx=Integer.parseInt(coordinates[2]);	int ury=Integer.parseInt(coordinates[3]);
					
					String data=inputOutputrow.get(ConfigTableRow.get("ColumnName"));
					pdf.feedInData(PageNumber, llx, lly, urx, ury, data);*/
					pdf.feedinData(SampleLocation+formlist.getFormFileName()+".docx", TempLocation, inputOutputrow);
				}
				pdf.closePDF();
    		}
    		else
    		{
    			pdf.Copy(SampleLocation+formlist.getFormFileName()+".pdf", TempLocation+formlist.getFormFileName()+".pdf");
    		}
		}
    }
    
    public void mergeForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList,String outputFilePath, String TempPath) throws IOException, DocumentException
    {
    	pdf.mergeFiles(formsList, outputFilePath,true,TempPath);
    }
    
    public void generateActualPDF(LinkedHashMap<String, String> inputOutputrow, String ActualPDFPath) throws IOException
    {
    	pdf.urltopdf(inputOutputrow.get("ISSUANCE"), ActualPDFPath);
    }
    
    public void comparePDFS(LinkedHashMap<String, String> inputOutputrow, String ActualPDFPath, String ExpectedPDFPath, String ScreenShotPath) throws IOException
    {
    	pdf.comparePDFVisually(ActualPDFPath, ExpectedPDFPath, ScreenShotPath,inputOutputrow.get("Testdata"));
    }
    
    public void checkPDFPageSizes(String filePath, double width, double height) throws IOException
    {
    	pdf.checkPageSize(filePath, width, height);
    }
    
    public void sheduleOfForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList)
    {
    	//pdf.SheduleOfForms(formsList, "E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates");
    }
    
    public static void main(String args[]) throws IOException, DocumentException
    {
    	LinkedHashMap<Integer,SheduleOfFormsList> formsList = new LinkedHashMap<Integer,SheduleOfFormsList> ();
    	//formsList.put(1, new SheduleOfFormsList("number","editin","SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf","","static"));
    	//formsList.put(2, new SheduleOfFormsList("number","editin","SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance.pdf","","dynamic"));
    	StarrGLPDFChecker pdfcheck = new StarrGLPDFChecker();
    	//pdfcheck.mergeForms(formsList, "Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\mergedform.pdf");
    	pdfcheck.sheduleOfForms(formsList);
    			
    }
    
}
