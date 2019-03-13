/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apidefinition.connector

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

object Registration {
  implicit val format = Json.format[Registration]
}

class ServiceLocatorConnector @Inject()(http: HttpClient, config: ServiceLocatorConfig)(implicit val ec: ExecutionContext) {

  def register(): Future[Boolean] = {
    implicit val hc: HeaderCarrier = new HeaderCarrier

    val registration = Registration(config.appName, config.appUrl, Some(Map("third-party-api" -> "true")))

    http.POST(s"${config.serviceLocatorBaseUrl}/registration", registration) map { _ =>
      Logger.info("Service is registered on the service locator")
      true
    } recover {
      case e: Throwable =>
        Logger.error("Service could not register on the service locator", e)
        false
    }
  }
}

case class ServiceLocatorConfig(appName: String, appUrl: String, serviceLocatorBaseUrl: String)

case class Registration(serviceName: String, serviceUrl: String, metadata: Option[Map[String, String]] = None)
