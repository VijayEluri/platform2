//idega 2000 - eiki

package is.idega.idegaweb.golf.entity;

//import java.util.*;
import com.idega.data.GenericEntity;

public class PaymentTypeBMPBean extends GenericEntity implements PaymentType{

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("name","Grei�slumi�ill",true,true,"java.lang.String");
		addAttribute("extra_info","Athugasemd",true,true,"java.lang.String");
                addAttribute("default_installment_nr","Grei�slufj�ldi",true,true,"java.lang.Integer");

	}

	public String getEntityName(){
		return "payment_type";
	}

	public String getName(){
		return getStringColumnValue("name");
	}

	public void setName(String name){
		setColumn("name", name);
	}

	public String getExtraInfo(){
		return getStringColumnValue("extra_info");
	}

	public void setExtraInfo(String extra_info){
		setColumn("extra_info", extra_info);
	}

        public int getDefaultInstallNr(){
		return getIntColumnValue("default_installment_nr");
	}

	public void setDefaultInstallNr(Integer default_installment_nr){
		setColumn("default_installment_nr",default_installment_nr);
	}




}
