/*
 * Created on 4.3.2004
 */
package is.idega.idegaweb.golf.tournament.presentation;

import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import is.idega.idegaweb.golf.moduleobject.GolfTournamentAdminDialog;

/**
 * @author gimmi
 */
public class TournamentUpdater extends TournamentBlock {

	protected boolean tournamentMustBeSet() {
		return true;
	}

	public void _main(IWContext modinfo)throws Exception{
		int tID = getTournamentID(modinfo);

	    if ( tID < 1) {
	      getParentPage().setToRedirect(modinfo.getIWMainApplication().getObjectInstanciatorURI(TournamentSelector.class)+"&"+super.PARAMETER_CLASS_NAME+"="+this.getClassName());
	    }else {
	      getParentPage().setToRedirect(modinfo.getIWMainApplication().getObjectInstanciatorURI(TournamentCreator.class)+"&tournament_control_mode=edit&tournament="+tID+"&"+GolfTournamentAdminDialog.ADMIN_VIEW_PARAMETER+"="+GolfTournamentAdminDialog.ADMIN_VIEW_MODIFY_TOURNAMENT);
	    }
	    
	}
	
	public void main(IWContext modinfo) throws Exception {
		System.out.println("Test");
	}

}
