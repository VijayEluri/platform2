package com.idega.block.websearch.presentation;

/**
* This class is a part of the websearch webcrawler and search engine block. <br>
* It is based on the <a href="http://lucene.apache.org">Lucene</a> java search engine from the Apache group and loosly <br>
* from the work of David Duddleston of i2a.com.<br>
*
* @copyright Idega Software 2002
* @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */

import java.util.HashMap;
import java.util.Map;

import com.idega.idegaweb.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.block.websearch.business.*;
import com.idega.block.websearch.data.*;

public class WebSearcher extends Block {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.websearch";
	private final static String SEARCH_PARAM = "iw_bl_ws_search";
	private final static String CRAWL_PARAM = "iw_bl_ws_crawl";
	private final static String CRAWL_REPORT_PARAM = "iw_bl_ws_cr_re";
	private final static String HITS_PER_SET_PARAM = "iw_bl_ws_hi_p_s";
	private final static String EXACT_PHRASE_PARAM = "iw_bl_ws_ex_ph";
	private final static String PUBLISHED_FROM_PARAM = "iw_bl_ws_pb_fr";
	private final static String DETAILED_PARAM = "iw_bl_ws_de";
	private final static String DIRECTION_PARAM = "iw_bl_ws_set_dir";

	private final static String HITS_ITERATOR_SESSION_PARAM = "iw_bl_ws_hitsiterator";
	private final static String HITS_MAP_SESSION_PARAM = "iw_bl_ws_hitsmap";

	private final static String INDEX_NO_REPORT = "0";
	private final static String INDEX_MINOR_REPORT = "1";
	private final static String INDEX_NORMAL_REPORT = "2";
	private final static String INDEX_DETAILED_REPORT = "3";

	private final static String DIRECTION_NEXT = "next";
	private final static String DIRECTION_PREV = "prev";

	private WebSearchIndex index;
	private Crawler crawler;

	private boolean showResults = false;
	private boolean crawl = false;
	private boolean exact = false;
	private boolean detailed = false;
	private boolean canEdit = false;

	private String queryString = null;
	private String direction = null;

	private Link titleLinkProto = new Link();
	private Text contentTextProto = new Text();
	private Text extraInfoTextProto = new Text();
	private String contentTextProtoStyle = null;
	private String extraInfoTextProtoStyle = null;
	private String titleLinkProtoStyle = null;

	private int hitsPerSet = 10;
	private int publishedFromDays = 0;
	private int report = 0;

	private IWBundle iwb = null;
	private IWResourceBundle iwrb = null;

