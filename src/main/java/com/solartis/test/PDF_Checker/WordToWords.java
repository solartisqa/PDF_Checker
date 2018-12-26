package com.solartis.test.PDF_Checker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.poi.xwpf.converter.pdf.internal.Converter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

import com.convertapi.Config;
import com.convertapi.ConvertApi;
import com.convertapi.Param;

public class WordToWords extends Converter {

	// private static final Logger log = LoggerFactory.make();
	
	public void feedinData(String wordTemplatePath, String outputFile, LinkedHashMap<String, String> dataIn) throws FileNotFoundException, IOException, XmlException  {
		// log.info("Merging data from " + wordTemplate + " and " + dataFile + " into "
		// + outputFile);

		File wordTemplate = new File(wordTemplatePath);

		if (!wordTemplate.exists() || !wordTemplate.isFile()) {
			throw new IllegalArgumentException("Could not read Microsoft Word template " + wordTemplate);
		}

		
		// now open the word file and apply the changes
		try (InputStream is = new FileInputStream(wordTemplate)) {
			XWPFDocument doc = new XWPFDocument(is);
			// apply the lines and concatenate the results into the document
			applyLines(dataIn, doc);

			// log.info("Writing overall result to " + outputFile);
			try (OutputStream out = new FileOutputStream(outputFile + ".docx")) {
				doc.write(out);
			}

			convert(outputFile + ".docx", "F:\\ProcessWord\\Expected_pdfs\\");
		}
		Files.deleteIfExists(Paths.get(outputFile + ".docx"));
	}

	private void applyLines(LinkedHashMap<String, String> dataIn, XWPFDocument doc) throws XmlException, IOException {
		// small hack to not having to rework the commandline parsing just now

		CTBody body = doc.getDocument().getBody();

		// read the current full Body text
		String srcString = body.xmlText();
		System.out.println(srcString);
		// apply the replacements line-by-line
		String replaced = srcString;

		for (Entry<String, String> entry : dataIn.entrySet()) {

			replaced = replaced.replace("${" + entry.getKey() + "}", entry.getValue());
		}

		// check for missed replacements or formatting which interferes
		if (replaced.contains("${")) {
			// log.warning("Still found template-marker after doing replacement: "
			// + StringUtils.abbreviate(StringUtils.substring(replaced,
			// replaced.indexOf("${")), 200));
		}

		appendBody(body, replaced, true);
	}

	private static void appendBody(CTBody src, String append, boolean first) throws XmlException {
		XmlOptions optionsOuter = new XmlOptions();
		optionsOuter.setSaveOuter();
		String srcString = src.xmlText();
		String prefix = srcString.substring(0, srcString.indexOf(">") + 1);

		final String mainPart;
		// exclude template itself in first appending
		if (first) {
			mainPart = "";
		} else {
			mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
		}

		String suffix = srcString.substring(srcString.lastIndexOf("<"));
		String addPart = append.substring(append.indexOf(">") + 1, append.lastIndexOf("<"));
		CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + suffix);
		src.set(makeBody);
	}

	public void convert(String docPath, String pdfPath) {
		try {
			Config.setDefaultSecret("8OjLNOukUkAe0klL");
			ConvertApi.convert("docx", "pdf", new Param("File", Paths.get(docPath))).get()
					.saveFilesSync(Paths.get(pdfPath));
		} catch (IOException | InterruptedException | ExecutionException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		// LoggerFactory.initLogging();

		/*
		 * if (args.length != 3) { throw new
		 * IllegalArgumentException("Usage: MailMerge <word-template> <excel/csv-template> <output-file>"
		 * ); }
		 */

		String wordTemplate = "F:\\ProcessWord\\CA2251OW.docx";
		// File excelFile = new File(args[1]);
		String outputFile = "F:\\ProcessWord\\edited_docs\\CA2251OW_edited";

		/*
		 * if (!excelFile.exists() || !excelFile.isFile()) { throw new
		 * IllegalArgumentException("Could not read data file " + excelFile); }
		 */
		LinkedHashMap<String, String> dataIn = new LinkedHashMap<String, String>();
				dataIn.put("col1", "policyNumber");
				dataIn.put("col2", "policypremium");
		new WordToWords().feedinData(wordTemplate, outputFile, dataIn);
	}
}
