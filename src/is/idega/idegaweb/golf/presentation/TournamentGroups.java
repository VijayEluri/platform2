package is.idega.idegaweb.golf.presentation;

import com.idega.presentation.ui.*;
import is.idega.idegaweb.golf.moduleobject.GolfDialog;
import com.idega.presentation.*;
import java.sql.SQLException;

/**
*@author <a href="mailto:gimmi@idega.is">Gr�mur</a>
*@version 1.0
*/
 public class TournamentGroups extends TournamentAdmin {

    public TournamentGroups(){
        super();
        //System.out.println("TournamentModifier()");
    }


    public void main(IWContext iwc)throws Exception{
        GolfDialog dialog = new GolfDialog("M�tastj�ri");
        add(dialog);

        Form form5 = new Form(Class.forName("is.idega.idegaweb.golf.presentation.TournamentGroupCreator"));
//        Form form5 = new Form("createtournamentgroup.jsp");
        SubmitButton Button5 = new SubmitButton("b5","N�r m�tsh�pur");
        form5.add(Button5);
        dialog.add(form5);

        Form form6 = new Form(Class.forName("is.idega.idegaweb.golf.presentation.TournamentGroupDeleter") );
//        Form form6 = new Form("deletetournamentgroup.jsp");
        SubmitButton Button6 = new SubmitButton("b5","Ey�a m�tsh�p");
        form6.add(Button6);
        dialog.add(form6);
    }


}// class TournamentGroups


