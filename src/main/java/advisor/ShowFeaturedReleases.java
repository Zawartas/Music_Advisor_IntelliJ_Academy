package advisor;

public class ShowFeaturedReleases extends AbstractShowController implements ApiOperation {

    public ShowFeaturedReleases(Spotify spotify) {
        super(spotify);
    }

    @Override
    public void execute(QueryArgs queryArgs) {
        spotify.showFeaturedReleases(queryArgs);
    }
}

