package com.company;

import java.net.http.HttpResponse;

public class ShowNewReleases extends AbstractShowController implements ApiOperation {

    public ShowNewReleases(Spotify spotify) {
        super(spotify);
    }

    @Override
    public HttpResponse<String> getResponse(QueryArgs queryArgs) {
        return spotify.getNewReleases(queryArgs);
    }
}

