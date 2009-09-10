package com.idega.core.ldap.client.naming;

import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.util.Vector;
import java.util.Enumeration;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import com.idega.core.ldap.client.cbutil.CBIntText;

/**
 *    A Data class that encapsulated the idea of an
 *    ldap Distinguished Name of the form:
 *    ou=frog farmers,o=frogcorp,c=au
 *    - and provides a bunch of utility methods for modifying
 *    and reading these values, especially the various bits
 *    of each rdn in various ways.  <p>
 *
 *    implements javax.naming.Name.<p>
 *
 *    Why don't we just use CompoundName or CompositeName?<br>
 *
 *    &nbsp;&nbsp;&nbsp;- basically because we're not supporting multiple
 *    naming systems - we're *only* supporting ldap.  So Name is implemented
 *    for support with existing jndi ftns, but also a lot of other stuff
 *    is needed for rdns, and multi-value rdns.  This could be architected
 *    as, say, compound name with another helper class, but that seems
 *    clumsy.<p>
 *
 */



public class DN implements Name
{
    // these variables are all there is!  Basically the magic is in the vector of
    // rdns - all the code below is simply utility stuff for parsing and manipulating
    // those rdns.

    private Vector RDNs;  // a list of segment RDNs, as strings, e.g. 'ou=frog farmers'
                          // element 0 is the 'root' RDN (i.e. 'c=au')
                          // element (RDNs.size()-1) is the lowest RDN (i.e. 'cn=fred').

    boolean binary = false; // whether the dn contains binary data, and should
                            // be base64 encoded before being written out...

    String errorString = "";

//    boolean empty = false;  // whether this is the blank DN "".

    /**
     *    Default constructor creates a DN with no value set.
     */

    public static String BLANKBASEDN = "World";

    public DN()
    {
        this.RDNs = new Vector();
    }

    /**
     *    Copy constructor creates a new DN with an item by item
     *    <i>copy</i> of the parameter DN.
     *
     *    @param the DN to be copied
     */

    public DN(DN copyMe)
    {
        try
        {
            this.RDNs = new Vector();

            if (copyMe != null)
            {
                for (int i=0; i<copyMe.size(); i++)
                {
                    try {
						add(new String(copyMe.get(i).getBytes(),"UTF-8"));
					}
					catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
                }
            }
        }
        catch (InvalidNameException e) // 'impossible' error - if copyMe is DN, how can this fail?
        {
            setError("error cloningDN " + copyMe.toString() + "\n" + e.toString()); clear();
        }
    }

    /**
     *    Main Constructor takes an ldap Distinguished Name string
     *    (e.g. 'ou=wombat botherers,o=nutters inc,c=au') and
     *    breaks it up into a vector of RDNs
     *
     *    @param ldapDN the ldap distinguished name to be parsed.
     *    @param escaped whether the name is escaped ldap or not...
     */

    public DN(String ldapDN)
    {
        try
        {
            this.RDNs = new Vector();

            if ("".equals(ldapDN) || BLANKBASEDN.equals(ldapDN))
            {
                return;
            }

            int start = 0;
            int end = NameUtility.next(ldapDN, 0, ',');

            // get the RDNs in the form xxx=xxx,xxx=xxx,xxx=xxx
            while (end!=-1)
            {
                String rdn = ldapDN.substring(start,end);

                add(0,rdn);
                start = end+1;
                end   = NameUtility.next(ldapDN, start, ',');
            }

            // ... and the last bit...
            add(0,ldapDN.substring(start).trim());

        }
        catch (InvalidNameException e)
        {
            setError("unable to make DN from " + ldapDN + "\n" + e.toString()); clear();
        }

    }




    /**
     *    This Constructor takes an existing jndi Name,
     *    And initialises itself by taking that Name's rdn elements
     *    an element at a time, and converting them to RDN objects.
     *
     *    @param ldapDN the ldap distinguished name to be parsed.
     */


    public DN(Name name)
    {
        try
        {
            this.RDNs = new Vector();

            if (name.isEmpty()) {
				return;
			}

            for (int i=0; i<name.size(); i++)
            {
                add(i,name.get(i));
            }
        }
        catch (InvalidNameException e)
        {
            setError("unable to create DN from name: " + name.toString()); clear();
        }
    }

/*
    public DN(byte[] name)
    {
        setError("Binary Distinguished Names not yet implemented ");
    }
*/

