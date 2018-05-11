package com.cherryperry.amiami.model.push

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
class PushServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var pushService: PushService

    @Before
    fun before() {
        server = MockWebServer()
        pushService = PushService(server.url("/").toString())
    }

    @Test
    fun testSuccessRequest() {
        server.enqueue(MockResponse().setResponseCode(HttpStatus.OK.value()))
        pushService.sendPushWithUpdatedCount(1)
        val request = server.takeRequest()
        assertEquals("/fcm/send", request.path)
        assertNotEquals(null, request.headers["Authorization"])
        assertEquals("""{"to":"/topics/updates2","data":{"count":1}}""", request.body.readUtf8())
    }

    @Test
    fun testFailedRequest() {
        server.enqueue(MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()))
        pushService.sendPushWithUpdatedCount(1)
    }

    @After
    fun afterAll() {
        server.close()
    }
}