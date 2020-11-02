package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ConfigData.readCommandLineArgs(args);

        boolean exit = false;
        boolean auth = false;
        int size = 5; // TODO to be given as an argument
        Scanner scanner = new Scanner(System.in);
        Spotify spotify = null;
        ApiOperationExecutor executor = null;
        QueryArgs queryArgs = null;

        while (!exit && scanner.hasNext()) {
            String command = scanner.next();

            if (command.equals("exit")) {
                exit = true;
            } else if (!command.equals("auth") && !auth) {
                System.out.println(ConfigData.PLEASE_PROVIDE_ACCESS_FOR_APPLICATION);
                continue;
            }

            switch (command) {
                // TODO maybe not switch/case but HashMap<String, Something>
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
                    queryArgs = new QueryArgs.Builder(size).build();
                    System.out.println(executor.executeOperation(new ShowNewReleases(spotify), queryArgs).body());
                    break;
                case "featured":
                    System.out.println("---FEATURED---");
                    queryArgs = new QueryArgs.Builder(size).build();
                    System.out.println(executor.executeOperation(new ShowFeaturedReleases(spotify), queryArgs).body());
                    break;
                case "categories":
                    System.out.println("---CATEGORIES---");
                    queryArgs = new QueryArgs.Builder(size).build();
                    String response = executor.executeOperation(new ShowAllCategories(spotify), queryArgs).body();
                    DataViewer.showAllCategories(response);
                    break;
                case "playlists":
                    String category = scanner.nextLine().trim();
                    System.out.println("---" + category + " PLAYLISTS---");
                    queryArgs = new QueryArgs.Builder(size).setCategory(category).build();
                    System.out.println(executor.executeOperation(new ShowCategoryPlaylists(spotify), queryArgs).body());
                    break;
                case "library":
                    queryArgs = new QueryArgs.Builder(size).build();
                    System.out.println(executor.executeOperation(new ShowUserLibrary(spotify), queryArgs).body());
                    break;
                case "next":
                    assert queryArgs != null;
                    queryArgs.increaseOffset();
                    //TODO Houston we've got a problem!!! Printing response also is dependent on how HttpResponse looks!!!
                    String response = executor.executeLastOperation(queryArgs).body());
                    break;
                case "prev":
                    assert queryArgs != null;
                    queryArgs.decreaseOffset();
                    System.out.println(executor.executeLastOperation(queryArgs).body());
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
