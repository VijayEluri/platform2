/*
 * Created on 8.7.2003 by  tryggvil in project com.project
 */
package com.idega.core.file.business;

import java.rmi.RemoteException;

import com.idega.business.IBOService;
import com.idega.core.file.data.ICFile;

/**
 * FileSystem: This is the interface to the file system in idegaWeb.
 * This interface should be used whenever possible instead of com.idega.block.media.business.MediaBusines.
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public interface ICFileSystem extends IBOService
{
	/**
		 * Initializes the filesystem correctly
		 * @throws RemoteException
		 */
	public void initialize()throws RemoteException;
	
	/**
	 * Get the URI to a file on the webserver.
	 * @param file The file to get the url to
	 * @return A String which is the url to the file
	 * @throws RemoteException
	 */
	public String getFileURI(ICFile file)throws RemoteException;
	/**
	 * Get the URI to a file on the webserver.
	 * @param fileId The id of the file to get the url to
	 * @return A String which is the url to the file
	 * @throws RemoteException
	 */
	public String getFileURI(int fileId)throws RemoteException;
	
	/**
	 * DRAFT OF METHODS TO BE IN THIS CLASS:
	 * 
	 * public ICFile getPublicRootFolder();
	 * public ICFile getUserHomeFolder(ICUser user);
	 * public ICFile getGroupHomeFolder(ICGroup group);
	 * 
	 * public ICFile createFileUnderPublicRoot(ICUser creator,String name);
	 * public ICFile createFileUnderUserHome(ICUser creator,String name);
	 * public ICFile createFileUnderGroupHome(ICUser creator,ICGroup group,String name);
	 * 
	 * public ICFile createFileUnderFolder(ICUser creator,ICFile folder,String name);
	 * 
	 * public void deleteFile(ICFile file,ICUser committer);
	 * public void moveFileUnder(ICFile file,ICFile oldFolder,ICFile newFolder,ICUser committer);
	 */
}