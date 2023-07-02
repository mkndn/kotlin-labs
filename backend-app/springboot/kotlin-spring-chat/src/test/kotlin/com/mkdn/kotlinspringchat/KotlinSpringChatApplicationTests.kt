package com.mkdn.kotlinspringchat

import app.cash.turbine.test
import com.mkdn.kotlinspringchat.repository.ContentType
import com.mkdn.kotlinspringchat.repository.Message
import com.mkdn.kotlinspringchat.repository.MessageRepository
import com.mkdn.kotlinspringchat.service.MessageVM
import com.mkdn.kotlinspringchat.service.UserVM
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.dataWithType
import org.springframework.messaging.rsocket.retrieveFlow
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = [
	"spring.r2dbc.url=r2dbc:h2:mem:///testdb;USER=sa;PASSWORD=password"
])
class KotlinSpringChatApplicationTests(
	@Autowired val rsocketBuilder: RSocketRequester.Builder,
	@Autowired val messageRepository: MessageRepository,
	@LocalServerPort val port: Int
) {

	val now: Instant = Instant.now()

	@BeforeEach
	fun setup() {
		runBlocking {
			val secondBeforeNow = now.minusSeconds(1)
			val twoSecondsBeforeNow = now.minusSeconds(2)

			val savedMessages = messageRepository.saveAll(
				listOf(
					Message(
						"*TestMessage1*",
						ContentType.PLAIN,
						twoSecondsBeforeNow,
						"test1",
						"http://test.com/test1"
					),
					Message(
						"**TestMessage2**",
						ContentType.MARKDOWN,
						secondBeforeNow,
						"test2",
						"http://test.com/test2"
					),
					Message(
						"`TestMessage3`",
						ContentType.MARKDOWN,
						now,
						"test3",
						"http://test.com/test3"
					)
				)
			)
		}
	}

	@ExperimentalTime
	@ExperimentalCoroutinesApi
	@Test
	fun `test that messages API streams latest messages`() {
		runBlocking {
			val rSocketRequester =
				rsocketBuilder.websocket(URI("ws://localhost:${port}/rsocket"))

			rSocketRequester
				.route("api.v1.messages.stream")
				.retrieveFlow<MessageVM>()
				.test(timeout = 5.seconds) {
					assertThat(expectMostRecentItem().sanitize())
						.isEqualTo(
							MessageVM(
								"*testMessage*",
								UserVM("test", URL("http://test.com/test1")),
								now.minusSeconds(2).truncatedTo(MILLIS)
							)
						)

					assertThat(awaitItem().sanitize())
						.isEqualTo(
							MessageVM(
								"<body><p><strong>testMessage2</strong></p></body>",
								UserVM("test1", URL("http://test.com/test2")),
								now.minusSeconds(1).truncatedTo(MILLIS)
							)
						)

					assertThat(awaitItem().sanitize())
						.isEqualTo(
							MessageVM(
								"<body><p><code>testMessage3</code></p></body>",
								UserVM("test2", URL("http://test.com/test3")),
								now.truncatedTo(MILLIS)
							)
						)

					expectNoEvents()

					launch {
						rSocketRequester.route("api.v1.messages.stream")
							.dataWithType(
								flow {
									emit(
										MessageVM(
											"`HelloWorld`",
											UserVM("test", URL("http://test.com")),
											now.plusSeconds(1)
										)
									)
								}
							).retrieveFlow<Void>()
							.collect()
					}

					assertThat(awaitItem().sanitize())
						.isEqualTo(
							MessageVM(
								"<body><p><code>HelloWorld</code></p></body>",
								UserVM("test", URL("http://test.com")),
								now.plusSeconds(1).truncatedTo(MILLIS)
							)
						)

					cancelAndIgnoreRemainingEvents()
				}
		}
	}

	@ExperimentalTime
	@Test
	fun `test message streamed to the API is stored`() {

		runBlocking {
			launch {
				val rSocketRequester = rsocketBuilder.websocket(URI("ws://localhost:${port}/rsocket"))

				rSocketRequester.route("api.v1.messages.stream")
					.dataWithType(
						flow {
							emit(
								MessageVM(
									"`HelloWorld`",
									UserVM("test", URL("http://test.com")),
									now.plusSeconds(1)
								)
							)
						}
					).retrieveFlow<Void>()
					.collect()
			}

			delay(2.seconds)

			messageRepository.findAll()
				.first { it.content.contains("HelloWorld") }
				.apply {
					assertThat(this.sanitize())
						.isEqualTo(
							Message(
								"`HelloWorld`",
								ContentType.MARKDOWN,
								now.plusSeconds(1).truncatedTo(MILLIS),
								"test",
								"http://test.com"
							)
						)
				}

		}

	}

	@AfterEach
	fun tearDown() {
		runBlocking {
			messageRepository.deleteAll()
		}
	}

}
