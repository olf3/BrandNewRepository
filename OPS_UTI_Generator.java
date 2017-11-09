package com.customer.OPS_USI_Generator;

import com.olf.openjvs.*;
import com.olf.openjvs.enums.*;

/*********************************************************************************************************
<div style="background-color:#EDF9FF; font-family:Arial,Helvetica,sans-serif; font-size:smaller;">
<h2>
<i><span style="color: #2188ca; font-weight: bold; font-size: 20pt;"><span style="font-family: 'Times New Roman',Georgia,Serif;font-size: 36pt; font-weight: 900;">O</span>PEN</span><span style="color: #bbbcbe; font-weight: bold; font-size: 20pt;"><span style="font-size: 26pt;">L</span>INK</span></i><br/>
OPS_USI_Generator.java
</h2>

<p>This plugin was developed for Keycorp.</p>

<p><strong>Description:</strong> The plugin description will be here - use as many lines as you like.</p>

<p><strong>Plugin Version:</strong> V1.0</p>

<p><strong>Mode of Execution:</strong> Post processing plugin </p>

<p><strong>Original Author:</strong> Gang Xu</p>

<table border="1" style="border-style:outset; border-width:2px; border-color:#888888; background-color:#FCFEFF;">
	<tr>
		<th colspan="3">Revisions</th>
	</tr>
	<tr>
		<th align="left">Date</th>
		<th align="left">Name</th>
		<th align="left">Version</th>
		<th align="left" width="450">Description</th>
	</tr>
	<tr>
		<td>Dec 20, 2013</td>
		<td>Gang Xu</td>
		<td>1.0</td>
		<td width="450">Initial Development.</td>
	</tr>	
</table>

<p><strong>Plugin Type:</strong> (Main, Param, UDW, etc..)</p>

<p><strong>Includes:</strong></p>
<ul>
	<li><i>ClassName1</i></li>
	<li><i>ClassName2</i></li>
</ul>


<table border="1" style="border-style:outset; border-width:2px; border-color:#888888; background-color:#FCFEFF;">
	<tr>
		<th colspan="2">Associated Plugins</th>
	</tr>
	<tr>
		<th align="left">Type</th>
		<th align="left">Name</th>
	</tr>
	<tr>
		<td><strong>Main Plugin</strong></td>
		<td>Main Plugin here...</td>
	</tr>
	<tr>
		<td><strong>Parameter Plugin</strong></td>
		<td>Parameter Plugin here...</td>
	</tr>
	<tr>
		<td><strong>Another Plugin</strong></td>
		<td>Another Plugin here...</td>
	</tr>
</table>

<p><strong>Additional Dependencies (Argument tables, Databases...):</strong> 

<table border="1" style="border-style:outset; border-width:2px; border-color:#888888; background-color:#FCFEFF;">
	<tr>
		<th colspan="2">Databases fields</th>
	</tr>
	<tr>
		<th align="left">Type</th>
		<th align="left">Name</th>
	</tr>
	<tr>
		<td><strong>......</strong></td>
		<td>.........</td>
	</tr>
</table>


</p> 

<p><strong>Returns/Output:</strong> Returns here.</p>
</div>
 ***************************************************************************************************************/

public class OPS_USI_Generator implements IScript
{
	
