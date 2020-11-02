package com.company;

import java.net.http.HttpResponse;

public class ApiOperationExecutor {
    private ApiOperation lastApiOperation = null;

    public HttpResponse<String> executeOperation(ApiOperation apiOperation, QueryArgs queryArgs) {
        lastApiOperation = apiOperation;
        return apiOperation.getResponse(queryArgs);
    }

    public HttpResponse<String> executeLastOperation(QueryArgs queryArgs) {
        return executeOperation(lastApiOperation, queryArgs);
    }
}
