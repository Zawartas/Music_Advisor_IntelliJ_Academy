package com.company;

import java.net.http.HttpResponse;

@FunctionalInterface
public interface ApiOperation {
    HttpResponse<String> getResponse(QueryArgs queryArgs);
}
