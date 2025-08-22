package com.uglyeagle.jass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class FileLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, String> userMap = new HashMap<>();
    private Map<String, String> roleMap = new HashMap<>();
    private String username;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("users.txt"); 
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    userMap.put(parts[0], parts[1]);
                    roleMap.put(parts[0], parts[2]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read users file", e);
        }
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("No CallbackHandler provided");
        }

        NameCallback nameCb = new NameCallback("Username: ");
        PasswordCallback passCb = new PasswordCallback("Password: ", false);

        try {
            callbackHandler.handle(new Callback[]{nameCb, passCb});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Error handling callbacks: " + e.getMessage());
        }

        username = nameCb.getName();
        String password = new String(passCb.getPassword());

        String expected = userMap.get(username);
        if (expected == null || !expected.equals(password)) {
            throw new LoginException("Invalid username or password");
        }

        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (username != null) {
            subject.getPrincipals().add(new UserPrincipal(username));
            subject.getPrincipals().add(new RolePrincipal(roleMap.get(username)));
            return true;
        }
        return false;
    }

    @Override
    public boolean abort() throws LoginException {
        username = null;
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().clear();
        username = null;
        return true;
    }
}
