/*
 * Created on 3.6.2004
 *
 */
package com.idega.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.idega.core.builder.data.ICPage;


/**
 * @author tryggvil
 *
 * A class to rewrite relative URLs in HTML code and rewrite them to an absolute http:// URL.
 */
public class HtmlReferenceRewriter {

	private Reader input;
	private Writer output;
	private String urlPrefix;
	private List patterns;
	private boolean rewriteOptionValues=false;
	private static String SLASH="/";
	
	/**
	 * @return Returns the rewriteOptionValues.
	 */
	public boolean isRewriteOptionValues() {
		return rewriteOptionValues;
	}
	/**
	 * Sets if to treat option values (in a select tag) as URLs and rewrite them also
	 * @param rewriteOptionValues The rewriteOptionValues to set.
	 */
	public void setRewriteOptionValues(boolean rewriteOptionValues) {
		this.rewriteOptionValues = rewriteOptionValues;
	}
	/**
	 * @return Returns the patterns.
	 */
	public List getPatterns() {
		if(patterns==null){
			patterns = new ArrayList();
			Pattern p1 = Pattern.compile("(<a[^>]+href=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p1);
			Pattern p2 = Pattern.compile("(<link[^>]+href=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p2);
			Pattern p3 = Pattern.compile("(<img[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p3);
			Pattern p4 = Pattern.compile("(<script[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p4);
			Pattern p5 = Pattern.compile("(<input[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p5);
			Pattern p6 = Pattern.compile("(<form[^>]+action=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p6);
			Pattern p7 = Pattern.compile("(<embed[^>]+src=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
			patterns.add(p7);
			if(this.isRewriteOptionValues()){
				Pattern p8 = Pattern.compile("(<option[^>]+value=\")([^#][^\"]+)([^>]+>)",Pattern.CASE_INSENSITIVE);
				patterns.add(p8);
			}
		}
		return patterns;
	}
	/**
	 * @param patterns The patterns to set.
	 */
	public void setPatterns(List patterns) {
		this.patterns = patterns;
	}
	public static void main(String[] args) throws Exception{
		performReykjavikNetworkTestToFile();
	}

	
	public static void performReykjavikFileTest()throws Exception{
		HtmlReferenceRewriter instance = new HtmlReferenceRewriter();
		String fromFile = "/Users/tryggvil/Documents/Reykjavik/rrvk-dtemplate.html";
		String toFile = "/Users/tryggvil/Documents/Reykjavik/rvktest.html";
		String urlPrefix = "http://www.rvk.is/";
		FileReader reader = new FileReader(fromFile);
		Reader input = new BufferedReader(reader);
		FileWriter output = new FileWriter(toFile);
		instance.setInput(input);
		instance.setOutput(output);
		instance.setUrlPrefix(urlPrefix);
		instance.setRewriteOptionValues(true);
		instance.process();
	}
	
	public static void performReykjavikNetworkTestToFile()throws Exception{
		String sUrl = "http://nobel.idega.is/rvk/template.html";
		URL url = new URL(sUrl);
		InputStream iStream = url.openStream();
		
		InputStreamReader iReader = new InputStreamReader(iStream);
		
		HtmlReferenceRewriter instance = new HtmlReferenceRewriter();
		
		String toFile = "/Users/tryggvil/Documents/Reykjavik/rvktest2.html";
		String urlPrefix = "http://www.rvk.is/";
		
		Reader input = new BufferedReader(iReader);
		FileWriter output = new FileWriter(toFile);
		instance.setInput(input);
		instance.setOutput(output);
		instance.setUrlPrefix(urlPrefix);
		instance.setRewriteOptionValues(true);
		instance.process();
	}	
	
	
	public static void performReykjavikNetworkTestToIBPageTemplate()throws Exception{
		String sUrl = "http://nobel.idega.is/rvk/template.html";
		URL url = new URL(sUrl);
		InputStream iStream = url.openStream();
		
		InputStreamReader iReader = new InputStreamReader(iStream);
		
		HtmlReferenceRewriter instance = new HtmlReferenceRewriter();
		
		String urlPrefix = "http://www.rvk.is/";
		String pageKey = "101";
		
		//ServletContext application = null;
		//IWApplicationContext iwac = IWMainApplication.getIWMainApplication(application).getIWApplicationContext();
		//BuilderLogic.getInstance().getIBXMLPage(pageKey).
		
		ICPage ibpage = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class)).findByPrimaryKey(new Integer(pageKey));
		ibpage.setFormat("HTML");
		OutputStream outStream = ibpage.getPageValueForWrite();
		
		
		Reader input = new BufferedReader(iReader);
		Writer output = new OutputStreamWriter(outStream);
		
		instance.setInput(input);
		instance.setOutput(output);
		instance.setUrlPrefix(urlPrefix);
		instance.process();
		
		ibpage.store();
		//PageCacher.flagPageInvalid(pageKey);
		//PageCacher.flagAllPagesInvalid();
	}

	/**
	 * Execute the processing. Read the input, search/replace and write to the output.
	 * This method should be called last, after all set methods are called.
	 */
	public void process() {
		
		Reader reader = getInput();
		StringBuffer sb = new StringBuffer();
		int buffersize = 1000;
		char[] buffer = new char[buffersize];
		try {
			int read = reader.read(buffer);
			while(read!=-1){
				sb.append(buffer,0,read);
				read = reader.read(buffer);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuffer outString = null;
		Iterator patternIter = getPatterns().iterator();
		StringBuffer replaceBuffer = sb;
		while (patternIter.hasNext()) {
			outString = new StringBuffer();
			Pattern p = (Pattern) patternIter.next();
			Matcher m = p.matcher(replaceBuffer);
			while (m.find()) {
				// this pattern matches.
				int groupCount = m.groupCount();
				for(int i=0;i<=	groupCount;i++){
					String s = m.group(i);
					System.out.println(s);
				}
				String url = m.group(2);
				if(getIfRewriteURL(url)){
					//if this is a relative url:
					m.appendReplacement(outString,"$1"+getRewrittenURL(url)+"$3");
				}				
				else{
					//Do not replace the url
					m.appendReplacement(outString,"$0");
				}

			}
			m.appendTail(outString);
			replaceBuffer=new StringBuffer(outString.toString());
		}
		
		String utfString;
		try {
			utfString = new String(outString.toString().getBytes("UTF-8"),"UTF-8");
			StringReader sr = new StringReader(utfString);
			System.out.println("[HTMLReferenceWriter] The final html string in unicode:\n"+utfString);
			Writer out = getOutput();
			
			int bufferlength=1000;
			char[] buf = new char[bufferlength];
			int read = sr.read(buf);
			while (read!=-1){
				out.write(buf,0,read);
				read = sr.read(buf);
			}
			sr.close();
			//out.close(); don't close the stream, that is done automatically when the page is stored
		}
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	/**
	 * Gets the rewritten URL. this can be overridden
	 */
	public String getRewrittenURL(String relativeURL){
		String urlPrefix = getUrlPrefix();
		if(relativeURL.startsWith(SLASH)&&urlPrefix.endsWith(SLASH)){
			return urlPrefix+relativeURL.substring(1,relativeURL.length());
		}
		else{
			return this.urlPrefix+relativeURL;
		}
	}
	
	/**
	 * Gets if th URL is appropriate to be rewritten<br>
	 * e.g. if it does not contain http:, javascript:,mailto: or # prefixes
	 * @param url the found url in the source
	 * @return
	 */
	public boolean getIfRewriteURL(String url){
		// not if it starts with these prefixes::
		return !(url.startsWith("http:")||url.startsWith("javascript:")||url.startsWith("mailto:")||url.startsWith("#"));
	}
	
	/**
	 * @return Returns the input.
	 */
	public Reader getInput() {
		return input;
	}
	/**
	 * Set the Input (file or stream)
	 * @param input The input to set.
	 */
	public void setInput(Reader input) {
		this.input = input;
	}
	/**
	 * @return Returns the output.
	 */
	public Writer getOutput() {
		return output;
	}
	/**
	 * Set the Output (file or stream) to write the rewritten HTML to.
	 * @param output The output to set.
	 */
	public void setOutput(Writer output) {
		this.output = output;
	}
	/**
	 * Returns the set URLPrefix and appends a "/" to the end if it is not set.
	 * @return Returns the urlPrefix.
	 */
	public String getUrlPrefix() {
		if(!urlPrefix.endsWith(SLASH)){
			return urlPrefix+SLASH;
		}
		return urlPrefix;
	}
	/**
	 * @param urlPrefix The urlPrefix to set.
	 */
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
}
