package com.idega.block.building.data;


public interface ApartmentType extends BuildingEntity
{
 public com.idega.block.building.data.ApartmentCategory getApartmentCategory();
 public int getApartmentCategoryId();
 public java.util.Collection getApartments();
 public float getArea();
 public boolean getBalcony();
 public boolean getBathRoom();
 public java.lang.String getExtraInfo();
 public int getFloorPlanId();
 public boolean getFurniture();
 public int getImageId();
 public java.lang.String getInfo();
 public boolean getKitchen();
 public boolean getLoft();
 public java.lang.String getName();
 public java.lang.String getAbbreviation();
 public int getRent();
 public int getRoomCount();
 public boolean getStorage();
 public boolean getStudy();
 public void setApartmentCategoryId(int p0);
 public void setApartmentCategoryId(java.lang.Integer p0);
 public void setArea(float p0);
 public void setArea(java.lang.Float p0);
 public void setBalcony(boolean p0);
 public void setBathRoom(boolean p0);
 public void setExtraInfo(java.lang.String p0);
 public void setFloorPlanId(int p0);
 public void setFloorPlanId(java.lang.Integer p0);
 public void setFurniture(boolean p0);
 public void setImageId(int p0);
 public void setImageId(java.lang.Integer p0);
 public void setInfo(java.lang.String p0);
 public void setKitchen(boolean p0);
 public void setLoft(boolean p0);
 public void setAbbreviation(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setRent(int p0);
 public void setRent(java.lang.Integer p0);
 public void setRoomCount(int p0);
 public void setRoomCount(java.lang.Integer p0);
 public void setStorage(boolean p0);
 public void setStudy(boolean p0);
}
