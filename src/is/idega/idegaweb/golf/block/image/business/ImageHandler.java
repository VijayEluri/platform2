package is.idega.idegaweb.golf.block.image.business;

import is.idega.idegaweb.golf.block.image.data.ImageEntity;
import is.idega.idegaweb.golf.block.image.data.ImageEntityHome;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

import com.idega.data.IDOLegacyEntity;
import com.idega.data.IDOLookup;
import com.idega.io.ImageSave;
import com.idega.presentation.IWContext;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;


/**
 * Title: ImageHandler
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company: idega
 * @author Eirikur Hrafnsson
 * @version 1.0
 *
 * Must seperate reading the image and resizing it for streams in the next version
 */

public class ImageHandler{

private PlanarImage originalImage = null;
private PlanarImage modifiedImage = null;
//private ImageDisplay canvas = null;
private boolean state = false;
private int imageId = -1;
private int modifiedImageId = -1;
private String contentType = null;
private String imageName = null;
private int width;
private int height;
private int scale = 1;
private int modifiedWidth = -1;
private int modifiedHeight = -1;
private float quality = 0.75f;
private boolean keepProportions = false;
private int brightness = 30;
private KernelJAI kernel;
private float sum = 9.0F;
private String modifiedImageURL="";
private int modifiedsize = 0;
private int modifiedImageCounter=1;

public ImageHandler( int imageId ) throws Exception{
  setImageId(imageId);
  getImageFromDatabase();
  updateOriginalInfo();
  setModifiedImageAsOriginal();
}

public ImageHandler( String fileName ) throws Exception{
  getImageFromFile(fileName);
  setModifiedImageAsOriginal();
}

public ImageHandler( PlanarImage originalImage, int ParentId ) throws Exception{
  setImageId(ParentId);
  setOriginalImage(originalImage);
  setModifiedImageAsOriginal();
}

//crappy constructor fix this!
public ImageHandler( ImageEntity imageEntity ) throws Exception{
  setImageId( imageEntity.getID() );
  getImageFromDatabase();
  updateOriginalInfo();
  setModifiedImageAsOriginal();
}

private void getImageFromFile(String fileName) throws Exception{

  File f = new File(fileName);

  if ( f.exists() && f.canRead() ) {
    originalImage = JAI.create("fileload", fileName);
  } else {
      System.err.println("The image \""+fileName+"\" does not exist or can't be read");
  }

}

private void getImageFromDatabase() throws Exception{
  //BlobWrapper wrapper = imageInfo.getImageValue();
/* BlobWrapper wrapper = new BlobWrapper(imageInfo,"image_value");
   if( wrapper== null)   System.out.println("ImageHandler: BlobWrapper is NULL!");
  BlobInputStream inputStream = wrapper.getBlobInputStream();
*/

  Connection Conn = null;
  Statement Stmt;
  ResultSet RS;
  InputStream inputStream = null;

  Conn = ((IDOLegacyEntity)IDOLookup.instanciateEntity(ImageEntity.class)).getConnection();
  Stmt = Conn.createStatement();
  RS = Stmt.executeQuery("select image_value from image where image_id='"+getImageId()+"'");

  while(RS.next()){
      inputStream = RS.getBinaryStream("image_value");
  }

  modifiedsize = inputStream.available();
  BufferedInputStream bufStream = getBufferedInputStream(inputStream);
  MemoryCacheSeekableStream memStream = getMemoryCacheSeekableStream(bufStream);
  originalImage = getPlanarImageFromStream(memStream);
  System.out.println("ImageHandler: After JAI.create!");

  setWidth(originalImage.getWidth());
  setHeight(originalImage.getHeight());

  System.out.println("ImageHandler: Before closing memStream");
  memStream.close();
  System.out.println("ImageHandler: Before closing bufferstream");
  bufStream.close();
  System.out.println("ImageHandler: Before closing inputstream");
  inputStream.close();//closes the blobinputstream and closes misc stmt and connections

  if( RS!=null ) RS.close();
  if( Stmt!=null ) Stmt.close();
  if( Conn!=null ) ((IDOLegacyEntity)IDOLookup.instanciateEntity(ImageEntity.class)).freeConnection(Conn);

  System.out.println("ImageHandler: DONE!");

}

protected void updateOriginalInfo() throws SQLException{
  ImageEntity imageInfo = (ImageEntity)((ImageEntityHome)IDOLookup.getHomeLegacy(ImageEntity.class)).findByPrimaryKeyLegacy( imageId );
  setContentType( imageInfo.getContentType() );
  setImageName( imageInfo.getName() );
  imageInfo.setWidth(Integer.toString(originalImage.getWidth()));
  imageInfo.setHeight(Integer.toString(originalImage.getHeight()));
  imageInfo.setSize(modifiedsize);
 // imageInfo.update();
}

private synchronized BufferedInputStream getBufferedInputStream(InputStream inputStream){
  return (new BufferedInputStream(inputStream));
}

private synchronized MemoryCacheSeekableStream getMemoryCacheSeekableStream(BufferedInputStream bufStream){
  return (new MemoryCacheSeekableStream(bufStream));
}

private synchronized PlanarImage getPlanarImageFromStream(MemoryCacheSeekableStream memStream){
  return (JAI.create("stream", memStream));
}

protected void setOriginalImage(PlanarImage originalImage){
  this.originalImage = originalImage;
  setWidth(originalImage.getWidth());
  setHeight(originalImage.getHeight());
}

private void setImageId( int imageId ){
  this.imageId = imageId;
}

protected int getImageId(){
  return this.imageId;
}

protected int getOriginalImageId(){
  return getImageId();
}

protected void setBrightness( int brightness ){
  this.brightness = brightness;
}

private int getBrightness(){
  return this.brightness;
}

private void setModifiedImageId( int modifiedImageId ){
  this.modifiedImageId = modifiedImageId;
}

public int getModifiedImageId(){
  return this.modifiedImageId;
}

protected void setContentType( String contentType ){
  this.contentType = contentType;
}

protected String getContentType(){
  return this.contentType;
}

protected void setImageName( String imageName ){
  this.imageName = imageName;
}

protected String getImageName(){
  return this.imageName;
}


protected void setWidth( int width ){
  this.width = width;
}

protected int getWidth(){
  return this.width;
}

protected void setHeight( int height ){
  this.height = height;
}

protected int getHeight(){
  return this.height;
}

protected void setModifiedWidth( int modifiedWidth ){
  this.modifiedWidth = modifiedWidth;
}

/*
*@todo make this protected and fix in ImageViewer
*/
public int getModifiedWidth(){
  return this.modifiedWidth;
}

protected void setModifiedHeight( int modifiedHeight ){
  this.modifiedHeight = modifiedHeight;
}

/*
*@todo make this protected and fix in ImageViewer
*/
public int getModifiedHeight(){
  return this.modifiedHeight;
}

protected boolean keepProportions(){
  return this.keepProportions;
}

protected void keepProportions(boolean keepProportions){
  this.keepProportions = keepProportions;
}

private void setModifiedImageAttributes(){

  int tempWidth = getModifiedWidth();
  int tempHeight = getModifiedHeight();

  //if tempWidth and tempHeight =-1 then we are scaling
  if ( (tempWidth==-1) && (tempHeight ==-1) ){
    setModifiedWidth(getWidth()*getScale());
    setModifiedHeight(getHeight()*getScale());
  }
  else if( (tempWidth!=-1) && (tempHeight !=-1) ){//changed both
    setModifiedWidth(tempWidth);
    setModifiedHeight(tempHeight);
  }
  else {//we changed either the width or height

    if ( tempWidth == -1 ){//missing width
      if ( keepProportions() ) setModifiedWidth( (getWidth()*tempHeight)/getHeight());
      else setModifiedWidth( getWidth() );
      setModifiedHeight( tempHeight );
    }
    if( tempHeight == -1 ){//missing height
      if ( keepProportions() ) setModifiedHeight( (getHeight()*tempWidth)/getWidth() );
      else setModifiedHeight( getHeight() );
      setModifiedWidth( tempWidth );
    }

  }
}


protected void resizeImage() throws Exception{
  ParameterBlock pb = new ParameterBlock();
  //pb.addSource(originalImage);
  pb.addSource(getModifiedImage());
  RenderableImage ren = JAI.createRenderable("renderable", pb);
  setModifiedImageAttributes();
  RenderingHints effect = new RenderingHints(RenderingHints.KEY_INTERPOLATION , RenderingHints.VALUE_INTERPOLATION_BILINEAR );
  setModifiedImage((PlanarImage)ren.createScaledRendering(getModifiedWidth(), getModifiedHeight(), effect ));
 /*
  //try some rendering options
  //RenderingHints effect = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  //RenderingHints effect = new RenderingHints(RenderingHints.KEY_DITHERING , RenderingHints.VALUE_DITHER_ENABLE );
  // RenderingHints effect = new RenderingHints(RenderingHints.KEY_DITHERING , RenderingHints.VALUE_DITHER_DISABLE );
*/

}

protected void convertModifiedImageToGrayscale(){
  setModifiedImage(convertColorToGray(getModifiedImage(), getBrightness() ));
}

protected void convertModifiedImageToColor(){
  setModifiedImage(convertGrayToColor(getModifiedImage(), getBrightness() ));
}

protected int getScale(){
  return this.scale;
}

protected void setScale(int scale){
  this.scale = scale;
}

protected void setModifiedImage( PlanarImage modifiedImage ){
  this.modifiedImage = modifiedImage;
  setModifiedWidth(modifiedImage.getWidth());
  setModifiedHeight(modifiedImage.getHeight());
}

protected void setModifiedImageAsOriginal(){
  this.setModifiedImage(originalImage);
  setModifiedWidth(originalImage.getWidth());
  setModifiedHeight(originalImage.getHeight());
}

protected PlanarImage getModifiedImage(){
  return this.modifiedImage;
}

protected PlanarImage getOriginalImage(){
  return this.originalImage;
}
/*
*  1.0 best quality 0.75 high quality 0.5  medium quality  0.25 low quality 0.10 crappy quality
*
*/
protected void setQuality(float quality){
  this.quality = quality;
}

protected float getQuality(){
  return this.quality;
}

public com.idega.presentation.Image getModifiedImageAsImageObject(IWContext modinfo) throws Exception{
  if( modifiedImageCounter != 1 ) ImageBusiness.deleteImageFile(modifiedImageURL);
  String seperator = System.getProperty("file.separator");
  modifiedImageURL = modinfo.getServletContext().getRealPath(seperator)+seperator+"pics"+seperator+modinfo.getSession().getId()+"ModifiedImagetemp"+modifiedImageCounter+".jpg";
  writeModifiedImageToFile(modifiedImageURL);//temporary storage
  com.idega.presentation.Image image = new com.idega.presentation.Image(modifiedImageURL,getImageName(),getModifiedWidth(),getModifiedHeight());
  modifiedImageCounter++;
return image;
}



protected void writeModifiedImageToDatabase(boolean update) throws Exception{
  writeModifiedImageToFile(modifiedImageURL);//temporary storage
  InputStream input = new FileInputStream(modifiedImageURL);
  modifiedsize = input.available();
  if(update){
    ImageSave.saveImageToDataBase(getImageId(),-1,input,getContentType(),getImageName(),Integer.toString(getModifiedWidth()),Integer.toString(getModifiedHeight()), false);
  }
  else ImageSave.saveImageToDataBase(-1,getImageId(),input,getContentType(),getImageName(),Integer.toString(getModifiedWidth()),Integer.toString(getModifiedHeight()), true);
}

protected void writeModifiedImageToFile(String filename) throws Exception{

  if ( filename.equalsIgnoreCase("")) filename = getImageName();
  OutputStream output = new FileOutputStream(filename);

  ImageEncoder imageEncoder;

  if( getContentType().indexOf("bmp") != -1 ){
    imageEncoder = ImageCodec.createImageEncoder("BMP", output, null);
  }
  else{
    JPEGEncodeParam jpgParam = new JPEGEncodeParam();
    jpgParam.setQuality(getQuality());
    imageEncoder = ImageCodec.createImageEncoder("JPEG",output,jpgParam);
  }

  PlanarImage modified = getModifiedImage();

  if ( modified != null) imageEncoder.encode( getModifiedImage() );
  else System.out.println("getModifiedImage() returned null!");

  output.flush();
  output.close();
}


protected static PlanarImage convertColorToGray(PlanarImage src, int brightness) {
   PlanarImage dst = null;
   double b = (double) brightness;
   double[][] matrix = {
                         { .114D, 0.587D, 0.299D, b },
                           { .114D, 0.587D, 0.299D, b },
                           { .114D, 0.587D, 0.299D, b }
                    /*      { .114D, 0.200D, 0.100D, b },//red
                           { 0.05D, 0.700D, 0.050D, b },//green
                           { .114D, 0.200D, 0.100D, b }//blue
                           //{HUE,SATURATION,CONTRAST,BRIGHTNESS)*/
                       };
    if ( src != null ) {
       ParameterBlock pb = new ParameterBlock();
       pb.addSource(src);
       pb.add(matrix);
       dst = JAI.create("bandcombine", pb, null);
   }
    return dst;
}

