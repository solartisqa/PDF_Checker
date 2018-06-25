package com.solartis.test.PDF_Checker;

import java.io.IOException;

import de.redsix.pdfcompare.CompareResultWithMemoryOverflow;
import de.redsix.pdfcompare.PdfComparator;

public class CompareCheck 
{

	public static void main(String args[]) throws IOException
	{
		new PdfComparator("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\Actual\\SepLCV_AL_01.pdf", 
				"E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\Expected\\SepLCV_AL_01.pdf").compare().writeTo("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\PDFs\\PolicyPDF\\Result\\diffOutput.pdf");
	}
}
