package advisor;

public class ShowCategoryPlaylists extends AbstractShowController implements ApiOperation {

    public ShowCategoryPlaylists(Spotify spotify) {
        super(spotify);
    }

    @Override
    public void execute(QueryArgs queryArgs) {
        spotify.showCategoryPlaylist(queryArgs);
    }

}

