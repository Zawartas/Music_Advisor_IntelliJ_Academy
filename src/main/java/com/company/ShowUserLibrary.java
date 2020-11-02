package com.company;

import java.net.http.HttpResponse;

public class ShowUserLibrary extends AbstractShowController implements ApiOperation {

    public ShowUserLibrary(Spotify spotify) {
        super(spotify);
    }

    @Override
    public HttpResponse<String> getResponse(QueryArgs queryArgs) {
        return spotify.getUserLibrary(queryArgs);
    }
}
