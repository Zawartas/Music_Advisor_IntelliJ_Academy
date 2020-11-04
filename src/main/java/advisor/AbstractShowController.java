package advisor;

public abstract class AbstractShowController {
    protected Spotify spotify;

    protected AbstractShowController(Spotify spotify) {
        this.spotify = spotify;
    }
}
