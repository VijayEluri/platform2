package is.idega.idegaweb.member.isi.block.accounting.netbokhald.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;

public class NetbokhaldAccountingKeysBMPBean extends GenericEntity implements
		NetbokhaldAccountingKeys {

	protected final static String ENTITY_NAME = "nb_acc_key";
	
	protected final static String COLUMN_SETUP_ID = "setup_id";
	
	protected final static String COLUMN_TYPE = "type";
	
	protected final static String COLUMN_KEY = "internal_key";
	
	protected final static String COLUMN_DEBIT_KEY = "debit_key";
	
	protected final static String COLUMN_CREDIT_KEY = "credit_key";
	
	protected final static String COLUMN_DELETED = "deleted";
	
	public final static String TYPE_ASSESSMENT = "A";
	
	public final static String TYPE_PAYMENT = "P";
	
	public final static String TYPE_CREDITCARD = "C";
	
	public final static String TYPE_BANK = "B";
	
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(COLUMN_SETUP_ID, NetbokhaldSetup.class);
		addAttribute(COLUMN_TYPE, "Type", String.class);
		addAttribute(COLUMN_KEY, "Key", Integer.class);
		addAttribute(COLUMN_DEBIT_KEY, "Debet key", String.class);
		addAttribute(COLUMN_CREDIT_KEY, "Credit key", String.class);
		addAttribute(COLUMN_DELETED, "Deleted", Boolean.class);
	}
	
	//getters
	public NetbokhaldSetup getSetup() {
		return (NetbokhaldSetup) getColumnValue(COLUMN_SETUP_ID);
	}
	
	public String getType() {
		return getStringColumnValue(COLUMN_TYPE);
	}
	
	public int getKey() {
		return getIntColumnValue(COLUMN_KEY, -1);
	}
	
	public String getDebitKey() {
		return getStringColumnValue(COLUMN_DEBIT_KEY);
	}
	
	public String getCreditKey() {
		return getStringColumnValue(COLUMN_CREDIT_KEY);
	}
	
	public boolean getDeleted() {
		return getBooleanColumnValue(COLUMN_DELETED, false);
	}
	
	//setters
	public void setSetup(NetbokhaldSetup setup) {
		setColumn(COLUMN_SETUP_ID, setup);
	}
	
	public void setType(String type) {
		setColumn(COLUMN_TYPE, type);
	}
	
	public void setKey(int key) {
		setColumn(COLUMN_KEY, key);
	}
	
	public void setDebitKey(String key) {
		setColumn(COLUMN_DEBIT_KEY, key);
	}
	
	public void setCreditKey(String key) {
		setColumn(COLUMN_CREDIT_KEY, key);
	}
	
	public void setDeleted(boolean deleted) {
		setColumn(COLUMN_DELETED, deleted);
	}
	
	//ejb
	public Collection ejbFindAllBySetupID(NetbokhaldSetup setup) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this);
		query.appendWhereEqualsQuoted(COLUMN_SETUP_ID, setup.getExternalID());
		query.appendAnd();
		query.appendLeftParenthesis();
		query.append(COLUMN_DELETED);
		query.append(" is null");
		query.appendOr();
		query.appendEquals(COLUMN_DELETED, false);
		query.appendRightParenthesis();
				
		return idoFindPKsByQuery(query);
	}
	
	public Object ejbFindBySetupIDTypeAndKey(NetbokhaldSetup setup, String type, int key) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this);
		query.appendWhereEqualsQuoted(COLUMN_SETUP_ID, setup.getExternalID());
		query.appendAndEqualsQuoted(COLUMN_TYPE, type);
		query.appendAndEquals(COLUMN_KEY, key);
		query.appendAnd();
		query.appendLeftParenthesis();
		query.append(COLUMN_DELETED);
		query.append(" is null");
		query.appendOr();
		query.appendEquals(COLUMN_DELETED, false);
		query.appendRightParenthesis();
		
		return idoFindOnePKByQuery(query);
	}
}