	public WebSearcher() {
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	private void parseAction(IWContext iwc) {
		if (iwc.isParameterSet(SEARCH_PARAM)) {
			showResults = true;
			queryString = iwc.getParameter(SEARCH_PARAM);
			exact = iwc.isParameterSet(EXACT_PHRASE_PARAM);
			detailed = iwc.isParameterSet(DETAILED_PARAM);
			String fromDays = iwc.getParameter(PUBLISHED_FROM_PARAM);
			if (fromDays != null)
				publishedFromDays = Integer.parseInt(fromDays);
			String perSet = iwc.getParameter(HITS_PER_SET_PARAM);
			if (perSet != null)
				hitsPerSet = Integer.parseInt(perSet);
			direction = iwc.getParameter(DIRECTION_PARAM);
		}
		else if (iwc.isParameterSet(CRAWL_PARAM)) {
			crawl = true;
			String sReport = iwc.getParameter(CRAWL_REPORT_PARAM);
			if (sReport != null)
				report = Integer.parseInt(sReport);

		}

		canEdit = this.hasEditPermission();
	}

	/**
	 * @see com.idega.presentation.PresentationObject#main(IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		iwb = getBundle(iwc);
		iwrb = getResourceBundle(iwc);

		parseAction(iwc);

		if (showResults) {
			prepStyles();
			search(iwc);
		}
		else if (crawl) {
			iwc.removeSessionAttribute(HITS_MAP_SESSION_PARAM);
			crawl();
		}
		else {
			add(getSearchForm());
		}
	}

	private void crawl() throws Exception {
		add(iwrb.getLocalizedString("indexing", "Indexing..."));
		addBreak();

		index = WebSearchManager.getIndex("main"); //this should not be hard coded

		if (report > 0) {
			crawler = new Crawler(index, report); //change so that crawler return an arraylist and then print that or something
		}
		else { //no report
			crawler = new Crawler(index);
		}

		crawler.crawl();
		addBreak();
		add(iwrb.getLocalizedString("done", "Done!"));
		addBreak();
		add(new BackButton(iwrb.getLocalizedString("back", "back")));

	}

	private Form getSearchForm() {
		Form searchForm = new Form();
		Table table = new Table(3, 1);
		TextInput search = new TextInput(SEARCH_PARAM);
		if (queryString != null) {
			search.setContent(queryString);
		}

		SubmitButton button = new SubmitButton(iwrb.getLocalizedString("search", "Search"));
		//button.setAsImageButton(true);

		table.add(search, 1, 1);
		table.add(button, 2, 1);

		searchForm.add(table);

		if (canEdit) {
			Link crawl = new Link(iwrb.getLocalizedString("index.this.site", "Index this site"));
			crawl.addParameter(CRAWL_PARAM, "true");
			crawl.addParameter(CRAWL_REPORT_PARAM, INDEX_MINOR_REPORT);
			table.add(crawl, 3, 1);
		}

		return searchForm;

	}

	private void search(IWContext iwc) {
		add(getSearchForm());
		addBreak();

		WebSearchHitIterator hits = (WebSearchHitIterator) getHitsFromSession(iwc,HITS_ITERATOR_SESSION_PARAM + queryString);

		if (hits == null) {
			try {
				com.idega.block.websearch.business.WebSearcher searcher = new com.idega.block.websearch.business.WebSearcher(WebSearchManager.getIndex("main"));
				//hits per page
				searcher.setHitsPerSet(hitsPerSet);
				// exact phrase
				searcher.setPhraseSearch(exact);
				// from days
				if (publishedFromDays > 0) {
					searcher.setFromDays(publishedFromDays);
				}
				hits = searcher.search(queryString);
				setHitsToSession(iwc,HITS_ITERATOR_SESSION_PARAM + queryString, hits);

			}
			catch (Exception e) {
				e.printStackTrace();
				add(iwrb.getLocalizedString("you.have.to.index.first", "You need to run the indexer first!"));
			}
		}

		if (hits != null) {
			addBreak();
			add(getResultSetInfo(hits));
			addBreak();

			Table results = new Table();
			results.setWidth(Table.HUNDRED_PERCENT);
			//results.setHeight(Table.HUNDRED_PERCENT);
			results.setCellpaddingAndCellspacing(0);

			int row = 1;
			int row2 = 1;

			while (hits.hasNextInSet()) {
				WebSearchHit hit = hits.next();
				//String bgColor = (hit.getRank()%2==0)?"#BBBBBB":"#CDCDCD";
				Table hitTable = new Table(1, 3);
				hitTable.setWidth(Table.HUNDRED_PERCENT);
				hitTable.setHeight(50);
				hitTable.setCellpadding(0);
				hitTable.setCellspacing(1);

				//hitTable.setColor(bgColor);
				//if detailed ?
				//hitTable.add(new Text("Score: "+hit.getScore()),1,row++);
				//hitTable.add(new Text("Published: "+ hit.getPublishedFormated()),1,row++);
				//hitTable.add(new Text("Content Type: "+hit.getContentType()),1,row++);
				//hitTable.add(new Text("Keywords: "+hit.getKeywords()),1,row++); veit ekki afhverju thetta skilar alltaf null !?
				//hitTable.add(new Text("Categories: "+hit.getCategories()),1,row++);
				//hitTable.add(new Text("Description: "+hit.getDescription()),1,row++);
				Link title = (Link) titleLinkProto.clone();
				title.setURL(hit.getURL());

				String sTitle = hit.getTitle();
				if (sTitle == null)
					sTitle = iwrb.getLocalizedString("websearch.untitled", "Untitled");
				else if (sTitle.equals("null"))
					sTitle = iwrb.getLocalizedString("websearch.untitled", "Untitled");

				title.setText(sTitle);

				hitTable.add(title, 1, row++);
				String contents = hit.getContents(queryString); //could be heavy....

				if (contents != null) {
					contents = "..." + contents + "...";
					Text contentsText = (Text) contentTextProto.clone();
					contentsText.setText(contents);
					hitTable.add(contentsText, 1, row++);

				}

				String extraInfo = hit.getHREF() + " - " + hit.getContentType() + " - " + iwrb.getLocalizedString("rank", "rank") + ": " + hit.getRank();
				Text extraInfoText = (Text) extraInfoTextProto.clone();
				extraInfoText.setText(extraInfo);
				hitTable.add(extraInfoText, 1, row);
				row = 1;
				results.add(hitTable, 1, row2++);
				results.add(Text.getBreak(), 1, row2++);

			}

			add(results);
		}

	}

	private void prepStyles() {
		if (contentTextProtoStyle != null) {
			contentTextProto.setFontStyle(contentTextProtoStyle);
		}
		else {
			contentTextProto.setFontFace(Text.FONT_FACE_ARIAL);
			contentTextProto.setFontSize(Text.FONT_SIZE_10_HTML_2);

		}

		if (extraInfoTextProtoStyle != null) {
			extraInfoTextProto.setFontStyle(extraInfoTextProtoStyle);
		}
		else {
			extraInfoTextProto.setFontFace(Text.FONT_FACE_ARIAL);
			extraInfoTextProto.setFontSize(Text.FONT_SIZE_10_HTML_2);
			extraInfoTextProto.setFontColor("#008000");
		}

		if (titleLinkProtoStyle != null) {
			titleLinkProto.setFontStyle(titleLinkProtoStyle);
		}
		else {
			titleLinkProto.setFontSize(Text.FONT_SIZE_12_HTML_3);
			titleLinkProto.setFontColor("#0000CC");
		}
	}

	private Table getResultSetInfo(WebSearchHitIterator hits) {
		//temporary html tags inline and need to localize
		Table info = new Table();
		info.setWidth(Table.HUNDRED_PERCENT);
		info.setColor("#3366cc");

		// Get Set by direction
		if (direction != null && direction.equals(DIRECTION_NEXT))
			hits.nextSet();
		if (direction != null && direction.equals(DIRECTION_PREV))
			hits.previousSet();

		Text textProto = new Text();
		textProto.setFontColor("#FFFFFF");
		textProto.setFontFace(Text.FONT_FACE_ARIAL);
		textProto.setFontSize(Text.FONT_SIZE_10_HTML_2);

		Text text1 = (Text) textProto.clone();
		Text text2 = (Text) textProto.clone();
		Text text3 = (Text) textProto.clone();

		text1.setText(iwrb.getLocalizedString("searched.for", "Searched for") + " : <b>" + hits.getQuery() + "</b>. " + iwrb.getLocalizedString("results", "Results") + " ");
		info.add(text1, 1, 1);

		if (hits.hasPreviousSet()) {
			Link prev = new Link("<< ");
			prev.addParameter(DIRECTION_PARAM, DIRECTION_PREV);
			prev.addParameter(SEARCH_PARAM, queryString);
			prev.setFontColor("#FFFFFF");
			prev.setFontFace(Text.FONT_FACE_ARIAL);
			prev.setFontSize(Text.FONT_SIZE_10_HTML_2);
			prev.setBold();

			info.add(prev, 1, 1);
		}

		text2.setText("<b>" + hits.getSetStartPosition() + " - " + hits.getSetEndPosition() + "</b>");
		info.add(text2, 1, 1);

		if (hits.hasNextSet()) {
			Link next = new Link(" >> ");
			next.addParameter(DIRECTION_PARAM, DIRECTION_NEXT);
			next.addParameter(SEARCH_PARAM, queryString);
			next.setFontColor("#FFFFFF");
			next.setFontFace(Text.FONT_FACE_ARIAL);
			next.setFontSize(Text.FONT_SIZE_10_HTML_2);
			next.setBold();

			info.add(next, 1, 1);
		}

		text3.setText(" " + iwrb.getLocalizedString("of", "of") + " <b>" + hits.getTotalHits() + "</b>" + ".");
		info.add(text3, 1, 1);

		return info;

	}

	public void setContentTextStyle(String style) {
		this.contentTextProtoStyle = style;
	}

	public void setExtraInfoTextStyle(String style) {
		this.extraInfoTextProtoStyle = style;
	}

	public void setTitleLinkStyle(String style) {
		this.titleLinkProtoStyle = style;
	}

	private Map getSessionMap(IWContext iwc) {
		Map sessionMap = (Map) iwc.getSessionAttribute(HITS_MAP_SESSION_PARAM);
		if (sessionMap == null) {
			sessionMap = new HashMap();
			iwc.setSessionAttribute(HITS_MAP_SESSION_PARAM, sessionMap);
		}
		return sessionMap;
	}
	
	private WebSearchHitIterator getHitsFromSession(IWContext iwc, String value) {
		return (WebSearchHitIterator) getSessionMap(iwc).get(value);
	}
	
	private void setHitsToSession(IWContext iwc, String value, WebSearchHitIterator wshi) {
		getSessionMap(iwc).put(value,wshi);
	}

}