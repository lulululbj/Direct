package luyao.ktx.ext

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/21 00:13
 */

//val urlRegex = Regex("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")
val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+\$")

//fun String.isUrl() = urlRegex.matches(this)
fun String.isUrl() =
    contains(".") && !startsWith(".") && !endsWith(".") && lastIndexOf(".") != length - 2

fun String.isEmail() = emailRegex.matches(this)

fun main() {
    println("http://www.baidu@com".isEmail())
    println("www@baidu.com".isEmail())

    println("sun@qq.com".isEmail())
    println("sunqq.com".isEmail())
}