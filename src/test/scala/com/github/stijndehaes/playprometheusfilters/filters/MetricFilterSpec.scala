package com.github.stijndehaes.playprometheusfilters.filters

import com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics.CounterRequestMetricBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.{ DefaultPlayUnmatchedDefaults, RequestMetric }
import com.github.stijndehaes.playprometheusfilters.mocks.MockController
import com.typesafe.config.ConfigFactory
import io.prometheus.client.CollectorRegistry
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test.{ DefaultAwaitTimeout, FakeRequest, FutureAwaits }

import scala.concurrent.ExecutionContext.Implicits.global

class MetricFilterSpec
    extends PlaySpec
    with MockitoSugar
    with Results
    with DefaultAwaitTimeout
    with FutureAwaits
    with GuiceOneAppPerSuite {

  val configurationForSeconds: Configuration = Configuration(
    ConfigFactory.parseString(
      """play-prometheus-filters.exclude.paths = ["/test"]
       play-prometheus-filters.metric.resolution = "seconds" """
    )
  )

  val configurationForMilliseconds: Configuration = Configuration(
    ConfigFactory.parseString(
      """play-prometheus-filters.exclude.paths = ["/test"]
       play-prometheus-filters.metric.resolution = "milliseconds" """
    )
  )

  "Seconds - Filter constructor" should {
    "Get exclude paths from configuration" in {
      implicit val mat = app.materializer
      val filter = new MetricsFilter(configurationForSeconds) {
        override val metrics =
          List.empty[RequestMetric[_, RequestHeader, Result]]
      }

      filter.excludePaths must have size 1 // only check size since cannot compare Regex's
    }
  }

  "Seconds - Apply method" should {
    "skip metrics for excluded paths" in {
      implicit val mat      = app.materializer
      val collectorRegistry = mock[CollectorRegistry]
      val filter = new MetricsFilter(configurationForSeconds) {
        override val metrics = List(
          CounterRequestMetricBuilder.build(
            collectorRegistry,
            DefaultPlayUnmatchedDefaults
          )
        )
      }

      val rh     = FakeRequest("GET", "/test")
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics(0).metric.collect()
      metrics must have size 1
      val samples = metrics.get(0).samples
      samples.size() mustBe 0 // expect no metrics
    }
  }

  "Milliseconds - Filter constructor" should {
    "Get exclude paths from configuration" in {
      implicit val mat = app.materializer
      val filter = new MetricsFilter(configurationForMilliseconds) {
        override val metrics =
          List.empty[RequestMetric[_, RequestHeader, Result]]
      }

      filter.excludePaths must have size 1 // only check size since cannot compare Regex's
    }
  }

  "Milliseconds - Apply method" should {
    "skip metrics for excluded paths" in {
      implicit val mat      = app.materializer
      val collectorRegistry = mock[CollectorRegistry]
      val filter = new MetricsFilter(configurationForMilliseconds) {
        override val metrics = List(
          CounterRequestMetricBuilder.build(
            collectorRegistry,
            DefaultPlayUnmatchedDefaults
          )
        )
      }

      val rh     = FakeRequest("GET", "/test")
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics(0).metric.collect()
      metrics must have size 1
      println(metrics)
      val samples = metrics.get(0).samples
      println(samples)
      samples.size() mustBe 0 // expect no metrics
    }
  }

  "Seconds - Apply method" should {
    "use the seconds resolution" in {
      implicit val mat      = app.materializer
      val collectorRegistry = mock[CollectorRegistry]
      val filter =
        new StatusAndRouteLatencyFilter(
          collectorRegistry,
          configurationForSeconds
        )

      val rh     = FakeRequest("GET", "/test2")
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics(0).metric.collect()
      metrics must have size 1
      println(s"Metrics: ${metrics}")
      val samples = metrics.get(0).samples
      println(s"Samples: ${samples}")
      samples.size() mustBe 0 // expect no metrics
    }
  }

  "Milliseconds - Apply method" should {
    "use the millisecond resolution" in {
      implicit val mat      = app.materializer
      val collectorRegistry = mock[CollectorRegistry]
      val filter =
        new StatusAndRouteLatencyFilter(
          collectorRegistry,
          configurationForSeconds
        )

      val rh     = FakeRequest("GET", "/test2")
      val action = new MockController(stubControllerComponents()).ok

      await(filter(action)(rh).run())

      val metrics = filter.metrics(0).metric.collect()
      metrics must have size 1
      println(s"Metrics: ${metrics}")

      val samples = metrics.get(0).samples
      println(s"Samples: ${samples}")
      samples.size() mustBe 0 // expect no metrics
    }
  }
}
