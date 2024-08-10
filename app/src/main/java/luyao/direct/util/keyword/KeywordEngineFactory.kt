package luyao.direct.util.keyword

import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.associationEngineList

object KeywordEngineFactory {

    private val baiduEngine by lazy { BaiduKeywordEngine() }
    private val bingEngine by lazy { BingKeywordEngine() }
    private val taobaoEngine by lazy { TaobaoKeywordEngine() }
    private val googleEngine by lazy { GoogleKeywordEngine() }
    private val _360Engine by lazy { _360KeywordEngine() }

    fun getEngine(): KeywordEngine {
        val engine = associationEngineList.find { it.id == MMKVConstants.associationEngineId }
        return if (engine == null) {
            baiduEngine
        } else {
            when (engine.id) {
                0 -> baiduEngine
                1 -> bingEngine
                2 -> taobaoEngine
                3 -> googleEngine
                4 -> _360Engine
                else -> baiduEngine
            }
        }
    }

}