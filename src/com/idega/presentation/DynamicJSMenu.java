package com.idega.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.presentation.text.Text;
/**
 * 
 * @author birna
 *
 * Creates a dynamic JavaScript menu using external
 * javaScript; coolmenus4.js 
 * see <a href="http://www.dhtmlcentral.com/projects/coolmenus/">http://www.dhtmlcentral.com/projects/coolmenus/</a>
 * 
 */
public class DynamicJSMenu extends PresentationObject {
	
	private PresentationObject _obj;
	
	IWContext iwc = new IWContext();
	
	String _menuObjectName = "oCMenu";
	/** a list of LinkMenu */
	private List _menus = new ArrayList();
	
	private List theMenuElements;
	private List theMenuLevelElements;
	
	private MenuLevelElement levelElement;
	private MenuElement menuElement;
	
	private Text text;
	
	private Page parentPage;
	private String coolMenuScript = "coolmenus4.js";
	private String menuStyleScript = "coolStyle.css";
	private String coolMenuSrc;
	private String menuStyleSrc;
	
	private int frames = 0;
	private int pxBetween = 0;
	private int fromLeft = 20;
	private int fromTop = 0;
	private int rows = 1;
	private String menuPlacement = " ";
	private String offlineRoot = " ";
	private String onlineRoot = " ";
	private int resizeCheck = 1;
	private int wait = 1000;
	private String fillImg = " ";
	private int zIndex = 0;
	
	private String barBorderClass = " ";
	private int barBorderX = 0;
	private int barBorderY = 0;
	private String barClass  = " ";
	private String barHeight = "menu";
	private String barWidth = "menu";
	private String barX = "menu";
	private String barY = "menu";
	private int useBar = 1;
	
	private String position = ""; 
	private String visibility = ""; 
	private String padding = ""; 
	private String top = ""; 
	private String left = "";
	private String width = "";
	private String height = "";  
	private String fontStyle; 
	private String fontOverStyle;
	private String barLayerBackgroundColor = "";
	private String barBackgroundColor = "";
	private String layerBackgroundColor = ""; 
	private String backgroundColor = "";
	private String overLayerBackgroundColor = "";
	private String overBackgroundColor = "";
	private String cursor = ""; 
	private String borderColor = ""; 
	private String borderLayerColor = "";
	
	/**
	 * the default constructor
	 *
	 */
	public DynamicJSMenu (){
		this("undefined");
	}
	/**
	 * constructor to creating a <code> DynamicJSMenu </code> object whith
	 * a specific name.
	 * @param name
	 */
	public DynamicJSMenu(String name){
		setName(name);
		theMenuLevelElements = new ArrayList();
		theMenuElements = new ArrayList();
		initialMenuValues();
	}
	/**
	 * sets the initial values for the <code> DynamicJSMenu </code>
	 *
	 */
	public void initialMenuValues(){
		setBarBorderClass(barBorderClass);
		setBarBorderX(barBorderX);
		setBarBorderY(barBorderY);
		setBarClass(barClass);
		setBarHeight(barHeight);
		setBarWidth(barWidth);
		setBarX(barX);
		setBarY(barY);
		setFillImg(fillImg);
		setFromLeft(fromLeft);
		setFromTop(fromTop);
		setMenuPlacement(menuPlacement);
		setOfflineRoot(offlineRoot);
		setOnlineRoot(onlineRoot);
		setPxBetween(pxBetween);
		setResizeCheck(resizeCheck);
		setRows(rows);
		setUseBar(useBar);
		setZIndex(zIndex);
		setWait(wait);
	}
	
