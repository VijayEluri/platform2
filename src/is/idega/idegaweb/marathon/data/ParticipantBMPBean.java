/*
 * Created on Jul 6, 2004
 */
package is.idega.idegaweb.marathon.data;

import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOQuery;
import com.idega.data.query.Column;
import com.idega.data.query.CountColumn;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.MaxColumn;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * Description: <br>
 * Copyright: Idega Software 2004 <br>
 * Company: Idega Software <br>
 * 
 * @author birna
 */
public class ParticipantBMPBean extends GenericEntity implements Participant {

	public ParticipantBMPBean() {
		super();
	}

	public ParticipantBMPBean(int id) throws SQLException {
		super(id);
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());

		addManyToOneRelationship(getColumnNameRunTypeGroupID(), "Run Type", Group.class);
		addManyToOneRelationship(getColumnNameRunYearGroupID(), "Run Year", Group.class);
		addManyToOneRelationship(getColumnNameRunDistanceGroupID(), "Run Distance", Group.class);
		addManyToOneRelationship(getColumnNameRunGroupGroupID(), "Run Group", Group.class);
		addManyToOneRelationship(getColumnNameUserID(), "User ID", User.class);
		
		addAttribute(getColumnNameRunTime(), "Run Time", true, true, Integer.class);
		addAttribute(getColumnNameChipTime(), "Chip Time", true, true, Integer.class);
		addAttribute(getColumnNameChipNumber(), "Chip Number", true, true, String.class);
		addAttribute(getColumnNameChipBunchNumber(), "Chip bunch Number", true, true, String.class);
		addAttribute(getColumnNameChipOwnershipStatus(), "Chip ownership status", true, true, String.class);
		addAttribute(getColumnNameUserNationality(), "User Nationality", true, true, String.class);
		addAttribute(getColumnNameTShirtSize(), "TShirt Size", true, true, String.class);
		addAttribute(getColumnNameRunGroupName(), "Run Group Name", true, true, String.class);
		addAttribute(getColumnNameBestTime(), "Best Time", true, true, String.class);
		addAttribute(getColumnNameGoalTime(), "Goal Time", true, true, String.class);
		addAttribute(getColumnNameParticipantNumber(), "Participant number", true, true, Integer.class);
		addAttribute(getColumnNamePayMethod(), "Pay method", true, true, String.class);
		addAttribute(getColumnNameAmountPayed(), "Amount payed", true, true, String.class);
	}

	public static String getEntityTableName() {
		return "run";
	}

	public static String getColumnNameRunID() {
		return "run_id";
	}

	public static String getColumnNameRunTypeGroupID() {
		return com.idega.user.data.GroupBMPBean.getColumnNameGroupID() + "_run";
	}

	public static String getColumnNameRunYearGroupID() {
		return com.idega.user.data.GroupBMPBean.getColumnNameGroupID() + "_year";
	}

	public static String getColumnNameRunDistanceGroupID() {
		return com.idega.user.data.GroupBMPBean.getColumnNameGroupID() + "_distance";
	}

	public static String getColumnNameRunGroupGroupID() {
		return com.idega.user.data.GroupBMPBean.getColumnNameGroupID() + "_group";
	}

	public static String getColumnNameRunTime() {
		return "run_time";
	}

	public static String getColumnNameChipTime() {
		return "chip_time";
	}

	public static String getColumnNameSplitTime1() {
		return "split_time_1";
	}

	public static String getColumnNameSplitTime2() {
		return "split_time_2";
	}

	public static String getColumnNameChipNumber() {
		return "run_chip_number";
	}
	
	public static String getColumnNameChipBunchNumber() {
		return "run_chip_bunch_number";
	}
	
	public static String getColumnNameChipOwnershipStatus() {
		return "run_chip_ownership_status";
	}

	public static String getColumnNameUserNationality() {
		return "user_nationality";
	}

	public static String getColumnNameTShirtSize() {
		return "run_tShirt_size";
	}

	public static String getColumnNameRunGroupName() {
		return "run_group_name";
	}

	public static String getColumnNameBestTime() {
		return "run_best_time";
	}

	public static String getColumnNameGoalTime() {
		return "run_goal_time";
	}

	public static String getColumnNameUserID() {
		return com.idega.user.data.UserBMPBean.getColumnNameUserID();
	}

	public static String getColumnNameParticipantNumber() {
		return "participant_number";
	}
	
	public static String getColumnNamePayMethod() {
		return "pay_method";
	}
	
	public static String getColumnNameAmountPayed() {
		return "payed_amount";
	}

	public String getIDColumnName() {
		return getColumnNameRunID();
	}

	public String getEntityName() {
		return getEntityTableName();
	}

	public int getRunID() {
		return getIntColumnValue(getColumnNameRunID());
	}

	//GET
	public int getRunTypeGroupID() {
		return getIntColumnValue(getColumnNameRunTypeGroupID());
	}
	
	public Group getRunTypeGroup() {
		return (Group) getColumnValue(getColumnNameRunTypeGroupID());
	}

	public int getRunYearGroupID() {
		return getIntColumnValue(getColumnNameRunYearGroupID());
	}

	public Group getRunYearGroup() {
		return (Group) getColumnValue(getColumnNameRunYearGroupID());
	}

	public int getRunDistanceGroupID() {
		return getIntColumnValue(getColumnNameRunDistanceGroupID());
	}

	public Group getRunDistanceGroup() {
		return (Group) getColumnValue(getColumnNameRunDistanceGroupID());
	}

	public int getRunGroupGroupID() {
		return getIntColumnValue(getColumnNameRunGroupGroupID());
	}

	public Group getRunGroupGroup() {
		return (Group) getColumnValue(getColumnNameRunGroupGroupID());
	}

	public int getRunTime() {
		return getIntColumnValue(getColumnNameRunTime());
	}

	public int getChipTime() {
		return getIntColumnValue(getColumnNameChipTime());
	}
	
	public int getSplitTime1() {
		return getIntColumnValue(getColumnNameSplitTime1());
	}
	
	public int getSplitTime2() {
		return getIntColumnValue(getColumnNameSplitTime2());
	}
	
	public String getChipOwnershipStatus() {
		return getStringColumnValue(getColumnNameChipOwnershipStatus());
	}

	public User getUser() {
		return (User) getColumnValue(getColumnNameUserID());
	}

	public int getUserID() {
		return getIntColumnValue(getColumnNameUserID());
	}

	public String getChipNumber() {
		return getStringColumnValue(getColumnNameChipNumber());
	}
	
	public String getChipBunchNumber() {
		return getStringColumnValue(getColumnNameChipBunchNumber());
	}

	public String getUserNationality() {
		return getStringColumnValue(getColumnNameUserNationality());
	}

	public String getTShirtSize() {
		return getStringColumnValue(getColumnNameTShirtSize());
	}

	public String getRunGroupName() {
		return getStringColumnValue(getColumnNameRunGroupName());
	}

	public String getBestTime() {
		return getStringColumnValue(getColumnNameBestTime());
	}

	public String getGoalTime() {
		return getStringColumnValue(getColumnNameGoalTime());
	}

	public int getParticipantNumber() {
		return getIntColumnValue(getColumnNameParticipantNumber());
	}
	
	public String getPayMethod() {
		return getStringColumnValue(getColumnNamePayMethod());
	}
	
	public String getPayedAmount() {
		return getStringColumnValue(getColumnNameAmountPayed());
	}

	//SET
	public void setRunTypeGroupID(int runTypeGroupID) {
		setColumn(getColumnNameRunTypeGroupID(), runTypeGroupID);
	}

	public void setRunTypeGroup(Group runTypeGroup) {
		setColumn(getColumnNameRunTypeGroupID(), runTypeGroup);
	}

	public void setRunYearGroupID(int runYearGroupID) {
		setColumn(getColumnNameRunYearGroupID(), runYearGroupID);
	}

	public void setRunYearGroup(Group runYearGroup) {
		setColumn(getColumnNameRunYearGroupID(), runYearGroup);
	}

	public void setRunDistanceGroupID(int runDisGroupID) {
		setColumn(getColumnNameRunDistanceGroupID(), runDisGroupID);
	}

	public void setRunDistanceGroup(Group runDisGroup) {
		setColumn(getColumnNameRunDistanceGroupID(), runDisGroup);
	}

	public void setRunGroupGroupID(int runGroupGroupID) {
		setColumn(getColumnNameRunGroupGroupID(), runGroupGroupID);
	}

	public void setRunGroupGroup(Group runGroupGroup) {
		setColumn(getColumnNameRunGroupGroupID(), runGroupGroup);
	}

	public void setRunTime(int runTime) {
		setColumn(getColumnNameRunTime(), runTime);
	}

	public void setChipTime(int chipTime) {
		setColumn(getColumnNameChipTime(), chipTime);
	}
	
	public void setSplitTime1(int splitTime) {
		setColumn(getColumnNameSplitTime1(), splitTime);
	}
	
	public void setSplitTime2(int splitTime) {
		setColumn(getColumnNameSplitTime2(), splitTime);
	}
	
	public void setChipOwnershipStatus(String ownershipStatus) {
		setColumn(getColumnNameChipOwnershipStatus(), ownershipStatus);
	}

	public void setUserID(int userID) {
		setColumn(getColumnNameUserID(), userID);
	}

	public void setUser(User user) {
		setColumn(getColumnNameUserID(), user);
	}

	public void setChipNumber(String chipNumber) {
		setColumn(getColumnNameChipNumber(), chipNumber);
	}
	
	public void setChipBunchNumber(String chipBunchNumber) {
		setColumn(getColumnNameChipBunchNumber(), chipBunchNumber);
	}

	public void setUserNationality(String nationality) {
		setColumn(getColumnNameUserNationality(), nationality);
	}

	public void setTShirtSize(String tShirtSize) {
		setColumn(getColumnNameTShirtSize(), tShirtSize);
	}

	public void setRunGroupName(String runGrName) {
		setColumn(getColumnNameRunGroupName(), runGrName);
	}

	public void setBestTime(String bestTime) {
		setColumn(getColumnNameBestTime(), bestTime);
	}

	public void setGoalTime(String goalTime) {
		setColumn(getColumnNameGoalTime(), goalTime);
	}
	
	public void setParticipantNumber(int participantNumber) {
		setColumn(getColumnNameParticipantNumber(), participantNumber);
	}
	
	public void setPayMethod(String payMethod) {
		setColumn(getColumnNamePayMethod(), payMethod);
	}
	
	public void setPayedAmount(String amount) {
		setColumn(getColumnNameAmountPayed(),amount);
	}
	
	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		
		return idoFindPKsBySQL(query.toString());
	}
	
	public int ejbHomeGetNextAvailableParticipantNumber(Object distancePK, int min, int max) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new MaxColumn(getColumnNameParticipantNumber()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.GREATEREQUAL, min));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.LESSEQUAL, max));
		
		return idoGetNumberOfRecords(query.toString());
	}
	
	public int ejbHomeGetNumberOfParticipantsByDistance(Object distancePK, int min, int max) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new CountColumn(getColumnNameParticipantNumber()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.GREATEREQUAL, min));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.LESSEQUAL, max));
		
		return idoGetNumberOfRecords(query.toString());
	}
	
	public int ejbHomeGetCountByDistanceAndNumber(Object distancePK, int number) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new CountColumn(getColumnNameParticipantNumber()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameParticipantNumber(), MatchCriteria.EQUALS, number));
		
		return idoGetNumberOfRecords(query.toString());
	}
	
	public int ejbHomeGetCountByDistanceAndGroupName(Object distancePK, String groupName) throws IDOException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distancePK));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunGroupName(), MatchCriteria.EQUALS, groupName));
		
		return idoGetNumberOfRecords(query);
	}
	
	public Collection ejbFindAllByDistanceAndGroup(Group distance, Group runGroup) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table, getColumnNameRunDistanceGroupID(), MatchCriteria.EQUALS, distance));
		if (runGroup != null) {
			query.addCriteria(new MatchCriteria(table, getColumnNameRunGroupGroupID(), MatchCriteria.EQUALS, runGroup));
		}
		query.addOrder(table, getColumnNameRunTime(), true);
		
		return idoFindPKsBySQL(query.toString());
	}
	
	public Integer ejbFindByUserIDandDistanceID(int userID, int distanceID) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),userID);
		query.appendAndEquals(getColumnNameRunDistanceGroupID(),distanceID);
		return (Integer) super.idoFindOnePKByQuery(query);
	}
	
	public Integer ejbFindByDistanceAndParticpantNumber(Object distancePK, int participantNumber) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameRunDistanceGroupID(), distancePK);
		query.appendAndEquals(getColumnNameParticipantNumber(), participantNumber);
		return (Integer) super.idoFindOnePKByQuery(query);
	}
	
	public Integer ejbFindByUserAndRun(User user, Group run, Group year) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),user);
		query.appendAndEquals(getColumnNameRunTypeGroupID(),run);
		query.appendAndEquals(getColumnNameRunYearGroupID(),year);
		return (Integer) super.idoFindOnePKByQuery(query);
	}
	
	public Collection ejbFindByUserAndParentGroup(int userID, int runGroupID, int yearGroupID, int distanceGroupID) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),userID);
		query.appendAndEquals(getColumnNameRunTypeGroupID(),runGroupID);
//		query.appendAndEquals(getColumnNameRunYearGroupID(),yearGroupID);
		query.appendAndEquals(getColumnNameRunDistanceGroupID(),distanceGroupID);
		return super.idoFindPKsByQuery(query);
	
	}
	public Collection ejbFindByUserID(int userID) throws FinderException{
		IDOQuery query = idoQueryGetSelect();
		query.appendWhereEquals(getColumnNameUserID(),userID);
		return super.idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindAllWithoutChipNumber(int distanceIDtoIgnore) throws FinderException {
		IDOQuery query = idoQueryGetSelect();
		query.appendWhere().append("("+getColumnNameChipNumber()).appendIsNull().appendOr().append(getColumnNameChipNumber()).append("= '')");
		if (distanceIDtoIgnore != -1) {
			query.appendAnd().append(getColumnNameRunDistanceGroupID()).appendNOTEqual().append(distanceIDtoIgnore);
		}
		return super.idoFindPKsByQuery(query);		
	}
}