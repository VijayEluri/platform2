/*
 * $Id: KBDataInsert.java,v 1.5.4.2 2006/04/21 11:20:25 palli Exp $
 * Created on Feb 8, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.block.finance.business.kb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.idega.block.finance.business.BankFileManager;
import com.idega.block.finance.business.BankInvoiceFileManager;
import com.idega.block.finance.business.InvoiceDataInsert;
import com.idega.block.finance.data.BankInfo;
import com.idega.util.IWTimestamp;

/**
 * 
 * Last modified: $Date: 2006/04/21 11:20:25 $ by $Author: palli $
 * 
 * @author <a href="mailto:birna@idega.com">birna</a>
 * @version $Revision: 1.5.4.2 $
 */
public class KBDataInsert /* extends Window */implements InvoiceDataInsert {
	private static String POST_METHOD = "https://www.kbbanki.is/KBKrofur/Vefur/Pages/FileUpload.aspx";

	private static String POST_ANSWER_METHOD = "https://www.bi.is/krofulinan/Pages/saekja_greidsluskra.aspx";

	private static String FILE_NAME = "iw_cache/kbb.txt";

	private String emptyString = new String();

	private String zeroString = new String();
	
	private String batchName = null;

	public void createClaimsInBank(int batchNumber, BankInfo info) {
		BankFileManager bfm = new BankInvoiceFileManager(info);

		createClaimsInBank(batchNumber, bfm);
	}

	public void createClaimsInBank(int batchNumber, int groupId) {
		BankFileManager bfm = new BankInvoiceFileManager(groupId);

		createClaimsInBank(batchNumber, bfm);
	}

