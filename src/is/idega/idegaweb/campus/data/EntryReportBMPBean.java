package is.idega.idegaweb.campus.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EntityContext;

import com.idega.util.IWTimestamp;
import com.idega.util.database.ConnectionBroker;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author <br>
 *         <a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class EntryReportBMPBean implements EntryReport {

	public static String getEntityTableName() {
		return "V_BUILDING_ACCOUNT_ENTRY";
	}

	public static String getColumnBuildingId() {
		return "BUILDING_ID";
	}

	public static String getColumnBuildingName() {
		return "BUILDING_NAME";
	}

	public static String getColumnKeyId() {
		return "KEY_ID";
	}

	public static String getColumnKeyName() {
		return "KEY_NAME";
	}

	public static String getColumnKeyInfo() {
		return "KEY_INFO";
	}

	public static String getColumnTotal() {
		return "TOTAL";
	}

	public static String getColumnNumber() {
		return "NUMBER2";
	}

	private Integer BuildingId;
	private String BuildingName;
	private Integer ApartmentId;
	private String ApartmentName;
	private Integer KeyId;
	private String KeyName;
	private String KeyInfo;
	private Float Total;
	private Integer Number;

	private EntityContext _entityContext;
	private EJBHome _ejbHome;
	private EJBLocalHome _ejbLocalHome;
	private Object _primaryKey;

	public String getEntityName() {
		return getEntityTableName();
	}

	public int getBuildingId() {
		return BuildingId.intValue();
	}

	public int getApartmentId() {
		return ApartmentId.intValue();
	}

	public int getKeyId() {
		return KeyId.intValue();
	}

	public String getBuildingName() {
		return BuildingName;
	}

	public String getApartmentName() {
		return ApartmentName;
	}

	public String getKeyName() {
		return KeyName;
	}

	public String getKeyInfo() {
		return KeyInfo;
	}

	public float getTotal() {
		return Total.floatValue();
	}

	public int getNumber() {
		return Number.intValue();
	}

	private void setBuildingId(int id) {
		BuildingId = new Integer(id);
	}

	private void setApartmentId(int id) {
		ApartmentId = new Integer(id);
	}

	private void setKeyId(int id) {
		KeyId = new Integer(id);
	}

	private void setBuildingName(String name) {
		BuildingName = name;
	}

	private void setApartmentName(String name) {
		ApartmentName = name;
	}

	private void setKeyName(String name) {
		KeyName = name;
	}

	private void setKeyInfo(String info) {
		KeyInfo = info;
	}

	private void setTotal(float total) {
		Total = new Float(total);
	}

	private void setNumber(int number) {
		Number = new Integer(number);
	}

	public Class getPrimaryKeyClass() {
		return Integer.class;
	}

	public void setEJBHome(EJBHome home) {
		this._ejbHome = home;
	}

	public void setEJBLocalHome(EJBLocalHome home) {
		this._ejbLocalHome = home;
	}

	public Object ejbFindByPrimaryKey(Object primaryKey) {
		return null;
	}

	public Object ejbCreate() {
		return null;
	}

	public void unsetEntityContext() {
		_entityContext = null;
	}

	public void setEntityContext(EntityContext context) {
		_entityContext = context;
	}

	public void ejbStore() {
	}

	public void ejbPassivate() {
	}

	public void ejbRemove() {
	}

	public void ejbLoad() {
	}

	public void ejbActivate() {
	}

	public static List findAllBySearch(String[] buildingIds,
			String[] accountKeys, Timestamp from, Timestamp to,
			boolean byApartment, Integer entryGroupID) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		ResultSetMetaData metaData;
		Vector vector = null;
		String sql = null;
		if (!byApartment) {
			sql = getFindSql(buildingIds, accountKeys, from, to, entryGroupID);
		} else {
			sql = getFindSql2(buildingIds, accountKeys, from, to, entryGroupID);
		}

		System.out.println("sql = " + sql);

		try {
			conn = ConnectionBroker.getConnection();
			Stmt = conn.createStatement();

			// System.err.println(sql);
			ResultSet RS = Stmt.executeQuery(sql);
			metaData = RS.getMetaData();

			while (RS.next()) {
				EntryReportBMPBean tempobj = new EntryReportBMPBean();

				if (tempobj != null) {
					if (byApartment) {
						tempobj.setBuildingId(RS.getInt(1));
						tempobj.setBuildingName(RS.getString(2));
						tempobj.setApartmentId(RS.getInt(3));
						tempobj.setApartmentName(RS.getString(4));
						tempobj.setKeyId(RS.getInt(5));
						tempobj.setKeyName(RS.getString(6));
						tempobj.setKeyInfo(RS.getString(7));
						tempobj.setTotal(RS.getFloat(8));
						tempobj.setNumber(RS.getInt(9));
					} else {
						tempobj.setBuildingId(RS.getInt(1));
						tempobj.setBuildingName(RS.getString(2));
						tempobj.setKeyId(RS.getInt(3));
						tempobj.setKeyName(RS.getString(4));
						tempobj.setKeyInfo(RS.getString(5));
						tempobj.setTotal(RS.getFloat(6));
						tempobj.setNumber(RS.getInt(7));
					}

				}
				if (vector == null) {
					vector = new Vector();
				}
				vector.addElement(tempobj);

			}
			RS.close();
		} catch (SQLException ex) {
			throw new SQLException("SQL : " + sql);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}

		if (vector != null) {
			vector.trimToSize();
			return vector;
		} else {
			return null;
		}
	}

	private static String getFindSql(String[] buildingIds,
			String[] accountKeys, java.sql.Timestamp from,
			java.sql.Timestamp to, Integer entryGroupID) {
		StringBuffer sql = new StringBuffer(" select ");
		sql.append(" b.bu_building_id building_id,");
		sql.append(" b.name building_name,");
		sql.append(" k.fin_acc_key_id key_id, ");
		sql.append(" k.name key_name, ");
		sql.append(" k.info key_info, ");
		sql.append(" sum(e.total) total, ");
		sql.append(" count(acc.fin_account_id) number2 ");
		sql.append(" from ");
		sql.append(" bu_apartment a,bu_building b,bu_floor f, ic_user u ,  ");
		sql
				.append(" fin_account acc,fin_acc_entry e,fin_acc_key k,cam_aprt_acc_entry ce");
		sql.append(" where b.bu_building_id = f.bu_building_id ");
		sql.append(" and f.bu_floor_id = a.bu_floor_id ");
		sql.append(" and a.bu_apartment_id = ce.aprt_id ");
		sql.append(" and u.ic_user_id  = acc.ic_user_id ");
		sql.append(" and ce.entry_id = e.fin_acc_entry_id ");
		sql.append(" and e.fin_account_id = acc.fin_account_id ");
		sql.append(" and k.fin_acc_key_id = e.fin_acc_key_id ");

		if (buildingIds != null) {
			sql.append(" and ");
			sql.append(" b.bu_building_id ");
			sql.append(" in (");
			for (int i = 0; i < buildingIds.length; i++) {
				if (i > 0 && i < buildingIds.length)
					sql.append(",");
				sql.append(buildingIds[i]);
			}
			sql.append(" ) ");
		}
		if (accountKeys != null) {
			sql.append(" and ");
			sql.append(" k.fin_acc_key_id ");
			sql.append(" in (");
			for (int i = 0; i < accountKeys.length; i++) {
				if (i > 0 && i < accountKeys.length)
					sql.append(",");
				sql.append(accountKeys[i]);
			}
			sql.append(" ) ");
		}
		if (entryGroupID == null) {
			if (from != null) {
				sql.append(" and e.payment_date >= '");
				IWTimestamp stamp = new IWTimestamp(from);
				sql.append(stamp.getDateString("yyyy-MM-dd"));
				sql.append("'");
			}
			if (to != null) {
				sql.append(" and e.payment_date <= '");
				IWTimestamp stamp = new IWTimestamp(to);
				sql.append(stamp.getDateString("yyyy-MM-dd"));
				sql.append("'");
			}
		} else {
			sql.append(" and e.FIN_ENTRY_GROUP_ID = ");
			sql.append(entryGroupID.intValue());
		}

		sql
				.append(" group by b.bu_building_id,b.name,k.fin_acc_key_id,k.name,k.info ");
		sql.append(" order by b.bu_building_id ");

		return sql.toString();
	}

	private static String getFindSql2(String[] buildingIds,
			String[] accountKeys, java.sql.Timestamp from,
			java.sql.Timestamp to, Integer entryGroupID) {
		StringBuffer sql = new StringBuffer(" select ");
		sql.append(" b.bu_building_id building_id, ");
		sql
				.append(" b.name building_name, a.bu_apartment_id apartment_id, a.name apartment_name,");
		sql.append(" k.fin_acc_key_id key_id, ");
		sql.append(" k.name key_name, ");
		sql.append(" k.info key_info, ");
		sql.append(" sum(e.total) total, ");
		sql.append(" count(acc.fin_account_id) number2 ");
		sql.append(" from ");
		sql.append(" bu_apartment a,bu_building b,bu_floor f, ic_user u ,  ");
		sql
				.append(" fin_account acc,fin_acc_entry e,fin_acc_key k,cam_aprt_acc_entry ce");
		sql.append(" where b.bu_building_id = f.bu_building_id ");
		sql.append(" and f.bu_floor_id = a.bu_floor_id ");
		sql.append(" and a.bu_apartment_id = ce.aprt_id ");
		sql.append(" and u.ic_user_id  = acc.ic_user_id ");
		sql.append(" and ce.entry_id = e.fin_acc_entry_id ");
		sql.append(" and e.fin_account_id = acc.fin_account_id ");
		sql.append(" and k.fin_acc_key_id = e.fin_acc_key_id ");

		if (buildingIds != null) {
			sql.append(" and ");
			sql.append(" b.bu_building_id ");
			sql.append(" in (");
			for (int i = 0; i < buildingIds.length; i++) {
				if (i > 0 && i < buildingIds.length)
					sql.append(",");
				sql.append(buildingIds[i]);
			}
			sql.append(" ) ");
		}
		if (accountKeys != null) {
			sql.append(" and ");
			sql.append(" k.fin_acc_key_id ");
			sql.append(" in (");
			for (int i = 0; i < accountKeys.length; i++) {
				if (i > 0 && i < accountKeys.length)
					sql.append(",");
				sql.append(accountKeys[i]);
			}
			sql.append(" ) ");
		}
		if (entryGroupID == null) {
		if (from != null) {
			sql.append(" and e.payment_date >= '");
			IWTimestamp stamp = new IWTimestamp(from);
			sql.append(stamp.getDateString("yyyy-MM-dd"));
			sql.append("'");
		}
		if (to != null) {
			sql.append(" and e.payment_date <= '");
			IWTimestamp stamp = new IWTimestamp(to);
			sql.append(stamp.getDateString("yyyy-MM-dd"));
			sql.append("'");
		}} else {
			sql.append(" and e.FIN_ENTRY_GROUP_ID = ");
			sql.append(entryGroupID.intValue());
		}

		sql
				.append(" group by b.bu_building_id,b.name,a.bu_apartment_id,a.name,k.fin_acc_key_id,k.name,k.info ");
		sql.append(" order by b.bu_building_id ");

		return sql.toString();
	}

	/**
	 * @see com.idega.data.IDOEntityBean#getAttributes()
	 */
	public Collection getAttributes() {
		return null;
	}

}