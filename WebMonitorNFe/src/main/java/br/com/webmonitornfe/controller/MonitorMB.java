package br.com.webmonitornfe.controller;

import java.io.StringReader;
import java.net.URL;
import java.security.Security;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import br.inf.portalfiscal.www.nfe.wsdl.nfestatusservico2.NfeStatusServico2Stub;

@ManagedBean
@RequestScoped
public class MonitorMB {
	
	private String habilitaImagemVerde = "display: none;";
	private String habilitaImagemAmarelo = "display: none;";
	private String habilitaImagemVermelho = "display: none;";	
	
	@PostConstruct
	public void init(){
		verificarStatus();
	}
	
	public void verificarStatus(){
		int codigoStatus = processarRetorno(consultarStatusNfe());
		switch (codigoStatus) {
		case 107:
			habilitaImagemVerde = "display: block;margin-left: auto;margin-right: auto;";
			habilitaImagemAmarelo = "display: none;";
			habilitaImagemVermelho = "display: none;";
			break;
		case 108:
			habilitaImagemAmarelo = "display: block;margin-left: auto;margin-right: auto;";
			habilitaImagemVermelho = "display: none;";
			habilitaImagemVerde = "display: none;";
			break;
		case 109:
			habilitaImagemVermelho = "display: block;margin-left: auto;margin-right: auto;";
			habilitaImagemAmarelo = "display: none;";
			habilitaImagemVerde = "display: none;";
			break;		
		}
	}
	
	private int processarRetorno(String xml){
		int codStatus = 0;
		SAXBuilder builder = new SAXBuilder();
		try{
			Document documento = (Document) builder.build(new InputSource(new StringReader(xml)));
			Element noPrincipal = documento.getRootElement();				
			Element no = (Element) noPrincipal.getChildren().get(2);
			codStatus = Integer.parseInt(no.getValue());		
		}catch(Exception e){
			e.printStackTrace();			
		}	
		return codStatus;
	}
	
	private String consultarStatusNfe(){
		String retornoXml = "";
		try {           
            String codigoEstado = "41";
            URL urlWebService = new URL("https://homologacao.nfe2.fazenda.pr.gov.br/nfe/NFeStatusServico2?wsdl");             
            String caminhoCertificado = "caminhodopfx";  
            String senhaCertificado = "teste";  
            String arquivoCacerts = "caminhodocacerts";  
  
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");  
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());  
  
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");  
  
            System.clearProperty("javax.net.ssl.keyStore");  
            System.clearProperty("javax.net.ssl.keyStorePassword");  
            System.clearProperty("javax.net.ssl.trustStore");  
  
            System.setProperty("javax.net.ssl.keyStore", caminhoCertificado);  
            System.setProperty("javax.net.ssl.keyStorePassword", senhaCertificado);  
  
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");  
            System.setProperty("javax.net.ssl.trustStore", arquivoCacerts);  
  
            StringBuilder xml = new StringBuilder();  
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")  
                .append("<consStatServ versao=\"2.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\">")  
                .append("<tpAmb>2</tpAmb>")  
                .append("<cUF>")  
                .append(codigoEstado)  
                .append("</cUF>")  
                .append("<xServ>STATUS</xServ>")  
                .append("</consStatServ>");  
  
            OMElement ome = AXIOMUtil.stringToOM(xml.toString());  
            NfeStatusServico2Stub.NfeDadosMsg dadosMsg = new NfeStatusServico2Stub.NfeDadosMsg();  
            dadosMsg.setExtraElement(ome);  
  
            NfeStatusServico2Stub.NfeCabecMsg nfeCabecMsg = new NfeStatusServico2Stub.NfeCabecMsg();  
           
            nfeCabecMsg.setCUF(codigoEstado);  
  
            nfeCabecMsg.setVersaoDados("2.00");  
            NfeStatusServico2Stub.NfeCabecMsgE nfeCabecMsgE = new NfeStatusServico2Stub.NfeCabecMsgE();  
            nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);  
  
            NfeStatusServico2Stub stub = new NfeStatusServico2Stub(urlWebService.toString());  
            NfeStatusServico2Stub.NfeStatusServicoNF2Result result = stub.nfeStatusServicoNF2(dadosMsg, nfeCabecMsgE);  
           
            retornoXml = result.getExtraElement().toString();
        } catch (Exception e) {  
            e.printStackTrace();  
        }		
		return retornoXml;
	}
	
//	public static void main(String args[]){
//		MonitorMB monitor = new MonitorMB();
//		monitor.consultarStatusNfe();
//	}

	public String getHabilitaImagemVerde() {
		return habilitaImagemVerde;
	}

	public void setHabilitaImagemVerde(String habilitaImagemVerde) {
		this.habilitaImagemVerde = habilitaImagemVerde;
	}

	public String getHabilitaImagemAmarelo() {
		return habilitaImagemAmarelo;
	}

	public void setHabilitaImagemAmarelo(String habilitaImagemAmarelo) {
		this.habilitaImagemAmarelo = habilitaImagemAmarelo;
	}

	public String getHabilitaImagemVermelho() {
		return habilitaImagemVermelho;
	}

	public void setHabilitaImagemVermelho(String habilitaImagemVermelho) {
		this.habilitaImagemVermelho = habilitaImagemVermelho;
	}
}
