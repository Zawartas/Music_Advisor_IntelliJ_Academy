package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileWriter;
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

    public void showNewReleases(QueryArgs queryArgs) {
        HttpResponse<String> response = getNewReleases(queryArgs);
        if (response != null) {
            DataViewer.showNewReleases(response, queryArgs);
        }
    }

    public void showAllCategories(QueryArgs queryArgs) {
        HttpResponse<String> response = getAllCategories(queryArgs);
        if (response != null) {
            DataViewer.showAllCategories(response, queryArgs);
        }
    }

    public void showFeaturedReleases(QueryArgs queryArgs) {
        HttpResponse<String> response = getFeaturedReleases(queryArgs);
        if (response != null) {
            DataViewer.showFeaturedReleases(response, queryArgs);
        }
    }

    public void showCategoryPlaylist(QueryArgs queryArgs) {
        HttpResponse<String> response = getCategoryPlaylists(queryArgs);
        if (response != null) {
            DataViewer.showCategoryPlaylist(response, queryArgs);
        }
    }

    public void showUserLibrary(QueryArgs queryArgs) {
        HttpResponse<String> response = getUserLibrary(queryArgs);
        if (response != null) {
            DataViewer.showUserLibrary(response, queryArgs);
        }
    }

    private HttpResponse<String> getUserLibrary(QueryArgs queryArgs) {
        return getResponseFromRequest(
                "/v1/me/tracks?limit=" + queryArgs.getSize()
                + "&offset=" + queryArgs.getOffset());
    }

    private HttpResponse<String> getFeaturedReleases(QueryArgs queryArgs) {
        return getResponseFromRequest(
                "/v1/browse/featured-playlists?limit=" + queryArgs.getSize()
                        + "&offset=" + queryArgs.getOffset());
    }

    private HttpResponse<String> getNewReleases(QueryArgs queryArgs) {
        return getResponseFromRequest(
                "/v1/browse/new-releases?limit="+ queryArgs.getSize()
                + "&offset=" + queryArgs.getOffset());
    }

    private HttpResponse<String> getAllCategories(QueryArgs queryArgs) {
        return getResponseFromRequest("/v1/browse/categories" +
                "?limit=" + queryArgs.getSize()
                + "&offset=" + queryArgs.getOffset()
                + "&locale=us_US" + "&country=us");
    }

    private HttpResponse<String> getCategoryPlaylists(QueryArgs queryArgs) {
        String category_id = getCategoryId(queryArgs.getCategory());

        if (!category_id.isEmpty()) {
            return getResponseFromRequest("/v1/browse/categories/"+ category_id + "/playlists" +
                    "?limit=" + queryArgs.getSize()
                    + "&offset=" + queryArgs.getOffset());
        } else {
            System.out.println("Not found category");
        }
        return null;
    }

    private String getCategoryId(String category) {
        int offset = 0;
        int total;

        do {
            HttpResponse<String> response =
                    getResponseFromRequest("/v1/browse/categories"
                            + "?limit=" + MAX + "&offset=" + offset);

            if (response == null || requestUnsuccessful(response)) {
                return getError(response);
            } else {
                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject categories = root.getAsJsonObject("categories");
                total = categories.get("total").getAsInt();
                for (JsonElement item : categories.getAsJsonArray("items")) {
                    if (category.equals(item.getAsJsonObject().get("name").getAsString())) {
                        return item.getAsJsonObject().get("id").getAsString();
                    }
                }
            }
            offset += Integer.parseInt(MAX);
        } while (total >= offset);
        return "";
    }

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

    private boolean requestUnsuccessful(HttpResponse<String> response) {
        return !response.headers().toString().contains("status=[200]");
    }

    private String getError(HttpResponse<String> response) {
        if (response == null) {
            return "Error.";
        }
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject error = root.getAsJsonObject("error");
        return error.getAsJsonPrimitive("message").getAsString();
    }

    public void getAndSaveLibraryToFile(QueryArgs queryArgs) {
        String allTracks = getAllTracks();

        File file = new File("./file.txt");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(allTracks);
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }

    private String getAllTracks() {
        int offset = 0;
        int total;
        StringBuilder sb = new StringBuilder();

        do {
            HttpResponse<String> response =
                    getResponseFromRequest("/v1/me/tracks?limit=" + MAX
                            + "&offset=" + offset);

            if (response == null || requestUnsuccessful(response)) {
                return getError(response);
            } else {
                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                total = root.get("total").getAsInt();
                for (JsonElement item : root.getAsJsonArray("items")) {
                    sb.append(System.lineSeparator());
                    sb.append(item.getAsJsonObject().get("added_at").getAsString(), 0, 10)
                            .append(" ").append(item.getAsJsonObject().get("track").getAsJsonObject().get("name"))
                            .append(" ");
                    sb.append("[ ");
                    for (JsonElement artist : item.getAsJsonObject()
                            .get("track").getAsJsonObject().getAsJsonArray("artists")) {
                        sb.append(artist.getAsJsonObject().get("name").getAsString()).append(" ");
                    }
                    sb.append("] ");
                    sb.append(item.getAsJsonObject().get("track").getAsJsonObject()
                            .get("album").getAsJsonObject()
                            .get("name")).append("\n\n");
                }
            }
            offset += Integer.parseInt(MAX);
        } while (total >= offset);
        return sb.toString();
    }
}
