package org.example.config.authentication;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Objects;
import java.util.Set;

@CustomFormAuthenticationMechanismDefinition(
	loginToContinue = @LoginToContinue(
		loginPage = "/login.xhtml",
		errorPage = ""
	)
)
@ApplicationScoped
public class UserServiceIdentityStore implements IdentityStore {
	private final User admin = new User("admin", "admin", Set.of("ADMIN", "USER"));
	private final User user = new User("user", "user", Set.of("USER"));

	@Override
	public CredentialValidationResult validate(Credential credential) {
		User user = null;

		if (credential instanceof UsernamePasswordCredential) {
			user = this.findUser(
					((UsernamePasswordCredential) credential).getCaller(),
					((UsernamePasswordCredential) credential).getPasswordAsString()
			);
		}
		else if (credential instanceof CallerOnlyCredential) {
			user = this.findUser(((CallerOnlyCredential) credential).getCaller());
		}

		if (user != null) {
			return new CredentialValidationResult(user.name, user.groups);
		}
		else {
			return CredentialValidationResult.INVALID_RESULT;
		}
	}

	private User findUser(String username) {
		if (Objects.equals(username, "admin")) return admin;
		if (Objects.equals(username, "user")) return user;

		return null;
	}

	private User findUser(String username, String password) {
		User user = findUser(username);
		if (user != null && Objects.equals(password, user.password)) return user;

		return null;
	}

	public class User {
		String name;
		String password;
		Set<String> groups;

		public User(String name, String password, Set<String> groups) {
			this.name = name;
			this.password = password;
			this.groups = groups;
		}
	}
}
