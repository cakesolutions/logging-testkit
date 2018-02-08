// Copyright 2018 Cake Solutions Limited

package net.cakesolutions.testkit.logging.elasticsearch

import scala.concurrent.Future
import com.sksamuel.elastic4s.aws.{Aws4ElasticClient, Aws4ElasticConfig}
import com.sksamuel.elastic4s.http.RequestFailure
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.searches.SearchDefinition
import monix.execution.Scheduler
import monix.reactive.Observable

/**
  * Client for querying logs from ElasticSearch.
  *
  * @param config AWS elasticsearch configuration
  */
class ElasticSearchLogClient(config: Aws4ElasticConfig) {
  import ElasticSearchLogClient._

  private val elasticSearchClient = Aws4ElasticClient(config)

  /**
    * Query ElasticSearch and return the results as an Observable.
    *
    * @param searchDef search query
    * @param scheduler monix scheduler
    * @return query results as observable stream
    */
  def search(searchDef: SearchDefinition)(
      implicit scheduler: Scheduler
  ): Observable[String] = {
    Observable
      .fromFuture(
        elasticSearchClient.execute(searchDef).flatMap {
          case Left(failure) =>
            Future.failed(ElasticSearchRequestFailureException(failure))
          case Right(result) =>
            Future.successful(result)
        }
      )
      .flatMap { result =>
        Observable.fromIterable(
          result.result.hits.hits.map(_.sourceAsString)
        )
      }
  }
}

object ElasticSearchLogClient {

  /**
    * Execution of an ElasticSearch request failed.
    *
    * @param requestFailure failure response
    */
  case class ElasticSearchRequestFailureException(
      requestFailure: RequestFailure
  ) extends Exception(
        s"ElasticSearch request failed: ${requestFailure.error.reason}"
      )
}
