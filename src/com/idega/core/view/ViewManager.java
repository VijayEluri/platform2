/*
 * $Id: ViewManager.java,v 1.13 2005/06/22 14:03:20 tryggvil Exp $
 * Created on 2.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.FacesUtil;


/**
 * This class is responsible for managing the "ViewNode" hierarchy.<br>
 * <br>
 * 
 *  Last modified: $Date: 2005/06/22 14:03:20 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.13 $
 */
public class ViewManager implements Singleton {
	
	private static Instantiator instantiator = new Instantiator() 
		{ 
			public Object getInstance(Object parameter) {
				IWMainApplication iwma = null;
				if (parameter instanceof FacesContext) {
					iwma = IWMainApplication.getIWMainApplication((FacesContext) parameter);
				}
				else {
					iwma = (IWMainApplication) parameter;
				}
				return new ViewManager(iwma);
			}
		};
	
	private ViewNode rootNode;
	private ViewNode workspaceNode;
	private IWMainApplication iwma;
	private boolean showMyPage=false;
	
	public static ViewManager getInstance(IWMainApplication iwma){
		return (ViewManager) SingletonRepository.getRepository().getInstance(ViewManager.class, instantiator, iwma);
	}
	
	public static ViewManager getInstance(FacesContext context){
		return (ViewManager) SingletonRepository.getRepository().getInstance(ViewManager.class, instantiator, context);
	}
	
	private ViewManager(IWMainApplication iwma){
		this.iwma=iwma;
	}
	
	public void initializeStandardViews(ViewHandler handler){
		
		setApplicationRoot(iwma,handler);
		
		
		try {

			Class applicationClass = RefactorClassRegistry.forName("com.idega.builder.app.IBApplication");
			FramedWindowClassViewNode builderNode = new FramedWindowClassViewNode("builder",getWorkspaceRoot());
			Collection roles = new ArrayList();
			roles.add(StandardRoles.ROLE_KEY_BUILDER);
			builderNode.setAuthorizedRoles(roles);
			builderNode.setWindowClass(applicationClass);
			builderNode.setJspUri(getWorkspaceRoot().getResourceURI());
			builderNode.setKeyboardShortcut(new KeyboardShortcut("2"));
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Class applicationClass = RefactorClassRegistry.forName("com.idega.user.app.UserApplication");
			FramedWindowClassViewNode userNode = new FramedWindowClassViewNode("user",getWorkspaceRoot());
			userNode.setKeyboardShortcut(new KeyboardShortcut("1"));
			
			Collection roles = new ArrayList();
			roles.add(StandardRoles.ROLE_KEY_USERADMIN);
			userNode.setAuthorizedRoles(roles);
			userNode.setWindowClass(applicationClass);
			userNode.setJspUri(getWorkspaceRoot().getResourceURI());
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Class applicationClass = RefactorClassRegistry.forName("com.idega.development.presentation.IWDeveloper");
			FramedWindowClassViewNode developerNode = new FramedWindowClassViewNode("developer",getWorkspaceRoot());
			Collection roles = new ArrayList();
			roles.add(StandardRoles.ROLE_KEY_DEVELOPER);
			developerNode.setAuthorizedRoles(roles);
			developerNode.setWindowClass(applicationClass);
			developerNode.setJspUri(getWorkspaceRoot().getResourceURI());
			developerNode.setKeyboardShortcut(new KeyboardShortcut("3"));
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if(showMyPage){
			DefaultViewNode myPageNode = new ApplicationViewNode("mypage",getWorkspaceRoot());
			myPageNode.setName("My Page");
			//TODO: Change this
			myPageNode.setJspUri("/idegaweb/bundles/com.idega.block.article.bundle/jsp/cmspage.jsp");
			myPageNode.setKeyboardShortcut(new KeyboardShortcut("5"));
		}

	}
	
	public ViewNode getWorkspaceRoot(){
		//ViewNode workspaceNode = getApplicationRoot().getChild("workspace");
		if(workspaceNode==null){
			DefaultViewNode node = new DefaultViewNode(iwma);
			node.setViewId("workspace");
			//getApplicationRoot().addChildViewNode(node);
			node.setParent(getApplicationRoot());
			//String jspUri = iwma.getBundle("com.idega.webface").getJSPURI("workspace.jsp");
			String jspUri = "/idegaweb/bundles/com.idega.workspace.bundle/jsp/workspace.jsp";
			node.setJspUri(jspUri);
			workspaceNode = node;
		}
		return workspaceNode;
	}

	public ViewNode getApplicationRoot(){
		//if(rootNode==null){
		//	DefaultViewNode node = new DefaultViewNode();
		//	node.setViewId("/");
		//	rootNode = node;
		//}
		return rootNode;
	}
	
	protected void setApplicationRoot(IWMainApplication iwma,ViewHandler rootViewhandler){
		DefaultViewNode node = new DefaultViewNode(iwma);
		node.setViewId("/");
		node.setViewHandler(rootViewhandler);
		rootNode = node;
	}
	
	
	public ViewNode getViewNodeForUrl(String url){
		
		ViewNode root = this.getApplicationRoot();
		
		ViewNode node = root.getChild(url);
		if(node!=null){
			return node;
		}
		else{
			//Return the rootNode if nothing found:
			return root;
		}
	}
	
	/**
	 * <p>
	 * Checks if the given node is in the hierarchy of the URI uri.<br>
	 * e.g. if the given node has the uri /workspace/content then this method returns true for the uri /workspace/content/documents.
	 * </p>
	 * @param node
	 * @param uri
	 * @return
	 */
	public boolean isNodeInHierarchy(ViewNode node,String uri){
		
		String nodeUri = node.getURI();
		if(uri!=null){
			if(uri.startsWith(nodeUri)){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * @param ctx
	 * @return
	 */
	public String getRequestUriWithoutContext(FacesContext ctx) {
		/*HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
		//String contextPath = request.getContextPath();
		//String fullRequestUri = request.getRequestURI();
		String contextPath = "/";
		String fullRequestUri = ctx.getExternalContext().getRequestServletPath()+ctx.getExternalContext().getRequestPathInfo();
		if(contextPath.equals("/")){
			return fullRequestUri;
		}
		else{
			String subPath = fullRequestUri.substring(contextPath.length());
			return subPath;
		}*/
		return FacesUtil.getRequestUri(ctx,false);
	}
	
	
	public ViewNode getViewNodeForContext(FacesContext context){
		String url = getRequestUriWithoutContext(context);
		return this.getViewNodeForUrl(url);
	}
	
	
	protected IWMainApplication getIWMainApplication(){
		return iwma;
	}
	
	/**
	 * Checks if the user has access to a node. This uses the role system.
	 * @param node
	 * @param user
	 * @return
	 */
	public boolean hasUserAcess(ViewNode node,IWUserContext userContext){
		Collection roles = node.getAuthorizedRoles();
		if(roles!=null){
			if(roles.size()>0){
				for (Iterator iter = roles.iterator(); iter.hasNext();) {
					String roleKey = (String) iter.next();
					if(getIWMainApplication().getAccessController().hasRole(roleKey,userContext)){
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}
	
}
