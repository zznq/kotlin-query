package io.andygrove.kquery

interface Token

data class IdentifierToken(val s: String) : Token {
    override fun toString(): String {
        return s
    }
}

abstract class TokenBase(val s: String) : Token {
    override fun toString(): String {
        return s
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }
}

class LiteralStringToken(s: String) : TokenBase(s)
class LiteralLongToken(s: String) : TokenBase(s)
class KeywordToken(s: String) : TokenBase(s)
class OperatorToken(s: String) : TokenBase(s)
class PunctuationToken(s: String) : TokenBase(s)

class TokenStream(val tokens: List<Token>) {

    var i = 0

    fun peek(): Token? {
        if (i < tokens.size) {
            return tokens[i]
        } else {
            return null
        }
    }

    fun next(): Token? {
        if (i < tokens.size) {
            return tokens[i++]
        } else {
            return null
        }
    }

    fun consumeKeyword(s: String): Boolean {
        val peek = peek()
        println("consumeKeyword('$s') next token is $peek")
        return if (peek == KeywordToken(s)) {
            i++
            println("consumeKeyword() returning true")
            true
        } else {
            println("consumeKeyword() returning false")
            false
        }
    }
}

class Tokenizer(val sql: String) {

    //TODO this whole class is pretty crude and needs a lot of attention + unit tests (Hint: this would be a great
    // place to start contributing!)

    val keywords = listOf("SELECT", "FROM", "WHERE", "AND", "OR", "NOT", "GROUP", "ORDER", "BY")

    var i = 0

    fun tokenize(): TokenStream {
        var token = nextToken()
        val list = mutableListOf<Token>()
        while (token != null) {
            list.add(token)
            token = nextToken()
        }
        return TokenStream(list)
    }

    private fun nextToken(): Token? {

        // skip whitespace
        while (i < sql.length && sql[i].isWhitespace()) {
            i++
        }

        // EOF check
        if (i == sql.length) {
            return null
        }

        // look for start of token
        if (sql[i] == ',') {
            i++
            return PunctuationToken(",")

        } else if (isIdentifierStart(sql[i])) {
            val start = i
            while (i < sql.length && isIdentifierPart(sql[i])) {
                i++
            }
            val s = sql.substring(start, i)
            if (keywords.contains(s.toUpperCase())) {
                return KeywordToken(s.toUpperCase())
            } else {
                return IdentifierToken(s)
            }

        } else if (listOf('=', '*', '/', '%', '-', '+', '<', '>').contains(sql[i])) {

            //TODO add support for `>=`, `<=`, `<>`, and `!=`

            i++
            return OperatorToken(sql[i-1].toString())

        } else if (sql[i] == '\'') {
            //TODO handle escaped quotes in string
            i++
            val start = i
            while (i < sql.length && sql[i] != '\'') {
                i++
            }
            i++
            return LiteralStringToken(sql.substring(start, i-1))

        } else if (sql[i].isDigit()) {
            //TODO support floating point numbers
            val start = i
            while (i < sql.length && sql[i].isDigit()) {
                i++
            }
            return LiteralLongToken(sql.substring(start, i))

        } else {
            throw IllegalStateException("Invalid character '${sql[i]}' at position $i in '$sql'")
        }

    }

    private fun isIdentifierStart(ch: Char): Boolean {
        return ch.isLetter()
    }

    private fun isIdentifierPart(ch: Char): Boolean {
        return ch.isLetter() || ch.isDigit() || ch == '_'
    }
}
