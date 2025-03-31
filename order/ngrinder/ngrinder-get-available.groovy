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
import groovy.json.JsonSlurper

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
        // Send GET request to retrieve available ticket IDs
        HTTPResponse getResponse = request.GET("http://host.docker.internal:8080/tickets/available/1")
        assertThat(getResponse.statusCode, is(200))

        // Parse the response to get the list of ticket IDs
        def jsonResponse = new JsonSlurper().parseText(getResponse.getText())
        def ticketIds = jsonResponse.ticketIds

        if (ticketIds.isEmpty()) {
            grinder.logger.error("사용 가능한 티켓이 없습니다.")
            return
        }

        // Randomly select some ticket IDs
        def random = new Random()
        def selectedTicketId = ticketIds.get(random.nextInt(ticketIds.size()))
		
		grinder.logger.info("i'm ready reservation ticketId: " + selectedTicketId)
    }
}