 /** produce a 3 band image from a single band gray scale image */
 protected static PlanarImage convertGrayToColor(PlanarImage src, int brightness) {
   PlanarImage dst = null;
   double b = (double) brightness;
   double[][] matrix = {
                          { 1.0D, b },
                          { 1.0D, b },
                          { 1.0D, b }
                       };

   if ( src != null ) {
       int nbands = src.getSampleModel().getNumBands();

// MUST check color model here
       if ( nbands == 1 ) {
           ParameterBlock pb = new ParameterBlock();
           pb.addSource(src);
           pb.add(matrix);
           dst = JAI.create("bandcombine", pb, null);
       } else {
           dst = src;
       }
   }

   return dst;
}

  protected void embossModifiedImage(){
    loadKernel(8);
    setModifiedImage( convolve(getModifiedImage(),8));
  }

  protected void blurModifiedImage(){
    loadKernel(9);
    setModifiedImage( convolve(getModifiedImage(),9));
  }

  protected void sharpenModifiedImage(){
    loadKernel(2);
    setModifiedImage( convolve(getModifiedImage(),2));
  }

  protected void invertModifiedImage(){
    setModifiedImage( invert(getModifiedImage()));
  }

  protected PlanarImage invert(PlanarImage source) {

    ParameterBlock pb = new ParameterBlock();
    pb.addSource(source);
    RenderableImage ren = JAI.createRenderable("renderable", pb);

    pb = new ParameterBlock();
    pb.addSource(ren);
    RenderableImage inv = JAI.createRenderable("invert", pb);

    PlanarImage dst = (PlanarImage)inv.createScaledRendering(source.getWidth(), source.getHeight(), null);

   return dst;
  }

