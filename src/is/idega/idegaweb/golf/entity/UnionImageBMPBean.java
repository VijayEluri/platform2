//idega 2000 - Laddi

package is.idega.idegaweb.golf.entity;

//import java.util.*;
import java.sql.SQLException;

import com.idega.data.GenericEntity;

public class UnionImageBMPBean extends GenericEntity implements UnionImage{

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("union_id", "Kl�bbur", true, true, "java.lang.Integer");
		//addAttribute("image_id", "Mynd", true, true, "java.lang.Integer");
                      addAttribute("image_id","Image",false,false,"java.lang.Integer","one-to-many","com.idega.jmodule.image.data.ImageEntity");

	}

	public String getEntityName(){
		return "union_image";
	}

	public int getUnionId(){
		return getIntColumnValue("union_id");
	}

	public void setUnionId(int union_id){
		setColumn("union_id", union_id);
	}

	public int getImageId(){
		return getIntColumnValue("image_id");
	}

	public void setImageId(int image_id){
		setColumn("image_id", image_id);
	}

}
