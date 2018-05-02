/** Copyright Payara Services Limited * */

package org.vendoree.payara.security.yubikey;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.junit.Assert.assertTrue;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.File;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This sample tests the use of the two factor authentication mechanism using the Payara API.
 *
 * @author Mark Wareham
 */
@RunWith(Arquillian.class)
public class TwoFactorAuthenticationMechanismTest {

    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;
    
    private WebClient webClient;
    private URL protectedServletUrl;

    @Before
    public void setup() throws MalformedURLException {
        webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        protectedServletUrl = new URL(base, "protected");
    }
    
    @After
    public void teardown() {
        webClient.close();
    }
    
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = create(WebArchive.class)
                .addClasses(
                        ApplicationConfiguration.class,
                        ProtectedServlet.class,
                        LoginBean.class
                ).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsLibraries(
                Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .resolve("fish.payara.security:yubikey-authentication")
                        .withTransitivity()
                        .as(JavaArchive.class)
        );
        for (File f : new File(WEBAPP_SRC).listFiles()) {
            if (f.isFile()) {
                archive.addAsWebResource(f);
            }
        }

        System.out.println("************************************************************");
        System.out.println(archive.toString(true));
        System.out.println("************************************************************");

        return archive;
    }
    
    private HtmlForm getToLoginPage() throws IOException, FailingHttpStatusCodeException, ElementNotFoundException {
        HtmlPage firstPage = webClient.getPage(protectedServletUrl);
        WebResponse webResponse = firstPage.getWebResponse();
        assertTrue("Expected login page", webResponse.getContentAsString().contains("Login to continue"));
        HtmlForm form = firstPage.getFormByName("form");
        return form;
    }
    
    @Test
    @RunAsClient
    public void testAuthorised() throws IOException {
        HtmlForm form = getToLoginPage();
        form.getInputByName("form:username1").type("mark");
        form.getInputByName("form:password1").type("test");
        form.getInputByName("form:username2").type("bob");
        form.getInputByName("form:password2").type("secret");

        Page secondPage = form.getInputByName("form:submit").click();
        WebResponse webResponsePage2 = secondPage.getWebResponse();
        String page2ContentAsString = webResponsePage2.getContentAsString();

        assertTrue("Expected secure page. Instead got "+ page2ContentAsString, 
                page2ContentAsString.contains("This is a protected servlet"));        
    }
    
    @Test
    @RunAsClient
    public void testNotAuthorised() throws IOException {

        HtmlForm form = getToLoginPage();
        form.getInputByName("form:username1").type("fred");//invalid user
        form.getInputByName("form:password1").type("test");
        form.getInputByName("form:username2").type("tom");//invalid user
        form.getInputByName("form:password2").type("secret");

        Page secondPage = form.getInputByName("form:submit").click();

        assertTrue("Expected login failure page. Instead got " + secondPage.getWebResponse().getContentAsString(),
                secondPage.getWebResponse().getContentAsString().contains("Login failure"));
            
    }
    
    @Test
    @RunAsClient
    public void testNotAuthorisedSingleValidCredentialFirst() throws IOException {

        HtmlForm form = getToLoginPage();
        form.getInputByName("form:username1").type("mark");
        form.getInputByName("form:password1").type("test");
        form.getInputByName("form:username2").type("tom");//invalid user
        form.getInputByName("form:password2").type("secret");

        Page secondPage = form.getInputByName("form:submit").click();

        assertTrue("Expected login failure page. Instead got " + secondPage.getWebResponse().getContentAsString(),
                secondPage.getWebResponse().getContentAsString().contains("Login failure"));
            
    }

    @Test
    @RunAsClient
    public void testNotAuthorisedSingleValidCredentialLast() throws IOException {

        HtmlForm form = getToLoginPage();
        form.getInputByName("form:username1").type("tom"); //invalid user
        form.getInputByName("form:password1").type("secret");
        form.getInputByName("form:username2").type("mark"); //valid user
        form.getInputByName("form:password2").type("test");

        Page secondPage = form.getInputByName("form:submit").click();

        assertTrue("Expected login failure page. Instead got " + secondPage.getWebResponse().getContentAsString(),
                secondPage.getWebResponse().getContentAsString().contains("Login failure"));
            
    }
    
    @Test
    @RunAsClient
    public void testNotAuthorisedSingleValidCredentialSole() throws IOException {

        HtmlForm form = getToLoginPage();
        form.getInputByName("form:username1").type("mark");
        form.getInputByName("form:password1").type("test");
        
        Page secondPage = form.getInputByName("form:submit").click();

        assertTrue("Expected login failure page. Instead got " + secondPage.getWebResponse().getContentAsString(),
                secondPage.getWebResponse().getContentAsString().contains("Login failure"));
            
    }
    
    @Test
    @RunAsClient
    public void testNotAuthorisedNoCredential() throws IOException {

        HtmlForm form = getToLoginPage();
        
        Page secondPage = form.getInputByName("form:submit").click();

        assertTrue("Expected login failure page. Instead got " + secondPage.getWebResponse().getContentAsString(),
                secondPage.getWebResponse().getContentAsString().contains("Login failure"));
            
    }
}
