package com.solartis.test.PDF_Checker;

import com.itextpdf.kernel.pdf.canvas.parser.listener.ILocationExtractionStrategy;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
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
import java.io.ByteArrayOutputStream;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import org.apache.commons.io.FileUtils;

public class PDFUtilities {
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
	
	
	
    public void mergeFiles(LinkedHashMap<Integer,SheduleOfFormsList> files, String result, boolean smart,String TempPath) throws IOException, DocumentException 
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
            reader[i-1] = new PdfReader(TempPath+files.get(i).getFormFileName()+".pdf");
            copy.addDocument(reader[i-1]);
            copy.freeReader(reader[i-1]);
            reader[i-1].close();
        }
        document.close();
        deleteFileFromDirectory(TempPath); 
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
	public String comparePDFVisually(String PDF1path,String PDF2path, String Resultpath, String TestCaseName) throws IOException
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
	    	File dir = new File(Resultpath+"\\"+TestCaseName+"\\");
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
	          	pdfUtil.setImageDestinationPath(Resultpath+"\\"+TestCaseName+"\\");
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
	    	System.out.println("Expected PDF Page Count ------------->"+Autual_PageCount);
	    	System.out.println("Actual PDF Page Count ------------->"+Expected_PageCount);
            result = "PageCountError";
	    }
        return result;
	}
	
	public void urltopdf(String URL,String path) throws IOException
	{
		System.setProperty("jsse.enableSNIExtension", "false");	
		URL website = new URL(URL);
		Path targetPath = new File(path).toPath();
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
	
	public void checkPageSize(String filePath, double width, double height) throws IOException
	{
		PdfReader reader = new PdfReader(filePath);				
		for(int i=1;i<=reader.getNumberOfPages();i++)
		{
			float Heightt = reader.getPageSizeWithRotation(i).getHeight()/72;
			float Widthh = reader.getPageSizeWithRotation(i).getWidth()/72;
			if(!(width==Widthh)||!(Heightt==height))
			{
				System.out.println("PageSize does not match with the given standards at PageNumber "+i);
			}
		}
		reader.close();
	}
	
	 public void removeBlankPdfPages(String source, String destination, int BLANK_THRESHOLD)  throws IOException, DocumentException
	 {
		 PdfReader r = null;
		 RandomAccessSourceFactory rasf = null;
		 RandomAccessFileOrArray raf = null;
		 com.itextpdf.text.Document document = null;
		 //PdfCopy writer = null;

		 try 
		 {
			 r = new PdfReader(source);
		     // deprecated
		     //    RandomAccessFileOrArray raf
		     //           = new RandomAccessFileOrArray(pdfSourceFile);
		     // itext 5.4.1
		     rasf = new RandomAccessSourceFactory();
		     raf = new RandomAccessFileOrArray(rasf.createBestSource(source));
		     document = new com.itextpdf.text.Document(r.getPageSizeWithRotation(1));
		    // writer = new PdfCopy(document, new FileOutputStream(destination));
		     document.open();

		     for (int i=1; i<=r.getNumberOfPages(); i++) 
		     {
		    	 // first check, examine the resource dictionary for /Font or
		         // /XObject keys.  If either are present -> not blank.
		         PdfDictionary pageDict = r.getPageN(i);
		         PdfDictionary resDict = (PdfDictionary) pageDict.get( PdfName.RESOURCES );
		         boolean noFontsOrImages = true;
		         if (resDict != null) 
		         {
		        	 noFontsOrImages = resDict.get( PdfName.FONT ) == null && resDict.get( PdfName.XOBJECT ) == null;
		         }
		         System.out.println(i + " noFontsOrImages " + noFontsOrImages);

		         if (!noFontsOrImages) 
		         {
			         byte bContent [] = r.getPageContent(i,raf);
			         ByteArrayOutputStream bs = new ByteArrayOutputStream();
			         bs.write(bContent);
			         System.out.println(i + bs.size() + " > BLANK_THRESHOLD " +  (bs.size() > BLANK_THRESHOLD));
			         if (bs.size() > BLANK_THRESHOLD) 
			         {
			        	 //page = writer.getImportedPage(r, i);
			             //writer.addPage(page);
			        	 System.out.println("is blank");
			         }
		         }
		     }
		 }
		 finally 
		 {
			// if (writer != null) writer.close();
			 if (document != null) document.close();
		     
		     if (raf != null) raf.close();
		     if (r != null) r.close();
	     }
    }
		 
	public void manipulateSheduleofFormsPdf(String src, String dest) throws DocumentException, IOException 
	{
	    PdfReader reader = new PdfReader(src);
	    Rectangle pagesize = reader.getPageSize(1);
	    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
	   
	    PdfPTable table = new PdfPTable(2);
	    table.getDefaultCell().setBorder(0);
	//    table.addCell("#");
	  //  table.addCell("description");
	    
	    //table.setHeaderRows(1);
	    table.setWidths(new int[]{ 6, 13 });
	    for (int i = 1; i <= 150; i++) 
	    {
	        table.addCell(String.valueOf(i));
	        table.addCell("test " + i);
	    }
	    ColumnText column = new ColumnText(stamper.getOverContent(1));
	    Rectangle rectPage1 = new Rectangle(36, 36, 559, 590);
	    column.setSimpleColumn(rectPage1);
	    column.addElement(table);
	    int pagecount = 1;
	    Rectangle rectPage2 = new Rectangle(36, 36, 559, 806);
	    int status = column.go();
	    while (ColumnText.hasMoreText(status)) 
	    {
	        status = triggerNewPage(stamper, pagesize, column, rectPage2, ++pagecount);
	    }
	    stamper.setFormFlattening(true);
	    stamper.close();
	    reader.close();
	}
	 
	private int triggerNewPage(PdfStamper stamper, Rectangle pagesize, ColumnText column, Rectangle rect, int pagecount) throws DocumentException 
	{
	    stamper.insertPage(pagecount, pagesize);
	    PdfContentByte canvas = stamper.getOverContent(pagecount);
	    column.setCanvas(canvas);
	    column.setSimpleColumn(rect);
	    return column.go();
	}
	
	/*public String getFontName()
	{
		string curFont = renderInfo.GetFont().PostscriptFontName;
		return curFont;
	}*/

	public String extractSpecificText(int pageNumber,float llx, float lly,float urx, float ury) throws IOException
	{
		//Rectangle mediabox = pdfReaderr.GetPageSize(pagenum);
		Rectangle rectPage1 = new Rectangle(llx, lly, urx, ury);
		RenderFilter[] filter = {new RegionTextRenderFilter(rectPage1)};
		FilteredTextRenderListener strategy;
		StringBuilder sb = new StringBuilder();
		//for (int i = 1; i <= pdfReaderr.getNumberOfPages(); i++) {
		    strategy =  new FilteredTextRenderListener( new LocationTextExtractionStrategy(), filter);
		    sb.append(PdfTextExtractor.getTextFromPage(pdfReaderr, pageNumber, (TextExtractionStrategy) strategy));//GetTextFromPage(pdfReaderr, i, strategy));
		//}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException, DocumentException {
		PDFUtilities trial1 = new PDFUtilities();
		trial1.openPDF("Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL DS 01 FL (0117) Common Policy Dec Page - Copy.pdf", "Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf");
		//trial1.feedInData(1, 300, 600, 400, 600, "My trial text");
		System.out.println(trial1.extractSpecificText(1, 300, 600, 400, 700));
		//trial1.feedInData(2, 100, 200, 300, 300, "My trial text2");
		trial1.closePDF();
		//trial1.comparePDFVisually("Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance.pdf",
			//	"Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf",
				//"Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\resutlt\\resutl\\");
		//trial1.checkPageSize("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates\\SIIL-0001 (0115) Disclosure Pursuant to TRIA.pdf", 8.5, 11);
		//trial1.removeBlankPdfPages("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates\\SIIL DS 04 (0117) Schedule of Named Insured.pdf", "E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates\\SIIL DS 04 (0117) Schedule of Named Insured_temp.pdf", 5000);
		//trial1.manipulateSheduleofFormsPdf("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates\\SIIL DS 02 (0117) Schedule of Forms and Endorsements.pdf", "E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\SampleTemplates\\SIIL DS 02 (0117) Schedule of Forms and Endorsementstest.pdf");
	}
}
