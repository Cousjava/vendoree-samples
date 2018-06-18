/** Copyright Payara Services Limited **/

package org.vendoree.payara.security.twoFactor;

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
//import org.vendoree.payara.security.identityStore.TestCredential;

/**
 * This bean acts as the controller for login actions
 * 
 * @author Mark Wareham
 */
//@Named
//@RequestScoped
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
        
        //We do not care about the result of the first authenticate call as we're using a TwoFactorAuthenticationMechanism
        //We are interested in the result of the second authenticate call .
        securityContext.authenticate(
                getRequestFrom(facesContext),
                getResponseFrom(facesContext),
                withParams().credential(firstCredential));

        Credential secondCredential = new UsernamePasswordCredential(username2, password2);
        AuthenticationStatus status2 = securityContext.authenticate(
                getRequestFrom(facesContext),
                getResponseFrom(facesContext),
                withParams().credential(secondCredential));
        
    }

    private HttpServletRequest getRequestFrom(FacesContext facesContext) {
        return (HttpServletRequest) facesContext.getExternalContext().getRequest();
    }

    private HttpServletResponse getResponseFrom(FacesContext facesContext) {
        return (HttpServletResponse) facesContext.getExternalContext().getResponse();
    }
    
}