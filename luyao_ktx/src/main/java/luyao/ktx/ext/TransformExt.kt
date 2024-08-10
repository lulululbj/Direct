package luyao.ktx.ext

/**
 * Description:
 * Author: luyao
 * Date: 2022/8/26 13:41
 */
private val HEX_DIGITS =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

/**
 * byte array to hex string
 */
fun ByteArray.toHexString(): String {
    val result = CharArray(size shl 1)
    var index = 0
    for (b in this) {
        result[index++] = HEX_DIGITS[b.toInt().shr(4) and 0xf]
        result[index++] = HEX_DIGITS[b.toInt() and 0xf]
    }
    return String(result)
}

/**
 * hex string to byte array
 */
fun String.hexToByteArray(): ByteArray {
    var len = length
    var hexString = this
    if (len % 2 != 0) {
        hexString = "0$hexString"
        len++
    }
    val hexBytes = hexString.uppercase().toCharArray()
    val ret = ByteArray(len shr 1)
    var i = 0
    while (i < len) {
        ret[i shr 1] = ((hexBytes[i].hexToInt()) shl 4 or (hexBytes[i + 1].hexToInt())).toByte()
        i += 2
    }
    return ret
}

fun Char.hexToInt(): Int {
    return when (this) {
        in '0'..'9' -> this - '0'
        in 'A'..'F' -> this - 'A' + 10
        else -> throw IllegalArgumentException()
    }
}
