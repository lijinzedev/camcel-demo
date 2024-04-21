package camcel.test;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:hello?period={{timer.period}}").routeId("hello")
            .transform().method("myBean", "saySomething")
            .filter(simple("${body} contains 'foo'"))
                .to("log:foo")
            .end()
            .to("stream:out");
    }

}

//@Component
 class MyRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:foo").to("log:bar");
    }
}
@Component
class FileRouter extends RouteBuilder {


    private static final String SOURCE_FOLDER =
            "/Users/curiosity/IdeaProjects/camcel-test/src/test/new";
    private static final String DESTINATION_FOLDER =
            "/Users/curiosity/IdeaProjects/camcel-test/src/test/old";

    @Override
    public void configure() throws Exception {
        from("file://" + SOURCE_FOLDER + "?delete=true").to("file://" + DESTINATION_FOLDER);
    }
}
@Component
class JDBCRouter extends RouteBuilder {
    protected static final String SELECT_QUERY = "select * from horses";



    @Override
    public void configure() throws Exception {
        from("timer://MoveNewCustomersEveryHour?period=10000")
                .setBody(constant("select * from MyTable "))
                .to("jdbc:default")
                .split(body())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        final Message message = exchange.getMessage();
                        final Message in = exchange.getIn();
                        final Object body = message.getBody();
                    }
                }) //filter/transform results as needed
                .setBody(simple("insert into MyTable_copy1(name) values('${body[name]}')"))
                .to("jdbc:default");
    }
}