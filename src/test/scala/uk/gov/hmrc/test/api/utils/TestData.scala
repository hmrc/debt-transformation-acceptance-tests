/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.test.api.utils

import com.typesafe.scalalogging.LazyLogging
import java.io.File
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.io.Source

object TestData extends LazyLogging {

  private lazy val files: Seq[File] = FileUtils
    .listFiles(new File("src/test/resources/testdata"), Array("txt"), false)
    .asScala
    .toList

  private lazy val suppressionFiles: Seq[File] = FileUtils
    .listFiles(new File("src/test/resources/testdata/suppression"), Array("txt"), false)
    .asScala
    .toList

  lazy val loadedFiles: Map[String, String] =
    files.map { file =>
      val source = Source.fromFile(file.getCanonicalPath)
      val data   = source.mkString
      source.close()
      file.getName.replace(".txt", "") -> data
    }.toMap

  private lazy val ttppFiles: Seq[File] = FileUtils
    .listFiles(new File("src/test/resources/testdata/ttpp"), Array("txt"), false)
    .asScala
    .toList

  lazy val loadedTtppFiles: Map[String, String] =
    ttppFiles.map { file =>
      val source = Source.fromFile(file.getCanonicalPath)
      val data   = source.mkString
      source.close()
      file.getName.replace(".txt", "") -> data
    }.toMap

  lazy val loadedSuppressionFiles: Map[String, String] =
    suppressionFiles.map { file =>
      val source = Source.fromFile(file.getCanonicalPath)
      val data   = source.mkString
      source.close()
      file.getName.replace(".txt", "") -> data
    }.toMap
}
