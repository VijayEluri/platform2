package is.idega.idegaweb.travel.service.hotel.presentation;

import is.idega.idegaweb.travel.service.hotel.data.HotelHome;
import is.idega.idegaweb.travel.service.hotel.data.Hotel;
import is.idega.idegaweb.travel.service.hotel.data.RoomType;
import is.idega.idegaweb.travel.service.hotel.data.RoomTypeHome;

import java.rmi.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.*;

import com.idega.block.media.presentation.*;
import com.idega.block.trade.stockroom.business.*;
import com.idega.block.trade.stockroom.data.*;
import com.idega.business.*;
import com.idega.data.*;
import com.idega.idegaweb.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.util.*;
import is.idega.idegaweb.travel.data.*;
import is.idega.idegaweb.travel.presentation.*;
import is.idega.idegaweb.travel.service.hotel.business.*;
import is.idega.idegaweb.travel.service.presentation.*;

/**
 * <p>Title: idega</p>
 * <p>Description: software</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega software</p>
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class HotelDesigner extends TravelManager implements DesignerForm {

  private IWResourceBundle _iwrb;
  private Supplier _supplier;
  private Service _service;
  private Product _product;
  private Timeframe _timeframe;
  private Collection _roomTypes;

  String NAME_OF_FORM = ServiceDesigner.NAME_OF_FORM;
  String ServiceAction = ServiceDesigner.ServiceAction;

  private String PARAMETER_IS_UPDATE       = "isTourUpdate";
  private String PARAMETER_TIMEFRAME_ID    = "td_timeframeId";
  private String PARAMETER_NAME            = "hd_par_name";
  private String PARAMETER_DESCRIPTION     = "hd_par_desc";
  private String PARAMETER_NUMBER          = "hd_par_num";
  private String PARAMETER_NUMBER_OF_UNITS = "hd_par_num_un";
  private String PARAMETER_MAX_PER_UNIT    = "hd_par_max_un";
  private String PARAMETER_DISCOUNT_TYPE   = "hd_par_disc";
  private String PARAMETER_DESIGN_IMAGE_ID = "hd_par_des_img_id";
  private String PARAMETER_USE_IMAGE_ID    = "hd_par_use_img_id";
  private String PARAMETER_PICKUP_ID       = "hd_par_pick_id";
  private String PARAMETER_ROOM_TYPE_ID    = "hd_par_rt_id";


  public HotelDesigner(IWContext iwc) throws Exception {
    init(iwc);
  }

  private void init( IWContext iwc ) throws Exception {
    super.main( iwc );
    _iwrb = super.getResourceBundle();
    _supplier = super.getSupplier();
		RoomTypeHome rth = (RoomTypeHome) IDOLookup.getHome(RoomType.class);
		_roomTypes = rth.findAll();
  }

  private boolean setupData(int serviceId) {
    try {
      ServiceHome sHome = (ServiceHome) IDOLookup.getHome(Service.class);
      ProductHome pHome = (ProductHome) IDOLookup.getHome(Product.class);
      _service = sHome.findByPrimaryKey(new Integer(serviceId));
      _product = pHome.findByPrimaryKey(new Integer(serviceId));
      _timeframe = _product.getTimeframe();
      
      return true;
    }catch (Exception e) {
      return false;
    }
  }

  public int handleInsert(IWContext iwc) throws RemoteException {
    String sServiceId = iwc.getParameter( PARAMETER_IS_UPDATE );
    int serviceId = -1;
    if ( sServiceId != null ) {
      serviceId = Integer.parseInt( sServiceId );
    }
    setupData(serviceId);

    String name = iwc.getParameter( PARAMETER_NAME );
    String number = iwc.getParameter( PARAMETER_NUMBER );
    String description = iwc.getParameter( PARAMETER_DESCRIPTION );
    if ( description == null ) {
      description = "";
    }
    String imageId = iwc.getParameter( PARAMETER_DESIGN_IMAGE_ID );
    String numberOfUnits = iwc.getParameter( PARAMETER_NUMBER_OF_UNITS );
    String maxPerUnit = iwc.getParameter(PARAMETER_MAX_PER_UNIT );
    String useImageId = iwc.getParameter( PARAMETER_USE_IMAGE_ID );
    String discountType = iwc.getParameter( PARAMETER_DISCOUNT_TYPE );
		String[] pickupIds = iwc.getParameterValues( PARAMETER_PICKUP_ID );
		String roomTypeId = iwc.getParameter( PARAMETER_ROOM_TYPE_ID );

    int iDiscountType = com.idega.block.trade.stockroom.data.ProductBMPBean.DISCOUNT_TYPE_ID_PERCENT;
    if ( discountType != null ) {
      iDiscountType = Integer.parseInt( discountType );
    }
    
    int iRoomTypeId = -1;
    try {
    	iRoomTypeId = Integer.parseInt(roomTypeId);
    }catch (Exception e) {
    }

    Integer iImageId = null;
    if ( imageId != null ) {
      if ( !imageId.equals( "-1" ) ) {
        iImageId = new Integer( imageId );
      }
    }

    int iNumberOfUnits = 0;
    if (numberOfUnits != null) {
      try {
        iNumberOfUnits = Integer.parseInt(numberOfUnits);
      }catch (NumberFormatException n){
      }
    }

		int iMaxPerUnit = 0;
		if (maxPerUnit != null) {
			try {
				iMaxPerUnit = Integer.parseInt( maxPerUnit);
			}catch (NumberFormatException n) {
			}
		}

    int returner = -1;

    HotelBusiness hb = (HotelBusiness) IBOLookup.getServiceInstance(iwc, HotelBusiness.class);

    try {
      if ( serviceId == -1 ) {
//        hb.setTimeframe( activeFromStamp, activeToStamp, yearly );
        returner = hb.createHotel(_supplier.getID(), iImageId, name, number, description, iNumberOfUnits,iMaxPerUnit, true, iDiscountType, iRoomTypeId);
      } else {
        String timeframeId = iwc.getParameter( PARAMETER_TIMEFRAME_ID );
        if ( timeframeId == null ) {
          timeframeId = "-1";
        }
//        hb.setTimeframe( Integer.parseInt( timeframeId ), activeFromStamp, activeToStamp, yearly );
        returner = hb.updateHotel(serviceId, _supplier.getID(), iImageId, name, number, description, iNumberOfUnits, iMaxPerUnit, true, iDiscountType, iRoomTypeId);
        if ( useImageId == null ) {
          ProductEditorBusiness.getInstance().dropImage( _product, true );
        }
      }


	  	Service service = ((ServiceHome) IDOLookup.getHome(Service.class)).findByPrimaryKey(returner);
		  service.removeAllHotelPickupPlaces();
			if (pickupIds != null && returner > 0) {
        for (int i = 0; i < pickupIds.length; i++) {
          if (!pickupIds[i].equals("-1")) {
	          try{
	            ((is.idega.idegaweb.travel.data.PickupPlaceHome)com.idega.data.IDOLookup.getHome(PickupPlace.class)).findByPrimaryKey(new Integer(pickupIds[i])).addToService(service);
	//                service.addTo(((is.idega.idegaweb.travel.data.HotelPickupPlaceHome)com.idega.data.IDOLookup.getHome(HotelPickupPlace.class)).findByPrimaryKey(new Integer(hotelPickupPlaceIds[i])));
	          }catch (IDOAddRelationshipException sql) {
	          }catch (NumberFormatException nfe) {
	          }
	        }
				}
			}

    } catch ( Exception e ) {
      e.printStackTrace( System.err );
      //add("TEMP - Service EKKI sm��u�");
    }

    return returner;
  }

  public void finalizeCreation(IWContext iwc, Product product) throws RemoteException, FinderException {
  		getHotelBusiness(iwc).finalizeHotelCreation(product);
  }

  public Form getDesignerForm( IWContext iwc ) throws RemoteException, FinderException {
    return getDesignerForm(iwc, -1);
  }

  public Form getDesignerForm( IWContext iwc, int serviceId ) throws RemoteException, FinderException {
    boolean isDataValid = true;
    boolean isChangingFromOtherType = false;


    if ( serviceId != -1 ) {
      isDataValid = setupData( serviceId );
    } 


    Form form = new Form();
    form.setName( NAME_OF_FORM );
    Table table = new Table();
    form.add( table );

    if ( isDataValid ) {

      table.setWidth( "90%" );
      table.setAlignment("center");

      int row = 0;
      IWTimestamp stamp = IWTimestamp.RightNow();

      TextInput name = new TextInput( PARAMETER_NAME );
      name.setSize( 40 );
      TextArea description = new TextArea( PARAMETER_DESCRIPTION );
      description.setWidth( "50" );
      description.setHeight( "12" );
      TextInput number = new TextInput( PARAMETER_NUMBER );
      number.setSize( 20 );
      number.keepStatusOnAction();
      DropdownMenu locales = getProductBusiness(iwc).getLocaleDropDown( iwc );

      int currentYear = IWTimestamp.RightNow().getYear();

      TextInput numberOfUnits = new TextInput( PARAMETER_NUMBER_OF_UNITS );
      TextInput maxPerUnit = new TextInput( PARAMETER_MAX_PER_UNIT );

			DropdownMenu roomTypes = new DropdownMenu( _roomTypes, PARAMETER_ROOM_TYPE_ID );

      DropdownMenu discountType = new DropdownMenu( PARAMETER_DISCOUNT_TYPE );
      discountType.addMenuElement( com.idega.block.trade.stockroom.data.ProductBMPBean.DISCOUNT_TYPE_ID_AMOUNT, _iwrb.getLocalizedString( "travel.amount", "Amount" ) );
      discountType.addMenuElement( com.idega.block.trade.stockroom.data.ProductBMPBean.DISCOUNT_TYPE_ID_PERCENT, _iwrb.getLocalizedString( "travel.percent", "Percent" ) );

      PickupPlaceHome ppHome = (PickupPlaceHome) IDOLookup.getHome(PickupPlace.class);
      Collection pickupCollection = ppHome.findHotelPickupPlaces(this._supplier);
      List pps = ListUtil.convertCollectionToList(pickupCollection);
      SelectionBox pickups = new SelectionBox( pps );

      ++row;
      Text nameText = ( Text ) theBoldText.clone();
      nameText.setText( _iwrb.getLocalizedString( "travel.name_of_room", "Name of room" ) );
      table.add( nameText, 1, row );
      table.add( name, 2, row );

      ++row;
      Text numberText = ( Text ) theBoldText.clone();
      numberText.setText( _iwrb.getLocalizedString( "travel.number", "Number" ) );
      table.add( numberText, 1, row );
      table.add( number, 2, row );

      ++row;

      Text descText = ( Text ) theBoldText.clone();
      descText.setText( _iwrb.getLocalizedString( "travel.description", "Description" ) );
      Text imgText = ( Text ) theBoldText.clone();
      imgText.setText( _iwrb.getLocalizedString( "travel.image", "Image" ) );

      table.setVerticalAlignment( 1, row, "top" );
      table.setVerticalAlignment( 2, row, "top" );

      ++row;

      ImageInserter imageInserter = new ImageInserter( PARAMETER_DESIGN_IMAGE_ID );
      imageInserter.setHasUseBox( true, PARAMETER_USE_IMAGE_ID );
      String imageId = iwc.getParameter( PARAMETER_DESIGN_IMAGE_ID );
      if ( _service != null ) {
        _product = _service.getProduct();
        Hotel hotel = null;
        try {
          hotel = ((HotelHome) IDOLookup.getHome(Hotel.class)).findByPrimaryKey(_product.getPrimaryKey());
          int iNoUnits = hotel.getNumberOfUnits();
          int iMaxPerUnit = hotel.getMaxPerUnit();
          int iRoomTypeId = hotel.getRoomTypeId();
          
          if (iNoUnits >= 0 ) {
	          numberOfUnits.setContent(Integer.toString(iNoUnits));
          }
          if (iMaxPerUnit >= 0 ) {
	          maxPerUnit.setContent( Integer.toString(iMaxPerUnit));
          }
          if (iRoomTypeId > 0) {
          	roomTypes.setSelectedElement(iRoomTypeId);	
          }
        }catch (FinderException fe) {
          System.out.println("[HotelDesigner] HotelBean not available");
        }
        if ( imageId != null ) {
          imageInserter.setImageId( Integer.parseInt( imageId ) );
          imageInserter.setSelected( true );
        } else if ( _product.getFileId() != -1 ) {
          imageInserter.setImageId( _product.getFileId() );
          imageInserter.setSelected( true );
        }
        Collection pickupService = ppHome.findHotelPickupPlaces(_service);
        Iterator iter = pickupService.iterator();
        while (iter.hasNext()) {
          pickups.setSelectedElement(iter.next().toString());
        }
      }


      table.setVerticalAlignment( 1, row, "top" );
      table.setVerticalAlignment( 2, row, "top" );
      table.add( imgText, 1, row );
      table.add( imageInserter, 2, row );

      ++row;
      Text timeframeText = ( Text ) theBoldText.clone();
      timeframeText.setText( _iwrb.getLocalizedString( "travel.timeframe", "Timeframe" ) );
      Text tfFromText = ( Text ) smallText.clone();
      tfFromText.setText( _iwrb.getLocalizedString( "travel.from", "from" ) );
      Text tfToText = ( Text ) smallText.clone();
      tfToText.setText( _iwrb.getLocalizedString( "travel.to", "to" ) );
      Text tfYearlyText = ( Text ) smallText.clone();
      tfYearlyText.setText( _iwrb.getLocalizedString( "travel.yearly", "yearly" ) );

		  ++row;
		  Text roomTypeText = ( Text ) theBoldText.clone();
			roomTypeText.setText( _iwrb.getLocalizedString( "travel.room_type", "Room type" ) );
		//		HotelPickupPlace[] hpps = (HotelPickupPlace[]) coll.toArray(new HotelPickupPlace[]{});
			table.add(roomTypeText, 1, row);
		  table.add(roomTypes, 2, row);
			roomTypes.keepStatusOnAction();

      ++row;
      Text pickupText = ( Text ) theBoldText.clone();
      pickupText.setText( _iwrb.getLocalizedString( "travel.pickup", "Pickup" ) );
//      HotelPickupPlace[] hpps = (HotelPickupPlace[]) coll.toArray(new HotelPickupPlace[]{});
      pickups.setName(PARAMETER_PICKUP_ID);
      pickups.keepStatusOnAction();

      table.add( pickupText, 1, row );
      table.add( pickups, 2, row );

      table.setVerticalAlignment( 1, row, "top" );
      table.setVerticalAlignment( 2, row, "top" );

      ++row;
      Text noUnitsText = ( Text ) theBoldText.clone();
      noUnitsText.setText( _iwrb.getLocalizedString( "travel.number_of_units", "Number of units" ) );
      table.add( noUnitsText, 1, row );
      table.add( numberOfUnits, 2, row );

      ++row;
      Text maxPerUnitText = ( Text ) theBoldText.clone();
      maxPerUnitText.setText( _iwrb.getLocalizedString( "travel.maximum_passegers_per_room", "Maximum passengers per room" ) );
      table.add( maxPerUnitText, 1, row );
      table.add( maxPerUnit, 2, row );

      ++row;
      Text discountTypeText = ( Text ) theBoldText.clone();
      discountTypeText.setText( _iwrb.getLocalizedString( "travel.discount_type", "Discount type" ) );
      table.add( discountTypeText, 1, row );
      table.add( discountType, 2, row );

      ++row;
      table.mergeCells( 1, row, 2, row );
      table.setAlignment( 1, row, "right" );
      SubmitButton submit = new SubmitButton( _iwrb.getImage( "buttons/save.gif" ), ServiceDesigner.ServiceAction, ServiceDesigner.parameterCreate );
      table.add( submit, 1, row );

      table.setColumnAlignment( 1, "right" );
      table.setColumnAlignment( 2, "left" );

      if ( _service != null ) {
        Parameter par1 = new Parameter( PARAMETER_IS_UPDATE, Integer.toString( serviceId ) );
        par1.keepStatusOnAction();
        table.add( par1 );

        name.setContent( _product.getProductName( super.getLocaleId() ) );
        number.setContent( _product.getNumber() );
        description.setContent( _product.getProductDescription( super.getLocaleId() ) );


        discountType.setSelectedElement( Integer.toString( _product.getDiscountTypeId() ) );
      } else {
        discountType.setSelectedElement( Integer.toString( com.idega.block.trade.stockroom.data.ProductBMPBean.DISCOUNT_TYPE_ID_PERCENT ) );
      }
    } else {
      table.add( _iwrb.getLocalizedString( "travel.data_is_invalid", "Data is invalid" ) );
    }
    return form;
  }

	private HotelBusiness getHotelBusiness(IWContext iwc) throws RemoteException {
		return (HotelBusiness) IBOLookup.getServiceInstance(iwc, HotelBusiness.class);	
	}

}
