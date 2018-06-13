package com.solartis.common;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import com.solartis.test.exception.DatabaseException;

public class DBColoumnVerify extends DatabaseOperation 
{
	protected String CondColName;
	public DBColoumnVerify()
	{
		
	}
	public DBColoumnVerify(String CondColName)
	{
		this.CondColName = CondColName;
	}
	public boolean DbCol(LinkedHashMap<String, String> DataTable) throws DatabaseException
	{
			try 
			{
				return ConditionReading(this.rs.getString(CondColName),DataTable);
			}
			catch (SQLException e) 
			{
				throw new DatabaseException("ERROR IN DB CONDITION COLOUMN", e);
			}
	}

	public boolean ConditionReading(String condition,LinkedHashMap<String, String> DataTable) throws DatabaseException
	{
		boolean ConditionReading=false;
		
			if(condition.equals("")||condition.isEmpty()||condition==null)
			{
				ConditionReading=true;
				return ConditionReading;
			}
			else
			{
				String[] splits=condition.split(";");
				int length=splits.length;
				
				for(int i=0;i<length;i++)
					{
						ConditionReading=false;
						String[] CondValue = new String[10];
						String operator = null;
					
							if(splits[i].contains(">="))
							{
								CondValue=splits[i].split(">=");
								operator = ">=";
							}
							else if(splits[i].contains("<="))
							{
								CondValue=splits[i].split("<=");
								operator = "<=";
							}
							else if(splits[i].contains("="))
							{
								CondValue=splits[i].split("=");
								operator = "=";
							}
							else if(splits[i].contains("<>"))
							{
								CondValue=splits[i].split("<>");
								operator = "<>";
							}
							else if(splits[i].contains(">"))
							{
								CondValue=splits[i].split(">");
								operator = ">";
							}
							else if(splits[i].contains("<"))
							{
								CondValue=splits[i].split("<");
								operator = "<";
							}
							
							
						String cond=CondValue[0];
						String value=CondValue[1];
						String[] individualValue = value.split("\\|");
			
							for(int j=0;j<individualValue.length;j++)
							{
								switch(operator)
								{
								case "=": if((DataTable.get(cond).equals(individualValue[j])))
										   {
												
												ConditionReading=true;
											}
											break;
								case "<>": if((DataTable.get(cond).equals(individualValue[j])))
											{
												ConditionReading=false;
						 						return ConditionReading;
											}
											else
											{
												ConditionReading=true;
											}
											break;	
								case ">": if(Integer.parseInt(DataTable.get(cond)) > Integer.parseInt(individualValue[j]))
											{
												ConditionReading=true;
						 						
											}
											else
											{
												ConditionReading=false;
												return ConditionReading;
											}
											break;	
								case "<": if(Integer.parseInt(DataTable.get(cond)) < Integer.parseInt(individualValue[j]))
											{
												ConditionReading=true;
						 						
											}
											else
											{
												ConditionReading=false;
												return ConditionReading;
											}
											break;
								case ">=": if(Integer.parseInt(DataTable.get(cond)) >= Integer.parseInt(individualValue[j]))
											{
												ConditionReading=true;
						 						
											}
											else
											{
												ConditionReading=false;
												return ConditionReading;
											}
											break;
								case "<=": if(Integer.parseInt(DataTable.get(cond)) <= Integer.parseInt(individualValue[j]))
											{
												ConditionReading=true;
											}
											else
											{
												ConditionReading=false;
												return ConditionReading;
											}
											break;
								}
								
							}
							
						if(!ConditionReading)
						{
							return ConditionReading;
						}
					}	
			}
		
	return ConditionReading;
	}
	
}