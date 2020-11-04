package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;

public class DataViewer {

    private static final String NAME = "name";
    private static final String ITEMS = "items";
    private static final String TOTAL = "total";
    private static final String TRACK = "track";
    private static final String ALBUM = "album";
    private static final String ERROR_NO_MORE_PAGES = "Error. No more pages.";
    private static final String CATEGORIES = "categories";
    private static final String ALBUMS = "albums";
    private static final String EXTERNAL_URLS = "external_urls";
    private static final String SPOTIFY = "spotify";
    private static final String PLAYLISTS = "playlists";
    private static final String ADDED_AT = "added_at";
    private static final String ARTISTS = "artists";
    private static final String SPACE = " ";

    public static void showAllCategories(HttpResponse<String> response, QueryArgs queryArgs) {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject categories = root.getAsJsonObject(CATEGORIES);

        int total = categories.get(TOTAL).getAsInt();

        if (queryArgs.getOffset() >= total) {
            queryArgs.decreaseOffset();
            System.out.println(ERROR_NO_MORE_PAGES);
            return;
        }

        int pages = (int) Math.ceil((double)total/queryArgs.getSize());
        int page = (int) (queryArgs.getOffset() / ((double) total/pages)) + 1;

        for (JsonElement item : categories.getAsJsonArray(ITEMS)) {
            System.out.println(item.getAsJsonObject().get(NAME).getAsString());
        }
        printPageSummary(pages, page);
    }

    public static void showNewReleases(HttpResponse<String> response, QueryArgs queryArgs) {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject albums = root.getAsJsonObject(ALBUMS);

        int total = albums.get(TOTAL).getAsInt();

        if (queryArgs.getOffset() >= total) {
            queryArgs.decreaseOffset();
            System.out.println(ERROR_NO_MORE_PAGES);
            return;
        }

        int pages = (int) Math.ceil((double)total/queryArgs.getSize());
        int page = (int) (queryArgs.getOffset() / ((double) total/pages)) + 1;

        for (JsonElement item : albums.getAsJsonArray(ITEMS)) {
            StringBuilder sb = new StringBuilder();
            // get album name
            sb.append(item.getAsJsonObject().get(NAME).getAsString());
            // cycle through and print artists
            sb.append(System.lineSeparator()).append("[");
            for (JsonElement artist : item.getAsJsonObject().getAsJsonArray(ARTISTS)) {
                sb.append(artist.getAsJsonObject().get(NAME).getAsString()).append(", ");
            }
            sb.delete(sb.lastIndexOf(","), sb.length());
            sb.append("]").append(System.lineSeparator());
            // link to Spotify
            sb.append(item.getAsJsonObject().getAsJsonObject(EXTERNAL_URLS).get(SPOTIFY).getAsString());
            sb.append(System.lineSeparator());

            System.out.println(sb.toString());
        }
        printPageSummary(pages, page);
    }

    public static void showFeaturedReleases(HttpResponse<String> response, QueryArgs queryArgs) {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject playlists = root.getAsJsonObject(PLAYLISTS);

        int total = playlists.get(TOTAL).getAsInt();

        if (queryArgs.getOffset() >= total) {
            queryArgs.decreaseOffset();
            System.out.println(ERROR_NO_MORE_PAGES);
            return;
        }

        int pages = (int) Math.ceil((double)total/queryArgs.getSize());
        int page = (int) (queryArgs.getOffset() / ((double) total/pages)) + 1;

        for (JsonElement item : playlists.getAsJsonArray(ITEMS)) {
            System.out.println(item.getAsJsonObject().get(NAME).getAsString());
            System.out.println(item.getAsJsonObject().getAsJsonObject(EXTERNAL_URLS).get(SPOTIFY).getAsString());
            System.out.print(System.lineSeparator());
        }
        printPageSummary(pages, page);
    }

    public static void showCategoryPlaylist(HttpResponse<String> response, QueryArgs queryArgs) {
        showFeaturedReleases(response, queryArgs);
    }

    public static void showUserLibrary(HttpResponse<String> response, QueryArgs queryArgs) {
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

        int total = root.get(TOTAL).getAsInt();

        if (queryArgs.getOffset() >= total) {
            queryArgs.decreaseOffset();
            System.out.println(ERROR_NO_MORE_PAGES);
            return;
        }

        int pages = (int) Math.ceil((double)total/queryArgs.getSize());
        int page = (int) (queryArgs.getOffset() / ((double) total/pages)) + 1;

        for (JsonElement item : root.getAsJsonArray(ITEMS)) {
            System.out.print(item.getAsJsonObject().get(ADDED_AT).getAsString().substring(0, 10) + SPACE
                    + item.getAsJsonObject().get(TRACK).getAsJsonObject().get(NAME) + SPACE);
            System.out.print("[ ");
            for (JsonElement artist : item.getAsJsonObject().get(TRACK).getAsJsonObject().getAsJsonArray(ARTISTS)) {
                System.out.print(artist.getAsJsonObject().get(NAME).getAsString() + SPACE);
            }
            System.out.print("] ");
            System.out.print(item.getAsJsonObject().get(TRACK).getAsJsonObject()
                    .get(ALBUM).getAsJsonObject().get(NAME) + "\n");
        }
        printPageSummary(pages, page);
    }

    private static void printPageSummary(int pages, int page) {
        System.out.println(String.format("Page: %d/%d", page, pages));
    }
}
