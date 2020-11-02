package com.company;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Spotify {

    private static final String MAX = "50";
    private static Spotify instance;
    private final String accessToken;
    private HttpClient client;

    private Spotify(String accessToken) {
        this.client = HttpClient.newBuilder().build();
        this.accessToken = accessToken;
    }

    public static Spotify getSpotifyInstance(String accessToken) {
        if (instance == null) {
            instance = new Spotify(accessToken);
        }
        return instance;
    }

    public HttpResponse<String> getUserLibrary(QueryArgs queryArgs) {
        try {
            // TODO can be replaced with some template
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(ConfigData.API_PATH
                            + "/v1/me/tracks?limit=" + queryArgs.getSize() + "&offset=" + queryArgs.getOffset()))
                    .GET()
                    .build();
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResponse<String> getNewReleases(QueryArgs queryArgs) {
        try {
            // TODO can be replaced with some template
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(ConfigData.API_PATH + "/v1/browse/new-releases?limit="
                            + queryArgs.getSize() + "&offset=" + queryArgs.getOffset()))
                    .GET()
                    .build();
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResponse<String> getFeaturedReleases(QueryArgs queryArgs) {
        try {
            // TODO can be replaced with some template
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(ConfigData.API_PATH + "/v1/browse/featured-playlists?limit="
                            + queryArgs.getSize() + "&offset=" + queryArgs.getOffset()))
                    .GET()
                    .build();
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResponse<String> getAllCategories(QueryArgs queryArgs) {
        try {
            // TODO can be replaced with some template
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(ConfigData.API_PATH + "/v1/browse/categories?limit="
                            + queryArgs.getSize() + "&offset=" + queryArgs.getOffset()))
                    .GET()
                    .build();
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResponse<String> getCategoryPlaylists(QueryArgs queryArgs) {
        //TODO change logic, so that we don't keep some map but with every request we check if playlist is present

        String category_id = getCategoryId(queryArgs.getCategory());
        System.out.println("________category_id: " + category_id);

        if (!category_id.isEmpty()) {
            try {
                // TODO can be replaced with some template
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .uri(URI.create(ConfigData.API_PATH + "/v1/browse/categories/"
                                + category_id + "/playlists?limit=" + queryArgs.getSize() + "&offset=" + queryArgs.getOffset()))
                        .GET()
                        .build();
                return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Not found category");
        }
        return null;
    }

    private String getCategoryId(String category) {
        System.out.println("________Looking for id for category: " + category);
        int offset = 0;
        int total;
        HttpResponse<String> response = null;

        do {
            try {
                // TODO can be replaced with some template
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .uri(URI.create(ConfigData.API_PATH
                                + "/v1/browse/categories?limit=" + MAX + "&offset=" + offset))
                        .GET()
                        .build();
                response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//                System.out.println(response.headers());
//                System.out.println(response.body());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = root.getAsJsonObject("categories");
            total = categories.get("total").getAsInt();
            System.out.println("________TOTAL: " + total);

            for (JsonElement item : categories.getAsJsonArray("items")) {
                System.out.println("________id: " + item.getAsJsonObject().get("id").getAsString());
                System.out.println("________name: " + item.getAsJsonObject().get("name").getAsString());
                if (category.equals(item.getAsJsonObject().get("name").getAsString())) {
                    System.out.println(item.getAsJsonObject().get("id").getAsString());
                    return item.getAsJsonObject().get("id").getAsString();
                }
            }
            offset += Integer.parseInt(MAX);
        } while (offset <= total);

        return "";
    }
}
