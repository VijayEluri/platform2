package is.idega.idegaweb.golf.business;

import java.sql.Timestamp;
import is.idega.idegaweb.golf.entity.GolferPageFriendsData;
import is.idega.idegaweb.golf.entity.GolferPageData;
import java.sql.Date;
import java.lang.String;
import com.idega.util.idegaTimestamp;
import com.idega.data.*;
import java.sql.SQLException;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson</a>
 * @version 1.0
 */

public class GolferFriendsDataBusiness {

  public static void insertFriendsData(String name, String sSNumber, String email, String adress,
  /* TimeStamp signingDate,*/ String cardType, String cardNumber, String cardExpDate,
    boolean nameAppearance, String paymentAmount, String paymentDuration, String billAdress,
    String billName, String billSSNumber, String nameToAppear, String golferName) throws SQLException{

    GolferPageFriendsData golferPageFriendsData = new GolferPageFriendsData();
    golferPageFriendsData.setName(name);
    golferPageFriendsData.setGolferName(golferName);
    golferPageFriendsData.setAdress(adress);
    golferPageFriendsData.setCreditCardExpDate(cardExpDate);
    golferPageFriendsData.setCreditCardNumber(cardNumber);
    golferPageFriendsData.setCreditCardType(cardType);
    golferPageFriendsData.setEmail(email);
    golferPageFriendsData.setNameAppearance(nameAppearance);
    golferPageFriendsData.setPaymentAmount(paymentAmount);
    golferPageFriendsData.setPaymentDuration(paymentDuration);
    golferPageFriendsData.setSigningDate(new idegaTimestamp().getTimestampRightNow());
    golferPageFriendsData.setSocialSecurityNumber(sSNumber);
    golferPageFriendsData.setBillAdress(billAdress);
    golferPageFriendsData.setBillName(billName);
    golferPageFriendsData.setBillSSNumber(billSSNumber);
    golferPageFriendsData.setNameToView(nameToAppear);
    golferPageFriendsData.insert();
  }

  public static void insertNewGolferPageData(int memberID){
   /**@todo Implement insertNewGolferPageData, especially NewsReader.
   */
  }

  public static void insertGolferPageData(int memberID, int newsReaderID,
    int profileID, int golfbagID, int resultsAbroadID, int statisticsID,
    int supportesID, int supportPreSigningID) throws SQLException{
    GolferPageData golferPageData = new GolferPageData();

    golferPageData.setGolfbagID(golfbagID);
    golferPageData.setMemberID(memberID);
    golferPageData.setNewsReaderID(newsReaderID);
    golferPageData.setProfilerID(profileID);
    golferPageData.setResultsAbroadID(resultsAbroadID);
    golferPageData.setStatisticsID(statisticsID);
    golferPageData.setSupportesID(supportesID);
    golferPageData.setSupportPreSigningID(supportPreSigningID);
    golferPageData.insert();
  }

}