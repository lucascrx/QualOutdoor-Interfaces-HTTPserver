<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
//creation d'une requete POST
<body>
    <form method=post action="welcomeURL" enctype="multipart/form-data">
        <p>
        <label for="user">user </label>
        <input type="text" name="username" id="iduser"/>
        </p>
        <p>
        <label for="pass">password: </label>
        <input type="password" name="password" id="pwd"/>
        </p>
        <p>
        <label for="mail">fichier: </label>
        <input type="file" name="fileToUpload" id="myfile"/>
        </p> 
        <p>
          <input type="submit" value="OK"/>
        </p> 
      </form>   
</body>
</html>