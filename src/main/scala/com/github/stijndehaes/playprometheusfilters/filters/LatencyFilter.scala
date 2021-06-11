package com.github.stijndehaes.playprometheusfilters.filters

import akka.stream.Materializer
import com.github.stijndehaes.playprometheusfilters.metrics.DefaultPlayUnmatchedDefaults
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetrics.LatencyOnlyRequestMetricsBuilder
import io.prometheus.client.CollectorRegistry
import play.api.Configuration

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext
import com.github.stijndehaes.playprometheusfilters.metrics.LatencyRequestMetric

/**
  * A simple [[MetricsFilter]] using a histogram metric to record latency without any labels.
  */
@Singleton
class LatencyFilter @Inject() (
    registry: CollectorRegistry,
    configuration: Configuration
  )(implicit mat: Materializer,
    ec: ExecutionContext)
    extends MetricsFilter(configuration) {

  override val metrics: List[LatencyRequestMetric] = List(
    LatencyOnlyRequestMetricsBuilder.build(registry, DefaultPlayUnmatchedDefaults)
  )
}
