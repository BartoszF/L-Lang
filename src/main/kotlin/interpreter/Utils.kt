package pl.bfelis.fc93.language.interpreter

object Utils {
    fun stringify(obj: Any?): String {
        if (obj == null) return "nil"
        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return obj.toString()
    }

    fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        return if (obj is Boolean) obj else true
    }
}
