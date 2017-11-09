package com.op_service;

import com.olf.openjvs.*;
import com.olf.openjvs.enums.*;

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
					tran.setField(TRANF_FIELD.TRANF_TRAN_INFO.toInt(), 0, "Confirmation Time", getDateTime());

					if (iTranNum > 0)
			    		tran.saveTranInfo();

				tran.destroy();
			}
		}
		OConsole.print("*** End: " + sScriptName + "***\n");
		Util.exitSucceed();
	}
        private String getDateTime() throws OException {
        String retValue = "";
        ODateTime dtd = ODateTime.dtNew();
        dtd.setDateTime(OCalendar.getServerDate(), Util.timeGetServerTime());
        retValue = Str.dtToString(dtd, DATE_FORMAT.DATE_FORMAT_DMLY_NOSLASH.toInt(), TIME_FORMAT.TIME_FORMAT_HMS.toInt());
        return retValue;        
    } 
}
