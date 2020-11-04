package advisor;

public class ApiOperationExecutor {
    private ApiOperation lastApiOperation = null;

    public void executeOperation(ApiOperation apiOperation, QueryArgs queryArgs) {
        lastApiOperation = apiOperation;
        apiOperation.execute(queryArgs);
    }

    public void executeLastOperation(QueryArgs queryArgs) {
        executeOperation(lastApiOperation, queryArgs);
    }
}
