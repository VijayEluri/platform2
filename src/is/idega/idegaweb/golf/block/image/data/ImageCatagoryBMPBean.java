package is.idega.idegaweb.golf.block.image.data;

import java.sql.*;
import com.idega.data.*;


public class ImageCatagoryBMPBean extends GenericEntity implements ImageCatagory{

	public ImageCatagoryBMPBean(){
		super();
	}

	public ImageCatagoryBMPBean(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("image_catagory_name","Image Category Name",true,true,"java.lang.String");
                addAttribute(getParentIdColumnName(),"Image Category parent",true,true,"java.lang.Integer");
	}

	public String getEntityName(){
		return "image_catagory";
	}


         public void setDefaultValues() {
          this.setParentId(-1);
        }

        public void setImageCatagoryName(String name){
          setColumn("image_catagory_name",name);
        }

	public String getImageCatagoryName() {
          return (String) getColumnValue("image_catagory_name");
	}

        public String getName(){
          return getImageCatagoryName();
        }

        public static String getParentIdColumnName(){
          return "parent_id";
        }

        public void setParentId(int parent_id) {
          setColumn(getParentIdColumnName(),new Integer(parent_id));
        }

        public int getParentId() {
          return getIntColumnValue(getParentIdColumnName());
        }


}