	public void execute(IContainerContext context) throws OException
	{
		Table argt = context.getArgumentsTable();
		Table tDealInfo = argt.getTable("Deal Info", 1);

		int iToolsetSwap = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "Swap"); 
		int iToolsetSwaption = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "Swaption");
		int iToolsetOption = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "Option");
		int iToolsetDigOpt = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "DigOpt");
		int iToolsetFRA = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "FRA");
		int iToolsetCreditDeriv = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "CreditDeriv");
		int iToolsetComSwap = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "ComSwap");
		int iToolsetComOpt = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "ComOpt");
		int iToolsetMetalSwap = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "MetalSwap");
		int iToolsetFX = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "FX");
		int iToolsetFXOption = Ref.getValue(SHM_USR_TABLES_ENUM.TOOLSETS_TABLE, "FX-Option");

		int i = 0;
		int iNumRows = 0;
		int iRet = 0;
		int iTranNum = 0;
		int iUsiRow = 0;
		int iToolset = 0;
		int iInternalLe = 0;
		int iUsiLen = 0;

		String sUsi = "";
		String sUsiValue = "";
		String sUsiNameSpace = "";
		String sInternalLe = "";
		String sStr1 = "";
		String sStr11 = "";
		String sStr12 = "";
		String sStr2 = "";
		String sStr3 = "";
		String sStr4 = "";
		String sStr5 = "";
		String sStr6 = "";
		String sSql = "";
		String sNameSpace = "REG Reporting USI Namespace";
		String sPrefix = "REG Reporting USI Prefix";
		String sLogFileName = "OPS_USI_Generator";

		Table tTranInfo = Util.NULL_TABLE;
		Table tPartyInfo = Util.NULL_TABLE;
		Table tSysDate = Util.NULL_TABLE;
		Transaction trnPtr = Util.NULL_TRAN;

		Util.errorInitScriptErrorLog(sLogFileName);
		sLogFileName = sLogFileName + ".log";
		OConsole.print("\nStart: OPS_USI_Generator");
		Util.errorLogMessage(sLogFileName, "INFO", "Start: OPS_USI_Generator"); 

		iNumRows = tDealInfo.getNumRows();
		for(i = 1; i <= iNumRows; i++)
		{
			iTranNum     = tDealInfo.getInt( "tran_num",           i);
			trnPtr = Transaction.retrieve(iTranNum);

			tTranInfo = trnPtr.getTranInfo();
 
			OConsole.print("\nProcess transaction " + iTranNum);
			Util.errorLogMessage(sLogFileName, "INFO", "Process transaction " + iTranNum); 
			
			iUsiRow = tTranInfo.unsortedFindString("Type", "USI", SEARCH_CASE_ENUM.CASE_INSENSITIVE);
			if (iUsiRow > 0)
			{ 
				//tTranInfo.viewTable();
				sUsi = tTranInfo.getString("Value", iUsiRow);
				if(Str.isEmpty(sUsi) == 1) //empty
				{
					iInternalLe = trnPtr.getFieldInt(TRANF_FIELD.TRANF_INTERNAL_LENTITY.toInt(), 0);
					tPartyInfo = Table.tableNew();
					sSql = "select p.party_id, p.value from party_info p, party_info_types pt where p.party_id = " + iInternalLe + 
					" and p.type_id = pt.type_id and pt.type_name = '" + sNameSpace + "'";

					DBaseTable.execISql(tPartyInfo, sSql);
					if(tPartyInfo.getNumRows() > 0)
						sStr11 = tPartyInfo.getString(2, 1);
					tPartyInfo.destroy();

					tPartyInfo = Table.tableNew();
					sSql = "select p.party_id, p.value from party_info p, party_info_types pt where p.party_id = " + iInternalLe + 
					" and p.type_id = pt.type_id and pt.type_name = '" + sPrefix + "'";

					DBaseTable.execISql(tPartyInfo, sSql);
					if(tPartyInfo.getNumRows() > 0)
						sStr12 = tPartyInfo.getString(2, 1);
					tPartyInfo.destroy();

					sStr1 = sStr11 + sStr12;

					sStr2 = trnPtr.getField(TRANF_FIELD.TRANF_DEAL_TRACKING_NUM.toInt(), 0);

					iToolset = trnPtr.getFieldInt(TRANF_FIELD.TRANF_TOOLSET_ID.toInt(), 0);
					if (iToolset == iToolsetSwap || iToolset == iToolsetSwaption || iToolset == iToolsetFRA || 
							iToolset == iToolsetOption || iToolset == iToolsetDigOpt)
						sStr3 = "RA";
					else if (iToolset == iToolsetCreditDeriv)
						sStr3 = "CR";
					else if (iToolset == iToolsetComSwap || iToolset == iToolsetComOpt || iToolset == iToolsetMetalSwap)
						sStr3 = "CO";
					else if (iToolset == iToolsetFX || iToolset == iToolsetFXOption)
						sStr3 = "FX";
					else
						sStr3 = "OT";

					tSysDate = Table.tableNew();
					DBaseTable.execISql(tSysDate, "select last_update from ab_tran where tran_num = " + iTranNum);
					sStr4 = "" + tSysDate.getDate(1, 1) + "";
					sStr5 = "" + tSysDate.getTime(1, 1) + "";
					tSysDate.destroy();
					
					//JH added:
					sStr1 = "OLF";
					
					sUsiValue = sStr1 + sStr2 + sStr3 + sStr4 + sStr5;

					iUsiLen = Str.len(sUsiValue);
					if(iUsiLen > 32)
						sUsiValue = Str.substr(sUsiValue, 0, 32);
					else if(iUsiLen < 32)
						sUsiValue = trailingCharPadding (sUsiValue, 32, "0");

					tTranInfo.setString("Value", iUsiRow, sUsiValue);
					iRet = trnPtr.saveTranInfo();
					if(iRet == 1)
					{
						OConsole.print("\nUpdated tran info USI for transaction " + iTranNum);
						Util.errorLogMessage(sLogFileName, "INFO", "Updated tran info USI for transaction " + iTranNum);
					}
					else
					{
						OConsole.print("\nFailed to update tran info USI for transaction " + iTranNum);
						Util.errorLogMessage(sLogFileName, "INFO", "Failed to update tran info USI for transaction " + iTranNum);
					}
				}
				else //not empty
				{
					OConsole.print("\nTran info USI is not blank");
					Util.errorLogMessage(sLogFileName, "ERROR", "Tran info USI is not blank");
				}
			}
			else
			{
				OConsole.print("\nTran info USI does not exist");
				Util.errorLogMessage(sLogFileName, "ERROR", "Tran info field USI does not exist"); 
			}
			trnPtr.destroy();
		}
		OConsole.print("\nEnd: OPS_USI_Generator");
		Util.errorLogMessage(sLogFileName, "INFO", "End: OPS_USI_Generator"); 
	}
	
	private String trailingCharPadding (String sValue, int iDisplayLength, String sPad) throws OException
	{
		String sPaddedValue = "";
		int iNumPad = 0;
		int iLen = 0;
		int i = 0;
		
		iLen = Str.len(sValue);
		iNumPad = iDisplayLength - iLen;
		
		sPaddedValue = sValue;
		
		for(i = 1; i <= iNumPad; i++)
			sPaddedValue = sPaddedValue + sPad;
		
		return sPaddedValue;
	}

}
