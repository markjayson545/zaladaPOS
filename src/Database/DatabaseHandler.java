package Database;

import java.sql.*;

public class DatabaseHandler {
    // Update connection string to include timeout and busy timeout settings
    private static final String ITEMS_DB = "jdbc:sqlite:src/Database/ItemsDatabase.db?busy_timeout=30000";

    // Items
    public static void createDefaultItems() {
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);

            // Create brands table
            String createBrandsTable = "CREATE TABLE IF NOT EXISTS brands ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT UNIQUE"
                    + ");";

            // Modify items table to include brand_id
            String createItemsTable = "CREATE TABLE IF NOT EXISTS items ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "brand_id INTEGER,"
                    + "name TEXT,"
                    + "price DOUBLE,"
                    + "stock INTEGER,"
                    + "FOREIGN KEY(brand_id) REFERENCES brands(id)"
                    + ");";

            Statement statement = connection.createStatement();
            statement.execute(createBrandsTable);
            statement.execute(createItemsTable);

            // Check if brands exist
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM brands");
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert default brands
                statement.execute("INSERT INTO brands (name) VALUES ('Samsung');");
                statement.execute("INSERT INTO brands (name) VALUES ('Apple');");
                statement.execute("INSERT INTO brands (name) VALUES ('Huawei');");

                // Sample items for each brand
                String[][] samsungItems = {
                        { "Samsung Galaxy S23", "999.99", "50" },
                        { "Samsung Galaxy Tab", "699.99", "30" },
                        { "Samsung Watch", "299.99", "40" }
                };

                String[][] appleItems = {
                        { "iPhone 14", "1099.99", "45" },
                        { "iPad Pro", "799.99", "35" },
                        { "Apple Watch", "399.99", "25" }
                };

                String[][] huaweiItems = {
                        { "Huawei P50", "899.99", "55" },
                        { "Huawei MatePad", "599.99", "20" },
                        { "Huawei Watch", "199.99", "15" }
                };

                // Insert Samsung items
                for (String[] item : samsungItems) {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO items (brand_id, name, price, stock) VALUES (1, ?, ?, ?);");
                    ps.setString(1, item[0]);
                    ps.setDouble(2, Double.parseDouble(item[1]));
                    ps.setInt(3, Integer.parseInt(item[2]));
                    ps.execute();
                    ps.close();
                }

                // Insert Apple items
                for (String[] item : appleItems) {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO items (brand_id, name, price, stock) VALUES (2, ?, ?, ?);");
                    ps.setString(1, item[0]);
                    ps.setDouble(2, Double.parseDouble(item[1]));
                    ps.setInt(3, Integer.parseInt(item[2]));
                    ps.execute();
                    ps.close();
                }

