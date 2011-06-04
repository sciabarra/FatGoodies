<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="publication" uri="futuretense_cs/publication.tld"
%><cs:ftcs>
<publication:load 
  name="theSite" 
  objectid='<%=ics.GetVar("pubid")%>'/>
<publication:get
   name="theSite"
   field="name"
   output="site"/>
<render:calltemplate       
   ttype='CSElement'
   tname='/fpLayout'
   tid='<%= ics.GetVar("eid") %>'
   site='<%= ics.GetVar("site") %>' 
   c='<%=ics.GetVar("AssetType") %>'
   cid='<%=ics.GetVar("id") %>'
   slotname='layout'>   
</render:calltemplate>
</cs:ftcs>