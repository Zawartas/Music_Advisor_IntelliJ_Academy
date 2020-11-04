package advisor;

public class SaveLibraryToFile extends AbstractShowController implements ApiOperation {

    public SaveLibraryToFile(Spotify spotify) {
        super(spotify);
    }

    @Override
    public void execute(QueryArgs queryArgs) {
        spotify.getAndSaveLibraryToFile(queryArgs);
    }
}
