package com.sqlens.service;

import com.sqlens.dto.SampleQueryDto;
import com.sqlens.dto.TableMetadataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchemaService {

    private static final Logger log = LoggerFactory.getLogger(SchemaService.class);

    private final JdbcTemplate jdbcTemplate;

    public SchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SampleQueryDto> getSampleQueries() {
        return List.of(
                SampleQueryDto.builder()
                        .id("sample-1")
                        .title("1. Customer City Lookup (Sequential Scan Demo)")
                        .category("Filtering & Scans")
                        .description("Filters 5,000 customer records by city. Demonstrates how PostgreSQL performs a Sequential Scan on non-primary key predicates.")
                        .expectedPlanType("Seq Scan")
                        .query("SELECT id, name, email, city, country\nFROM customers\nWHERE city = 'Colombo';")
                        .build(),

                SampleQueryDto.builder()
                        .id("sample-2")
                        .title("2. Order Lookup by Customer (Index Scan vs Seq Scan)")
                        .category("Indexing")
                        .description("Finds all orders placed by customer ID 105. Demonstrates Index Scan using idx_orders_customer_id.")
                        .expectedPlanType("Bitmap Heap Scan / Index Scan")
                        .query("SELECT id, customer_id, order_date, total_amount, status\nFROM orders\nWHERE customer_id = 105;")
                        .build(),

                SampleQueryDto.builder()
                        .id("sample-3")
                        .title("3. Customers with Completed Orders (Hash Join)")
                        .category("Joins")
                        .description("Joins customers with completed orders. Demonstrates Hash Join and filter evaluation across related tables.")
                        .expectedPlanType("Hash Join")
                        .query("SELECT c.name, c.email, o.id AS order_id, o.total_amount, o.order_date\nFROM customers c\nJOIN orders o ON c.id = o.customer_id\nWHERE o.status = 'COMPLETED'\nORDER BY o.order_date DESC\nLIMIT 100;")
                        .build(),

                SampleQueryDto.builder()
                        .id("sample-4")
                        .title("4. Department Salary Aggregation (Hash Aggregate)")
                        .category("Aggregation")
                        .description("Calculates total and average employee salaries grouped by department with minimum headcount filter.")
                        .expectedPlanType("HashAggregate + Hash Join")
                        .query("SELECT d.name AS department, COUNT(e.id) AS employee_count,\n       ROUND(AVG(e.salary), 2) AS avg_salary,\n       SUM(e.salary) AS total_payroll\nFROM departments d\nJOIN employees e ON d.id = e.department_id\nGROUP BY d.name\nHAVING COUNT(e.id) > 50\nORDER BY avg_salary DESC;")
                        .build(),

                SampleQueryDto.builder()
                        .id("sample-5")
                        .title("5. Top Revenue Products (3-Table Join with Aggregates)")
                        .category("Complex Analytics")
                        .description("Joins products, order_items, and orders to compute total revenue per product category.")
                        .expectedPlanType("Sort + HashAggregate + Hash Join")
                        .query("SELECT p.category, p.name, SUM(oi.quantity) AS units_sold,\n       SUM(oi.quantity * oi.unit_price) AS total_revenue\nFROM products p\nJOIN order_items oi ON p.id = oi.product_id\nJOIN orders o ON oi.order_id = o.id\nWHERE o.status = 'COMPLETED'\nGROUP BY p.category, p.name\nORDER BY total_revenue DESC\nLIMIT 20;")
                        .build(),

                SampleQueryDto.builder()
                        .id("sample-6")
                        .title("6. High-Value Order Date Range (Bitmap Scan + Sort)")
                        .category("Range Queries")
                        .description("Scans orders within a specific date range exceeding $1,000 threshold, ordered by date.")
                        .expectedPlanType("Bitmap Index Scan / Sort")
                        .query("SELECT id, customer_id, order_date, total_amount, status\nFROM orders\nWHERE order_date BETWEEN '2023-01-01' AND '2023-12-31'\n  AND total_amount > 1000.00\nORDER BY total_amount DESC\nLIMIT 50;")
                        .build()
        );
    }

    public List<TableMetadataDto> getTables() {
        try {
            String sql = "SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = 'public' AND table_type = 'BASE TABLE' " +
                    "ORDER BY table_name;";

            List<String> tableNames = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("table_name"));
            if (tableNames.isEmpty()) {
                return getDefaultFallbackTables();
            }

            List<TableMetadataDto> result = new ArrayList<>();
            for (String tableName : tableNames) {
                // Get estimated row count
                long rowCount = 0;
                try {
                    String countSql = "SELECT n_live_tup FROM pg_stat_user_tables WHERE relname = ?;";
                    Long count = jdbcTemplate.queryForObject(countSql, Long.class, tableName);
                    rowCount = count != null ? count : 0;
                } catch (Exception ignored) {}

                // Get columns
                String colSql = "SELECT column_name, data_type, is_nullable " +
                        "FROM information_schema.columns " +
                        "WHERE table_schema = 'public' AND table_name = ? " +
                        "ORDER BY ordinal_position;";

                List<TableMetadataDto.ColumnInfoDto> columns = jdbcTemplate.query(colSql, (rs, rowNum) -> {
                    String colName = rs.getString("column_name");
                    String dataType = rs.getString("data_type");
                    boolean nullable = "YES".equalsIgnoreCase(rs.getString("is_nullable"));
                    boolean isPk = "id".equalsIgnoreCase(colName);
                    boolean isFk = colName.endsWith("_id");
                    return TableMetadataDto.ColumnInfoDto.builder()
                            .name(colName)
                            .dataType(dataType)
                            .isPrimaryKey(isPk)
                            .isForeignKey(isFk)
                            .isNullable(nullable)
                            .build();
                }, tableName);

                // Get indexes
                String indexSql = "SELECT indexname FROM pg_indexes WHERE schemaname = 'public' AND tablename = ?;";
                List<String> indexes = jdbcTemplate.query(indexSql, (rs, rowNum) -> rs.getString("indexname"), tableName);

                result.add(TableMetadataDto.builder()
                        .tableName(tableName)
                        .estimatedRowCount(rowCount)
                        .columns(columns)
                        .indexes(indexes)
                        .build());
            }

            return result;
        } catch (Exception e) {
            log.warn("Could not introspect live PostgreSQL schema: {}. Falling back to default schema definition.", e.getMessage());
            return getDefaultFallbackTables();
        }
    }

    private List<TableMetadataDto> getDefaultFallbackTables() {
        return List.of(
                TableMetadataDto.builder()
                        .tableName("customers")
                        .estimatedRowCount(5000)
                        .columns(List.of(
                                new TableMetadataDto.ColumnInfoDto("id", "integer", true, false, false),
                                new TableMetadataDto.ColumnInfoDto("name", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("email", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("city", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("country", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("created_at", "timestamp", false, false, false)
                        ))
                        .indexes(List.of("customers_pkey", "idx_customers_city"))
                        .build(),

                TableMetadataDto.builder()
                        .tableName("orders")
                        .estimatedRowCount(50000)
                        .columns(List.of(
                                new TableMetadataDto.ColumnInfoDto("id", "integer", true, false, false),
                                new TableMetadataDto.ColumnInfoDto("customer_id", "integer", false, true, false),
                                new TableMetadataDto.ColumnInfoDto("order_date", "date", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("total_amount", "numeric", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("status", "character varying", false, false, false)
                        ))
                        .indexes(List.of("orders_pkey", "idx_orders_customer_id", "idx_orders_order_date"))
                        .build(),

                TableMetadataDto.builder()
                        .tableName("order_items")
                        .estimatedRowCount(120000)
                        .columns(List.of(
                                new TableMetadataDto.ColumnInfoDto("id", "integer", true, false, false),
                                new TableMetadataDto.ColumnInfoDto("order_id", "integer", false, true, false),
                                new TableMetadataDto.ColumnInfoDto("product_id", "integer", false, true, false),
                                new TableMetadataDto.ColumnInfoDto("quantity", "integer", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("unit_price", "numeric", false, false, false)
                        ))
                        .indexes(List.of("order_items_pkey", "idx_order_items_order_id", "idx_order_items_product_id"))
                        .build(),

                TableMetadataDto.builder()
                        .tableName("products")
                        .estimatedRowCount(10000)
                        .columns(List.of(
                                new TableMetadataDto.ColumnInfoDto("id", "integer", true, false, false),
                                new TableMetadataDto.ColumnInfoDto("name", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("category", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("price", "numeric", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("stock_quantity", "integer", false, false, false)
                        ))
                        .indexes(List.of("products_pkey", "idx_products_category"))
                        .build(),

                TableMetadataDto.builder()
                        .tableName("employees")
                        .estimatedRowCount(2000)
                        .columns(List.of(
                                new TableMetadataDto.ColumnInfoDto("id", "integer", true, false, false),
                                new TableMetadataDto.ColumnInfoDto("name", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("email", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("department_id", "integer", false, true, true),
                                new TableMetadataDto.ColumnInfoDto("salary", "numeric", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("hire_date", "date", false, false, false)
                        ))
                        .indexes(List.of("employees_pkey", "idx_employees_dept_salary"))
                        .build(),

                TableMetadataDto.builder()
                        .tableName("departments")
                        .estimatedRowCount(20)
                        .columns(List.of(
                                new TableMetadataDto.ColumnInfoDto("id", "integer", true, false, false),
                                new TableMetadataDto.ColumnInfoDto("name", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("building", "character varying", false, false, false),
                                new TableMetadataDto.ColumnInfoDto("budget", "numeric", false, false, false)
                        ))
                        .indexes(List.of("departments_pkey"))
                        .build()
        );
    }
}
