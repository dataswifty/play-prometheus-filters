package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.CounterRequestMetrics.CounterRequestMetricBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.LatencyRequestMetricsBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.{ DefaultPlayUnmatchedDefaults, PlayRequestMetric }
import io.prometheus.client.Collector.Describable
import io.prometheus.client._
import play.api.Configuration

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

/**
  * A [[MetricsFilter]] using a both a histogram and counter metric to record latency and count requests.
  *
  * Latency metric adds 'RouteActionMethod', 'Status', 'Controller', 'Path' and 'Verb' labels.
  * Counter metric adds 'method', 'status', 'controller', 'path' and 'verb' labels.
  */
@Singleton
class StatusAndRouteLatencyAndCounterFilter @Inject() (
    registry: CollectorRegistry,
    configuration: Configuration
  )(implicit mat: Materializer,
    ec: ExecutionContext)
    extends MetricsFilter(configuration) {

  override val metrics: List[PlayRequestMetric[_ <: SimpleCollector[_ <: Object] with Describable]] = List(
    LatencyRequestMetricsBuilder.build(registry, DefaultPlayUnmatchedDefaults),
    CounterRequestMetricBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}