	/**
	 * adds a level to the <code> DynamicJSMenu </code>
	 * @param level
	 */
	public void addLevel(String level){
		levelElement = new MenuLevelElement(level);
//		lElement.setLevel(level);
		levelElement.setRegClass("clLevel" + level);
		levelElement.setOverClass("clLevel" + level + "over");
		levelElement.setBorderClass("clLevel" + level + "border");
		theMenuLevelElements.add(levelElement);
	}
	/**
	 * 
	 * @param f
	 */
	public void setFrames(int f){
		setMarkupAttribute("frames", f);
	}
	/**
	 * sets the pixles between <code> MenuElements </code>
	 * @param pxB
	 */
	public void setPxBetween(int pxB){
		setMarkupAttribute("pxBetween", pxB);
	}
	/**
	 * sets the position the menu is located at from the left of the browserwindow, x-coordinate
	 * @param fromL
	 */
	public void setFromLeft(int fromL){
		setMarkupAttribute("fromLeft", fromL);
	}
	/**
	 * sets the position the menu is located at from the top of the browserwindow, y-coordinates
	 * @param fromT
	 */
	public void setFromTop(int fromT){
		setMarkupAttribute("fromTop", fromT);
	}	
	/**
	 * sets if the menu should appear horizontal (r=0) or vertical (r=0)
	 * @param r
	 */
	public void setRows(int r){
		setMarkupAttribute("rows", r);
	}
	/**
	 * 
	 * @param menuPlace
	 */
	public void setMenuPlacement(String menuPlace){
		setMarkupAttribute("menuPlacement", "\"" + menuPlace + "\"");
	}
	/**
	 * 
	 * @param offlineR
	 */
	public void setOfflineRoot(String offlineR){
		setMarkupAttribute("offlineRoot", "\"" + offlineR + "\"");
	}
	/**
	 * 
	 * @param onlineR
	 */
	public void setOnlineRoot(String onlineR){
		setMarkupAttribute("onlineRoot", "\"" + onlineR + "\"");
	}
	/**
	 * 
	 * @param resizeCh
	 */
	public void setResizeCheck(int resizeCh){
		setMarkupAttribute("resizeCheck", resizeCh);
	}
	/**
	 * 
	 * @param w
	 */
	public void setWait(int w){
		setMarkupAttribute("wait", w);
	}
	/**
	 * 
	 * @param fImg
	 */
	public void setFillImg(String fImg){
		setMarkupAttribute("fillImg", "\"" + fImg + "\"");
	}
	/**
	 * 
	 * @param zIn
	 */
	public void setZIndex (int zIn){
		setMarkupAttribute("zIndex", zIn);
	}
	/**
	 * 
	 * @param useB
	 */
	public void setUseBar(int useB){
		setMarkupAttribute("useBar", useB);
	}
	/**
	 * 
	 * @param barW
	 */
	public void setBarWidth(String barW){
		setMarkupAttribute("barWidth", "\"" + barW + "\"");
	}
	/**
	 * 
	 * @param barH
	 */
	public void setBarHeight(String barH){
		setMarkupAttribute("barHeight", "\"" + barH + "\"");
	}
	/**
	 * 
	 * @param barCl
	 */
	public void setBarClass(String barCl){
		setMarkupAttribute("barClass", "\"" + barCl + "\"");
	}
	/**
	 * 
	 * @param bX
	 */
	public void setBarX(String bX){
		setMarkupAttribute("barX", "\"" + bX + "\"");
	}
	/**
	 * 
	 * @param bY
	 */
	public void setBarY(String bY){
		setMarkupAttribute("barY", "\"" + bY + "\"");
	}
	/**
	 * 
	 * @param barBordX
	 */
	public void setBarBorderX(int barBordX){
		setMarkupAttribute("barBorderX", barBordX);
	}
	/**
	 * 
	 * @param barBordY
	 */
	public void setBarBorderY(int barBordY){
		setMarkupAttribute("barBorderY", barBordY);
	}
	/**
	 * 
	 * @param barBordCl
	 */
	public void setBarBorderClass(String barBordCl){
		setMarkupAttribute("barBorderClass", "\"" + barBordCl + "\"");
	}
	public void setStylePosition(String pos){
//		styleObject.setAttribute("position", pos);
		position = pos;
	}
	public void setStyleVisibility(String vis){
//		styleObject.setAttribute("visibility", vis);
		visibility = vis;
	}
	public void setStyleLeft(String l){
//		styleObject.setAttribute("left", l);
		left = l;
	}
	public void setStyleTop(String t){
//		setAttribute("top", t);
		top = t;
	}
	public void setStyleWidth(String w){
		width = w;
	}
	public void setStyleHeight(String h){
		height = h;
	}
	public void setStylePadding(String pad){
		padding = pad;
	}
	public void setStyleFontStyle(String style){
		fontStyle = style;
	}
	public void setStyleFontOverStyle(String style){
		fontOverStyle = style;
	}
	public void setBarStyleLayerBackgroundColor(String barLBCol){
		barLayerBackgroundColor = barLBCol;
	}
	public void setBarStyleBackgroundColor(String barBCol){
		barBackgroundColor = barBCol;
	}
	public void setStyleLayerBackgroundColor(String lBackgrCol){
		layerBackgroundColor = lBackgrCol;
	}
	public void setStyleBackgroundColor(String backgrCol){
		backgroundColor = backgrCol;
	}
	public void setOverStyleLayerBackgroundColor(String overLBCol){
		overLayerBackgroundColor = overLBCol;
	}
	public void setOverStyleBackgroundColor(String overBCol){
		overBackgroundColor = overBCol;
	}
	public void setStyleCursor(String curs){
		cursor = curs;
	}
	public void setStyleBorderColor(String bColor){
		borderColor = bColor;
	}
	public void setStyleLayerBorderColor(String blColor){
		borderLayerColor = blColor;
	}
	public String getStylePosition(){
		return position;
	}
	public String getStyleVisibility(){
		return visibility;
	}
	public String getStyleLeft(){
		return left;
	}
	public String getStyleTop(){
		return top;
	}
	public String getStyleWidth(){
		return width; 
	}
	public String getStyleHeight(){
		return height;
	}
	public String getStylePadding(){
		return padding; 
	}
	public String getBarStyleBackgroundColor(){
		barLayerBackgroundColor = barBackgroundColor;
		return barBackgroundColor; 
	}
	public String getBarStyleLayerBackgroundColor(){
		return barLayerBackgroundColor;
	}
	public String getStyleBackgroundColor(){
		return backgroundColor;
	}
	public String getStyleLayerBackgroundColor(){
		layerBackgroundColor = backgroundColor;
		return layerBackgroundColor;
	}
	public String getOverStyleBackgroundColor(){
		overLayerBackgroundColor = overBackgroundColor;
		return overBackgroundColor;
	}
	public String getOverStyleLayerBackgroundColor(){
		return overLayerBackgroundColor;
	}
	public String getStyleCursor(){
		return cursor;
	}
	public String getStyleFontStyle(){
		return fontStyle;
	}
	public String getStyleFontOverStyle(){
		return fontOverStyle;
	}
	public String getStyleBorderColor(){
		return borderColor;
	}
	public String getStyleLayerBorderColor(){
		borderLayerColor = borderColor;
		return borderLayerColor;
	}
	
