package com.idega.projects.golf.presentation;

import com.idega.presentation.ui.*;
import com.idega.projects.golf.moduleobject.GolfDialog;
import com.idega.presentation.*;
import java.sql.SQLException;

/**
*@author <a href="mailto:gimmi@idega.is">Gr�mur</a>
*@version 1.0
*/
 public class TournamentGroupDeleter extends TournamentAdmin {

    public TournamentGroupDeleter(){
        super();
        //System.out.println("TournamentGroupDeleter()");
    }


    public void main(IWContext iwc)throws SQLException{
        add("TournamentGroupDeleter");
    }


}// class TournamentGroupDeleter


