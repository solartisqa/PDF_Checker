package com.solartis.test.PDF_Checker;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.solartis.test.exception.PDFException;
import com.testautomationguru.utility.CompareMode;
import com.testautomationguru.utility.PDFUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class EditExisting1 {
	private PdfReader pdfReaderr;
	public PdfStamper pdfStampers;
	public PdfReader pdfReaders;
	public PdfReader getPdfReaderr() {
		return pdfReaderr;
	}

	public void setPdfReaderr(PdfReader pdfReaderr) {
		this.pdfReaderr = pdfReaderr;
	}
	
	private PdfReader openPDFReader(String SourcePath)
	{
		PdfReader pdfReader = null;
		try {
			pdfReader = new PdfReader(SourcePath);
			this.setPdfReaderr(pdfReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pdfReader;
	}
	
	private void closePDFReader(PdfReader pdfReader)
	{
		pdfReader.close();
	}
	
	private PdfStamper openPDFStamper(PdfReader pdfReader, String DestinationPath)
	{
		PdfStamper pdfStamper = null;
		try 
		{		
			pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(DestinationPath));
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return pdfStamper;
	}
	
	private void closePDFStamper(PdfStamper pdfStamper)
	{
		try {
			pdfStamper.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openPDF(String SourcePath,String DestinationPath)
	{
		pdfReaders = this.openPDFReader(SourcePath);
		pdfStampers = this.openPDFStamper(pdfReaders, DestinationPath);
	}
	
	public void closePDF()
	{
		this.closePDFStamper(pdfStampers);
		this.closePDFReader(pdfReaders);	
	}
	
	public void feedInData(int pageNumber,float llx, float lly,float urx, float ury,String Text)
	{
		 try 
		 {
			 System.out.println(llx+"=----"+lly+"-----"+urx+"------"+ury+"------"+Text);
			// Here we define the location:
		    Rectangle linkLocation = new Rectangle(llx, lly, urx, ury);
		    
		    // here we add the actual content at this location:
		    ColumnText ct = new ColumnText(pdfStampers.getOverContent(pageNumber));
		    Paragraph para = new Paragraph();
		    
		    BaseFont arial = BaseFont.createFont("E:\\RestFullAPIDeliverable\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		    Font font = new Font(arial,11f,Font.NORMAL,BaseColor.BLACK);
		    font.setSize(11);
		    para.add(Text);
		    para.setFont(font);//FontFamily.TIMES_ROMAN);
		    ct.setSimpleColumn(linkLocation);
		    ct.addElement(para);		   
			ct.go();		
		
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
    public void mergeFiles(LinkedHashMap<Integer,SheduleOfFormsList> files, String result, boolean smart) throws IOException, DocumentException 
    {
    	com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        PdfCopy copy;
        if (smart)
        {
            copy = new PdfSmartCopy(document, new FileOutputStream(result));
        }
        else
        {
            copy = new PdfCopy(document, new FileOutputStream(result));
        }
        document.open();
        PdfReader[] reader = new PdfReader[files.size()];
        for(int i=1;i<=files.size();i++) 
        {
            reader[i-1] = new PdfReader("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\temp\\"+files.get(i).getFormDescription()+".pdf");
            copy.addDocument(reader[i-1]);
            copy.freeReader(reader[i-1]);
            reader[i-1].close();
        }
        document.close();
        deleteFileFromDirectory("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\temp\\"); 
    }
	
	public void SheduleOfForms(LinkedHashMap<Integer,SheduleOfFormsList> formsList,String SheduleFormPath) 
	{
		float y = 770;
		PdfDocument doc;
		try {
			doc = new PdfDocument(new PdfWriter(SheduleFormPath));		
			//doc.initializeOutlines();
			Document finalDoc = new Document(doc);
			for(Map.Entry<Integer, SheduleOfFormsList> entry : formsList.entrySet())
			{
				com.itextpdf.layout.element.Paragraph p = new com.itextpdf.layout.element.Paragraph();
				p.add(entry.getValue().getFormNumber());
				p.add(new Tab());
				p.add(entry.getValue().getFormEdition());
				p.add(new Tab());
				p.add(entry.getValue().getFormDescription());
				p.setAction(PdfAction.createGoTo("p10"));
				finalDoc.add(p.setFixedPosition(36, y, 595-72));
				y-=20;
			}
			finalDoc.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
		
	public  static void deleteFileFromDirectory(String DirName)
	{
		File directory = new File(DirName);

		File[] files = directory.listFiles();

		for (File file : files)

		{
	 
			if (!file.delete())
			{ 
				System.out.println("Failed to delete "+file);
			}
		} 
	}
	public static boolean successful;
	public String comparePDFVisually(String PDF1path,String PDF2path, String Resultpath) throws IOException
	{
		java.util.logging.Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("com.testautomationguru").setLevel(java.util.logging.Level.OFF);
		String result = null;
		
		PDFUtil pdfUtil = new PDFUtil();
	    pdfUtil.setCompareMode(CompareMode.VISUAL_MODE);
	    pdfUtil.highlightPdfDifference(true);
	    
	    int Expected_PageCount=pdfUtil.getPageCount(PDF1path);    
	    int Autual_PageCount=pdfUtil.getPageCount(PDF2path); 
	    
	    if(Expected_PageCount==Autual_PageCount)
	    {
	    	File dir = new File(Resultpath);
	            if (! dir.exists())
	            {
	                 successful = dir.mkdir();
	            }
	            else
	            {
	                System.out.println("Clearing all Past Datas from "+dir);
	                FileUtils.cleanDirectory(dir);
	                successful = true;
	            }
	           
	            if (successful)
	            {
	            	pdfUtil.setImageDestinationPath(Resultpath);
	    		    boolean isEqual=pdfUtil.compare(PDF1path, PDF2path, 1, Expected_PageCount, true, true);
	    		    if(!isEqual)
	    	        {
	    	            System.out.println("Difference found in PDFs");
	    	            result = "Fail";
	    	        }
	    	        else
	    	        {
	    	            System.out.println("Difference not found in PDFs");
	    	            result = "Pass";
	    	        }
	      }
	      else
	      {
	         System.out.println("Directory is not present");
	      } 
	        
	    }
	    else
	    {
	    	System.out.println("Count of PDF Pages are different");
            result = "PageCountError";
	    }
        return result;
	}
	
	public void urltopdf(String URL,String path) throws IOException
	{
		System.setProperty("jsse.enableSNIExtension", "false");	
		URL website = new URL(URL);
		Path targetPath = new File(path+".pdf").toPath();
		InputStream in = website.openStream();		
		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);		
	}
	
	@SuppressWarnings("resource")
	public void Copy(String SourcePath, String TargetPath) throws PDFException 
	{
		FileChannel source = null;
		FileChannel destination = null;

		try 
		{
			source = new FileInputStream(SourcePath).getChannel();

			destination = new FileOutputStream(TargetPath).getChannel();

			if (destination != null && source != null) 
			{
				destination.transferFrom(source, 0, source.size());
			}

		}
		
		catch (FileNotFoundException e) 
		{
			throw new PDFException("ERROR OCCURS WHILE COPYING THE WORKBOOK -- FILENOTFOUND", e);
		} 
		catch (IOException e)
		{
			throw new PDFException("ERROR OCCURS WHILE COPYING THE WORKBOOK -- I/O OPERATION FAILED", e);
		
		}
		finally 
		{
			try 
			{
				if (source != null) 
				{					
					source.close();					
				}
				if (destination != null) 
				{					
					destination.close();					
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}	
	}
	
	public static void main(String[] args) throws IOException {
		EditExisting1 trial1 = new EditExisting1();
/*		trial1.openPDF("Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance.pdf", "Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf");
		trial1.feedInData(1, 300, 600, 400, 600, "My trial text");
		trial1.feedInData(2, 100, 200, 300, 300, "My trial text2");
		trial1.closePDF();*/
		trial1.comparePDFVisually("Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance.pdf",
				"Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf",
				"Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\resutlt\\resutl\\");
	}
}
