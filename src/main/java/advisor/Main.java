package advisor;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ConfigData.readCommandLineArgs(args);

        boolean exit = false;
        boolean auth = false;
        Scanner scanner = new Scanner(System.in);
        Spotify spotify = null;
        ApiOperationExecutor executor = null;
        QueryArgs queryArgs = new QueryArgs.Builder(ConfigData.getSIZE()).build();

        while (!exit && scanner.hasNext()) {
            String command = scanner.next();

            if (command.equals("exit")) {
                exit = true;
            } else if (!command.equals("auth") && !auth) {
                System.out.println(ConfigData.PLEASE_PROVIDE_ACCESS_FOR_APPLICATION);
                continue;
            }

            switch (command) {
                case "auth":
                    Authentication authentication = new Authentication();

                    if (authentication.getAccessTokenFromSpotify()) {
                        executor = new ApiOperationExecutor();
                        spotify = Spotify.getSpotifyInstance(authentication.getAccessToken());
                        auth = true;
                        System.out.println("Success!");
                    } else {
                        System.out.println(("Wrong or no access token received. Try 'auth' again."));
                    }
                    break;
                case "new":
                    System.out.println("---NEW RELEASES---");
                    queryArgs = new QueryArgs.Builder(ConfigData.getSIZE()).build();
                    executor.executeOperation(new ShowNewReleases(spotify), queryArgs);
                    break;
                case "featured":
                    System.out.println("---FEATURED---");
                    queryArgs = new QueryArgs.Builder(ConfigData.getSIZE()).build();
                    executor.executeOperation(new ShowFeaturedReleases(spotify), queryArgs);
                    break;
                case "categories":
                    System.out.println("---CATEGORIES---");
                    queryArgs = new QueryArgs.Builder(ConfigData.getSIZE()).build();
                    executor.executeOperation(new ShowAllCategories(spotify), queryArgs);
                    break;
                case "playlists":
                    String category = scanner.nextLine().trim();
                    System.out.println("---" + category + " PLAYLISTS---");
                    queryArgs = new QueryArgs.Builder(ConfigData.getSIZE()).setCategory(category).build();
                    executor.executeOperation(new ShowCategoryPlaylists(spotify), queryArgs);
                    break;
                case "library":
                    queryArgs = new QueryArgs.Builder(ConfigData.getSIZE()).build();
                    executor.executeOperation(new ShowUserLibrary(spotify), queryArgs);
                    break;
                case "save_lib":
                    queryArgs = new QueryArgs.Builder(ConfigData.getSIZE()).build();
                    executor.executeOperation(new SaveLibraryToFile(spotify), queryArgs);
                    break;
                case "next":
                    assert queryArgs != null;
                    queryArgs.increaseOffset();
                    executor.executeLastOperation(queryArgs);
                    break;
                case "prev":
                    assert queryArgs != null;
                    if (queryArgs.getOffset() - queryArgs.getSize() < 0) {
                        System.out.println("Error. No more pages.");
                        break;
                    }
                    queryArgs.decreaseOffset();
                    executor.executeLastOperation(queryArgs);
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    exit = true;
                    break;
                default:
                    System.out.println("Unknown Operation");
            }
        }
    }
}
