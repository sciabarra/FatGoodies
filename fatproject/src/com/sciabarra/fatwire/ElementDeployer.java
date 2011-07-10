package com.sciabarra.fatwire;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class ElementDeployer {

	String cs = "http://localhost:8080/cs";
	String username = "fwadmin";
	String password = "xceladmin";
	String cm = cs + "/CatalogManager";
	String element = "web/Element";
	String cselement = "web/CSElement";
	String template = "web/Template";
	String propertyFile;

	HttpClient client = new HttpClient();
	HttpState state = new HttpState();
	boolean isTemplate = false;
	long pause = 1000;

	HashMap<String, Long> lastModifiedMap = new HashMap<String, Long>();

	ElementDeployer() throws FileNotFoundException, IOException {
		this("ElementDeployer.prp");
	}

	ElementDeployer(String propertyFile) throws FileNotFoundException,
			IOException {
		this.propertyFile = propertyFile;
		configure();
		client.setState(state);
		// client.getParams().setCookiePolicy(CookiePolicy.);
		// client.getHttpConnectionManager().getParams()//
		// .setConnectionTimeout(30000);

	}

	private void dumpCookie() {
		Cookie[] cookies = client.getState().getCookies();
		for (int i = 0; i < cookies.length; i++) {
			System.out.println(" - " + cookies[i].toExternalForm());
		}
	}

	private String slurp(InputStream in) throws IOException {
		StringBuffer sb = new StringBuffer();
		int c = in.read();
		while (c != -1) {
			// System.out.print((char)c);
			// if (c != '\r')
			sb.append((char) c);
			c = in.read();
		}
		int res = sb.lastIndexOf("<!--FTCS");
		// System.out.println("=== " + res);
		if (res == -1) {
			// System.out.println("<<< !!! " + sb);
		} else {
			// System.out.println("<<< " + sb.substring(res));
			sb.setLength(res - 1);
		}
		return sb.toString();
	}

	private String get(String url) throws HttpException, IOException {
		GetMethod get = new GetMethod(url);
		// System.out.println("\n>>> GET " + url);
		client.executeMethod(get);
		// dumpCookie();
		return slurp(get.getResponseBodyAsStream());
	}

	public void configure() throws FileNotFoundException, IOException {
		Properties prp = new Properties();
		prp.load(new FileReader(propertyFile));
		cs = prp.getProperty("cs");
		cm = cs + "/CatalogManager";
		username = prp.getProperty("username");
		password = prp.getProperty("password");
		element = prp.getProperty("element");
		cselement = prp.getProperty("cselement");
		template = prp.getProperty("template");
	}

	public String login() throws HttpException, IOException {
		String url = cm + //
				"?ftcmd=login" + //
				"&password=" + password + //
				"&username=" + username;
		return get(url);
	}

	public String logout() throws HttpException, IOException {
		String url = cm + //
				"?ftcmd=logout" + //
				"&killsession=true" + //
				"&username=" + username;
		return get(url);
	}

	SimpleDateFormat fmt = new SimpleDateFormat("hh:mm:ss ");

	public String upload(String element, File file) throws IOException {

		if (!file.getName().endsWith(".jspf")
				&& !file.getName().endsWith(".xml"))
			return null;

		if (!file.exists())
			return "NOT FOUND " + file;

		Long lastModified = lastModifiedMap.get(file.getAbsolutePath());
		if (lastModified == null) {
			lastModifiedMap.put(file.getAbsolutePath(),
					new Long(file.lastModified()));
		} else {
			if (file.lastModified() == lastModified.longValue())
				return null;
			else {
				lastModifiedMap.put(file.getAbsolutePath(),
						new Long(file.lastModified()));
			}
		}

		String filename = file.getName();
		String name = filename;
		// trim the 'f' from .jspf
		if (filename.endsWith(".jspf")) {
			name = name.substring(0, name.length() - 5);
			filename = filename.substring(0, filename.length() - 1);
		}
		if (filename.endsWith(".txt")) {
			name = name.substring(0, name.length() - 4) + ".xml";
		}

		element = element + "/" + name;

		String folder = "";
		int pos = element.lastIndexOf("/");
		if (pos != -1)
			folder = element.substring(0, pos);
		if (folder.length() == 0)
			if (isTemplate)
				folder = "Typeless";
			else {
				folder = "";
				element = element.substring(1);
			}

		String msg = "POST element=" + element + " folder=" + folder + " name="
				+ name + " file=" + file;

		if (false)
			return msg;
		// System.out.println(msg);

		PostMethod post = new PostMethod(cm);
		Part[] parts = { //
		new StringPart("tablename", "ElementCatalog"),
				new StringPart("ftcmd", "updaterow"),
				new StringPart("elementname", element),
				new StringPart("url_folder", folder),
				new FilePart("url", filename, file) };

		post.setRequestEntity(new MultipartRequestEntity(parts, post
				.getParams()));

		int result = client.executeMethod(post);
		msg += "\nresult=" + result;
		msg += " answer=" + slurp(post.getResponseBodyAsStream());
		return msg;
	}

	public void find(String path, File file) {
		// System.out.println("path="+path);
		if (file.isDirectory()) {
			for (File son : file.listFiles()) {
				String sub;
				if (path == null)
					sub = "";
				else if (path.length() == 0)
					sub = file.getName();
				else
					sub = path + "/" + file.getName();
				find(sub, son);
			}
		} else if (file.isFile())
			try {
				String answer = upload(path, file);
				if (answer != null)
					System.out.println(fmt.format(new Date()) + answer);
			} catch (IOException e) {
				System.out.println("!!! EXCEPTION !!!" + e.getMessage());
			}
	}

	public void find() {
		isTemplate = false;
		find(null, new File(element));
		find(null, new File(cselement));
		isTemplate = true;
		find(null, new File(template));
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		boolean isContinuous;
		ElementDeployer deployer;

		if (args.length == 0) {
			deployer = new ElementDeployer();
			isContinuous = true;
		} else {
			deployer = new ElementDeployer(args[0]);
			isContinuous = false;
		}

		do {
			deployer.configure();
			deployer.login();
			// deployer.dumpCookie();
			deployer.find();
			deployer.logout();
			try {
				Thread.sleep(deployer.pause);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (isContinuous);
	}
}
