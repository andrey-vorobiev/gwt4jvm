The **gwt4jvm** framework simplifies load testing of GWT applications. It grants a pure Java implementation of a few native components of GWT SDK (including GWT-RPC subsystem) and makes it possible to run GWT code on JVM. The framework enables you to reuse in load tests the code you have already had and gives you an ability to simulate thousands concurrently working users having only a single modern PC.

Take a look at a [quick example](http://code.google.com/p/gwt4jvm/#Quick_Example) and then [get started](http://code.google.com/p/gwt4jvm/wiki/GettingStarted) writing load tests.

### Quick Example ###

Let's look at a web application called Movie Chart—simplified GWT-based clone of [IMDB Top 250 Movies](http://www.imdb.com/chart/top) that shows the list of movies sorted by rating which is formed by votes of registered users. Client-side part of the application interacts with Java backend through the single GWT-RPC service:

```
@RemoteServiceRelativePath("service")
public interface Service extends RemoteService {
    public void login(String username, String password);
    public ArrayList<MovieDTO> getMovieList();
    public Float rateMovie(long movieId, byte rate);
    public void logout();
}
```

Here is one of the typical usage scenario of Movie Chart application: login, get the list of movies, rate one of them, and finally logout. If you tried to write GWT application that automatically simulates this scenario, probably you would end up with `EntryPoint` which `onModuleLoad` method is just a chain of GWT-RPCs:

```
public class TypicalScenarioTest implements EntryPoint {

    @Override
    public void onModuleLoad() {
        final ServiceAsync service = GWT.create(Service.class);
        service.login("username", "password", new SimpleAsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                service.getMovieList(new SimpleAsyncCallback<ArrayList<MovieDTO>>() {

                    @Override
                    public void onSuccess(ArrayList<MovieDTO> movies) {
                        final MovieDTO movie = movies.get((int) (Math.random() * movies.size()));
                        final byte rating = (byte) (Math.random() * 10);
                        service.rateMovie(movie.getId(), rating, new SimpleAsyncCallback<Float>() {

                            @Override
                            public void onSuccess(Float rating) {
                                service.logout(new SimpleAsyncCallback<Void>() {

                                    @Override
                                    public void onSuccess(Void result) {
                                        Window.alert("Test succeed!");
                                        // Context.getCurrentContext().getClient().success();
                                    }

                                });
                            }

                        });
                    }

                });
            }

        });
    }

    private abstract class SimpleAsyncCallback<T> implements AsyncCallback<T> {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Test failed!");
            // Context.getCurrentContext().getClient().failure();
        }

    }

}
```

With **gwt4jvm** it is possible to have multiple instances of `TypicalScenarioTest` running concurrently on JVM. All you have to do is insert `Context.getCurrentContext().getClient().success()` and `Context.getCurrentContext().getClient().failure()` at each point where `TypicalScenarioTest` should be terminated with success or failure respectively (as for `TypicalScenarioTest`, they are already there but commented out), and use `GwtLoadTest` helping class to start the load test:

```
String moduleBaseUrl = "http://localhost:8080/mc/";
int concurrentUsers = 100;
int rampUpSeconds = 10;
int testDurationSeconds = 30;
GwtLoadTest loadTest = new GwtLoadTest(TypicalScenarioTest.class, moduleBaseUrl);
loadTest.start(concurrentUsers, rampUpSeconds, testDurationSeconds, TimeUnit.SECONDS);
```

The code above will ramp up the load of the hundred concurrently working instances of `TypicalScenarioTest` in 10 seconds and will maintain it for half a minute stressing Movie Chart application on http://localhost:8080/. To see this in action, check out the [sources](http://code.google.com/p/gwt4jvm/source/checkout) of Movie Chart application (it is in `gwt4jvm-sample` directory) and follow the instructions in its [README](http://code.google.com/p/gwt4jvm/source/browse/gwt4jvm-sample/README).