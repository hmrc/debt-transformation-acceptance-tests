package uk.gov.hmrc.test.api.models

import play.api.libs.json.{Format, JsNull, JsResult, JsValue, Json, Reads, Writes}

trait ValueTypeFormatter {
  def valueTypeFormatter[T, U](
    apply: T => U,
    unapply: U => Option[T]
  )(implicit readsT: Reads[T], writesT: Writes[T]): Format[U] =
    new Format[U] {
      override def reads(json: JsValue): JsResult[U] =
        json.validate[T].map(apply)
      override def writes(o: U): JsValue             = unapply(o) match {
        case Some(x) => Json.toJson(x)
        case None    => JsNull
      }
    }
}
