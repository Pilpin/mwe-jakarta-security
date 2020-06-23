package org.example.view;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
public class Login {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject private SecurityContext securityContext;
    @Inject private FacesContext facesContext;
    @Inject private ExternalContext externalContext;

    private String url;

    @NotNull private String username;
    @NotNull private String password;

    @PostConstruct
    private void postConstruct() {
        if (url != null) return;
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        String requestURI = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        String queryString = (String) request.getAttribute(RequestDispatcher.FORWARD_QUERY_STRING);
        url = (queryString == null) ? requestURI : (requestURI + "?" + queryString);
    }

    public String submit() {
        switch (continueAuthentication()) {
            case SEND_CONTINUE:
                logger.log(Level.WARNING, "CONTINUE");
                facesContext.responseComplete();
            	break;

            case SEND_FAILURE:
                logger.log(Level.WARNING, "FAILURE");
                facesContext.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Login failed", null));
            	break;

            case SUCCESS:
                logger.log(Level.WARNING, "SUCCESS");
                redirect();
                break;

            case NOT_DONE:
                // Doesn’t happen here
            	break;
        }

        return null;
    }

    private AuthenticationStatus continueAuthentication() {
        return securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            AuthenticationParameters.withParams()
                    .newAuthentication(url == null)
                    .credential(new UsernamePasswordCredential(username, password))
        );
    }

    private void redirect() {
        String user = securityContext.getCallerPrincipal().getName();

        try {
            if (url != null) {
                externalContext.redirect(url);
                return;
            }

            if (Objects.equals("admin", user)) {
                externalContext.redirect(externalContext.getRequestContextPath() + "/admin");
                return;
            }

            if (Objects.equals("user", user)) {
                externalContext.redirect(externalContext.getRequestContextPath() + "/restricted");
                return;
            }
        } catch (IOException ignored) { }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
