package com.example.manejosalas.controlador;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.example.manejosalas.DAO.SalaDAO;
import com.example.manejosalas.DAO.SolicitudDAO;
import com.example.manejosalas.entidad.Sala;
import com.example.manejosalas.entidad.SalaSolicitud;
import com.example.manejosalas.entidad.SalaSolicitudSemana;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServicio {

	@Autowired
	DataSourceConfig dataSourceConfig;
	
    @Autowired
    private SalaDAO repository;
    
    @Autowired
    private SolicitudDAO solicitudesDAO;
    
    public String exportReport(String nombre, String reportFormat, JasperPrint jasperPrint) throws FileNotFoundException, JRException, SQLException {
    	Path currentRelativePath = Paths.get("");
    	String s = currentRelativePath.toAbsolutePath().toString();    	    

        String path = s + "/reports";
                            
        String finalPath = path;
        
        if (reportFormat.equalsIgnoreCase("html")) {
        	finalPath = path + "/" + nombre + ".html";
            JasperExportManager.exportReportToHtmlFile(jasperPrint, finalPath);
        }
        else if (reportFormat.equalsIgnoreCase("pdf")) {
        	finalPath = path + "/" + nombre + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, finalPath);
        }
        else{
        	finalPath = path;
        }     

        return finalPath;
    }
    
    
    public String generateReport(int id, TipoReporte tipoReporte) throws JRException, SQLException, IOException{
    	
    	String finalPath = "";
    	
    	//Determine the kind of report
    	if(TipoReporte.SUPER_SALAS.compareTo(tipoReporte) == 0){
    		
    		//load file and compile it
            InputStream file = ResourceUtils.getURL("classpath:super_salas.jrxml").openStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(file);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "GESCOL");
            //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);        
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceConfig.getDataSource().getConnection());
            
            String currentDate = ( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" ) ).format( Calendar.getInstance().getTime() );            
            //            nombreReporte = String.valueOf(id) + "_" +  Calendar.getInstance().getTime().toString();          
            String auxDate = String.valueOf(id)+String.valueOf(Math.abs(currentDate.hashCode()));           
            
            finalPath = exportReport(auxDate, "pdf", jasperPrint);
    	}
    	else if(TipoReporte.ADMIN_SALAS_1.compareTo(tipoReporte) == 0){
    		
            Collection<SalaSolicitud> solicitudesSala = solicitudesDAO.findConsultas(id);
    		
    		/*
    		ArrayList<SalaSolicitud> solicitudesSala = new ArrayList<>();
    		
    		for(int i = 0; i < 5; i++){
    			
    			SalaSolicitud ss = new SalaSolicitud();
    			
    			ss.setEdificio(i);
    			ss.setSala(i + 2);
    			ss.setSolicitudes(i*3);
    			
    			solicitudesSala.add(ss);
    		}
            
            */
          //load file and compile it
            InputStream file = ResourceUtils.getURL("classpath:admin_salas.jrxml").openStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(file);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(solicitudesSala);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "GESCOL");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            String currentDate = ( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" ) ).format( Calendar.getInstance().getTime() );            
            //            nombreReporte = String.valueOf(id) + "_" +  Calendar.getInstance().getTime().toString();          
            String auxDate = String.valueOf(id)+String.valueOf(Math.abs(currentDate.hashCode()));           
            
            finalPath = exportReport(auxDate, "pdf", jasperPrint);
            
                            		
    		    		
    	}
    	else if(TipoReporte.ADMIN_SALAS_2.compareTo(tipoReporte) == 0){
    		
            Collection<SalaSolicitudSemana> solicitudesSala = solicitudesDAO.findDiaConsultas(id);
            /*
    		ArrayList<SalaSolicitudSemana> adminSalaDias = new ArrayList<>();
    		
    		for(int i = 0; i < 5; i++){
    			
    			SalaSolicitudSemana ss = new SalaSolicitudSemana();
    			
				ss.setDia(String.valueOf(i));
				ss.setSolicitudes(i*2);
    			
    			adminSalaDias.add(ss);
    		}
            */
            
            //load file and compile it
          //load file and compile it
            InputStream file = ResourceUtils.getURL("classpath:admin_salas_sub_dias.jrxml").openStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(file);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(solicitudesSala);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "GESCOL");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            String currentDate = ( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" ) ).format( Calendar.getInstance().getTime() );            
            //            nombreReporte = String.valueOf(id) + "_" +  Calendar.getInstance().getTime().toString();          
            String auxDate = String.valueOf(id)+String.valueOf(Math.abs(currentDate.hashCode()));           
            
            finalPath = exportReport(auxDate, "pdf", jasperPrint);              		
    		    		
    	}    	    	
    	
    	return finalPath;
    	
    }
    
    
    public void deleteDocument(String src) throws IOException{
    	
	    File fileExist = new File(src);
	    
	    BufferedWriter bw = new BufferedWriter(new FileWriter(fileExist));

	    fileExist.delete();
//    	bw.flush();	    
    	
	    bw.close();	    	    
    }
    
    @SuppressWarnings("deprecation")
	public String getPagePdf(String src, int page) throws IOException, DocumentException {
	    PdfReader reader = new PdfReader(src);
	    int n = reader.getNumberOfPages();
	    reader.close();
	    String path2 = "";
	    String path = "";
	    PdfStamper stamper;
	    
	    String[] pathBroken = src.split("/");
	    
	    
	    for(int i = 0; i < pathBroken.length - 1; i++){
	    	path += pathBroken[i] + "/";
	    }
	   	    
	    path2 = pathBroken[pathBroken.length - 1];
	    
	    for (int i = 1; i <= n; i++) {
	        reader = new PdfReader(src);
	        reader.selectPages(String.valueOf(i));
	        String path3 = String.format(path + "%s.pdf", i);
	        stamper = new PdfStamper(reader,new FileOutputStream(path3));
	        stamper.close();
	        reader.close();
	    }        
	    

	    PDFMergerUtility ut = new PDFMergerUtility();
	    
	    for(int i = 1; i < page + 1; i++){
	    	File f1 = new File(path + String.valueOf(i) + ".pdf");
	    	ut.addSource(f1);
	    }
    	
	    ut.setDestinationFileName(src);
	    ut.mergeDocuments();
	    
    	return src;    
    }
    
    public String combineTwoPDF(String path1, String path2) throws IOException{
    	
    	
    	PDFMergerUtility ut = new PDFMergerUtility();
    	
    	File f1 = new File(path1);
    	File f2 = new File(path2);
    	
    	ut.addSource(f1);
    	ut.addSource(f2);
    	
	    ut.setDestinationFileName(path1);
	    ut.mergeDocuments();
	    
    	return path1;    
    	
    	
    }
    
    public static boolean renameFile(File toBeRenamed, String new_name) {
        //need to be in the same path
        File fileWithNewName = new File(toBeRenamed.getParent(), new_name);
        /*
        if (fileWithNewName.exists()) {
            return false;
        }
        */
        // Rename file (or directory)
        return toBeRenamed.renameTo(fileWithNewName);
    }    
    
    
}

