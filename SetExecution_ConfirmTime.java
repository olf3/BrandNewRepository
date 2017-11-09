package com.op_service;

import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.IContainerContext;
import com.olf.openjvs.IScript;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.ODateTime;
import com.olf.openjvs.OException;
import com.olf.openjvs.Ref;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TRANF_FIELD;

public class SetExecuionTimeInfoField implements IScript 
{
	/* (non-Javadoc)
	 * @see com.olf.openjvs.IScript#execute(com.olf.openjvs.IContainerContext)
	 */
	@Override
	public void execute(IContainerContext arg0) throws OException
	{
		Table argt = arg0.getArgumentsTable();
		String sScriptName = this.getClass().getSimpleName();
		Transaction tran = Util.NULL_TRAN;
		int iTranNum=0;

		OConsole.oprint("*** Start: " + sScriptName + "***\n");
	
		//* Get Pointers to all tables (even though some may not be used in this script)
		Table tAllDeals = argt.getTable( "Deal Info", 1);
		int iAllDealsLoop;

		//* Loop one time each row in all_results.
		for (iAllDealsLoop = 1; iAllDealsLoop <= tAllDeals.getNumRows(); iAllDealsLoop++)
		{     
			tran = Transaction.retrieve(tAllDeals.getInt(1, iAllDealsLoop));
			if ( Transaction.isNull(tran)  == 0)
			{
				iTranNum = tran.getTranNum();
				OConsole.oprint("***TranNum= " + iTranNum + "\n");		
	
					tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "Execution Time (GMT)", getDateTime());
					if (iTranNum > 0)
			    		tran.saveTranInfo();
				tran.destroy();
			}
		}
		OConsole.print("*** End: " + sScriptName + "***\n");
		Util.exitSucceed();
	}

   private String getCollType(String sExtLegal) throws OException
   {
	   String sCollType="";
	   String sSql;
	   Table tTemp = Util.NULL_TABLE;
	   int iParty = Ref.getValue(SHM_USR_TABLES_ENUM.PARTY_TABLE, sExtLegal);
	   
	   tTemp = Table.tableNew();
	   sSql = "select pi.party_id, pi.type_id, pi.value " +
	   		  "from party_info pi, party_info_types pit " +
	   		  "where pit.type_name = 'REG Collateralization Type' " +
	   		  "and pit.type_id = pi.type_id " +
	   		  "and pi.party_id=" + iParty;
	   DBaseTable.execISql(tTemp, sSql);
	   
	   if (tTemp.getNumRows()<=0)
	   {
		   sCollType = "";
		   OConsole.oprint("*** No REG Collateralization Type set on Counterparty " + sExtLegal + "***\n");
	   }
	   sCollType = tTemp.getString("value", 1);
	   OConsole.oprint("***REG Collateralization Type = " + sCollType + "\n");
	   
	   return sCollType;
   }


   private String getDateTime() throws OException {
        //DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
	   //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	   //Date date = new Date();
	   //return dateFormat.format(date);
        String retValue = "";
        ODateTime dtd = ODateTime.dtNew();
        dtd.setDateTime(OCalendar.getServerDate(), Util.timeGetServerTime());
        retValue = dtd.formatForDbAccess();
        return retValue;
        
    } 
	

}
