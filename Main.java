// Gwen Canos
// Database Design and Implement
// Dr. St. Clair
// HW #4
// 11/3/23
// JDBC Program using prepared statements and queries
// Menu Driven Interface allows users to run any of the following queries
// Query #1b
// Shows all supplier information for suppliers that supply a particular product
// (that a user inputs) using a join query
// Query #2
// Show product ids and supplier ids for any product
// that costs less than a particular value the user inputs
// Query #3
// Show the product id and warehouse id for any product that is out of stock at that warehouse
// Query #7
// Show all widget information for any widget that has no sales (no customer orders)
// Query #9
// For each widget, shows the total amount ordered from customers
// If no customers ordered the widget, '0' will be displayed.

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (Exception e){
            System.out.println("Can't load driver");
        }

        try{
            System.out.println("Starting Connection........");
            Scanner s = new Scanner(System.in);
            System.out.printf("Please Enter the Database Name: ");
            String dB;
            dB = s.nextLine();
            System.out.printf("Please Enter Your User Login: ");
            String userLogin;
            userLogin = s.nextLine();
            System.out.printf("Please Enter Your Password: ");
            String pwd;
            pwd = s.nextLine();
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://161.35.177.175:3306/"+dB, userLogin, pwd);
            System.out.println("Connection Established");

            String runProgram;
            System.out.println("Would you like to run the program? ");
            System.out.println("Please type in '1, 2, 3, 7 or 9' to run a query or 'n' for no and hit 'enter':");
            runProgram = s.nextLine();

            while(!"n".equals(runProgram)){
                System.out.println("Your query selection:" + runProgram);
                if (Objects.equals(runProgram, "1")){
                    queryOne(con);
                } else if (Objects.equals(runProgram, "2")) {
                    queryTwo(con);
                } else if (Objects.equals(runProgram, "3")) {
                    queryThree(con);
                } else if (Objects.equals(runProgram, "7")) {
                    querySeven(con);
                } else if (Objects.equals(runProgram, "9")) {
                    queryNine(con);
                } else {
                    System.out.println("Query Not Found");
                }

                System.out.printf("Would you like to run the program again? '1, 2, 3, 7, 9' or n:");
                runProgram = s.nextLine();
            }
//            con.close();
        }
        catch (SQLException e){
            System.out.println(e.getMessage() + " Can't connect to database");
            while(e!=null){
                System.out.println("Message: "+e.getMessage());
                e= e.getNextException();
            }
        }
        catch (Exception e){
            System.out.println("Other Error");
        }
    }

    private static void queryOne (Connection con) throws SQLException{
        Scanner scan = new Scanner(System.in);
        String product_id;
        System.out.println("---Running query one---");
        System.out.println("Enter a Product's ID");
        product_id = scan.nextLine();
        String query = "select s.SupplierID, s.Street, s.City, s.State, s.Zip, s.SupplierName"
                + " from Supplier s join SuppliersProducts x"
                + " on s.SupplierID = x.SupplierID"
                + " where x.ProductID = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,product_id);
        ResultSet result = stmt.executeQuery();
        System.out.println("-----Processing Results-----");
        System.out.println("\n The following results shows all Supplier info " +
                "for suppliers that supply products with the following id-> " + product_id);
        System.out.format("%-20s%-15s%-15s%-15s%-15s%-15s\n", "SupplierID:", "Street:", "City:", "State:", " Zip:", " Supplier Name:");
        while(result.next()) {
            System.out.format("%-15s%-20s%-15s%-15s%-15s%-20s\n",
                    result.getString("s.SupplierID"),
                    result.getString("s.Street"),
                    result.getString("s.City"),
                    result.getString("s.State"),
                    result.getString("s.Zip"),
                    result.getString("s.SupplierName"));
        }
    }

    private static void queryTwo (Connection con) throws SQLException{
        Scanner scan = new Scanner(System.in);
        String product_price;
        System.out.println("---Running query two---");
        System.out.println("Enter a Product's price");
        product_price = scan.nextLine();
        String query = "select p.ProductID, x.SupplierID"
                + " from SuppliersProducts x, Product p"
                + " where x.ProductID = p.ProductID"
                + " and p.price < ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1,product_price);
        ResultSet result = stmt.executeQuery();
        System.out.println("-----Processing Results-----");
        System.out.println("\n The following results show product ids and supplier ids " +
                "for products costing less than " + product_price + " dollars");
        System.out.format("%-15s%-15s\n", "ProductID:", "SupplierID:");
        while(result.next()) {
            System.out.format("%-15s%-15s\n",
                    result.getString("p.ProductID"),
                    result.getString("x.SupplierID"));
        }
    }
    private static void queryThree (Connection con) throws SQLException{
        System.out.println("---Running query three---");
        String query = "select ProductID, WarehouseID" +
                " from WarehousesProducts" +
                " where stockqty = 0";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("-----Processing Results-----");
        System.out.println("\n The following results show the Product ID and Warehouse ID " +
                "for any product that is out of stock at that warehouse");
        System.out.format("%-20s%-15s\n", "ProductID:", "WarehouseID:");
        while (result.next()) {
            String Pid = result.getString("ProductID");
            String Whid = result.getString("WarehouseID");
            System.out.format("%-20s%-15s\n", Pid, Whid);
        }
    }
    private static void querySeven (Connection con) throws SQLException{
        System.out.println("---Running query seven---");
        String query = "select w.WidgetID, w.Name, w.Price, w.Color " +
                "from Widget w " +
                "where w.WidgetID NOT IN " +
                "(select o.widgetID " +
                "from CustomersOrders o)";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("-----Processing Results-----");
        System.out.println("\n The following results show all widget info " +
                "for any widget that has no sales/customer orders");
        System.out.format("%-20s%-15s%-15s%-15s\n", "WidgetID:", "Name:", "Price:","Color:");
        while (result.next()) {
            int key = result.getInt(1);
            String name = result.getString("Name");
            String price = result.getString("Price");
            String color = result.getString("Color");
            System.out.format("%-20s%-15s%-15s%-15s\n", key, name, price, color);
        }
    }
    private static void queryNine(Connection con) throws SQLException{
        System.out.println("---Running query nine---:");
        String query = " select w.WidgetID, coalesce(sum(o.Quantity),0) " +
                "from CustomersOrders o right join Widget w on o.WidgetID = w.WidgetID " +
                "group by w.WidgetID";
        PreparedStatement stmt = con.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        System.out.println("-----Processing Results-----");
        System.out.println("\n The following results show total quantity amount ordered from customers " +
                "for each widget");
        System.out.format("%-20s%-15s\n", "WidgetID:", "Quantity:");
        while (result.next()) {
            int key = result.getInt(1);
            String qty = result.getString("coalesce(sum(o.Quantity),0)");
            System.out.format("%-20s%-15s\n", key, qty);
        }
    }
}
