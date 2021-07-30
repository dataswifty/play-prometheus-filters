package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.RouteLatencyRequestMetricsBuilder
import com.github.stijndehaes.playprometheusfilters.metrics.{ DefaultPlayUnmatchedDefaults, LatencyRequestMetric }
import io.prometheus.client.CollectorRegistry
import play.api.Configuration

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

/**
  * A simple [[MetricsFilter]] using a counter metric to count requests.
  * Only adds a 'route' label.
  */
@Singleton
class RouteLatencyFilter @Inject() (
    registry: CollectorRegistry,
    configuration: Configuration
  )(implicit mat: Materializer,
    ec: ExecutionContext)
    extends MetricsFilter(configuration) {

  override val metrics: List[LatencyRequestMetric] = List(
    RouteLatencyRequestMetricsBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}
