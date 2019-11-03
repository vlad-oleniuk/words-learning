package com.wordslearning.ve.words_extractor
import com.wordslearning.ve.model.article.Language


object SearchReqPreprocessor {
	
	def processReqKey(key : String, lang : Language) : String = {
		lang match{
			case Language.GER => processGermanReqKey(key)
			case _ => key
		}
	}
	
	private val germanArticleRegEx = """(der)|(die)|(das) """.r
	
	private def processGermanReqKey (key : String) : String = {
		germanArticleRegEx findPrefixOf key match {
			case Some(x) => key.substring(x.length)
			case None => key
		}
	}
		
	
}