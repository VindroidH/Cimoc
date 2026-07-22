package com.haleydu.cimoc.source

import android.text.TextUtils
import com.haleydu.cimoc.model.Chapter
import com.haleydu.cimoc.model.Comic
import com.haleydu.cimoc.model.ImageUrl
import com.haleydu.cimoc.model.Source
import com.haleydu.cimoc.parser.MangaParser
import com.haleydu.cimoc.parser.NodeIterator
import com.haleydu.cimoc.parser.SearchIterator
import com.haleydu.cimoc.parser.UrlFilter
import com.haleydu.cimoc.soup.Node
import com.haleydu.cimoc.utils.DecryptionUtils
import com.haleydu.cimoc.utils.StringUtils
import okhttp3.Headers
import okhttp3.Request
import java.io.UnsupportedEncodingException
import java.util.LinkedList

/**
 * Created by FEILONG on 2017/12/21.
 */
class MangaBZ(source: Source?) : MangaParser() {
    @Throws(UnsupportedEncodingException::class)
    override fun getSearchRequest(keyword: String, page: Int): Request {
        var url = "https://www.mangabz.com/search?title=$keyword&page=$page"
        return Request.Builder().url(url).header("cookie", "mangabz_lang=2").build()
    }

    override fun getSearchIterator(html: String, page: Int): SearchIterator {
        val body = Node(html)
        return object : NodeIterator(body.list(".mh-item")) {
            override fun parse(node: Node): Comic {
                var cid = node.attr("a", "href").trim('/')
                val title = node.text(".title")
                val cover = node.attr(".mh-cover", "src")
                val update = node.text(".chapter > a")
                val author = ""
                return Comic(TYPE, cid, title, cover, update, author)
            }
        }
    }

    override fun getUrl(cid: String): String {
        return "https://www.mangabz.com/$cid/"
    }

    override fun initUrlFilterList() {
        filter.add(UrlFilter("www.mangabz.com"))
    }

    override fun getInfoRequest(cid: String): Request {
        val url = "https://www.mangabz.com/$cid/"
        return Request.Builder().url(url).header("cookie", "mangabz_lang=2").build()
    }

    @Throws(UnsupportedEncodingException::class)
    override fun parseInfo(html: String, comic: Comic): Comic {
        val body = Node(html)
        val title = body.text(".detail-info-title")
        val cover = body.src(".detail-info-cover")
        val update = StringUtils.match(
            "(..月..号 | ....-..-..)",
            body.text(".detail-list-form-title"), 1
        )
        val author = body.text(".detail-info-tip > span > a")
        val intro = body.text(".detail-info-content")
        val status = isFinish(".detail-list-form-title")
        comic.setInfo(title, cover, update, intro, author, status)
        return comic
    }

    override fun parseChapter(html: String, comic: Comic, sourceComic: Long): List<Chapter> {
        val list: MutableList<Chapter> = LinkedList()
        var i = 0
        for (node in Node(html).list("#chapterlistload > a")) {
            var title = node.text()
            if (title == "") title = node.attr("title")
            val path = node.href().trim('/')
            list.add(
                Chapter(
                    (sourceComic.toString() + "00" + i++).toLong(),
                    sourceComic,
                    title,
                    path
                )
            )
        }
        return list
    }

    private var _cid = ""
    private var _path = ""

    override fun getImagesRequest(cid: String, path: String): Request {
        val url = "https://www.mangabz.com/$path/"
        this._cid = cid
        this._path = path
        return Request.Builder()
            .url(url)
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 15; Pixel 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Mobile Safari/537.36"
            )
            .build()
    }

    override fun parseImages(html: String, chapter: Chapter): List<ImageUrl> {
        val list: MutableList<ImageUrl> = LinkedList()
        try {
            val comicChapter = chapter.id
            for (node in Node(html).list("script")) {
                var eval = node.get().html()
                if (TextUtils.isEmpty(eval)) continue
                if (!eval.startsWith("eval")) continue
                eval = eval.replaceFirst("eval", "JSON.stringify")
                var data = DecryptionUtils.evalDecrypt(eval)
                val startIndex = data.indexOf('[')
                val endIndex = data.lastIndexOf(']')
                data = data.substring(startIndex + 1, endIndex)
                val urls = data.split(",")
                val pageCount = urls.size - 1
                for (i in 0..pageCount) {
                    val id = (comicChapter.toString() + "00" + i).toLong()
                    var url = urls[i]
                    url = url.substring(1, url.length - 1)
                    list.add(ImageUrl(id, comicChapter, i + 1, url, false))
                }
                break
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    override fun getCheckRequest(cid: String?): Request? {
        return getInfoRequest(cid!!)
    }

    override fun parseCheck(html: String?): String? {
        return StringUtils.match(
            "(..月..号 | ....-..-..)",
            Node(html).text(".detail-list-form-title"), 1
        )
    }

    override fun getHeader(): Headers {
        return Headers.headersOf(
            "Referer",
            "https://mangabz.com/",
            "User-Agent",
            "Mozilla/5.0 (Linux; Android 15; Pixel 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Mobile Safari/537.36"
        )
    }

    override fun getHeader(list: List<ImageUrl?>?): Headers? {
        return Headers.headersOf(
            "Referer",
            "https://mangabz.com/"
        )
    }

    companion object {
        @JvmStatic
        fun getDefaultSource(): Source {
            return Source(null, DEFAULT_TITLE, TYPE, true)
        }

        const val TYPE = 82
        const val DEFAULT_TITLE = "MangaBZ"
    }

    init {
        init(source, null)
    }
}