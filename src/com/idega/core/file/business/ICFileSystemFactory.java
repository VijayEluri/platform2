/*
 * Created on 8.7.2003 by  tryggvil in project com.project
 */
package com.idega.core.file.business;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWApplicationContext;

/**
 * ICFileSystemFactory: This is the class to use to fetch an instance of ICFileSystem
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public final class ICFileSystemFactory
{

	/**
	 * This class should not be instantiated
	 */
	private ICFileSystemFactory()
	{
	}
	
	/**
	 * This method fetches the instance of ICFileSystem for the current working application
	 * @param iwac The ApplicationContext for the working application
	 * @return the ICFileSystem instance
	 */
	public static ICFileSystem getFileSystem(IWApplicationContext iwac)throws RemoteException{
		/*Class serviceClass=null;
		try
		{
			//TODO: Remove hardcoding of serviceclass:
			serviceClass = Class.forName("com.idega.block.media.business.MediaFileSystem");
		}
		catch (ClassNotFoundException e)
		{
			throw new RemoteException("ICFileSystemFactory.getFileSystem:"+e.getClass()+":"+e.getMessage());
		}*/
		return (ICFileSystem)IBOLookup.getServiceInstance(iwac,ICFileSystem.class);
	}
	
}