	public String commonStyleDefinitions(){
		StringBuffer attributeString = new StringBuffer();
		setStylePosition("absolute");
		setStyleVisibility("hidden");
		attributeString.append("position:" + position + ";");
		attributeString.append("visibility:" + visibility + ";");
		return attributeString.toString();
	}
	public String defaultStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStyleLeft("0");
		setStyleTop("0");
		attributeString.append("left:" + left + ";");
		attributeString.append("top:" + top + ";");
		return attributeString.toString();
	}
	public String setBarStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStyleWidth("10");
		setStyleHeight("10");
		attributeString.append("width:" + width + ";");
		attributeString.append("height:" + height + ";");
		attributeString.append("background-color:" + getBarStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getBarStyleLayerBackgroundColor() + ";");
		return attributeString.toString();
	}
	public String commonLevelStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStylePadding("2px");
		setStylePosition("absolute");
		attributeString.append("padding:" + padding + ";");
		attributeString.append("position:" + position + ";");
		return attributeString.toString();
	}
	public String setLevelStyle(){
		StringBuffer attributeString = new StringBuffer();
		attributeString.append("background-color:" + getStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getStyleLayerBackgroundColor() + ";");
		attributeString.append(getStyleFontStyle());
//		attributeString.append("color:" + getStyleColor() + ";");
		return attributeString.toString();
	}
	public String setLevelOverStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStyleCursor("pointer");
		attributeString.append("background-color:" + getOverStyleBackgroundColor() + ";");
		attributeString.append("layer-background-color:" + getOverStyleLayerBackgroundColor() + ";");
