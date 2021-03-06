/**
 * FelagsmadurLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package is.idega.idegaweb.member.isi.block.leagues.webservice;

public class FelagsmadurLocator extends org.apache.axis.client.Service implements is.idega.idegaweb.member.isi.block.leagues.webservice.Felagsmadur {

    public FelagsmadurLocator() {
    }


    public FelagsmadurLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FelagsmadurLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FelagsmadurSoap
    //TODO SIGTRYGGUR breyta thessu i alvoru slodina
    private java.lang.String FelagsmadurSoap_address = "http://ksi2.skyrr.is/ssl/vefthjon_felix/felagsmadur.asmx";

    public java.lang.String getFelagsmadurSoapAddress() {
        return this.FelagsmadurSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FelagsmadurSoapWSDDServiceName = "FelagsmadurSoap";

    public java.lang.String getFelagsmadurSoapWSDDServiceName() {
        return this.FelagsmadurSoapWSDDServiceName;
    }

    public void setFelagsmadurSoapWSDDServiceName(java.lang.String name) {
        this.FelagsmadurSoapWSDDServiceName = name;
    }

    public is.idega.idegaweb.member.isi.block.leagues.webservice.FelagsmadurSoap_PortType getFelagsmadurSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(this.FelagsmadurSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFelagsmadurSoap(endpoint);
    }

    public is.idega.idegaweb.member.isi.block.leagues.webservice.FelagsmadurSoap_PortType getFelagsmadurSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            is.idega.idegaweb.member.isi.block.leagues.webservice.FelagsmadurSoap_BindingStub _stub = new is.idega.idegaweb.member.isi.block.leagues.webservice.FelagsmadurSoap_BindingStub(portAddress, this);
            _stub.setPortName(getFelagsmadurSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFelagsmadurSoapEndpointAddress(java.lang.String address) {
        this.FelagsmadurSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (is.idega.idegaweb.member.isi.block.leagues.webservice.FelagsmadurSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                is.idega.idegaweb.member.isi.block.leagues.webservice.FelagsmadurSoap_BindingStub _stub = new is.idega.idegaweb.member.isi.block.leagues.webservice.FelagsmadurSoap_BindingStub(new java.net.URL(this.FelagsmadurSoap_address), this);
                _stub.setPortName(getFelagsmadurSoapWSDDServiceName());
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
        if ("FelagsmadurSoap".equals(inputPortName)) {
            return getFelagsmadurSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "Felagsmadur");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (this.ports == null) {
            this.ports = new java.util.HashSet();
            this.ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "FelagsmadurSoap"));
        }
        return this.ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FelagsmadurSoap".equals(portName)) {
            setFelagsmadurSoapEndpointAddress(address);
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
