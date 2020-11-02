package com.company;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ApiRequests {
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String CATEGORIES = "categories";
    public static final String ITEMS = "items";
    private String authCode;
    private HttpClient client;
    private String accessToken;
    private HttpResponse<String> response;


    public ApiRequests(String authCode) {
        this.authCode = authCode;
        client = HttpClient.newBuilder().build();
    }

    // TODO to be deleted - used only for trying command pattern
    public String getAccessToken() {
        return accessToken;
    }

    public boolean getAccessTokenFromSpotify() {
        // The second call is to the Spotify Accounts Service /api/token endpoint,
        // passing to it the authorization code returned by the first call and the client secret key.
        // This call returns an access token and also a refresh token.
        System.out.println("(07) Making http request for access_token...");

        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(ConfigData.SERVER_PATH + "/api/token"))
                .POST(HttpRequest
                        .BodyPublishers
                        .ofString("grant_type=authorization_code"
                                + "&code=" + authCode
                                + "&client_id=" + ConfigData.CLIENT_ID
                                + "&client_secret=" + ConfigData.CLIENT_SECRET
                                + "&redirect_uri=" + ConfigData.REDIRECT_URI))
                .build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (requestSuccessful()) {
                accessToken = getAccessTokenFromResponse();
                return true;
            }
        } catch (IOException | InterruptedException | UnsupportedOperationException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getUserLibrary(int size) {

        size = Math.min(size, 50);
        size = Math.max(size, 1);
        /* TODO command pattern
        *   with queue of commands so that we could remember last command
        *   and pass it to prev/ext commands
         */
        int offset = 0;
        response = getResponseFromRequest("/v1/me/tracks?limit=" + size + "&offset=" + offset);
        if (requestSuccessful()) {
            printUserLibrary(size, offset);
//            String innerCommand = scanner.nextLine();
            boolean scrolling = true;
            while (scrolling) {
                Scanner innerScanner = new Scanner(System.in);
                String innerCommand = innerScanner.nextLine();
                switch (innerCommand) {
                    case "next":
                        offset += size;
                        response = getResponseFromRequest("/v1/me/tracks?limit=" + size + "&offset=" + offset);
                        printUserLibrary(size, offset);
                        break;
                    case "prev":
                        offset -= size;
                        response = getResponseFromRequest("/v1/me/tracks?limit=" + size + "&offset=" + offset);
                        printUserLibrary(size, offset);
                        break;
                    default:
                        scrolling = false;
                        break;
                }
            }
        }
        else {
            printError();
        }
    }

    private int getSizeOfLibrary() {
        return 0;
    }

    public void getFeaturedPlaylists() {
        response = getResponseFromRequest("/v1/browse/featured-playlists");
        if (requestSuccessful()) {
            printFeturedPlaylists();
        }
    }

    public void getNewReleases() {
//        HttpRequest request = getResponseFromRequest("/v1/browse/new-releases");
//        try {
//            response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            if (requestSuccessful()) {
//                printNewReleases();
//            }
//        } catch (IOException | InterruptedException | UnsupportedOperationException e) {
//            e.printStackTrace();
//        }
    }

    public void getAllCategories(boolean printoutToConsole) {
//        HttpRequest request = getResponseFromRequest("/v1/browse/categories" + "?country=PL&limit=50");
//        try {
//            response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            if (requestSuccessful()) {
//                if (printoutToConsole) {
//                    printAllCategories();
//                } else {
//                    saveAllCategories();
//                }
//            }
//        } catch (IOException | InterruptedException | UnsupportedOperationException e) {
//            e.printStackTrace();
//        }
    }

    public void getCategoryPlaylists(String category) {

        getAllCategories(false);
//        String category_id = ConfigData.getCategoryId(category.trim());

//        HttpRequest request = getResponseFromRequest("/v1/browse/categories/"+ category_id + "/playlists");
//        try {
//            response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            if (requestSuccessful()) {
//                printFeturedPlaylists();
//            } else {
//                // TODO below handles JSON containing error msg
//                System.out.println("Unknown category name.");
//                printError();
//            }
//        } catch (IOException | InterruptedException | UnsupportedOperationException e) {
//            e.printStackTrace();
//        }
    }

    private void printUserLibrary(int size, int offset) {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        int total = root.get("total").getAsInt();
        int pages = (int) Math.ceil((double)total/size);
        int page = (int) (offset / ((double) total/pages)) + 1;

//        System.out.println(String.format("total %d pages :%d page %d", total, pages, page));
        System.out.println("Page: " + page + "/" + pages);
        for (JsonElement item : root.getAsJsonArray(ITEMS)) {
            System.out.print(   item.getAsJsonObject().get("added_at").getAsString().substring(0, 10) + " " +
                                item.getAsJsonObject().get("track").getAsJsonObject().get(NAME) + " ");
            System.out.print("[ ");
            for (JsonElement artist : item.getAsJsonObject().get("track").getAsJsonObject().getAsJsonArray("artists")) {
                System.out.print(artist.getAsJsonObject().get(NAME).getAsString() + " ");
            }
            System.out.print("] ");

            System.out.print(item.getAsJsonObject().get("track").getAsJsonObject().get("album"). getAsJsonObject().get("name") + "\n");
        }
    }

    private boolean requestSuccessful() {
        return response.headers().toString().contains("status=[200]");
    }

    private String getAccessTokenFromResponse() {
        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        return jo.get("access_token").getAsString();
    }

    private void printError() {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject error = root.getAsJsonObject("error");
//        System.out.println("status: " + error.getAsJsonPrimitive("status").getAsString());
        System.out.println(/*"message: " + */error.getAsJsonPrimitive("message").getAsString());

    }

    private void printAllCategories() {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject categories = root.getAsJsonObject(CATEGORIES);
//        System.out.println("Total: " + categories.getAsJsonPrimitive("total")/*.getAsString()*/);

        for (JsonElement item : categories.getAsJsonArray(ITEMS)) {
            System.out.println(item.getAsJsonObject().get(NAME).getAsString());
        }
    }

    private void saveAllCategories() {
//        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
//        JsonObject categories = root.getAsJsonObject(CATEGORIES);
////        System.out.println("Total: " + categories.getAsJsonPrimitive("total")/*.getAsString()*/);
//
//        for (JsonElement item : categories.getAsJsonArray(ITEMS)) {
//            String category_name = item.getAsJsonObject().get(NAME).getAsString();
//            String category_id = item.getAsJsonObject().get(ID).getAsString();
//            ConfigData.setCategory(category_name, category_id);
////            System.out.println(String.format("name: %s, id: %s, id_from_map: %s", category_name, category_id, ConfigData.getCategoryId(category_name)));
//        }
    }

    private void printFeturedPlaylists() {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject playlists = root.getAsJsonObject("playlists");

        for (JsonElement item : playlists.getAsJsonArray(ITEMS)) {
            System.out.println(item.getAsJsonObject().get(NAME).getAsString());
            System.out.println(item.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString());
            System.out.print(System.lineSeparator());
        }
    }

    private void printNewReleases() {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject albums = root.getAsJsonObject("albums");

        for (JsonElement item : albums.getAsJsonArray(ITEMS)) {
            StringBuilder sb = new StringBuilder();
            // get album name
            sb.append(item.getAsJsonObject().get(NAME).getAsString());
            // cycle through and print artists
            sb.append(System.lineSeparator()).append("[");
            for (JsonElement artist : item.getAsJsonObject().getAsJsonArray("artists")) {
                sb.append(artist.getAsJsonObject().get(NAME).getAsString()).append(", ");
            }
            sb.delete(sb.lastIndexOf(","), sb.length());
            sb.append("]").append(System.lineSeparator());
            // link to Spotify
            sb.append(item.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString());
            sb.append(System.lineSeparator());

            System.out.println(sb.toString());
        }
    }
    // TODO to be considered whether to use in some manner

    private HttpResponse<String> getResponseFromRequest(String requestedFeatureURL) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(ConfigData.API_PATH + requestedFeatureURL))
                    .GET()
                    .build();
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
