This is a template project for Fatwire content server to edit and deploy templates with eclipse.

You have to import it in eclipse, and set the eclipse variable 
CS_CONTEXTDIR to point to the "cs" folder of your local jumpstart kit.

Then configure the ElementDeployer.prp to point to your deployment content server.

Now you can create your content user web (templates under web/Template, 
elements under web/CSElement and web/Element).

You have to use the extension .jspf when editing jsp in eclipse, but a .jspf is deployer as ".jsp".

Please note that a toplevel template like "web/Template/topLevel.jspf" will be deployed as
"Typeless/topLevel.jsp" while a CSElement "web/CSElement/topLevel.jspf" will be deployed as "topLevel.jsp" in the ElementCatalog.


The deployer deploys only code. You have to create your assets (Template and CSElement and SiteEntries) to use the code appropriately.

If you run the ElementDeployer class from Eclipse (with no parameter) it will look for elements to deploy, it will deploy the existing ones, then it will go in a loop looking for a code change.





