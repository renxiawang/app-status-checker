import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTitle;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.javascript.host.Text;

public class AppStatusChecker {

    public static void main(String args[])
            throws FailingHttpStatusCodeException, MalformedURLException,
            IOException {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
        
        ArrayList<String> schoolAry = new ArrayList<String>();
        ArrayList<String> loginPageAry = new ArrayList<String>();
        ArrayList<String> userNameAry = new ArrayList<String>();
        ArrayList<String> passwordAry = new ArrayList<String>();
        
        String loginFormPath = "//*[@id=\"frmApplicantLogin\"]";
        String statusPath = "//*[@id=\"linksMouseOver\"]/table/tbody/tr[5]/td[2]/table/tbody/tr[1]/td[1]/table";

        BufferedReader br = new BufferedReader(new FileReader("schools.txt"));
        String school = br.readLine();
        while (school != null) {
            String[] schoolAttr = school.split(" ");
            schoolAry.add(schoolAttr[0]);
            loginPageAry.add(schoolAttr[1]);
            userNameAry.add(schoolAttr[2]);
            passwordAry.add(schoolAttr[3]);
            
            school = br.readLine();
        }
        
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(
                "status.txt"), "UTF-8");

        for (int i = 0; i < schoolAry.size(); i++) {

            try {
                System.out.println("Checking " + schoolAry.get(i));
                // config
                WebClient webClient = new WebClient(BrowserVersion.FIREFOX_10);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setJavaScriptEnabled(false);

                // login
                HtmlPage loginPage = webClient.getPage(loginPageAry.get(i));

                HtmlForm loginForm = (HtmlForm) loginPage.getByXPath(loginFormPath)
                        .get(0);

                HtmlInput userNameBox = loginForm.getInputByName("UserID");
                HtmlInput passwordBox = loginForm.getInputByName("Password");

                userNameBox.setValueAttribute(userNameAry.get(i));
                passwordBox.setValueAttribute(passwordAry.get(i));

                HtmlElement button = (HtmlElement) loginPage
                        .createElement("button");
                button.setAttribute("type", "submit");
                loginForm.appendChild(button);

                // display result
                HtmlPage applicationStatusPage = button.click();
                HtmlTable status = (HtmlTable) applicationStatusPage.getByXPath(
                        statusPath).get(0);

                out.write(schoolAry.get(i) + "\n");
                out.write(status.asText() + "\n");
                out.write("======================================\n");
                out.flush();
            } catch (Exception e) {
                // TODO: handle exception
                out.write("Get " + schoolAry.get(i) + " information failed. Skip.\n");
                out.write("======================================\n");
                System.out.print("Get " + schoolAry.get(i) + " information failed. Skip.");
                e.printStackTrace();
            }
            
        }
        System.out.println("Finished!");
        out.close();

    }
}
