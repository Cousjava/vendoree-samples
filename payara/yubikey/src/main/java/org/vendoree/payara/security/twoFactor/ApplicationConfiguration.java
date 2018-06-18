/** Copyright Payara Services Limited * */

package org.vendoree.payara.security.twoFactor;

import fish.payara.security.annotations.TwoIdentityStoreAuthenticationMechanismDefinition;
import fish.payara.security.annotations.YubikeyIdentityStoreDefinition;
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import org.glassfish.soteria.identitystores.annotation.Credentials;
import org.glassfish.soteria.identitystores.annotation.EmbeddedIdentityStoreDefinition;

/**
 * This class adds an <code>IdentityStore</code> to the application and replaces the default
 * <code>AuthenticationMechanism</code> with a <code>TwoFactorAuthenticationMechanism</code>
 *
 * @author Mark Wareham
 */


//first identity store
@EmbeddedIdentityStoreDefinition({
    @Credentials(callerName = "mark", groups = {"a", "b"}, password = "test"),
    @Credentials(callerName = "bob", groups = {"b", "c"}, password = "secret"),
    @Credentials(callerName = "alice", groups = {"d"}, password = "anothersecret")
})

//second identity store
@YubikeyIdentityStoreDefinition(yubikeyAPIKey="5hXBzTAm9fVhfzjLZ/ogMpwdjg4=", yubikeyAPIClientID=37486)

//Specify that this application is to use the TwoFactorAuthenticationMechanism
@TwoIdentityStoreAuthenticationMechanismDefinition(loginToContinue
        = @LoginToContinue(loginPage = "/Login", errorPage = "/Failure")
)

@ApplicationScoped
public class ApplicationConfiguration {
}