  private PlanarImage convolve(PlanarImage source, int k) {
     ParameterBlock pb = new ParameterBlock();
     pb.addSource(source);
     pb.add(kernel);
     PlanarImage target = JAI.create("convolve", pb, null);
      // emboss (special case)
     if ( k == 8 ) {
         double[] constants = new double[3];
         constants[0] = 128.0;
         constants[1] = 128.0;
         constants[2] = 128.0;
         pb = new ParameterBlock();
         pb.addSource(target);
         pb.add(constants);
         target = JAI.create("addconst", pb, null);
     }

    return target;
  }

 private void loadKernel(int choice) {

        float[] data = new float[9];

        switch( choice ) {
            case 0:
                data[0] = 0.0F; data[1] =-1.0F; data[2] = 0.0F;
                data[3] =-1.0F; data[4] = 5.0F; data[5] =-1.0F;
                data[6] = 0.0F; data[7] =-1.0F; data[8] = 0.0F;
            break;

            case 1:
                data[0] =-1.0F; data[1] =-1.0F; data[2] =-1.0F;
                data[3] =-1.0F; data[4] = 9.0F; data[5] =-1.0F;
                data[6] =-1.0F; data[7] =-1.0F; data[8] =-1.0F;
            break;
//sharpen very good
            case 2:
                data[0] = 1.0F; data[1] =-2.0F; data[2] = 1.0F;
                data[3] =-2.0F; data[4] = 5.0F; data[5] =-2.0F;
                data[6] = 1.0F; data[7] =-2.0F; data[8] = 1.0F;
            break;

            case 3:
                data[0] =-1.0F; data[1] = 1.0F; data[2] =-1.0F;
                data[3] = 1.0F; data[4] = 1.0F; data[5] = 1.0F;
                data[6] =-1.0F; data[7] = 1.0F; data[8] =-1.0F;
            break;

            case 4:
                data[0] =-1.0F; data[1] =-1.0F; data[2] =-1.0F;
                data[3] =-1.0F; data[4] = 8.0F; data[5] =-1.0F;
                data[6] =-1.0F; data[7] =-1.0F; data[8] =-1.0F;
            break;

            case 5:
                data[0] = 0.0F; data[1] =-1.0F; data[2] = 0.0F;
                data[3] =-1.0F; data[4] = 4.0F; data[5] =-1.0F;
                data[6] = 0.0F; data[7] =-1.0F; data[8] = 0.0F;
            break;

            case 6:
                data[0] = 1.0F; data[1] = 1.0F; data[2] = 1.0F;
                data[3] = 1.0F; data[4] = 1.0F; data[5] = 1.0F;
                data[6] = 1.0F; data[7] = 1.0F; data[8] = 1.0F;
            break;

            case 7:
                data[0] = 1.0F; data[1] = 2.0F; data[2] = 1.0F;
                data[3] = 2.0F; data[4] = 4.0F; data[5] = 2.0F;
                data[6] = 1.0F; data[7] = 2.0F; data[8] = 1.0F;
            break;
//emboss
            case 9:
                data[0] =-1.0F; data[1] =-2.0F; data[2] = 0.0F;
                data[3] =-2.0F; data[4] = 0.0F; data[5] = 2.0F;
                data[6] = 0.0F; data[7] = 2.0F; data[8] = 1.0F;
            break;
//tilraun til blurs
            case 8:
              data[0] = 1/9F; data[1] = 1/9F; data[2] = 1/9F;
              data[3] = 1/9F; data[4] = 1/9F; data[5] = 1/9F;
              data[6] = 1/9F; data[7] = 1/9F; data[8] = 1/9F;
            break;

        }
        normalize(data);
        kernel = new KernelJAI(3, 3, data);
}

private void normalize(float[] data) {
   sum = 0.0F;
    for ( int i = 0; i < data.length; i++ ) {
       sum += data[i];
   }
    if ( sum > 0.0F ) {
       for ( int i = 0; i < data.length; i++ ) {
          data[i] = data[i] / sum;
       }
   } else {
       sum = 1.0F;
   }
}



}//end of class

