/*
 * Created on Jan 20, 2005
 */
package is.idega.idegaweb.member.business;

import com.idega.business.IBOHomeImpl;

/**
 * @author Sigtryggur
 */
public class UserStatsBusinessHomeImpl extends IBOHomeImpl implements
        UserStatsBusinessHome {
    protected Class getBeanInterfaceClass() {
        return UserStatsBusiness.class;
    }

    public UserStatsBusiness create() throws javax.ejb.CreateException {
        return (UserStatsBusiness) super.createIBO();
    }

}
