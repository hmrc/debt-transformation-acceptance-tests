package uk.gov.hmrc.test.api.models.ttpp

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

import scala.collection.immutable

sealed abstract class QuoteType(override val entryName: String) extends EnumEntry

object QuoteType extends Enum[QuoteType] with PlayJsonEnum[QuoteType] {
  val values: immutable.IndexedSeq[QuoteType] = findValues

  case object Duration extends QuoteType("duration")
  case object InstalmentAmount extends QuoteType("instalmentAmount")
}
