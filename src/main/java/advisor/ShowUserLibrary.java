package advisor;

public class ShowUserLibrary extends AbstractShowController implements ApiOperation {

    public ShowUserLibrary(Spotify spotify) {
        super(spotify);
    }

    @Override
    public void execute(QueryArgs queryArgs) {
        spotify.showUserLibrary(queryArgs);
    }
}
