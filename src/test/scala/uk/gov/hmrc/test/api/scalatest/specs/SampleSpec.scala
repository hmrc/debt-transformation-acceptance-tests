/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.scalatest.specs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.test.api.scalatest.tags.IGNORE

//Added for DTD_4428: This SampleSpec test can be removed once the real scala test in specs folder
class SampleSpec extends AnyFlatSpec with Matchers {

  "This ScalaTest" should "add two numbers correctly" in {
    val result = 2 + 3
    result shouldBe 5
  }

  "This ScalaTest" should "shouldn be ignored" taggedAs(IGNORE) in {
    println("TEST 2: If this is shown, the ignore tag isn't working properly & this test has failed")
    val result = 12512 + 3
    result shouldBe 5
  }
}