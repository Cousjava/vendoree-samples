/** Copyright Payara Services Limited * */

package org.vendoree.payara.security.yubikey;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

import fish.payara.security.annotations.TwoFactorAuthenticationMechanismDefinition;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import org.glassfish.soteria.identitystores.annotation.Credentials;
import org.glassfish.soteria.identitystores.annotation.EmbeddedIdentityStoreDefinition;

/**
 * This class adds an <code>IdentityStore</code> to the application and replaces the default
 * <code>AuthenticationMechanism</code> with a <code>TwoFactorAuthenticationMechanism</code>
 *
 * @author Mark Wareham
 */

//enable JSF 2.3
@FacesConfig(
        version = JSF_2_3
)

//Two different identity stores would typically be used but as it's not currently possible to specify 
//two identity stores of this type, for the sake of simplicity in this example the same one is used.
@EmbeddedIdentityStoreDefinition({
    @Credentials(callerName = "mark", groups = {"a", "b"}, password = "test"),
    @Credentials(callerName = "bob", groups = {"b", "c"}, password = "secret"),
    @Credentials(callerName = "alice", groups = {"d"}, password = "anothersecret")
})

//Specify that this application is to use the TwoFactorAuthenticationMechanism
@TwoFactorAuthenticationMechanismDefinition(loginToContinue
        = @LoginToContinue(loginPage = "/login.xhtml", errorPage = "/loginFailure.xhtml")
)
public class ApplicationConfiguration {

}