    /**
     *   Spits back the DN as an escaped ldap DN string
     *
     *   @returns ldap DN in normal form (e.g. 'ou=linux fanatics,o=penguin fanciers pty. ltd.,c=us')
     */

    public String toString()
    {
        String ldapDN = "";
        for (int i=0; i<this.RDNs.size(); i++) {
			ldapDN =  get(i) + (i!=0?",":"") + ldapDN;
		}
        if (ldapDN.endsWith(","))
        {
            if (ldapDN.charAt(ldapDN.length()-2) != '\\')
            {
                ldapDN = ldapDN.substring(0,ldapDN.length()-1);
            }
        }
        try {
			return new String(ldapDN.getBytes(),"UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ldapDN;
    }



    /**
     *   Spits back the DN as a tree-level formatted string (mainly for debugging)
     *
     *   @returns ldap DN in level form (e.g. <pre>\nou=linux fanatics \no=penguin fanciers pty. ltd.\n c=us\n</pre>)
     */

    public String toFormattedString()
    {
        String ldapDN = "";
        for (int i=0; i<this.RDNs.size(); i++) {
			ldapDN += get(i) + "\n";
		}
        return ldapDN;
    }

    /**
     *    a synonym for 'toString()' this returns the full ldap DN.
     *    @depricated - use toString().
     *    @returns the full ldap Distinguished Name
     */

    public String getDN() { return toString(); }
    /**
     *    gets the ldap 'class' (e.g. 'c' or 'cn') for a particular
     *    RDN.  If there are multiple attributes (for a multi-valued
     *    rdn) only the first is returned (XXX).
     *
     *    @param i the index of the RDN class to return.
     *    @returns the ldap class name for the specified RDN
     */

    public String getRDNAttribute(int i)
    {
        if (isEmpty()) {
			return "";
		}
        if (i >= size()) {
			return "";
		}
        if (i < 0) {
			return "";
		}

        return getRDN(i).getAtt();
    }

    /**
     *    gets the ldap value (e.g. 'au' or 'Silverstone, Alicia') for a particular
     *    RDN.  If there are multiple values, only the first is
     *    returned (XXX).
     *
     *    @param i the index of the RDN value to return.
     *    @returns the actual value for the specified RDN.
     */

    public String getRDNValue(int i)
    {
        if (isEmpty()) {
			return "";
		}
        if (i >= size()) {
			return "";
		}
        if (i < 0) {
			return "";
		}

        return getRDN(i).getRawVal();
    }

    /**
     *   dumps the dn in a structured form, demonstrating parsing.
     */

    public void debugPrint()
    {
        System.out.print("\n");
        for (int i=0; i<size(); i++)
        {
            //String rdn = get(i);
            System.out.print("element [" + i + "]  = " + get(i).toString() + "\n");
            getRDN(i).dump();

        }
    }

    /**
     *    gets the full RDN  (e.g. 'c=au' or 'cn=Englebert Humperdink') for a particular
     *    indexed RDN.
     *
     *    @param i the index of the RDN to return.
     *    @returns the ldap RDN string name for the specified index
     */

    public void setRDN(RDN rdn, int i)
    {
        if (i<size() && i>= 0) {
			this.RDNs.setElementAt(rdn, i);
		}
    }


    /**
     *    gets the full RDN  (e.g. 'c=au' or 'cn=Englebert Humperdink') for a particular
     *    indexed RDN.
     *
     *    @param i the index of the RDN to return.
     *    @returns the ldap RDN string name for the specified index
     */

    public RDN getRDN(int i)
    {
        if (i==0 && isEmpty()) {
			return new RDN();  // return empty RDN for empty DN
		}

        if (i<0) {
			return new RDN();
		}
        if (i >= size()) {
			new RDN();
		}

        return (RDN) this.RDNs.elementAt(i);
    }

    /**
     *    Returns the root RDN as a string (e.g. 'c=au')
     *
     *    @returns the root RDN string.
     */

    public RDN getRootRDN()
    {
        if (isEmpty()) {
			return new RDN("");
		}
		else {
			return getRDN(0);
		}
    }

    /**
     *    Gets the value of the lowest LDAP RDN.
     *    That is, the furthest-from-the-root class value of the DN.
     *    For example, 'ou=frog fanciers' in 'ou=frog fanciers,o=nutters,c=uk'
     *
     *    @return the lowest level ldap value for the DN
     */

    public RDN getLowestRDN()
    {
        return getRDN(size()-1);
    }

    /**
     *    roughly checks if an RDN is syntactically and schematically correct.
     *
     *    @param RDN the RDN string to check for correctness
     */

// XXX REFACTOR -> this method should be in the RDN class

    public static void checkRDN(String RDN) throws InvalidNameException
    {
//        if (RDN==null || "".equals(RDN)) return;
        if (RDN==null || "".equals(RDN)) {
			throw new InvalidNameException(CBIntText.get("Empty RDN, please enter a valid RDN. ") + ((RDN==null)?"<null>":CBIntText.get("The RDN value entered was: '")+RDN) + "'");
		}

        if (NameUtility.next(RDN,0,'=')<0) {
			throw new InvalidNameException(CBIntText.get("Invalid RDN, please enter a naming attribute followed by '=' followed by a name in the RDN field (for example, 'cn=Trudi). '") + ((RDN==null)?"<null>":CBIntText.get("The RDN value entered was: '")+RDN) + "'");
		}
		else if (NameUtility.next(RDN,0,'=')<1) {
			throw new InvalidNameException(CBIntText.get("Invalid RDN, please enter a naming attribute in the RDN field (for example, cn: 'cn=Trudi). '") + ((RDN==null)?"<null>":CBIntText.get("The RDN value entered was: '")+RDN) + "'");
		}
    }


    /**
     *    Adds an RDN to an existing DN at the highest level
     *    - mainly used internally
     *    while parsing a DN.
     *
     *    @param RDN the RDN string to apend to the DN
     */

    public void addParentRDN(String rdn)
    {
        try
        {
            add(0,rdn);
        }
        catch (InvalidNameException e)
        {
            setError("DN.addParentRDN() \n" + e.toString());
        }

    }

    /**
     *    Adds a new 'deepest level' RDN to a DN
     *
     *    @param RDN the RDN to append to the end of the DN
     */

    public void addChildRDN(String rdn)
        throws InvalidNameException
    {
            add(rdn);
    }

    /**
     *    Adds a new 'deepest level' RDN to a DN
     *
     *    @param RDN the RDN to append to the end of the DN
     */
    public void addChildRDN(RDN rdn)
        throws InvalidNameException
    {
            add(rdn);
    }


    /**
     *    sets (or more often <i>re</i>sets) the lowest (furthest-from-root)
     *    value of the DN
     *
     *    @param value the (raw, unescaped) new lowest RDN value to overwrite the existing lowest RDN value with
     */

    // XXX code should be turned into wrapper for add(0,...) when that handles multi-val rdns properly
    public void setLowestRDNRawValue(String value)
    {
        try
        {
            RDN rdn = getRDN(size()-1);
            rdn.setRawVal(value);
        }
        catch (InvalidNameException e)
        {
            setError("DN.setLowestRDNRawValue: \n" + e.toString());
        }

    }

    /**
     *    Exchanges the value of an rdn att=val element, and returns
     */
    protected String exchangeRDNelementValue(String rdn, String value)
    {
        return rdn.substring(0,NameUtility.next(rdn,0,'=')) + "=" + value;

    }


   /**
    *    Check whether this DN is equal to another DN...
    *
    *    @param testDN the DN to compare against this DN
    */

    public boolean equals(DN testDN)
    {
       //XXX return (toString().equals(testDN.toString()));
        if (testDN == null) {
			return false;
		}

        if (testDN.size()!= size()) {
			return false;
		}

        for (int i=0; i<size(); i++)
        {
            if (getRDN(i).equals(testDN.getRDN(i)) == false) {
				return false;
			}
        }
        return true;
    }

    /**
     *  implement the object.equals(object) method for genericity and unit testing.
     *  Note that this is slower than DN.equals(DN), since it requires instanceof checks.
     * @param o a DN or Name object to test against
     */
    public boolean equals(Object o)
    {
        if (o == null) {
			return false;
		}
        if (o instanceof DN) {
			return equals((DN)o);
		}
		else if (o instanceof Name) {
			return (compareTo(o) == 0);
		}
		else {
			return false;  // cannot be equal in any sense if not a name
		}
    }
    /**
     *    Checks whether the testDN is a subset of the current DN,
     *    starting from the root.  Currently case insensitive.
     *
     *    @param testDN the subset DN to test against
     */

    public boolean startsWith(DN testDN)
    {
        return startsWith((Name)testDN);
    }

    /**
     *    Test if the DNs are identical except for the
     *    lowest RDN.
     *    In other words, test if they are leaves on the same branch.
     *
     *    @param testDN the putatitive sibling DN
     */

    public boolean sharesParent(DN testDN)
    {
        if (testDN.size()!= size()) {
			return false;
		}

        for (int i=0; i<size()-1; i++)
        {
            if ((testDN.getRDN(i).equals(getRDN(i)))==false) {
				return false;
			}
        }
        return true;
    }

    /**
     *    Return the full DN of this DN's immediate parent.
     *    In other words, return this DN after removing the lowest RDN.
     *
     *    @return the parent of this DN
     */

    public DN parentDN()
    {
        if (size()==1) {
			return new DN();  // return empty DN for top level DNs
		}

        DN newDN = new DN(this);
        newDN.RDNs.removeElementAt(size()-1);
        return newDN;
    }

    /**
     *    reverse the order of elements in a DN...
     */

    public void reverse()
    {
        Vector rev = new Vector();
        for (int i=this.RDNs.size()-1; i>=0; i--) {
			rev.add(this.RDNs.elementAt(i));
		}
        this.RDNs = rev;
    }

    /**
     *    Empties the DN of all RDNs.
     */
    public void clear()
    {
        this.RDNs.clear();
        this.errorString = null;
    }

    /**
     *    Overload this method for app specific error handling.
     */
    public void setError(String e)
    {
        this.errorString = e;
        System.out.println(e);
    }

    /**
     *    Whether there was an error using this DN (i.e. when creating it).
     */
    public boolean error()
    {
        return (this.errorString == null);
    }

    /**
     *    Gets the error message (if any) associated with this DN.
     */
    public String getError()
    {
        return this.errorString;
    }


    /**
     *    Prepare a dn for jndi transmission
     */
/*
    public void escape()
    {
         for (int i=0; i<size(); i++)
         {
             getRDN(i).escape();
         }
    }
*/
    /**
     *    Unescape a dn that has been *normally* escaped using ldap v3 (i.e. by the
     *    preceeding ftn.).
     */
/*
    public void unescape()
        throws InvalidNameException
    {
         for (int i=0; i<size(); i++)
         {
             getRDN(i).unescape();
         }
    }
*/
    /**  (Obsolete)
     *    Unescape a dn that has been returned by jndi, that may contain either
     *    ldap v2 escaping, or the multiple-slash wierdness bug.
     */
/*
    public void unescapeJndiReturn()
        throws InvalidNameException                // shouldn't happen...
    {
         for (int i=0; i<size(); i++)
         {
             getRDN(i).unescapeJndiReturn();
         }
    }
*/

    /**
     *    Add an RDN to the end of the DN.
     */
    public Name add(RDN rdn)
    {
        //RDNs.insertElementAt(rdn,size());
        add(size(), rdn);
        return this;
    }

    /**
     *    The core method for adding RDN objects to the name.
     *    Called by all add methods.
     *    @param posn the position in the DN to add the RDN at (0 = root)
     *    @param rdn the RDN to add (may be multi-valued).
     */

    public Name add(int posn, RDN rdn)
    {
        this.RDNs.insertElementAt(rdn,posn);
        return this;
    }



    //    NN   N      A       MM   MM  EEEEEE
    //    NNN  N     A A      M MMM M  EE
    //    N NN N    A   A     M  M  M  EEEE         (Interface Def.)
    //    N  NNN   AAAAAAA    M  M  M  EE
    //    N   NN  AA     AA   M  M  M  EEEEEE

    /*
     *    Adds a single component at a specified position within this name.
     */

    // These two ftns should be used by all code to add rdns... the RDN array
    // should not be accessed directly.

    public Name add(int posn, String rdn)
        throws InvalidNameException
    {
        RDN r = new RDN(rdn);             // may throw invalidName Exception
        add(posn, r);
        return this;
    }

    /*
     *    Adds a single component to the end of this name.
     */

    public Name add(String rdn)
        throws InvalidNameException
    {
        RDN r = new RDN(rdn);             // may throw invalidName Exception
        add(size(), r);
        return this;
    }


    /*
     *    Adds the components of a name -- in order -- at a specified position within this name.
     */

    public Name addAll(int posn, Name n)
        throws InvalidNameException
    {
         Enumeration e = n.getAll();
         while (e.hasMoreElements()) {
			add(posn++, e.nextElement().toString());
		}
         return this;
    }


    /*
     *    Adds the components of a name -- in order -- to the end of this name.
     */

    public Name addAll(Name suffix)
        throws InvalidNameException
    {
         Enumeration e = suffix.getAll();
         while (e.hasMoreElements()) {
			add(e.nextElement().toString());
		}
         return this;
    }


    /*
     *    Generates a new copy of this name.
     */
    public Object clone()
    {
        return new DN(this);
    }


    /*
     *     Compares this name with another name for order.
     *     ... for the time being, ordering is alphabetical by rdns ordered
     *     right to left.  Damn but the ldap rdn ordering system is screwed.
     */

    public int compareTo(Object obj)
    {
        int val = 0;
        int pos = 1;
        if (obj instanceof Name)
        {
            Name compareMe = (Name)obj;
            int size = size();
            int compSize = compareMe.size();
            while (val == 0)
            {
                String RDN = get(size-pos);
                String compRDN = compareMe.get(compSize-pos);
                int rdnOrder = RDN.compareTo(compRDN);

                if (rdnOrder != 0) {
					return rdnOrder;  // return alphabetic order of rdn.
				}

                pos++;
                if (pos>size || pos>compSize)
                {
                    if (size==compSize) {
						return 0;  // names are equal
					}
                    if (pos>size) {
						return -1;  // shorter dns first
					}
					else {
						return 1;
					}
                }
            }
        }
		else {
			throw new ClassCastException("non Name object in DN.compareTo - object was " + obj.getClass());
		}

        return 0; // never reached.
    }


    /*
     *    Determines whether this name ends with a specified suffix.
     */

    public boolean endsWith(Name n)
    {
          return false;
    }


    /*
     *    Retrieves a component of this name.  Returns zero length string for
     *    an empty DN's first element - otherwise throws a ArrayIndexException.
     *    (is this correct behaviour?).
     */

    public String get(int posn)
    {
        if (posn==0 && isEmpty()) {
			return "";  // return empty string for empty DN
		}

        return this.RDNs.elementAt(posn).toString();
    }

    /*
     *    Retrieves the components of this name as an enumeration of strings.
     */

    public java.util.Enumeration getAll()
    {
        DXNamingEnumeration ret = new DXNamingEnumeration();
        for (int i=0; i<size(); i++) {
			ret.add(get(i));
		}
        return ret;
    }

    /*
     *    Creates a name whose components consist of a prefix of the components of this name.
     */

    public Name getPrefix(int posn)
    {
        DN returnMe = new DN();
        try
        {
            for (int i=0; i<posn; i++) {
				returnMe.add(get(i));
			}
            return returnMe;
        }
        catch (InvalidNameException e)
        {
            System.err.println("unexpected error in DN:\n  " + e);
            return new DN();
        }
    }

    /*
     *    Creates a name whose components consist of a suffix of the components in this name.
     */

    public Name getSuffix(int posn)
    {
        DN returnMe = new DN();
        for (int i=posn; i<size(); i++)
        {
            returnMe.add(new RDN(getRDN(i)));
        }
        return returnMe;
    }

   /**
    *    returns true if this is an 'empty', or root DN (i.e. \"\")
    *    @returns empty status
    */

    public boolean isEmpty()
    {
        return (size()==0);
    }


    /*
     *    Removes a component from this name.
     */

    public Object remove(int posn)
    {
          return this.RDNs.remove(posn);
    }

   /*
    *     Returns the number of components in this name,
    *   and hence the level (the number
    *    of nodes from root) of the DN.
    */

    public int size()
    {
        return this.RDNs.size();
    }

   /*
    *     Returns the number of components in this name,
    *   and hence the level (the number
    *    of nodes from root) of the DN.
    *   (synonym of 'size()'.  Why java didn't standardise on just one...)
    */
/*
    public int length()
    {
        return RDNs.size();
    }
*/
    /*
     *    Determines whether this name starts with a specified prefix.
     */

    public boolean startsWith(Name n)
    {
        int pos = 0;
        Enumeration e = n.getAll();
        while (e.hasMoreElements()) {
			if (e.nextElement().toString().equalsIgnoreCase(get(pos++).toString())==false) {
				return false;
			}
		}

        return true;  // falls through - all tested components must be equal!
    }



}
















