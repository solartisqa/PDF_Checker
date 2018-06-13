package com.solartis.test.PDF_Checker;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
			// Here we define the location:
		    Rectangle linkLocation = new Rectangle(llx, 695, 560, 741);
		    
		    // here we add the actual content at this location:
		    ColumnText ct = new ColumnText(pdfStampers.getOverContent(pageNumber));
		    Paragraph para = new Paragraph();
		    Font font = new Font();
		    font.setSize(11);
		    font.setFamily("TIMES_NEW_ROMAN");
		    para.add(Text);
		    para.setFont(font);//FontFamily.TIMES_ROMAN);
		    ct.setSimpleColumn(linkLocation);
		    ct.addElement(para);
		   
			ct.go();		
		
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	
	/*private void mergeForms(LinkedHashMap<String,PdfDocument> formList,String finalDocPath)
	{
		LinkedHashMap<String,PdfDocument> formlistt=
		try 
		{
			PdfDocument doc = new PdfDocument(new PdfWriter(finalDocPath));
			doc.initializeOutlines();
			Document finalDoc = new Document(doc);
			int n;
			for(Map.Entry<String,PdfDocument> entry : formList.entrySet())
			{
				n = entry.getValue().getNumberOfPages();
				for(int i=1;i<=n;i++)
				{
					entry.getValue().copyPagesTo(i,i,doc);
				}
			}
			
			for(PdfDocument srcDoc : formList.values())
			{
				srcDoc.close();
			}
			finalDoc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}*/
	
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
            reader[i-1] = new PdfReader(files.get(i).getFormDescription());
            copy.addDocument(reader[i-1]);
            copy.freeReader(reader[i-1]);
            reader[i-1].close();
        }
        document.close();
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
	
	public static void main(String[] args) {
		EditExisting1 trial1 = new EditExisting1();
		trial1.openPDF("Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance.pdf", "Q:\\Manual Testing\\Starr\\Starr-GL\\FormsTemplate\\All Forms\\SIIL C 001 (0517) Starr Certificate of Commercial Liability Insurance_edited.pdf");
		trial1.feedInData(1, 300, 600, 400, 600, "My trial text");
		trial1.feedInData(2, 100, 200, 300, 300, "My trial text2");
		trial1.closePDF();
	}
}