                // Insert Huawei items
                for (String[] item : huaweiItems) {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO items (brand_id, name, price, stock) VALUES (3, ?, ?, ?);");
                    ps.setString(1, item[0]);
                    ps.setDouble(2, Double.parseDouble(item[1]));
                    ps.setInt(3, Integer.parseInt(item[2]));
                    ps.execute();
                    ps.close();
                }
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Error creating items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertItem(String name, double price, int stock) {
        String query = "INSERT INTO items (name, price, stock) VALUES (?, ?, ?);";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setDouble(2, price);
            statement.setInt(3, stock);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateItem(int id, String name, double price, int stock) {
        String query = "UPDATE items SET name = ?, price = ?, stock = ? WHERE id = ?;";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setDouble(2, price);
            statement.setInt(3, stock);
            statement.setInt(4, id);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteItem(int id) {
        String query = "DELETE FROM items WHERE id = ?;";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String[][] getItems(int brandId) {
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM items WHERE brand_id = ?");
            statement.setInt(1, brandId);
            ResultSet countRs = statement.executeQuery();
            int rowCount = countRs.next() ? countRs.getInt(1) : 0;

            if (rowCount == 0) {
                return new String[0][0];
            }

            PreparedStatement dataStatement = connection.prepareStatement(
                    "SELECT * FROM items WHERE brand_id = ?");
            dataStatement.setInt(1, brandId);
            ResultSet resultSet = dataStatement.executeQuery();

            String[][] items = new String[rowCount][4];
            int index = 0;
            while (resultSet.next()) {
                items[index][0] = resultSet.getString("id");
                items[index][1] = resultSet.getString("name");
                items[index][2] = resultSet.getString("price");
                items[index][3] = resultSet.getString("stock");
                index++;
            }
            connection.close();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0][0];
    }

    public static String[] getBrands() {
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM brands ORDER BY id");

            java.util.List<String> brands = new java.util.ArrayList<>();
            while (rs.next()) {
                brands.add(rs.getString("name"));
            }

            connection.close();
            return brands.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public static void updateStock(int id, int stock) {
        String query = "UPDATE items SET stock = ? WHERE id = ?;";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, stock);
            statement.setInt(2, id);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static int getStock(int id) {
        String query = "SELECT stock FROM items WHERE id = ?;";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            int stock = resultSet.getInt("stock");
            statement.close();
            connection.close();
            return stock;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static void insertBrand(String name) {
        String query = "INSERT INTO brands (name) VALUES (?);";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static int getBrandId(String name) {
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM brands WHERE name = ?");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getBrandIdByItemId(int itemId) {
        try (Connection connection = DriverManager.getConnection(ITEMS_DB);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT brand_id FROM items WHERE id = ?")) {
            statement.setInt(1, itemId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("brand_id");
            }
        } catch (Exception e) {
            System.out.println("Error getting brand ID: " + e.getMessage());
        }
        return -1;
    }

    public static String getBrandNameById(int brandId) {
        try (Connection connection = DriverManager.getConnection(ITEMS_DB);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT name FROM brands WHERE id = ?")) {
            statement.setInt(1, brandId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception e) {
            System.out.println("Error getting brand name: " + e.getMessage());
        }
        return "";
    }

    // Sales
    // Update createDefaultSales to use try-with-resources
    public static void createDefaultSales() {
        String createQuery = "CREATE TABLE IF NOT EXISTS sales ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "item_id INTEGER,"
                + "brand_id INTEGER,"
                + "brand_name TEXT,"
                + "product_name TEXT,"
                + "quantity INTEGER,"
                + "total DOUBLE,"
                + "date TEXT,"
                + "FOREIGN KEY(item_id) REFERENCES items(id),"
                + "FOREIGN KEY(brand_id) REFERENCES brands(id)"
                + ");";
                
        try (Connection connection = DriverManager.getConnection(ITEMS_DB);
             Statement statement = connection.createStatement()) {
            // Enable WAL mode
            statement.execute("PRAGMA journal_mode=WAL;");
            statement.execute(createQuery);
        } catch (Exception e) {
            System.out.println("Error creating sales table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void insertSale(int item_id, int brand_id, String brand_name, String product_name, int quantity, double total, String date) {
        String insertQuery = "INSERT INTO sales (item_id, brand_id, brand_name, product_name, quantity, total, date) VALUES (?, ?, ?, ?, ?, ?, ?);";
        String updateQuery = "UPDATE items SET stock = stock - ? WHERE id = ?;";
        
        try (Connection connection = DriverManager.getConnection(ITEMS_DB)) {
            // Enable WAL mode for better concurrency
            try (Statement pragmaStmt = connection.createStatement()) {
                pragmaStmt.execute("PRAGMA journal_mode=WAL;");
            }
            
            connection.setAutoCommit(false);

            // Verify stock
            try (PreparedStatement checkStock = connection.prepareStatement("SELECT stock FROM items WHERE id = ?")) {
                checkStock.setInt(1, item_id);
                ResultSet rs = checkStock.executeQuery();
                if (rs.next() && rs.getInt("stock") < quantity) {
                    throw new SQLException("Insufficient stock for item ID: " + item_id);
                }
            }

            // Insert sale
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, item_id);
                insertStmt.setInt(2, brand_id);
                insertStmt.setString(3, brand_name);
                insertStmt.setString(4, product_name);
                insertStmt.setInt(5, quantity);
                insertStmt.setDouble(6, total);
                insertStmt.setString(7, date);
                insertStmt.executeUpdate();
            }

            // Update stock
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, item_id);
                updateStmt.executeUpdate();
            }

            connection.commit();
            System.out.println("Sale successfully recorded for item ID: " + item_id);
            
        } catch (Exception e) {
            System.out.println("Error inserting sale: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String[][] getSales() {
        String query = "SELECT * FROM sales;";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }
            resultSet.beforeFirst();
            String[][] sales = new String[rowCount][6];
            int index = 0;
            while (resultSet.next()) {
                sales[index][0] = resultSet.getString("id");
                sales[index][1] = resultSet.getString("item_id");
                sales[index][2] = resultSet.getString("product_name");
                sales[index][3] = resultSet.getString("quantity");
                sales[index][4] = resultSet.getString("total");
                sales[index][5] = resultSet.getString("date");
                index++;
            }
            statement.close();
            connection.close();
            return sales;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void deleteSale(int id) {
        String query = "DELETE FROM sales WHERE id = ?;";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.execute();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static double getSalesCountPerItem(int item_id) {
        String query = "SELECT SUM(quantity * total/quantity) FROM sales WHERE item_id = ?;";
        try {
            Connection connection = DriverManager.getConnection(ITEMS_DB);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, item_id);
            ResultSet resultSet = statement.executeQuery();
            double total = resultSet.getDouble(1);
            statement.close();
            connection.close();
            return total;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0.0;
    }

}