//		attributeString.append("color:" + getOverStyleColor() + ";");
		attributeString.append(getStyleFontOverStyle());
		attributeString.append("cursor:" + cursor + ";");
		return attributeString.toString();
	}
	public String setLevelBorderStyle(){
		StringBuffer attributeString = new StringBuffer();
		setStylePosition("absolute");
		setStyleVisibility("visible");
		attributeString.append("position:" + position + ";");
		attributeString.append("visibility:" + visibility + ";");
		attributeString.append("background-color:" + getStyleBorderColor() + ";");
		attributeString.append("layer-background-color:" + getStyleLayerBorderColor() + ";");
		return attributeString.toString();
	}
	public void addStyles(Page page){
		page.setStyleDefinition(".clCMAbs", commonStyleDefinitions() + defaultStyle());
		page.setStyleDefinition(".clBar", commonStyleDefinitions() + setBarStyle());
		page.setStyleDefinition(".clLevel1", commonLevelStyle() + setLevelStyle());
		page.setStyleDefinition(".clLevel1over", commonLevelStyle() + setLevelOverStyle());
		page.setStyleDefinition(".clLevel1border", setLevelBorderStyle());
	}
	/**
	 * 
	 * @param map
	 * @return a string of the attributes for the <code> DynamicJSMenu </code>
	 * on the form menuName.attributeName=attributeValue
	 */
	public String _getAttributeString(Map map){
		StringBuffer returnString = new StringBuffer();
		String Attribute ="";
		String attributeValue = "";
		Map.Entry mapEntry;

		if (map != null) {
		  Iterator i = map.entrySet().iterator();
		  while (i.hasNext()) {
		mapEntry = (Map.Entry) i.next();
		Attribute = (String) mapEntry.getKey();
//		returnString.append(" ");
		returnString.append(getName()); //added for javascript output
		returnString.append("."); //added for javascript output
		returnString.append(Attribute);
		attributeValue = (String) mapEntry.getValue();
		if(!attributeValue.equals(slash)){
		  returnString.append("=");  //quotes removed, added in setAttribute()
		  returnString.append(attributeValue);
//		  returnString.append("\""); quotes are added in setAttribute()
		  returnString.append("\n"); //added for readable output
		}
		returnString.append("");
		  }
		}
		return returnString.toString();
	  }
	  public String _getAttributeString() {
		return _getAttributeString(this.attributes);
	  }
	/**
	 * 
	 * @param fileName - the file containing the javascript
	 * @param iwc - the IWContext object
	 * @return String - the url of the javascript source code
	 */
	public String scriptSource(String fileName, IWContext iwc){
		String url = iwc.getIWMainApplication().getCoreBundle().getResourcesURL();
		url = url + "/" + fileName;
		return url;
	}
	public void main(IWContext iwc) throws Exception{
		// get the current page to print the coolMenu4.js and the coolStyle.css src to it
		parentPage = this.getParentPage();
		coolMenuSrc = scriptSource(coolMenuScript, iwc);
		menuStyleSrc = scriptSource(menuStyleScript, iwc);
		parentPage.addJavascriptURL(coolMenuSrc);
		parentPage.addStyleSheetURL(menuStyleSrc);
		addStyles(parentPage);	
	}
	public void print(IWContext main) throws Exception {
		if (getLanguage().equals("HTML")){
			
			print("<script type=\"text/javascript\">\n");
			print(_menuObjectName + "=new makeCM(\"" + _menuObjectName + "\")\n" );
			
			if(theMenuLevelElements!=null && theMenuLevelElements.size()>0) {
				print(_getAttributeString());
				Map levelMap = levelElement.attributes;
				Map.Entry mapEntry;
				
	            Iterator iter = theMenuLevelElements.iterator();
				while (iter.hasNext()) {
					levelElement = (MenuLevelElement) iter.next();
					print(_menuObjectName + ".level[" + levelElement.getLevel() + "]=new cm_makeLevel()\n");
	
					Iterator levelIter = levelMap.entrySet().iterator();
					while (levelIter.hasNext()) {
						mapEntry = (Map.Entry) levelIter.next();
						print(_menuObjectName + ".level[" + levelElement.getLevel() + "]." + (String) mapEntry.getKey() + "=" + (String) mapEntry.getValue() + "\n");
					}
				}
			} else {
				printMenuProperties(_menuObjectName);
				int count = _menus.size();
				for(int i=0; i<count; i++) {
					LinkMenu menu = (LinkMenu) _menus.get(i);
					printMenu(menu, i);
				}
			}
			
			print(_menuObjectName + ".construct()\n\n");
			print("</script>");			
		}
		else if (getLanguage().equals("WML")){
			println("");
		}
	}
	
	private void printMenu(LinkMenu menu, int id) {
		List texts = menu.getTextList();
		List urls  = menu.getUrlList();
		int count = texts.size();
		String topId = "top" + id;
		String subIdPrefix = "sub" + id + "_";
		for(int i=0; i<count; i++) {
			String menuId = i==0?topId:(subIdPrefix + i);
			String parentMenuId = i==0?"":topId;
			System.out.println("Printing link \"" + texts.get(i) + "\" to menu \"" + topId + "\"");
			print(_menuObjectName + ".makeMenu('" + menuId + "','" + parentMenuId + "','" + 
			texts.get(i) + "','" + 
			urls.get(i) + "'" + (i==0?", ''":"") + ")\n");
		}
	}
	
	/**
	 * Adds a link to the i=th menu. Before a link is added to the i-th menu, make sure to add links to all preceeding
	 * menus, i.e. zeroth to (i-1)th.
	 * @param i the menu to add the link to, starting at zero.
	 * @param text The text in the link
	 * @param url The url for the link
	 */
	public void addLinkToMenu(int i, String text, String url) {
		LinkMenu menu = null;
		try {
			menu = (LinkMenu) _menus.get(i);
		} catch(Exception e) {
			// first link being added to menu
		}
		if(menu==null) {
			menu = new LinkMenu();
			_menus.add(menu);
		}
		
		System.out.println("Adding link \"" + text + "\" to menu #" + i);
		
		menu.getTextList().add(text);
		menu.getUrlList().add(url);
	}
	
	private void printMenuProperties(String menuName) {
		for(int i=0; i<menuProps.length; i++) {
			print(menuName + "." + menuProps[i] + "=" + menuValues[i] + "\n");
		}
		
		//print(menuName + ".level[0]=new cm_makeLevel(180,22,\"l1\",\"l1over\",0,1,\"clB\",0,\"right\",0,0,\"/images/arrow_closed.gif\",15,11)\n");
		print(menuName + ".level[0]=new cm_makeLevel()\n");
		for(int j=0; j<levelProps.length; j++) {
			print(menuName + ".level[0]." + levelProps[j] + "=" + levelValues[j] + "\n");
		}
		print(menuName + ".level[1]=new cm_makeLevel()\n");
	}
	
	private class LinkMenu {
		
		public List getTextList() {
			return _textList;
		}
		
		public List getUrlList() {
			return _urlList;
		}
		
		private List _textList = new ArrayList();
		private List _urlList = new ArrayList();
	}
	
	private String[] menuProps = {"frames","pxBetween","fromLeft","fromTop","rows","menuPlacement","offlineRoot","onlineRoot","resizeCheck","wait",
                                  "zIndex","useBar","barWidth","barHeight","barClass","barX","barY","barBorderX","barBorderY","barBorderClass"};
	private String[] menuValues = {"0","30","20","0","1","\"center\"","\"file:///idegaweb/daddara/\"","","1","1000","0","1","\"100%\"",
                                   "\"menu\"","\"clBar\"","0","0","0","0","\"\""};
	
	private String[] levelProps = {"width","height","regClass","overClass","borderX","borderY","borderClass","offsetX","offsetY","rows","arrow",
                                   "arrowWidth","arrowHeight","align"};
	private String[] levelValues = {"110","25","\"clLevel0\"","\"clLevel0over\"","1","1","\"clLevel0border\"","0","0","0","0","0","0","\"bottom\""};
	
	/*
	//EXAMPLE SUB LEVEL[1] PROPERTIES - You have to specify the properties you want different from LEVEL[0] - If you want all items to look the same just remove this
	oCMenu.level[1]=new cm_makeLevel() //Add this for each new level (adding one to the number)
	oCMenu.level[1].width=oCMenu.level[0].width-2
	oCMenu.level[1].height=22
	oCMenu.level[1].regClass="clLevel1"
	oCMenu.level[1].overClass="clLevel1over"
	oCMenu.level[1].borderX=1
	oCMenu.level[1].borderY=1
	oCMenu.level[1].align="right" 
	oCMenu.level[1].offsetX=-(oCMenu.level[0].width-2)/2+20
	oCMenu.level[1].offsetY=0
	oCMenu.level[1].borderClass="clLevel1border"
	*/
}