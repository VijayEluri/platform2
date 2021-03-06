/**
 * AccountingServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package is.idega.idegaweb.member.isi.block.accounting.webservice.general.client;

public class AccountingServiceServiceLocator extends org.apache.axis.client.Service implements is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingServiceService {

    public AccountingServiceServiceLocator() {
    }


    public AccountingServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AccountingServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AccountingService
    private java.lang.String AccountingService_address = "http://felixtest.sidan.is/services/AccountingService";

    public java.lang.String getAccountingServiceAddress() {
        return this.AccountingService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AccountingServiceWSDDServiceName = "AccountingService";

    public java.lang.String getAccountingServiceWSDDServiceName() {
        return this.AccountingServiceWSDDServiceName;
    }

    public void setAccountingServiceWSDDServiceName(java.lang.String name) {
        this.AccountingServiceWSDDServiceName = name;
    }

    public is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingService getAccountingService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(this.AccountingService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAccountingService(endpoint);
    }

    public is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingService getAccountingService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
        	is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingServiceSoapBindingStub _stub = new is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getAccountingServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAccountingServiceEndpointAddress(java.lang.String address) {
        this.AccountingService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingService.class.isAssignableFrom(serviceEndpointInterface)) {
            	is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingServiceSoapBindingStub _stub = new is.idega.idegaweb.member.isi.block.accounting.webservice.general.client.AccountingServiceSoapBindingStub(new java.net.URL(this.AccountingService_address), this);
                _stub.setPortName(getAccountingServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AccountingService".equals(inputPortName)) {
            return getAccountingService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:accounting", "AccountingServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (this.ports == null) {
            this.ports = new java.util.HashSet();
            this.ports.add(new javax.xml.namespace.QName("urn:accounting", "AccountingService"));
        }
        return this.ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AccountingService".equals(portName)) {
            setAccountingServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
