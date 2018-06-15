package com.solartis.test.PDF_Checker;

public class SheduleOfFormsList 
{
	private String formNumber;
	private String formEdition;
	private String formDescription;
	private String SheduleofFormsFlag;
	private String formNature;
	


	public SheduleOfFormsList(String formNumber,String formEdition,String formDescription, String SheduleofFormsFlag, String formNature)
	{
		this.formNumber=formNumber;
		this.formEdition=formEdition;
		this.formDescription=formDescription;
		this.SheduleofFormsFlag=SheduleofFormsFlag;
		this.formNature=formNature;
	}
		
	public String getSheduleofFormsFlag() {
		return SheduleofFormsFlag;
	}

	public void setSheduleofFormsFlag(String sheduleofFormsFlag) {
		SheduleofFormsFlag = sheduleofFormsFlag;
	}
	
	public String getFormNumber() {
		return formNumber;
	}
	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}
	public String getFormEdition() {
		return formEdition;
	}
	public void setFormEdition(String formEdition) {
		this.formEdition = formEdition;
	}
	public String getFormDescription() {
		return formDescription;
	}
	public void setFormDescription(String formDescription) {
		this.formDescription = formDescription;
	}
	public String getFormNature() {
		return formNature;
	}

	public void setFormNature(String formNature) {
		this.formNature = formNature;
	}
	
}
