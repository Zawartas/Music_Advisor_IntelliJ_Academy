package com.company;

import java.net.http.HttpResponse;

public class ShowCategoryPlaylists extends AbstractShowController implements ApiOperation {

    public ShowCategoryPlaylists(Spotify spotify) {
        super(spotify);
    }

    @Override
    public HttpResponse<String> getResponse(QueryArgs queryArgs) {
        return spotify.getCategoryPlaylists(queryArgs);
    }

}

