package com.idega.block.websearch.business;

import java.util.zip.InflaterInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.*;

/**
 * <p><code>PDFHandler</code> Content handler for PDF documents. </p>
* This class is a part of the websearch webcrawler and search engine block. <br>
* It is based on the <a href="http://lucene.apache.org">Lucene</a> java search engine from the Apache group and loosly <br>
* from the work of David Duddleston of i2a.com.<br>
*
* @copyright Idega Software 2002
* @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */

import java.util.List;


public class PDFHandler implements ContentHandler {
    
    private InputStream in;
    
    /*
     * Input cache.  This is much faster than calling down to a synchronized
     * method of BufferedReader for each byte.  Measurements done 5/30/97
     * show that there's no point in having a bigger buffer:  Increasing
     * the buffer to 8192 had no measurable impact for a program discarding
     * one character at a time (reading from an http URL to a local machine).
     */
    private byte buf[] = new byte[256];
    private int pos;
    private int len;
    /*
    tracks position relative to the beginning of the
    document.
     */
    private int currentPosition;
    
    // 1996.07.10 15:08:56 PST
    SimpleDateFormat dateFormatter;
    
    // Content Data
    private String author;
    private long published;
    private String keywords;
    private String description;
    private String title;
    private StringBuffer contents;
    
    // Flags
    private boolean streamHit = false;
    private boolean parseNextStream = false;
    
    // Compression
    private static final int NONE = 0;
    private static final int FLATE = 1;
    private static final int LZW = 2;
    private int compression = NONE;
    
    
    // TOKENS
    private static final char[] AUTHOR = "/Author".toCharArray();
    private static final char[] CREATIONDATE = "/CreationDate".toCharArray();
    private static final char[] ENDSTREAM = "endstream".toCharArray();
    private static final char[] KEYWORDS = "/Keywords".toCharArray();
    private static final char[] STREAM = "stream".toCharArray();
    private static final char[] SUBJECT = "/Subject".toCharArray();
    private static final char[] TITLE = "/Title".toCharArray();
    private static final char[] NEWLINE = {'\n'};
    private static final char[] RETURN = {'\r'};
    private static final char[] PARAMSTART = {'<','<'};
    
    private static final char[][] tokens = {
        AUTHOR, CREATIONDATE, ENDSTREAM, KEYWORDS, STREAM, SUBJECT,
        TITLE, PARAMSTART
    };
    
