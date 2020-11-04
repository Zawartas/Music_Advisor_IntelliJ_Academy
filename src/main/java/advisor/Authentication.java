package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Authentication {
    private Server server;
    private String code;
    private String accessToken;

    public Authentication() {
        server = new Server();
        server.start();
        provideWithAuthenticationLink();
        waitForSpotifyResponseWithAuthCode();
    }

    public String getCode() {
        return code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void provideWithAuthenticationLink() {
        System.out.println("____Authentication link below.");

        // The first call is the service /authorize endpoint, passing to it the client ID, scopes, and redirect URI.
        // This is the call that starts the process of authenticating to user and gets the user’s authorization to access data.
        System.out.println(
                ConfigData.SERVER_PATH      + ConfigData.AUTHORIZE
                        + "?client_id="     + ConfigData.CLIENT_ID
                        + "&scope="         + ConfigData.SCOPE
                        + "&redirect_uri="  + ConfigData.REDIRECT_URI
                        + "&response_type=" + ConfigData.RESPONSE_TYPE);
    }

    public void waitForSpotifyResponseWithAuthCode() {
            System.out.println("____Waiting for code...");
            while (server.getQuery() == null || !server.getQuery().contains("code=")) {
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setCode(server.getQuery().substring(5));
            System.out.println("____Potential code received: " + getCode());
            server.stop();
    }

    public boolean getAccessTokenFromSpotify() {
        // The second call is to the Spotify Accounts Service /api/token endpoint,
        // passing to it the authorization code returned by the first call and the client secret key.
        // This call returns an access token and also a refresh token.
        System.out.println("____Making http request for access_token...");

        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(ConfigData.SERVER_PATH + "/api/token"))
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString(
                            "grant_type="     + ConfigData.AUTHORIZTION_CODE
                                + "&code="          + getCode()
                                + "&client_id="     + ConfigData.CLIENT_ID
                                + "&client_secret=" + ConfigData.CLIENT_SECRET
                                + "&redirect_uri="  + ConfigData.REDIRECT_URI))
                .build();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (requestSuccessful(response)) {
                accessToken = getAccessTokenFromResponse(response);
                return true;
            }
        } catch (IOException | InterruptedException | UnsupportedOperationException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean requestSuccessful(HttpResponse<String> response) {
        return response.headers().toString().contains("status=[200]");
    }

    private String getAccessTokenFromResponse(HttpResponse<String> response) {
        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        return jo.get("access_token").getAsString();
    }
}
