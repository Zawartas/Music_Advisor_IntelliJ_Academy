package advisor;

public class ShowAllCategories extends AbstractShowController implements ApiOperation {

    public ShowAllCategories(Spotify spotify) {
        super(spotify);
    }

    @Override
    public void execute(QueryArgs queryArgs) {
        spotify.showAllCategories(queryArgs);
    }
}

