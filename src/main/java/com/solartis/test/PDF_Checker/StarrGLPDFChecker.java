package com.solartis.test.PDF_Checker;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.itextpdf.text.DocumentException;
import com.solartis.common.DatabaseOperation;
import com.solartis.test.Configuration.PropertiesHandle;
import com.solartis.test.exception.DatabaseException;
import com.solartis.test.exception.PDFException;

public class StarrGLPDFChecker 
{
	public LinkedHashMap<Integer,SheduleOfFormsList> formsList;
	public PDFUtilities pdf;
	protected PropertiesHandle config = null;
	protected DatabaseOperation DB;
	
    public StarrGLPDFChecker()
    {
    	pdf = new PDFUtilities();
    	DB=new DatabaseOperation();
    }
    
    public LinkedHashMap<Integer,SheduleOfFormsList> loadListofForms(PropertiesHandle config) throws DatabaseException
    {
    	int i=0;
    	formsList = new LinkedHashMap<Integer,SheduleOfFormsList>();
    	DB.switchDB(config.getProperty("ProjectDBName"));
    	LinkedHashMap<Integer, LinkedHashMap<String, String>> ConfigTable = DB.GetDataObjects("SELECT * FROM `"+config.getProperty("FormsMappingTable")+"` GROUP BY FormDescription ORDER BY PrintOrder");//config.getProperty("OutputColQuery"));		
		for (Entry<Integer, LinkedHashMap<String, String>> entry : ConfigTable.entrySet())	
		{
			LinkedHashMap<String, String> ConfigTableRow = entry.getValue();
			if(ConfigTableRow.get("FlagForExecution").equalsIgnoreCase("Y"))
			{
				i+=1;
				formsList.put(i, new SheduleOfFormsList(ConfigTableRow.get("FormNumber"),ConfigTableRow.get("FormEdition"),ConfigTableRow.get("FormDescription"),ConfigTableRow.get("SheduleofFormsFlag"),ConfigTableRow.get("FormNature")));
			}
		}
		return formsList;
		
    }
    
    public void pumpDatatoForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList,LinkedHashMap<String, String> inputOutputrow, PropertiesHandle config) throws DatabaseException, PDFException
    {
    	for (Entry<Integer,SheduleOfFormsList> entryy : formsList.entrySet())	
		{
    		SheduleOfFormsList formlist=entryy.getValue();
    		if(formlist.getFormNature().equalsIgnoreCase("dynamic"))
    		{
	    		pdf.openPDF("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates\\"+formlist.getFormDescription()+".pdf", "E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\temp\\"+formlist.getFormDescription()+".pdf");
		    	
	    		LinkedHashMap<Integer, LinkedHashMap<String, String>> ConfigTable = DB.GetDataObjects("Select * from `"+config.getProperty("FormsMappingTable")+"` where FormDescription = '"+formlist.getFormDescription()+"'");		
				for (Entry<Integer, LinkedHashMap<String, String>> entry : ConfigTable.entrySet())	
				{
					LinkedHashMap<String, String> ConfigTableRow = entry.getValue();
					System.out.println(ConfigTableRow.get("PDFPageNumbertoFill"));
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
    		else
    		{
    			pdf.Copy("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates\\"+formlist.getFormDescription()+".pdf", "E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\temp\\"+formlist.getFormDescription()+".pdf");
    		}
		}
    }
    
    public void mergeForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList,String outputFilePath) throws IOException, DocumentException
    {
    	pdf.mergeFiles(formsList, outputFilePath,true);
    }
    public void comparePDFS(LinkedHashMap<String, String> inputOutputrow)
    {
    	inputOutputrow.get("");
    }
    
    public static void main(String args[]) throws IOException, DocumentException
    {
    	LinkedHashMap<Integer,SheduleOfFormsList> formsList = new LinkedHashMap<Integer,SheduleOfFormsList> ();
    	formsList.put(1, new SheduleOfFormsList("number","editin","Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf","","static"));
    	formsList.put(2, new SheduleOfFormsList("number","editin","Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance.pdf","","dynamic"));
    	StarrGLPDFChecker pdfcheck = new StarrGLPDFChecker();
    	pdfcheck.mergeForms(formsList, "Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\mergedform.pdf");
    			
    }
    
}
