package io.andygrove.kquery

import org.junit.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

/**
 * Example source code for README in this repo.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReadmeExamplesTest {

    val employeeCsv = "src/test/data/employee.csv"

    @Test
    fun `SQL example`() {

        // Create a context
        val ctx = ExecutionContext()

        // Register a CSV data source
        val csv = ctx.csv(employeeCsv)
        ctx.register("employee", csv)

        // Execute a SQL query
        val df = ctx.sql("SELECT id, first_name, last_name FROM employee WHERE state = 'CO'")

        val expected =
            "Selection: #state = 'CO'\n" +
            "\tProjection: #id, #first_name, #last_name\n" +
            "\t\tScan: src/test/data/employee.csv; projection=None\n"

        assertEquals(expected, format(df.logicalPlan()))
    }

    @Test
    fun `DataFrame example`() {

        // Create a context
        val ctx = ExecutionContext()

        // Construct a query using the DataFrame API
        val df: DataFrame = ctx.csv(employeeCsv)
                .filter(col("state") eq lit("CO"))
                .select(listOf(col("id"), col("first_name"), col("last_name")))

        val expected =
                "Projection: #id, #first_name, #last_name\n" +
                "\tSelection: #state = 'CO'\n" +
                "\t\tScan: src/test/data/employee.csv; projection=None\n"

        assertEquals(expected, format(df.logicalPlan()))
    }

}