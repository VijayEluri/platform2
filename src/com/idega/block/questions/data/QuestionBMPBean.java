package com.idega.block.questions.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.CategoryEntity;
import com.idega.data.CategoryEntityBMPBean;
import com.idega.block.text.data.TxText;

/**
 * 
 * <p>Company: idegaweb </p>
 * @author aron
 * 
 *
 */
public class QuestionBMPBean extends CategoryEntityBMPBean implements Question{
	
	public final static String TABLE_NAME = "qa_question";
	public final static String QUESTION = "question_id";
	public final static String ANSWER = "answer_id";
	public final static String VALID = "valid";
	
	/**
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		 addAttribute(getIDColumnName());
		 addAttribute(QUESTION, "Question", true, true, Integer.class,MANY_TO_ONE,TxText.class);
		 addAttribute(ANSWER, "Answert", true, true, Integer.class,MANY_TO_ONE,TxText.class);
		 addAttribute(VALID,"Valid",true,true,Boolean.class);
	}

	
	/**
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return TABLE_NAME;
	}
	
	public int getQuestionID(){
		return this.getIntColumnValue(QUESTION);
	}
	
	public int getAnswerID(){
		return this.getIntColumnValue(ANSWER);
	}
	
	public void setQuestionID(int question){
		this.setColumn(QUESTION,question);
	}
	
	public void setAnswerID(int answer){
		this.setColumn(ANSWER,answer);
	}
	
	public boolean getValid(){
		return this.getBooleanColumnValue(VALID);
	}
	
	public void setValid(boolean valid){
		this.setColumn(VALID,valid);
	}
	
	public Collection ejbFindAllByCategory(int iCategory) throws FinderException{
		StringBuffer sql = new StringBuffer("select * from ").append(TABLE_NAME);
		sql.append(" where ").append(this.getColumnCategoryId()).append("=").append(iCategory);
		sql.append(" and ").append(VALID).append("='Y'");
		sql.append(" order by ").append(getIDColumnName());
		return this.idoFindPKsBySQL(sql.toString());
	} 
	
	
}
