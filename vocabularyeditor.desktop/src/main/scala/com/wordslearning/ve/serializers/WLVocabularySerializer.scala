package com.wordslearning.ve.serializers

import java.io.File
import scala.xml.{ XML, Elem, Node }
import scala.collection.JavaConversions._
import com.wordslearning.ve.model.article.{ Language, WLArticle, WLVocabulary }

object WLVocabularySerializer {

 /* def sserializeWLVocabularyToFile(voc: WLVocabulary, filePath: String) {
    XML.save(filePath, sserializeWLVocabulary(voc), "UTF-8")
  }

  def sserializeWLVocabulary(voc: WLVocabulary): Elem = {
    <wlvoc lang_from={ String.valueOf(voc.getLangFrom) }>
      <full_name>{ voc.getName }</full_name>
      {
        voc.getArticles.map(a =>
          <ar>
            <k>{ a.getKey }</k>
            <v>{ a.getValue }</v>
            <illustration>{ a.getIllustrationURL }</illustration>
            <synonyms>
              { a.getSynonyms.map(synItem => <item>{ synItem }</item>) }
            </synonyms>
            <antonyms>
              { a.getAntonyms.map(antItem => <item>{ antItem }</item>) }
            </antonyms>
            <examples>
              { a.getRawExamples.map(exmplItem => <item>{ exmplItem }</item>) }
            </examples>
          </ar>)
      }
    </wlvoc>

  }
*/
  /**
   * Parses wl vocabulary file, and returns parsing result or null, if the file was not correct wl vocabulary file
   */
  def deserializeWLVocabulary(file: File): WLVocabulary = {
    try {
      val vocabulary = new WLVocabulary()
      val vocNode = XML.loadFile(file)
      vocabulary.setLangFrom(Language.valueOf((vocNode \ "@lang_from").text))
      vocabulary.setName((vocNode \ "full_name").text)
      vocabulary.setArticles((vocNode \ "ar").map(deserializeArticle(_)))
      vocabulary
    } catch {
      case _ => throw new VocabularyFileFormatException
    }
  }

  private def deserializeArticle(articleNode: Node): WLArticle = {
    val article = new WLArticle()
    article.setKey((articleNode \ "k").text)
    article.setValue((articleNode \ "v").text)
    article.setIllustrationURL((articleNode \ "illustration").text)
    article.setSynonyms(new java.util.HashSet((articleNode \ "synonyms" \ "item").map(_.text).toSet))
    article.setAntonyms(new java.util.HashSet((articleNode \ "antonyms" \ "item").map(_.text).toSet))
    article.setRawExamples(new java.util.HashSet((articleNode \ "examples" \ "item").map(_.text).toSet))
    article
  }

}