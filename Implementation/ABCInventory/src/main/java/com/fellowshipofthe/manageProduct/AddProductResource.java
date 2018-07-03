package com.fellowshipofthe.manageProduct;

import com.fellowshipofthe.entityClasses.Product;
import com.fellowshipofthe.entityClasses.ProductItem;
import com.fellowshipofthe.searchProduct.SearchProductDAO;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("manageproduct")
public class AddProductResource {
    SearchProductDAO productDAO = new SearchProductDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean addProduct(Product newProduct) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        System.out.println("addProduct called!");
        //creating variables to store new product details
        String newCode= newProduct.getProductCode();
        String newName= newProduct.getProductName();
        String newPrice= newProduct.getPrice();
        String newDescription= newProduct.getDescription();
        //creating and array list to store each String value of the product details
        ArrayList<String> newProductList = new ArrayList<>();
        //adding new product details to the arraylist
        newProductList.add(newCode);
        newProductList.add(newName);
        newProductList.add(newPrice);
        newProductList.add(newDescription);
        //check if arraylist items are valid
        System.out.println(newProductList.get(0) + newProductList.get(1) + newProductList.get(2) + newProductList.get(3));
        //calling productDAO to connect with database and insert new product
        return productDAO.addProduct(newProductList);
    }

    //resource for adding productItem
    @POST
    @Path("addproductitem")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean addProductItem(ProductItem newProductItem) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        System.out.println("addProductItem called!");
        String newProductItemCode= newProductItem.getProductItemCode();
        String productCode= newProductItem.getProductCode();
        String newProductSize= newProductItem.getProductSize();
        ArrayList<String> newProductItemList= new ArrayList<>();
        newProductItemList.add(newProductItemCode);
        newProductItemList.add(productCode);
        newProductItemList.add(newProductSize);
        System.out.println(newProductItemList.get(0) + newProductItemList.get(1) + newProductItemList.get(2));
        return productDAO.addProductItem(newProductItemList);

    }
}
