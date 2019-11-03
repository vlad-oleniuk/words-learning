package com.wordslearning.ve.words_extractor
import scala.xml.XML
import scala.xml.Utility
import scala.collection.JavaConversions._
import scala.xml.Node
import com.wordslearning.ve.model.article.WLArticle

object LingvoTutorExtractor {

  def getAllWords(tutorFilePath: String): java.util.List[WLArticle] = {
    val dictionaryNode = Utility.trim(XML.loadFile(tutorFilePath))
    val nestedSeq=for (<card><word>{ articleKey }</word><meanings>{ meanings @ _* }</meanings></card> <- dictionaryNode \ "card") yield {
      for (<meaning><statistics>{ _* }</statistics><translations>{ words @ _* }</translations><examples>{ examples @ _* }</examples></meaning> <- meanings) yield {
        val article = new WLArticle()
        article.setKey(articleKey.text)
        article.setValue(extractTranslation(words));
        article.setRawExamples(extractExamples(examples))
        article
      }
    }
    nestedSeq.flatten[WLArticle]
  }

  private def extractTranslation(translations: Seq[Node]): String = {
    (translations.first.text /: translations.tail)(_ + ", " + _.text)
  }

  private def extractExamples(examples: Seq[Node]): Set[String] = {
    examples.map(_.text).toSet
  }

}