	private void createClaimsInBank(int batchNumber, BankFileManager bfm) {
		for (int i = 0; i < 200; i++) {
			emptyString += " ";
			zeroString += "0";
		}
		StringBuffer buffer = new StringBuffer();

		String claimantSSN = bfm.getClaimantSSN();
		if (claimantSSN != null && !claimantSSN.equals("")) {
			if (claimantSSN.length() < 10)
				claimantSSN = zeroString
						.substring(0, 10 - claimantSSN.length())
						+ claimantSSN;
		} else {
			claimantSSN = zeroString.substring(0, 10);
		}

		String bankNr = bfm.getBankBranchNumber();
		if (bankNr != null && !bankNr.equals("")) {
			if (bankNr.length() < 4)
				bankNr = zeroString.substring(0, 4 - bankNr.length()) + bankNr;
		} else {
			bankNr = zeroString.substring(0, 4);
		}

		String claimantName = bfm.getClaimantName();
		if (claimantName != null && !claimantName.equals("")) {
			if (claimantName.length() < 8)
				claimantName = claimantName
						+ emptyString.substring(0, 8 - claimantName.length());
		} else {
			claimantName = emptyString.substring(0, 8);
		}

		String claimantAccountId = bfm.getClaimantsAccountId();
		if (claimantAccountId != null && !claimantAccountId.equals("")) {
			if (claimantAccountId.length() < 3)
				claimantAccountId = zeroString.substring(0,
						3 - claimantAccountId.length());
		} else {
			claimantAccountId = zeroString.substring(0, 3);
		}

		// start the header string: "hausfaersla"
		/* Kt_krofuhafa - 10 */
		buffer.append(claimantSSN);// "5904002890"
		/* Utgafa - 4 */
		buffer.append("0140");// the version number of the data sending to RB
		/* Faerslugerd - 1 */
		buffer.append("H");// the type of the first line is: "hausfaersla" (H)
		/* Keyrsludagur - 8 */
		buffer.append(new IWTimestamp(Calendar.getInstance().getTime())
				.getDateString("yyyyMMdd"));// the run date - today!
		/* Verkefni - 2 */
		buffer.append("IK");// a code for "verkefni"
		/* autt - 8 */
		buffer.append(emptyString.substring(0, 8));// 8 empty spaces
		/* Gjaldkeranumer - 4 */
		buffer.append("BI01");// a code for "gjaldkeranumer"
		/* autt - 7 */
		buffer.append(emptyString.substring(0, 7));
		/* Iban - 4 */
		buffer.append(bankNr);// this is the number of the bank that sends the
								// claims
		/* Skjar - 8 */
		buffer.append(claimantName);// "IKtest "
		/* autt - 50 */
		buffer.append(emptyString.substring(0, 50));
		buffer.append("\n");
		// "hausfaersla" ends

		Integer[] krofuNumer = bfm.getInvoiceNumbers(batchNumber);// new
																	// Integer[2];
		// krofuNumer[0] = new Integer(1);
		// krofuNumer[1] = new Integer(2);

		String numberOfClaims = String.valueOf(krofuNumer.length);
		if (numberOfClaims.length() < 6)
			numberOfClaims = zeroString.substring(0, 6 - numberOfClaims
					.length())
					+ numberOfClaims;

		int totalAmount = 0;

		for (int i = 0; i < krofuNumer.length; i++) {
			int number = krofuNumer[i].intValue();

			totalAmount += Integer.valueOf(bfm.getAmount100(number)).intValue();

			String payersSSN = bfm.getPayersSSN(number);
			if (payersSSN != null && !payersSSN.equals("")) {
				if (payersSSN.length() < 10)
					payersSSN = zeroString
							.substring(0, 10 - payersSSN.length())
							+ payersSSN;
			} else {
				payersSSN = zeroString.substring(0, 10);
			}

			String numberString = String.valueOf(number);
			if (numberString.length() < 6)
				numberString = zeroString.substring(0, 6 - numberString
						.length())
						+ numberString;

			String amount = bfm.getAmount100(number);
			if (amount != null && !amount.equals("")) {
				if (amount.length() < 11)
					amount = zeroString.substring(0, 11 - amount.length())
							+ amount;
			}

			String batchString = String.valueOf(batchNumber);// String.valueOf(12);
			if (batchString.length() < 16)
				batchString = batchString
						+ zeroString.substring(0, 16 - batchString.length());

			String noteNumber = bfm.getNoteNumber(number);
			if (noteNumber != null && !noteNumber.equals("")) {
				if (noteNumber.length() < 7)
					noteNumber = zeroString.substring(0, 7 - noteNumber
							.length())
							+ noteNumber;
			} else {
				noteNumber = zeroString.substring(0, 7);
			}

			/* Kt_krofuhafa - 10 */
			buffer.append(claimantSSN);// "5904002890"
			/* Utgafa - 4 */
			buffer.append("0140");// the version number of the data sending to
									// RB
			/* Faerslugerd - 1 */
			buffer.append("K");// the type of the second line is: "stofnun a
								// krofum" (K)
			/* Gjalddagi - 8 */
			buffer.append(new IWTimestamp(bfm.getDueDate(number).getTime())
					.getDateString("yyyyMMdd"));// "20040801"
			Calendar nidurfellingardagur = bfm.getDueDate(number);
			nidurfellingardagur.add(Calendar.YEAR, 4);
			/* Nidurfellingardagur - 8 */
			buffer.append(new IWTimestamp(nidurfellingardagur.getTime())
					.getDateString("yyyyMMdd"));// "20050801"
			/* Audkenni (Visir) - 3 */
			buffer.append(claimantAccountId);// "001"
			/* Kt_greidanda - 10 */
			buffer.append(payersSSN);// "5904002970"
			/* Bankanumer - 4 */
			buffer.append(bankNr);// "0303"
			/* Hofudbok - 2 */
			buffer.append(String.valueOf(bfm.getAccountBook()));// "66"
			/* Numer - 6 */
			buffer.append(numberString);// "10028" + i
			/* Upphaed - 11 */
			buffer.append(amount);// "00000000100"
			/* Tilvisun - 16 */
			buffer.append(batchString);// "5904002970000000"
			/* Sedilnumer - 7 */
			buffer.append(noteNumber);// "0000000"
			/* Vidskiptanumer - 16 */
			buffer.append(emptyString.substring(0, 6) + payersSSN);// "5904002970"
			/* Eindagi - 8 */
			buffer
					.append(new IWTimestamp(bfm.getFinalDueDate(number)
							.getTime()).getDateString("yyyyMMdd"));// "20040815"

			/* information not needed for now */
			/* Tilkynningar- og greidslugjald 1 - 11 */
			buffer.append("00000000000");// bfm.getNotificationAndPaymentFee1(number));
			/* Tilkynningar- og greidslugjald 2 - 11 */
			buffer.append("00000000000");// bfm.getNotificationAndPaymentFee2(number));
			/* Fyrra vanskilagjald - 11 */
			buffer.append("00000000000");
			/* Seinna vanskilagjald - 11 */
			buffer.append("00000000000");
			/* Dagafjoldi fyrra vanskilagjalds - 2 */
			buffer.append("00");
			/* Dagafjoldi seinna vanskilagjalds - 2 */
			buffer.append("00");
			/* Vanskilagjaldskodi - 1 */
			buffer.append(" ");
			/* Annar kostnadur - 11 */
			buffer.append("00000000000");// bfm.getOtherCost(number));
			/* Annar vanskilakostnadur - 11 */
			buffer.append("00000000000");// bfm.getOtherOverdueCost(number));
			/* Drattarvaxtaprosenta - 7 */
			buffer.append("0000000");
			/* Drattarvaxtaregla - 1 */
			buffer.append("0");
			/* Drattarvaxtastofnskodi - 1 */
			buffer.append("0");
			/* Gengistegund - 1 */
			buffer.append("0");
			/* Mynt - 3 */
			buffer.append("000");
			/* Gengisbanki - 2 */
			buffer.append("00");
			/* Gengiskodi - 1 */
			buffer.append("0");
			/* Greidslukodi - 1 */
			buffer.append(" ");
			/* Fyrri afslattur - 11 */
			buffer.append("00000000000");
			/* Seinni afslattur - 11 */
			buffer.append("00000000000");
			/* Dagafjoldi fyrri afslattar - 2 */
			buffer.append("00");
			/* Dagafjoldi seinni afslattar - 2 */
			buffer.append("00");
			/* Afslattarkodi - 1 */
			buffer.append("0");
			/* Innborgunarkodi - 1 */
			buffer.append("1");
			/* Birtingarkodi - 1 */
			buffer.append("1");
			/* URL - 200 */
			buffer.append(emptyString);
			buffer.append("\n");

		}

		String totalString = String.valueOf(totalAmount);
		if (totalString.length() < 15)
			totalString = zeroString.substring(0, 15 - totalString.length())
					+ totalString;

		// "lokafaersla" begins
		/* Kt_krofuhafa - 10 */
		buffer.append(claimantSSN);// "5904002890"
		/* autt - 4 */
		buffer.append(emptyString.substring(0, 4));
		/* Faerslugerd - 1 */
		buffer.append("L");// the type of the last line is: "lokafaersla" (L)
		/* autt - 31 */
		buffer.append(emptyString.substring(0, 31));
		/* Fjoldi - 6 */
		buffer.append(numberOfClaims);
		/* Upphaed 15 */
		buffer.append(totalString);
		/* autt - 39 */
		buffer.append(emptyString.substring(0, 39));
		// "lokafaersla" ends

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					FILE_NAME, false));
			writer.write(buffer.toString());
			writer.close();

			BufferedReader reader = new BufferedReader(
					new FileReader(FILE_NAME));
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (batchName == null) {
			batchName = Integer.toString(batchNumber);
		}
		
		sendCreateClaimsRequest(bfm);
	}

	public void getClaimStatusFromBank(int batchNumber, BankInfo info,
			java.util.Date from, java.util.Date to) {
		BankFileManager bfm = new BankInvoiceFileManager(info);

		getClaimStatusFromBank(batchNumber, bfm, from, to);
	}

	public void getClaimStatusFromBank(int batchNumber, int groupId,
			java.util.Date from, java.util.Date to) {
		BankFileManager bfm = new BankInvoiceFileManager(groupId);

		getClaimStatusFromBank(batchNumber, bfm, from, to);
	}

	private void getClaimStatusFromBank(int batchNumber, BankFileManager bfm,
			java.util.Date from, java.util.Date to) {
		String response = sendGetClaimStatusRequest(bfm, new IWTimestamp(from)
				.getDateString("yyyyMMdd"), new IWTimestamp(to)
				.getDateString("yyyyMMdd"));
		String number = response.substring(6, 12);
		String faerslugerd = response.substring(20, 21);

		bfm.setInvoiceStatus(faerslugerd, Integer.parseInt(number));

	}

	public void deleteClaim(BankInfo info, int claimNumber,
			java.util.Date dueDate, String payersSSN) {
		BankFileManager bfm = new BankInvoiceFileManager(info);
		deleteClaim(bfm, claimNumber, dueDate, payersSSN);
	}

	public void deleteClaim(int groupId, int claimNumber,
			java.util.Date dueDate, String payersSSN) {
		BankFileManager bfm = new BankInvoiceFileManager(groupId);
		deleteClaim(bfm, claimNumber, dueDate, payersSSN);
	}

	private void deleteClaim(BankFileManager bfm, int claimNumber,
			java.util.Date dueDate, String payersSSN) {

	}
	
	/**
	 * Sends a http multipart post method to KBBanki to create the claims
	 * 
	 * @param bfm
	 * @param groupId
	 * @return
	 */
	private void sendCreateClaimsRequest(BankFileManager bfm) {
/*		HttpClient client = new HttpClient();
		MultipartPostMethod post = new MultipartPostMethod(POST_METHOD);
		File file = new File(FILE_NAME);
		
		System.out.println("!!!kb:");
		System.out.println("username = " + bfm.getUsername());
		System.out.println("password = " + bfm.getPassword());
		
		try {
			post.addParameter("cguser", bfm.getUsername());// "IK66TEST"
			post.addParameter("cgpass", bfm.getPassword());// "KBIK66"
			post.addParameter("cgutli", "0");
			post.addParameter("cgskra", file);
			post.setDoAuthentication(false);
			client.executeMethod(post);

			System.out.println("responseString: "
					+ post.getResponseBodyAsString());

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return post;*/
		
		PostMethod filePost = new PostMethod(POST_METHOD);
		File file = new File(FILE_NAME);
		
		filePost.getParams().setBooleanParameter(
				HttpMethodParams.USE_EXPECT_CONTINUE, true);

		try {
			StringPart userPart = new StringPart("cguser", bfm.getUsername());
			StringPart pwdPart = new StringPart("password", bfm.getPassword());
			StringPart clubssnPart = new StringPart("cgclaimBatchName", batchName);
			FilePart filePart = new FilePart("cgskra", file);
			
			Part[] parts = { userPart, pwdPart, clubssnPart, filePart };
			filePost.setRequestEntity(new MultipartRequestEntity(parts,
					filePost.getParams()));
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(
					5000);

			int status = client.executeMethod(filePost);
			if (status == HttpStatus.SC_OK) {
				System.out.println("Upload complete, response="
						+ filePost.getResponseBodyAsString());
			} else {
				System.out.println("Upload failed, response="
						+ HttpStatus.getStatusText(status));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			filePost.releaseConnection();
		}

	}

	/**
	 * Sends a http post method to KBBanki to get the status of claims
	 * 
	 * @param bfm
	 * @param groupId
	 * @return
	 */
	private String sendGetClaimStatusRequest(BankFileManager bfm,
			String fromDate, String toDate) {
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(POST_ANSWER_METHOD);
		String response = new String();
		try {
			post.addParameter("cguser", bfm.getUsername());
			post.addParameter("cgpass", bfm.getPassword());
			// post.addParameter("cgbnum", "64014");
			post.addParameter("cgdfra", fromDate);// "20030701"
			post.addParameter("cgdtil", toDate);// "20040823"
			post.addParameter("cgvisi", bfm.getClaimantsAccountId());
			post.addParameter("cgSvarskra", "on");
			client.executeMethod(post);

			response = post.getResponseBodyAsString();

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		// System.out.println("response: " + response);
		return response;
	}
}
