package com.idega.core.ldap.client.naming;

import java.util.*;

import javax.naming.*;
//import javax.naming.directory.*;

/**
 *    A simple class that implements NamingEnumeration, used to 
 *    bundle up search results 'n stuff.  Used by not-really-jndi
 *    ftns to wrap things nicely for higher level ftns that are
 *    expecting NamingEnumerations.
 *
 *    as usual, the enumeration can be enumerated using the model:
 *    <pre>
 *     while (myEnumeration.hasMoreElements())
 *          doSomethingWith(myEnumeration.nextElement();
 *    </pre>
 *    in addition, it supports the NamingEnumeration interface 
 *    equivalent, which (<b>in the future</b>) may also throw a NamingException.
 *    <pre>
 *     try
 *     {
 *         while (myEnumeration.hasMore())
 *             doSomethingWith(myEnumeration.next();
 *     }
 *     catch (NamingException e) { }
 *    </pre>
 *
 *     As a spot of convenience evil, it implements a sort() ftn as well...
 *     (Since it's not a 'dynamic' enumeration as a normal naming enumeration
 *     would be, but uses a vector base object), and also allows the enumeration
 *     to be dumped out as a vector or a string array :-) .
 */
 
public class DXNamingEnumeration implements NamingEnumeration
{
    private int pointer = 0;
    private ArrayList data;
    //private Exception e = null; // not currently used; implement Naming Exceptions
    
    
    /**
     *    A quickie class for ordering stuff using their intrinsic toString() methods...
     */
    class SimpleComparator implements Comparable
    {
        Object myObject;
        String compareString;
        
        public SimpleComparator(Object o) 
        {
            myObject = (o==null)?"null":o;
            
            if (myObject instanceof DXAttribute)
                compareString = ((DXAttribute)myObject).getID().toLowerCase();
            else
                compareString = myObject.toString().toLowerCase();    
        }
        
        public int compareTo(Object compObject) 
        { 
            return compareString.compareTo(compObject.toString()); 
        }
        
        public String toString() { return compareString; }
        
        public Object getObject() {return myObject; }
    }
                                // with this if needed in future...
    /**
     *    The constructor does nothing except initialise class variables.
     */        
    public DXNamingEnumeration() { data = new ArrayList(); }

    /**
     *    A quicky to wrap normal enumerations with as well...
     */
    public DXNamingEnumeration(Enumeration ne) 
    { 
        data = new ArrayList();
        while (ne.hasMoreElements())
            add(ne.nextElement());
    }



    /**
     *    The constructor takes another NamingEnumeration and uses
     *    it to initialise with.     
     */        
     
    public DXNamingEnumeration(NamingEnumeration ne) 
    { 
        data = new ArrayList();
        if (ne!=null)
            while (ne.hasMoreElements())
                add(ne.nextElement());
    }

    /**
     *    A convenience constructor to wrap an existing ArrayList.
     *    Note that since the enumeration is supposed to be read-only,
     *    the initialising ArrayList is <i>not</i> cloned, but used as is.
     */

    public DXNamingEnumeration(ArrayList listData)
    {
        data = listData;
    }

    /**
     *    Adds an object to the enumeration.
     *    @param o object to be added.
     */        
    public void add(Object o)        { data.add(o); }

    /**
     *    Removes an object from the enumeration.
     *    @param o the object to be removed.
     */        
    public void remove(Object o)        { data.remove(o); }
    
    
    /** 
     *    Enumerations can't usually be re-used.  Sometimes it would be
     *    nice though... this resets the enumeration so you can reread it,
     *    which is useful for debugging (i.e. you can print it before use...)
     */
    public void reset()              { pointer = 0; }
    
    /**
     *    Not really necessary, this returns the number of elements in the
     *    enumeration.
     *    @return number of objects in enumeration
     */
    public int size()                { return data.size(); }
    
    /* 
     *    identical in ftn to hasMoreElements().  In future, this may
     *    throw NamingEnumerationExceptions.
     *    @return true if more elements are available.
     */
    public boolean hasMore()         { return (pointer < data.size()); }
    
    /*
     *    standard enumeration ftn.
     *    @return true if more elements are available
     */
    public boolean hasMoreElements() { return hasMore(); }
        
    /*
     *    identical in ftn to nextElement().
     *
     *    @return returns the next element in the enumeration.
     */        
    public Object next() throws NoSuchElementException
    {
        try
        { return data.get(pointer++); }
        catch (ArrayIndexOutOfBoundsException e)
        { throw new NoSuchElementException(); }
    }    
    
    /*
     *    standard enumeration ftn.
     *
     *    @return returns the next element in the enumeration.
     */        
    public Object nextElement() throws NoSuchElementException { return next(); }
    
    
    /**
     *    This method attempts to order the components of the
     *    SimpleEnumeration using their intrinsic 'toString()'
     *    methods (this may be meaningless for some components).
     */
     
    // XXX there may be a a heft time hit for using this...
     
    public DXNamingEnumeration sort()
    {
        int size = data.size();
        // wrap the contents of the data ArrayList in SimpleComparators, and
        // dump them to an array...
        SimpleComparator[] a = new SimpleComparator[size];
        for (int i=0; i<size; i++)
            a[i] = new SimpleComparator(data.get(i));

        // use the built in static Arrays.sort method
        Arrays.sort(a);
        
        // reload the ArrayList with the sorted data
        data.clear();
        for (int i=0; i<size; i++)
            data.add(a[i].getObject());

        return this;            
    }
    
    /**
     *    A simple existance test against the core ArrayList list of
     *    objects.
     */   
      
    public boolean contains(Object test)
    {
        return data.contains(test);
    }
    
    /** 
    *    Included for Naming Enumeration compatibility... does nothing,
    *    'cause DXNamingEnumeration isn't really an enumeration, and
    *    has already slurped all the data... :-)
    */
    public void close() {;} 
    
    public String toString()  // mainly used for debugging
    {
        StringBuffer ret = new StringBuffer();
        for (int i=0; i<data.size(); i++)
        {
            Object o = data.get(i);
            ret.append((o==null)?"null":o.toString() + "\n");
        }    
        return ret.toString();   
    }
    
    public Object[] toArray()
    {
        return data.toArray();
    } 
     
    
    public String[] toStringArray()
    {
        String[] ret = new String[data.size()];
        for (int i=0; i<data.size(); i++)
        {
            Object o = data.get(i);
            ret[i] = ((o==null)?null:o.toString());
        }   
        return ret; 
    } 
 
    public ArrayList getArrayList() { return data; }
}