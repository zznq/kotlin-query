package kquery;

import org.junit.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SqlParserTest {

    @Test
    fun parseSimpleSelect() {
        val tokens = tokenize("SELECT a FROM b")
        val ast = SqlParser(tokens).parse()
        println(ast)


    }

    private fun tokenize(sql: String) : TokenStream {
        return Tokenizer(sql).tokenize()
    }
}