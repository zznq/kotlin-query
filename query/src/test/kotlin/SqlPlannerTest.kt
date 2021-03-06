package io.andygrove.kquery

import org.junit.Ignore
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SqlPlannerTest {

    val employeeCsv = "src/test/data/employee.csv"

    @Test
    fun `plan simple SELECT`() {

        val ctx = ExecutionContext()
        ctx.register("employee", ctx.csv(employeeCsv))
        val df = ctx.sql("SELECT id FROM employee")

        val expected =
            "Projection: #id\n" +
            "\tScan: src/test/data/employee.csv; projection=None\n"

        assertEquals(expected, format(df.logicalPlan()))
    }

    @Test
    fun `plan SELECT with WHERE`() {

        val ctx = ExecutionContext()
        ctx.register("employee", ctx.csv(employeeCsv))
        val df = ctx.sql("SELECT id FROM employee WHERE state = 'CO'")

        val expected =
            "Selection: #state = 'CO'\n" +
            "\tProjection: #id\n" +
            "\t\tScan: src/test/data/employee.csv; projection=None\n"

        assertEquals(expected, format(df.logicalPlan()))
    }
}