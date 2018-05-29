package com.fellowshipofthe;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("searchproduct")
public class SearchProductResource{


    @GET
    @Path("searchproductcode/{productcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SearchProduct> searchProduct(@PathParam("productcode") String productCode) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        SearchProductDAO searchProductDAO= new SearchProductDAO();

        System.out.println("search product called!");

        return searchProductDAO.searchProduct(productCode);

    }

    @GET
    @Path("viewproductitems/{productcode}-{locationID}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SearchProduct> viewProduct(@PathParam("productcode") String productItemCode, @PathParam("locationID") String locationID) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        SearchProductDAO searchProductDAO= new SearchProductDAO();

        System.out.println("view product items called!");

        return searchProductDAO.viewProductItems(productItemCode,locationID);

    }


}