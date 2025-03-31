  import static net.grinder.script.Grinder.grinder
    import static org.junit.Assert.*
    import static org.hamcrest.Matchers.*
    import net.grinder.script.GTest
    import net.grinder.script.Grinder
    import net.grinder.scriptengine.groovy.junit.GrinderRunner
    import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
    import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
    
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Test
    import org.junit.runner.RunWith
    
    import java.util.List
    import java.util.ArrayList
    import java.util.Random
    
    import net.grinder.plugin.http.HTTPRequest
    import net.grinder.plugin.http.HTTPPluginControl
    
    import HTTPClient.Cookie
    import HTTPClient.CookieModule
    import HTTPClient.HTTPResponse
    import HTTPClient.NVPair
    import groovy.json.JsonOutput
    
    @RunWith(GrinderRunner)
    class TestRunner {

    public static GTest test
    public static HTTPRequest request
    public static NVPair[] headers = []
    public static Cookie[] cookies = []

    @BeforeProcess
    public static void beforeProcess() {
    HTTPPluginControl.getConnectionDefaults().timeout = 6000
    test = new GTest(1, "Ticket Reservation Test")
    request = new HTTPRequest()
    
    List<NVPair> headerList = new ArrayList<>()
    headerList.add(new NVPair("Content-Type", "application/json"))
    headers = headerList.toArray()
  
  grinder.logger.info("Before process: Initialized HTTP request and headers.")
  }

    @BeforeThread
    public void beforeThread() {
    test.record(this, "test")
    grinder.statistics.delayReports = true
  grinder.logger.info("Before thread: Thread setup complete.")
  }

    @Before
    public void before() {
    request.setHeaders(headers)
    cookies.each { CookieModule.addCookie(it, HTTPPluginControl.getThreadHTTPClientContext()) }
  grinder.logger.info("Before test: Headers and cookies initialized.")
  }

    @Test
    public void test() {
    def random = new Random()
    
    def userId = random.nextInt(1000) + 1
    def ticketIds = (1..120).toList().shuffled().take(random.nextInt(4) + 1)
    
    def requestBody = JsonOutput.toJson([
  userId    : userId,
  ticketIds : ticketIds
  ])
  
  grinder.logger.info("Request Body: ${requestBody}")
    
    HTTPResponse response = request.PUT(
    "http://host.docker.internal:8080/tickets/reservations",
    requestBody.getBytes("UTF-8")
    )
  
  grinder.logger.info("Response Code: ${response.statusCode}")
    
    assertThat(response.statusCode, is(200))
  }
  }