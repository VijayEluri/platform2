package com.idega.block.navigation.presentation;

import java.util.Iterator;

import com.idega.builder.business.PageTreeNode;
import com.idega.core.builder.business.BuilderService;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;

/**
 * Title: Description: Copyright: Copyright (c) 2000-2001 idega.is All Rights
 * Reserved Company: idega
 * 
 * @author <a href="mailto:aron@idega.is">Aron Birkir </a>
 * @version 1.0
 */

public class NavigationDropdownMenu extends Block {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.navigation";

	private IWResourceBundle iwrb;

	private String prmDropdown = "nav__drp__mnu_";

	private int rootNode = -1;
	private int spaceBetween = 0;

	private boolean useSubmitButton = false;
	private boolean setButtonAsLink = false;
	private boolean useGeneratedButton = false;
	private boolean useImageLink = false;
	private Image buttonImage;

	private String iLinkStyleClass;
	private String iInputStyleClass;
	private String iButtonStyleClass;
	private String iDropDownMenuWidth;
	private String iFirstMenuElementText;
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	public String getDropdownParameter() {
		return this.prmDropdown + getICObjectInstanceID();
	}

	public void main(IWContext iwc) throws Exception {
		BuilderService bs = getBuilderService(iwc);
		this.iwrb = getResourceBundle(iwc);
		if (this.rootNode == -1) {
			this.rootNode = bs.getRootPageId();
		}

		String name = getDropdownParameter();
		DropdownMenu dropDown = new DropdownMenu(name);
		if (this.iInputStyleClass != null) {
			dropDown.setStyleClass(this.iInputStyleClass);
		}
		if (this.iDropDownMenuWidth != null) {
			dropDown.setWidth(this.iDropDownMenuWidth);
		}

		PageTreeNode node = new PageTreeNode(this.rootNode, iwc);
		Iterator iter = node.getChildrenIterator();
		while (iter.hasNext()) {
			PageTreeNode n = (PageTreeNode) iter.next();
			int id = n.getNodeID();
			String url = bs.getPageURI(id);
			dropDown.addMenuElement(url, n.getLocalizedNodeName(iwc));
		}

		Form f = new Form();
		f.setAction("");
		String formName = name + "form";
		f.setName(formName);
		add(f);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		int column = 1;
		f.add(table);
		
		f.getParentPage().getAssociatedScript().addFunction("navHandler", getScriptSource());
		table.add(dropDown, column++, 1);
		
		if (this.spaceBetween > 0) {
			table.setWidth(column++, this.spaceBetween);
		}
		
		if (this.useSubmitButton) {
			if (this.useImageLink) {
				Link btn = new Link(this.buttonImage);
				btn.setURL("javascript:" + getScriptCaller(name));
				btn.setOnClick("javascript:" + getScriptCaller(name));
				table.add(btn, column, 1);
			}
			else if (this.useGeneratedButton) {
				Link btn = new Link(this.iwrb.getLocalizedImageButton("go", "Go!"));
				btn.setURL("javascript:" + getScriptCaller(name));
				btn.setOnClick("javascript:" + getScriptCaller(name));
				table.add(btn, column, 1);
			}
			else if (this.setButtonAsLink) {
				Link btn = new Link(this.iwrb.getLocalizedString("go", "Go!"));
				btn.setURL("javascript:" + getScriptCaller(name));
				btn.setOnClick("javascript:" + getScriptCaller(name));
				if (this.iLinkStyleClass != null) {
					btn.setStyleClass(this.iLinkStyleClass);
				}
				table.add(btn, column, 1);
			}
			else {
				GenericButton btn = new GenericButton("go", this.iwrb.getLocalizedString("go", "Go!"));
				btn.setOnClick("javascript:" + getScriptCaller(name));
				if (this.iButtonStyleClass != null) {
					btn.setStyleClass(this.iButtonStyleClass);
				}
				table.add(btn, column, 1);
			}
		}
		else {
			if (this.iFirstMenuElementText != null) {
				dropDown.addMenuElementFirst("", this.iFirstMenuElementText);
			} else {
				dropDown.addMenuElementFirst("", "");
			}
			dropDown.setOnChange(getScriptCaller(name));
		}

	}

	public String getScriptCaller(String dropDownName) {
		return "navHandler(findObj('" + dropDownName + "'))";
	}

	public String getScriptSource() {
		StringBuffer s = new StringBuffer();
		s.append("\n function navHandler(input){");
		s.append("\n\t var URL = input.options[input.selectedIndex].value;");
		s.append("\n\t window.location.href = URL;");
		s.append("\n }");
		return s.toString();
	}

	public void setUseSubmitButton(boolean use) {
		this.useSubmitButton = use;
	}

	public void setRootNode(int rootId) {
		this.rootNode = rootId;
	}
	/**
	 * @param buttonStyleClass The buttonStyleClass to set.
	 */
	public void setButtonStyleClass(String buttonStyleClass) {
		this.iButtonStyleClass = buttonStyleClass;
	}
	/**
	 * @param inputStyleClass The inputStyleClass to set.
	 */
	public void setInputStyleClass(String inputStyleClass) {
		this.iInputStyleClass = inputStyleClass;
	}
	/**
	 * @param linkStyleClass The linkStyleClass to set.
	 */
	public void setLinkStyleClass(String linkStyleClass) {
		this.iLinkStyleClass = linkStyleClass;
	}
	/**
	 * @param dropDownMenuWidth The dropDownMenuWidth to set.
	 */
	public void setDropDownMenuWidth(String dropDownMenuWidth) {
		this.iDropDownMenuWidth = dropDownMenuWidth;
	}
	/**
	/**
	 * @param firstMenuElementText The firstMenuElementText to set.
	 */
	public void setFirstMenuElementText(String firstMenuElementText) {
		this.iFirstMenuElementText = firstMenuElementText;
	}
	/**
	 * @param spaceBetween The spaceBetween to set.
	 */
	public void setSpaceBetween(int spaceBetween) {
		this.spaceBetween = spaceBetween;
	}
	/**
	 * @param setButtonAsLink The setButtonAsLink to set.
	 */
	public void setSetButtonAsLink(boolean setButtonAsLink) {
		this.setButtonAsLink = setButtonAsLink;
	}
	/**
	 * @param useGeneratedButton The useGeneratedButton to set.
	 */
	public void setUseGeneratedButton(boolean useGeneratedButton) {
		this.useGeneratedButton = useGeneratedButton;
	}
	
	public void setButtonImage(Image buttonImage) {
		this.buttonImage = buttonImage;
		this.useImageLink = true;
	}
}