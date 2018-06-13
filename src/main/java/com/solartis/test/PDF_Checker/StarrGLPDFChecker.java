package com.solartis.test.PDF_Checker;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.itextpdf.text.DocumentException;
import com.solartis.common.DatabaseOperation;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;

public class StarrGLPDFChecker 
{
	public LinkedHashMap<Integer,SheduleOfFormsList> formsList;
	public EditExisting1 pdf;
	protected PropertiesHandle config = null;
	protected DatabaseOperation DB;
	
    public StarrGLPDFChecker()
    {
    	pdf = new EditExisting1();
    	DB=new DatabaseOperation();
    }
    
    public LinkedHashMap<Integer,SheduleOfFormsList> loadListofForms(PropertiesHandle config) throws DatabaseException
    {
    	int i=0;
    	formsList = new LinkedHashMap<Integer,SheduleOfFormsList>();
    	LinkedHashMap<Integer, LinkedHashMap<String, String>> ConfigTable = DB.GetDataObjects("SELECT * FROM `FormsConditionTable` GROUP BY FormDescription ORDER BY PrintOrder");//config.getProperty("OutputColQuery"));		
		for (Entry<Integer, LinkedHashMap<String, String>> entry : ConfigTable.entrySet())	
		{
			LinkedHashMap<String, String> ConfigTableRow = entry.getValue();
			i+=1;
			formsList.put(i, new SheduleOfFormsList(ConfigTableRow.get("FormNumber"),ConfigTableRow.get("FormEdition"),ConfigTableRow.get("FormDescription"),ConfigTableRow.get("SheduleofFormsFlag")));
			
		}
		return formsList;
		
    }
    
    public void pumpDatatoForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList,LinkedHashMap<String, String> inputOutputrow) throws DatabaseException
    {
    	for (Entry<Integer,SheduleOfFormsList> entryy : formsList.entrySet())	
		{
    		SheduleOfFormsList formlist=entryy.getValue();
    		pdf.openPDF("Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\"+formlist.getFormDescription()+".pdf", "Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf");
	    	
    		LinkedHashMap<Integer, LinkedHashMap<String, String>> ConfigTable = DB.GetDataObjects("Select * from table where FormDescription = '"+formlist.getFormDescription()+"'");		
			for (Entry<Integer, LinkedHashMap<String, String>> entry : ConfigTable.entrySet())	
			{
				LinkedHashMap<String, String> ConfigTableRow = entry.getValue();
				
				int PageNumber = Integer.parseInt(ConfigTableRow.get("PDFPageNumbertoFill"));
				String[] coordinates = ConfigTableRow.get("PDFCoordinatestoFill").split(",");
				
				int llx=Integer.parseInt(coordinates[0]);	int lly=Integer.parseInt(coordinates[1]);				
				int urx=Integer.parseInt(coordinates[2]);	int ury=Integer.parseInt(coordinates[3]);
				
				String data=inputOutputrow.get(ConfigTableRow.get("ColumnName"));
				pdf.feedInData(PageNumber, llx, lly, urx, ury, data);
				//pdf.feedInData(2, 100, 200, 300, 300, "My trial text2");				
			}
			pdf.closePDF();
		}
    }
    
    public void mergeForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList,String outputFilePath) throws IOException, DocumentException
    {
    	pdf.mergeFiles(formsList, outputFilePath,true);
    }
    public void comparePDFS()
    {
    	
    }
    
    public static void main(String args[]) throws IOException, DocumentException
    {
    	LinkedHashMap<Integer,SheduleOfFormsList> formsList = new LinkedHashMap<Integer,SheduleOfFormsList> ();
    	formsList.put(1, new SheduleOfFormsList("number","editin","Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf",""));
    	formsList.put(2, new SheduleOfFormsList("number","editin","Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance.pdf",""));
    	StarrGLPDFChecker pdfcheck = new StarrGLPDFChecker();
    	pdfcheck.mergeForms(formsList, "Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\mergedform.pdf");
    			
    }
    
}
