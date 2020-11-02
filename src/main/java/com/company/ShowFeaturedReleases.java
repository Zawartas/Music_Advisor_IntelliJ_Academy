package com.company;

import java.net.http.HttpResponse;

public class ShowFeaturedReleases extends AbstractShowController implements ApiOperation {

    public ShowFeaturedReleases(Spotify spotify) {
        super(spotify);
    }

    @Override
    public HttpResponse<String> getResponse(QueryArgs queryArgs) {
        return spotify.getFeaturedReleases(queryArgs);
    }
}

