package advisor;

@FunctionalInterface
public interface ApiOperation {
    void execute(QueryArgs queryArgs);
}
