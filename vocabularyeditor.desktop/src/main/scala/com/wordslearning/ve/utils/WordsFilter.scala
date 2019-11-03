package com.wordslearning.ve.utils

import scala.collection.JavaConversions._
import com.wordslearning.ve.model.article.WLArticle

object WordsFilter {
  def filterWords(wordsSource: java.util.List[WLArticle], filter: String): java.util.List[WLArticle] = {
    wordsSource.filter(article => article.getKey().indexOf(filter) != -1)
  }
}