package io.lvoxx.github;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;

/**
 * Hello world!
 */
public class GrantGoogleCredential {
    static final String CLIENT_ID = "<Your's Client Id>";
    static final String CLIENT_SECRET = "<Your's Client Secret>";

    static final Integer PORT = 9999;
    // README: Remember to put http://localhost:{PORT}/Callback on you API application

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credentials = getCredentials(HTTP_TRANSPORT);

        System.out.println(credentials.getRefreshToken());
    }

    private static Credential getCredentials(final NetHttpTransport transport) throws IOException {
        AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport,
                GsonFactory.getDefaultInstance(),
                CLIENT_ID,
                CLIENT_SECRET,
                Arrays.asList(DriveScopes.DRIVE_FILE))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(PORT).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
