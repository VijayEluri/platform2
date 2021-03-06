package com.idega.core.ldap.client.naming;

import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.idega.core.ldap.client.cbutil.CBUtility;

/**
 *    This class is a wrapper for DXAttributes, that 
 *    includes knowledge of the DN of the attributes
 *    set.
 */
 


public class DXEntry extends DXAttributes
{
    /**
     *    The full name of the entry
     */
     
    DN dn = null;

    /**
     *    This status constant implies that the entry is
     *    a 'normal' directory entry.
     */
     
    public static final int NORMAL = 0;
    
    /**
     *    This status constant implies that the entry is
     *    a new entry, that has not yet been written to the directory.
     */

    public static final int NEW = 1;

    /**
     *    This status constant implies that the entry is
     *    a new entry, that has just been written to the directory,
     *    but may not yet have been added to the tree or re-displayed
     *    by the editor.
     */

    public static final int NEW_WRITTEN = 2;

    private int status = NORMAL;

    /**
     *    Constructors simply chain to DXAttributes... 
     *    (except for two which allow for a DN as well...)
     */ 
     
    public DXEntry() { super(); }
    public DXEntry(DN dn) { super(); this.dn = dn;}
    public DXEntry(Attribute a) { super(a); }
    public DXEntry(Attributes a) { super(a); }   
    public DXEntry(Attributes a, DN dn) { super(a); this.dn = dn;}   
    public DXEntry(Hashtable newAtts) { super(newAtts); }
    public DXEntry(NamingEnumeration newAtts) { super(newAtts); }
    public DXEntry(DXEntry copyMe) 
    { 
        super(copyMe); 
        this.dn = copyMe.dn; 
        this.status = copyMe.status;
    }



    /**
     *    Intercept a DN being passed as an attribute
    *    (as might happen reading an ldif file);
     */
             
    public Attribute put(Attribute attr) 
    { 
        if (attr.getID().equalsIgnoreCase("dn"))
        {
            try
            {
                Object o = attr.get();
                    
                if (o instanceof String) {
					this.dn = new DN((String)o);
				}
				else {
					this.dn = new DN(o.toString());  // no idea what to do with it...
				}
            }
            catch (NamingException e)
            {
                CBUtility.error("Unexpected exception in DXEntry.put.", e);
            }
            return null;  // there is by definition no dn attribute...
        }
		else {
			return super.put(attr);
		}
    }
    
    /**
     *    This is used to set what stage of the 'new entry' life cycle
     *    an entry is in - one of 'NORMAL', 'NEW', or 'NEW_WRITTEN'.
     *    Almost all entries are 'NORMAL'.  When an entry has just been
     *    created by an entry editor, it is 'NEW'.  When it has just been
     *    written to the directory, it becomes 'NEW_WRITTEN'.
     *    
     *    @param entryStatus  The status of the entry as a constant
     *                        (one of {NORMAL, NEW, NEW_WRITTEN} ).
     */
     
    public void setStatus(int entryStatus) { this.status = entryStatus; }
    
    /**
     *    Returns the status of the entry as a status constant.  See
     *    status constants for more info.
     *
     *    @return entry status as one of {NORMAL, NEW, NEW_WRITTEN}.
     */
     
    public int getStatus() { return this.status; }

    /**
     *    Returns the status of the entry as a readable string.  See
     *    status constants for more info.
     *
     *    @return entry status as one of "Normal", "New", or "Newly Written".
     */
     
    public String getStringStatus() 
    { 
        switch (this.status)
        {
            case NORMAL:      return "Normal";
            
            case NEW:         return "New";
            
            case NEW_WRITTEN: return "Newly Written";
        }
        
        return "Unknown";
    }
    
    
    /**
     *    Returns whether the entry is new, (or just written to the directory),
     *    or whether it already existed in the directory.  
     *    @return true if entry status is NEW or NEW_WRITTEN.
     */
     
    public boolean isNewEntry() { return (this.status==NEW || this.status == NEW_WRITTEN); }
    
    /**
     *    Add a DN directly, without using an attribute.
     *    @param dn the distinguished name to add.
     *    @depricated use @setDN instead
     */
    
    public void putDN(DN dn)
    {
        setDN(dn);
    }

    
    public void setDN(DN dn)
    {
        this.dn = dn;
    }

    /**
     *    Returns the DN of this entry, or an empty DN if
     *    none has been set.
     *    @return the entry's distinguished name.
     */
    
    public DN getDN() 
    {
        return (this.dn==null)?new DN():this.dn;
    }
 
    /**
     *    Provides a string representation of the entry, as a set of
     *    attributes preceeded by a header of form 'entry = <DN>'
     */
    
    public String toString()
    {
        return ("entry = " + getDN().toString() + "\n status: " + 
                getStringStatus() + "\n" +
                super.toString());
    }
    
    /** 
     *    Utility class intended for recovering the value of a single
     *    value attribute as a string.
     *    @param id the attribute name
     *    @return the value of the attribute as a string.
     */
     
    public String getString(String id)
    {
        Attribute a = get(id);
        try
        {
            return a.get().toString();
        }
        catch (Exception e) 
        { 
            return null; 
        }    
    } 
    
    /**
     *    Returns the Entry's RDN. 
     *    @return RDN the lowest RDN of the entry's DN - e.g.
     *    'cn=fred' in 'cn=fred,ou=Frog Farmers,o=DemoCorp,c=au').
     */
     
    public RDN getRDN()
    {
        if (this.dn == null) {
			return null;
		}
            
        return this.dn.getLowestRDN();            
    }

}