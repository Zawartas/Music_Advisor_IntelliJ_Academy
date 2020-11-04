package advisor;

public class ConfigData {
    public static final String PLEASE_PROVIDE_ACCESS_FOR_APPLICATION = "Please, provide access for application.";
    public static final String AUTHORIZE = "/authorize";
    public static final String RESPONSE_TYPE = "code";
    public static final String CLIENT_ID = "e26d80b52e304fcbb79131a6bb2e6705";
    public static final String CLIENT_SECRET = "_________________";
    public static final String SCOPE = "user-library-read";
    public static final String AUTHORIZTION_CODE = "authorization_code";
    private static int SIZE = 5;
    public static String SERVER_PATH = "https://accounts.spotify.com";
    public static String API_PATH = "https://api.spotify.com";
    public static String REDIRECT_URI = "http://localhost:8888/SZAWspotify";

    public static void readCommandLineArgs(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-access")) {
                ConfigData.SERVER_PATH = args[i+1];
            }
            if (args[i].equals("-resource")) {
                ConfigData.API_PATH = args[i+1];
            }
            if (args[i].equals("-page")) {
                ConfigData.SIZE = Integer.parseInt(args[i+1]);
            }
        }
    }

    public static int getSIZE() {
        return SIZE;
    }
}