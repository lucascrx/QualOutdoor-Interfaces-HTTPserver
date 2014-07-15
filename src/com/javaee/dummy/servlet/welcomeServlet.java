package com.javaee.dummy.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;













import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/*Servlet de prototype qui définit elle seule le comportement
 * du serveur http
*/

public class welcomeServlet extends HttpServlet  {
  
  public static final String JSP_TO_GET_REQUEST= "/WEB-INF/welcome_get.jsp";
  
  
  
  public welcomeServlet() {
    super();
    // TODO Auto-generated constructor stub
  }
  
  /*A la reception d'une requete GET on transmet une jsp qui permetta
   * au client de préparer une requete POST a destination de cette meme
   * servlet*/
  
  protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    this.getServletContext().getRequestDispatcher(JSP_TO_GET_REQUEST)
        .forward(request, response);
  }
  
  /*A la réception d'une requete POST on récupère tous les champs transmis
   * et on affiche leur contenu
   */
  protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    //hashmap qui récupère les paramètres de nature INPUT
    HashMap<String,String> inputsRecieved = new HashMap<String,String>();
    //hashmap qui récupère les paramètres fichiers
    HashMap<String,FileToUpload> filesRecieved = new HashMap<String,FileToUpload>();
    try{
      /*Dans ce cas on ne peut pas appliquer la methode classique get parameters
       * car le formulaire est constitué de plusieurs parties séparées d'un délimiteur
       * (multipart/form_data)
       * 
       * On parse donc la request pour recuperer les paramètres
      */
      List<FileItem> formItems = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
      //traitement pour chaque paramètre
      for(FileItem item : formItems){
         
        if(item.isFormField()){//cas d'un input elementaire
          inputsRecieved.put(item.getFieldName(), item.getString());
        }else{//autre type d'input c'est donc forcement un fichier
          FileToUpload file = new FileToUpload(item.getName(),item.getInputStream());
          filesRecieved.put(item.getFieldName(),file);
          
        }
        
      }
    
    //on écrit le résultat dans un fichier log
    
    //recherche des détails de la requete
      
    //DATE  
    String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
    //CLIENT
    String userAddr = request.getRemoteAddr();
    //PORT 
    int port = request.getLocalPort();
    //NOMBRES D'INPUTS SIMPLES TRANSMIS
    int numInputs = inputsRecieved.size();
    //NOMBRES DE FICHIERS TRANSMIS
    int numFiles = filesRecieved.size();
    //ouverture du fichier de log
    File fichier = new File("log.txt");
    if(!fichier.exists()){
      fichier.createNewFile();
    }
    //écriture dans le fichier de log
    FileWriter ecrivain = new FileWriter (fichier.getName(),true);
    ecrivain.write("\r\n");
    ecrivain.write("\r\n");
    ecrivain.write("=========================================");
    ecrivain.write("=======================================");
    ecrivain.write("\r\n");
    ecrivain.write("HTTP POST request recieved at "+timeStamp);
    ecrivain.write("\r\n");
    ecrivain.write("request from "+ userAddr +" asking on port "+port);
    ecrivain.write("\r\n"); 
    ecrivain.write(numInputs+" input(s) recieved: \r\n");
   //écriture des inputs simples
    for(String key : inputsRecieved.keySet()){
      ecrivain.write("input: "+ key +" with value : "+ inputsRecieved.get(key));
      ecrivain.write("\r\n");
    }
    ecrivain.write("\r\n");
    ecrivain.write(numFiles+" file(s) recieved:");
    //écriture des fichiers
    for(String key : filesRecieved.keySet()){    
      InputStream temp = filesRecieved.get(key).getContent();
      BufferedReader lecteur = new BufferedReader(new InputStreamReader(temp));
      String message="";
      String line;
      while((line=lecteur.readLine())!=null){
        message = message + "\r\n" + line;
      }
      
      ecrivain.write("\r\n field name :" + key+" file name: "+ filesRecieved.get(key).getFileName() +"\r\nwith content : \r\n"+ message);
      
      ecrivain.write("\r\n");
      ecrivain.write("END OF FILE");
      ecrivain.write("\r\n");
    }
    ecrivain.write("\r\n");
    ecrivain.write("END OF REQUEST");
    ecrivain.write("\r\n");
    ecrivain.close();
   
    
    response.getWriter().print("post pris en compte \r\n ");
    response.getWriter().print("emplacement de sauvegarde : "+fichier.getAbsolutePath());
    
    /*
    //écriture du fichier dans la base de donnée
    
    //exemple avec le premier fichier de la hasmap de fichier
    
    String cle = filesRecieved.keySet().iterator().next();
    InputStream exemple = filesRecieved.get(cle).getContent();
    
    //on parse l'exemple pour remplir la bdd
    
    Scanner scan = new Scanner(exemple);
    scan.useDelimiter(",");
    
    try{
      String dbClassName = "com.mysql.jdbc.Driver";
    
      Class.forName(dbClassName);
      
      Properties p = new Properties();
      
      p.put("user","root");
      p.put("password", "");
      
      Connection c = DriverManager.getConnection("jdbc:mysql://192.168.1.17:3306/qualoutdoor_db",p);
      ecrivain.write("Connection with DB set \r\n");
      Statement state = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      
      //s'en suit la requete d'insertion : on commence par lire le ficher texte et le parser
      //selon les ",".
      ArrayList<Integer> parsedFile = new ArrayList<Integer>();
      while(scan.hasNext()){
        parsedFile.add(Integer.parseInt(scan.next()));
      }
      state.executeUpdate("INSERT INTO t_test (id_terminal,num_cellule,measured_value) VALUES('"+parsedFile.get(0)+"','"+parsedFile.get(1)+"','"+parsedFile.get(2)+"')");
      
     
      
    }catch(ClassNotFoundException e){
      // TODO Auto-generated catch block
      e.printStackTrace();
    }catch(SQLException e){
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    */
    
    
    
    
    
    
    }catch(FileUploadException e){
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
