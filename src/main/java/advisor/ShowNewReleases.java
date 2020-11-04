package advisor;

public class ShowNewReleases extends AbstractShowController implements ApiOperation {

    public ShowNewReleases(Spotify spotify) {
        super(spotify);
    }

    @Override
    public void execute(QueryArgs queryArgs) {
        spotify.showNewReleases(queryArgs);
    }
}