    /**
     * PdfParser constructor comment.
     */
    public PDFHandler() {
        this.contents = new StringBuffer();
        this.published = -1;
        
        // 19960710150856
        this.dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
    }
    /**
     * Look for tokens.  This is not effiecent.
     * Should use low, hi method with ordered array. NEED TO RECODE
     */
    private char[] findToken() throws IOException {
        
        
        // flags if token still matches.
        boolean[] match = new boolean[tokens.length];
        for (int i = 0; i < match.length; i++) {
            match[i] = true;
        }
        
        // how many tokens still match;
        int matchCount = tokens.length;
        
        // current position to look for char match in tokens
        int charPosition = 0;
        
        // look for matching tokens.
        while (true) {
            int b = read();
            if (b == -1 ) {
				break;
			}
            char ch = (char)b;
            
            
            // loop through all tokens
            for (int i = 0; i < tokens.length; i++) {
                // check to see if match flag is true for this token
                
                if (match[i] == true) {
                    // get the token
                    char[] token = tokens[i];
                    // check if char array of token is in bounds
                    if (charPosition >= token.length) {
                        // out of bounds, check to see if other tokens still match
                        if (matchCount >= 2) {
                            // other tokens still match, set this one to false.
                            match[i] = false;
                            matchCount--;
                        } else {
                            // last matching token;
                            return token;
                        }
                        // token is in bounds, check for match on char at charPosition.
                    } else {
                        if (token[charPosition] != ch) {
                            // did not match, set match to false;
                            match[i] = false;
                            matchCount--;
                        }
                    }
                }
            }
            if (matchCount <= 0 ) {
				break;
			}
            
            charPosition++;
            
        }
        
        return null;
    }
    /**
     * Parse Content. [24] 320:1
     */
    public String getAuthor() {
        return this.author;
    }
    /**
     * Return categories (from META tags)
     */
    public String getCategories() {
        return null;
    }
    /**
     * Parse Content. [24] 320:1
     */
    public String getContents() {
        return this.contents.toString();
    }
    /**
     * Parse Content. [24] 320:1
     */
    public String getDescription() {
        return this.description;
    }
    /**
     *	Return META HREF
     */
    public String getHREF() {
        return null;
    }
    /**
     * Parse Content. [24] 320:1
     */
    public String getKeywords() {
        return this.keywords;
    }
    /**
     * Return links
     */
    public List getLinks() {
        return null;
    }
    /**
     * Parse Content. [24] 320:1
     */
    public long getPublished() {
        return this.published;
    }
    /**
     * Return boolean true if links are to be followed
     */
    public boolean getRobotFollow() {
        return false;
    }
    /**
     * Return boolean true it this is to be indexed
     */
    public boolean getRobotIndex() {
        return true;
    }
    /**
     * Parse Content. [24] 320:1
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * Check for new line chars
     */
    private boolean isNewLineChar(char ch) {
        switch (ch) {
            case '\n' :
                return true;
            case '\r' :
                return true;
            default :
                return false;
        }
        
    }
    /**
     * Insert the method's description here.
     * Creation date: (2/21/2001 7:50:24 PM)
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
        
        //System.out.println("test");
        try {
          
            String path = "/Users/eiki/Desktop/documents.pdf";
            PDFHandler p = new PDFHandler();
            p.parse(new FileInputStream(path));
            System.out.println("Title: " + p.getTitle());
            System.out.println("Author: " + p.getAuthor());
            System.out.println("Published " + p.getPublished());
            System.out.println("Keywords: " + p.getKeywords());
            System.out.println("Description: " + p.getDescription());
            System.out.println("Content: " + p.getContents());
        } catch (Exception e) {e.printStackTrace();}
    }
    /**
     * Parse Content. [24] 320:1
     */
    private boolean nextLine() throws IOException {
        //System.out.println("look for new line");
        while (true) {
            int b = read();
            if (b == -1 ) {
				return false;
			}
            if (isNewLineChar((char)b)) {
				return true;
			}
        }
        
    }
    /**
     * Parse Content.
     */
    public void parse(InputStream in) {
        
        
        //System.out.println("mark supported" + in.markSupported());
        
        try {
            this.in = new BufferedInputStream(in);
            reset();
            parseContent();
            //System.out.println("Title: " + getTitle());
            //System.out.println("Author: " + getAuthor());
            //System.out.println("Published " + getPublished());
            //System.out.println("Keywords: " + getKeywords());
            //System.out.println("Description: " + getDescription());
            //System.out.println("Content: " + getContents());
            
            //int b;
            //while ((b = in.read()) != -1) {
            //System.out.print((byte)b + ".");
            //System.out.print((char)b + "*");
            //}
            
        } catch (Exception e) {e.printStackTrace();}
    }
    /**
     * Parse Content. [24] 320:1
     */
    private void parseContent() throws IOException {
        Thread curThread = Thread.currentThread();
        while (true) {
            if (curThread.isInterrupted()) {
                curThread.interrupt(); // resignal the interrupt
                break;
            }
            char[] token;
            while (true) {
                token = findToken();
                if (token != null) {
                    //System.out.println("found a token : " + token);
                    if (token == AUTHOR) {
                        this.author = parseData();
                    } else if (token == CREATIONDATE) {
                        this.published = parseDate();
                    } else if (token == KEYWORDS) {
                        this.keywords = parseData();
                    } else if (token == SUBJECT) {
                        this.description = parseData();
                    } else if (token == TITLE) {
                        this.title = parseData();
                    } else if (token == PARAMSTART) {
                        //System.out.println("param set mark");
                        this.in.mark(10000);
                        //parseDataParams();
                    } else if (token == STREAM) {
                        if (!this.streamHit) {
                            //System.out.println("new stream hit");
                            // first time this stream has been hit
                            // go back and parseDataParams.
                            this.in.reset();
                            this.streamHit = true;
                            parseDataParams();
                        } else {
                            //System.out.println("second stream hit");
                            if (this.parseNextStream) {
                                this.contents.append(parseDataStream());
                                this.parseNextStream = false;
                            }
                            this.streamHit = false;
                        }
                    }
                }
                if (!nextLine()) {
                    //System.out.println("no new line");
                    break;
                }
                //System.out.println("new line");
            }
            //System.out.println("hello");
            break;
            
        }
    }
    /**
     * Look for tokens.  This is not effiecent.
     * Should use low, hi method with ordered array. NEED TO RECODE
     */
    private String parseData() throws IOException {
        
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        
        // look for start '('
        while (true) {
            int b = read();
            if (b == -1 ) {
				break;
			}
            char ch = (char)b;
            if (ch == '(') {
				break;
			}
        }
        while (true) {
            int b = read();
            if (b == -1 ) {
				break;
			}
            char ch = (char)b;
            if (ch == ')') {
				break;
			}
            temp.write(b);
        }
        
        return new String(temp.toByteArray());
    }
    /**
     * Look for tokens.  This is not effiecent.
     * Should use low, hi method with ordered array. NEED TO RECODE
     */
    private String parseDataParams() throws IOException {
        
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        
        boolean end = false;
        int b = read();
        while (true) {
            // check to see if new line;
            if ((char)b == '>') {
                b = read();
                if ((char)b == '>') {
                    end = true;
                    break;
                } else {
                    temp.write(b);
                }
            } else {
                temp.write(b);
            }
            if (end) {
				break;
			}
            b = read();
        }
        String params = new String(temp.toByteArray());
        //System.out.println(params.length());
        //System.out.println(params);
        if (params.length() < 38
        && params.indexOf("0 R") != -1
        && params.indexOf("/Length ") != -1)  {
            if (params.indexOf("/FlateDecode") != -1) {
				this.compression = FLATE;
			}
            if (params.indexOf("/LZWDecode") != -1) {
				this.compression = LZW;
			}
            this.parseNextStream = true;
            //System.out.println();
            //System.out.println(params);
        }
        
        return new String(temp.toByteArray());
    }
    /**
     * Look for tokens.  This is not effiecent.
     * Should use low, hi method with ordered array. NEED TO RECODE
     */
    private String parseDataStream() throws IOException {
        
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        ByteArrayOutputStream tmp = new ByteArrayOutputStream(ENDSTREAM.length);
        boolean endstream = false;
        
        int b = read();
        char ch = (char)b;
        while (true) {
            // check to see if new line;
            if (isNewLineChar(ch)) {
                // check to see if it is endstream
                tmp.reset();
                boolean notMatch = false;
                for (int i = 0; i < ENDSTREAM.length; i++) {
                    b = read();
                    tmp.write(b);
                    if ((char)b != ENDSTREAM[i]) {
                        // not endsteam break..
                        notMatch = true;
                        tmp.writeTo(temp);
                        break;
                    }
                }
                if (!notMatch) {
					endstream = true;
				}
            } else {
                // not new line append byte
                temp.write(b);
                b = read();
                ch = (char)b;
            }
            if (endstream) {
				break; // endstream found
			}
        }
        
        // Uncompress if flateDecode is used
        if (this.compression == FLATE) {
            //System.out.println("FlateDecode = " +flateDecode);
            ByteArrayInputStream bis = new ByteArrayInputStream(temp.toByteArray());
            InflaterInputStream iin = new InflaterInputStream(bis);
            temp.reset();
            while ((b = iin.read()) != -1) {
                temp.write(b);
            }
        }
        
        //System.out.println(temp.size());
        //System.out.println(new String(temp.toByteArray()));
        
        // parse content out from formating data. Content is wrapped in a
        // bunch of ()
        
        // look for start '('
        ByteArrayInputStream bis = new ByteArrayInputStream(temp.toByteArray());
        tmp.reset();
        boolean end = false;
        while (true) {
            b = bis.read();
            if (b == -1 ) {
				break;
			}
            if ((char)b == '(') {
                while (true) {
                    b = bis.read();
                    if (b == -1 ) {end = true; break;}
                    // look for end ')'
                    if ((char)b == ')') {
						break;
					}
                    tmp.write(b);
                }
            }
            if (end) {
				break;
			}
        }
        
        // reset flateDecode flag
        this.compression = NONE;
        //System.out.println(tmp.size());
        //System.out.println(new String(tmp.toByteArray()));
        return new String(tmp.toByteArray());
    }
    /**
     * Look for tokens.  This is not effiecent.
     * Should use low, hi method with ordered array. NEED TO RECODE
     */
    private long parseDate() throws IOException {
        
        try {
            String date = parseData();
            return this.dateFormatter.parse(date.substring(2, date.length())).getTime();
        } catch(ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private final int read() throws IOException {
        
        ++this.currentPosition;
        return this.in.read();
        
        //return in.read();
    /*
    if (pos >= len) {
     
        // This loop allows us to ignore interrupts if the flag
        // says so
        for (;;) {
            try {
                len = in.read(buf);
                System.out.println("next");
                break;
            } catch (InterruptedIOException ex) {
                throw ex;
            }
        }
        if (len <= 0) {
            return -1; // eof
        }
        pos = 0;
    }
    ++currentPosition;
    return buf[pos++];
     */
    }
    private final char readCh() throws IOException {
        
        ++this.currentPosition;
        return (char)this.in.read();
    /*
    if (pos >= len) {
     
        // This loop allows us to ignore interrupts if the flag
        // says so
        for (;;) {
            try {
                len = in.read(buf);
                System.out.println("next");
                break;
            } catch (InterruptedIOException ex) {
                throw ex;
            }
        }
        if (len <= 0) {
            return -1; // eof
        }
        pos = 0;
    }
    ++currentPosition;
    return buf[pos++];
     */
    }
    /**
     *	Return contents
     */
    private void reset() {
        
        // Content
        this.title = null;
        this.description = null;
        this.keywords = null;
        this.author = null;
        
        this.contents.setLength(0);
        this.published = -1;
        
        
        // Flags
        this.streamHit = false;
        this.parseNextStream = false;
        this.compression = NONE;
        
        //buf[] = new byte[256];
        //pos = 0;
        //len = 0;
        //currentPosition = 0;
        
        
    }
}
