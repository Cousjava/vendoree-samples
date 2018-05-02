/** Copyright Payara Services Limited **/

package org.vendoree.payara.security.yubikey;

import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This bean acts as the controller for login actions
 * 
 * @author Mark Wareham
 */
@Named
@RequestScoped
public class LoginBean {

    @Inject 
    private FacesContext facesContext;
    
    @Inject 
    private SecurityContext securityContext;

    private String username1;
    private String password1;
    private String username2;
    private String password2;

    public void login() throws IOException {
        Credential firstCredential = new UsernamePasswordCredential(username1, password1);
        AuthenticationStatus status1 = securityContext.authenticate(
                getRequestFrom(facesContext),
                getResponseFrom(facesContext),
                withParams().credential(firstCredential));
        //printing passwords only for example app. Don't do this in your application.
        System.out.println("Authenticated " + username1 + "=" + password1 + " as " + status1.name());
        
        Credential secondCredential = new UsernamePasswordCredential(username2, password2);
        AuthenticationStatus status2 = securityContext.authenticate(
                getRequestFrom(facesContext),
                getResponseFrom(facesContext),
                withParams().credential(secondCredential));
        //printing passwords only for example app. Don't do this in your application.
        System.out.println("Authenticated " + username2 + "=" + password2 + " as " + status2.name());
        
    }

    private HttpServletRequest getRequestFrom(FacesContext facesContext) {
        return (HttpServletRequest) facesContext.getExternalContext().getRequest();
    }

    private HttpServletResponse getResponseFrom(FacesContext facesContext) {
        return (HttpServletResponse) facesContext.getExternalContext().getResponse();
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}