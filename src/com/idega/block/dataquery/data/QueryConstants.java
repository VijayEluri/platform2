package com.idega.block.dataquery.data;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Feb 2, 2004
 */
public abstract class QueryConstants {

	public final static String PERMISSION_PRIVATE_QUERY = "private";
	public final static String PERMISSION_PUBLIC_QUERY = "public";
	public final static String ENTITY_PATH_DELIMITER = "#";
	// used for adding a counter to a name if the name is not unique
	// e.g. reykjavik (already existing) -> reykjavik+COUNTER_TOKEN+1 
	public final static String COUNTER_TOKEN = "_";

}
