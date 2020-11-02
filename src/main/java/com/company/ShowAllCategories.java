package com.company;

import java.net.http.HttpResponse;

public class ShowAllCategories extends AbstractShowController implements ApiOperation {

    public ShowAllCategories(Spotify spotify) {
        super(spotify);
    }

    @Override
    public HttpResponse<String> getResponse(QueryArgs queryArgs) {
        return spotify.getAllCategories(queryArgs);
    }
}

