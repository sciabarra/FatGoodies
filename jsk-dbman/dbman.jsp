<% 
String s = "jdbc:hsqldb:file:"+getServletContext().getRealPath("/")
+"../../default/data/hypersonic/csDB";
org.hsqldb.util.DatabaseManagerSwing.main(new String[]{ "--url",s }); 
%>
