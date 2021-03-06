package transferProduct;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import entityClass.StoredProduct;
import entityClass.Transfer;
import entityClass.TransferItem;
import homePage.HomePageController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import manageProduct.AppScreen;
import reportTransfer.ReportTransferController;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AcceptTransferItemController {
    //initialize variables
    @FXML
    private TableView<TransferItem> tblTransferItem;
    @FXML
    AnchorPane anchorPane;

    ObservableList<TransferItem> data;
    List<TransferItem> transferItemList;
    List<StoredProduct> storedProductList = new ArrayList<StoredProduct>();

    // Variables for create the client
    Client client;
    WebResource webResourceGet;
    ClientResponse response;
    GenericType<List<TransferItem>> listc = new GenericType<List<TransferItem>>() {
    };

    GenericType<List<StoredProduct>> listcStoredProduct = new GenericType<List<StoredProduct>>() {
    };
    AppScreen screen = new AppScreen();

    String getTransferItemURL;

    SendProductController sendProductController = new SendProductController();
    AcceptProductController acceptProductController = new AcceptProductController();

    public void handleAcceptProducts() throws IOException {
        if(updateTransferStatusAccept().equals("updated")){
            updateProductQuantityAccept();
            screen.alertMessages("Accepted Transfer","Transfer has been accepted");

            //load text fields and labels for adding product item
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/AcceptTransferFXML.fxml"));
            AnchorPane pane = loader.load();

            AcceptProductController myController = loader.getController();

            //Set Data to FXML through controller
            myController.showAllTransfer();
            anchorPane.getChildren().setAll(pane);
        }else{
            AppScreen screen = new AppScreen();
            screen.alertMessages("Cannot Accept","Cannot accept this transfer! Please contact administrator!");
        }
    }

    // Search Transfer or Transfer ID
    private  void searchTransferItem(String URL ,String searchField, String code) {
        data.clear();
        webResourceGet = client.resource(URL).queryParam(searchField, code);
        response = webResourceGet.get(ClientResponse.class);
        transferItemList = response.getEntity(listc);
        if (response.getStatus() != 200) {
            throw new WebApplicationException();
        }
        if (transferItemList.isEmpty()) {
            screen.alertMessages("Non-Existent Transfer Item", "Transfer Item does not exist!");
        } else {
            for (TransferItem ti : transferItemList) {
                data.add(ti);
            }
        }
    }

    //search the stored product by combining locationID and productItemCode
    private  List<StoredProduct> searchStoredProductByCombinationCodes(String locationID, String productItemCode) {
        webResourceGet = client.resource("http://abcinventoryserver.ap-southeast-2.elasticbeanstalk.com/rest/transferproduct/searchstoredproductsbycominationcodes/").queryParam("locationID",locationID).queryParam("productItemCode",productItemCode);
        response = webResourceGet.get(ClientResponse.class);
        storedProductList = response.getEntity(listcStoredProduct);
        if (response.getStatus() != 200) {
            throw new WebApplicationException();
        }
        return storedProductList;
    }

    public void updateProductQuantityAccept(){
        for (TransferItem ti: transferItemList) {
            StoredProduct storedProduct = new StoredProduct();
            storedProduct.setProductQuantity(ti.getProductQuantity());
            storedProduct.setProductItemCode(ti.getProductItemCode());
            storedProduct.setLocationID(acceptProductController.getSELECTED_DESTINATION_LOCATIO_ID());
            storedProductList.add(storedProduct);
        }
        List<StoredProduct> updateStoredProductList = new ArrayList<StoredProduct>();
        List<StoredProduct> addStoredProductList = new ArrayList<StoredProduct>();
        for (StoredProduct st: storedProductList){
            if(searchStoredProductByCombinationCodes(st.getLocationID(),st.getProductItemCode()).isEmpty()){
                addStoredProductList.add(st);
            }else{
                updateStoredProductList.add(st);
            }
        }
        clientRequestPut(updateStoredProductList,"updatetransferitemquantityaccept","","");

        //add store product
        String postURL= "http://abcinventoryserver.ap-southeast-2.elasticbeanstalk.com/rest/transferproduct/addstoredproduct";
        WebResource webResourcePost= client.resource(postURL);
        //use the object passed as a parameter to send a request
        response= webResourcePost.type("application/json").post(ClientResponse.class, addStoredProductList);
        response.bufferEntity();
        response.getEntity(String.class);

        data.clear();
    }

    public String updateTransferStatusAccept(){
        String transferID = tblTransferItem.getItems().get(0).getTransferID();
        String responseUpdate = clientRequestPut(transferID,"updatetransferstatusaccept","transferID",transferID);
        return responseUpdate;
    }

    public String clientRequestPut(Object entity, String path, String searchField, String code){
        String postURL= "http://abcinventoryserver.ap-southeast-2.elasticbeanstalk.com/rest/transferproduct/" + path;
        WebResource webResourcePost= client.resource(postURL);
        //use the object passed as a parameter to send a request
        response= webResourcePost.type("application/json").put(ClientResponse.class, entity);
        response.bufferEntity();
        String responseValue= response.getEntity(String.class);
        return responseValue;
    }

    //show all the Transfer when initialize the sceen
    public void showAllTransferItem(String transferID){
        //connect to the server to retrieve the data
        data = tblTransferItem.getItems();
        data.clear();
        //creating a new client to send get request
        ClientConfig clientConfig= new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        client = Client.create(clientConfig);

        getTransferItemURL="http://abcinventoryserver.ap-southeast-2.elasticbeanstalk.com/rest/transferproduct/displaysendingtransferitem/";
        searchTransferItem(getTransferItemURL,"transferID",transferID);
    }

    public void handleBackButton() throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getClassLoader().getResource("fxml/ReportTransferFXML.fxml"));
        AnchorPane pane = loader.load();
        ReportTransferController controller= loader.getController();
        controller.showAllTransfer();
        anchorPane.getChildren().setAll(pane);
    }

    public void handleAcceptInstanceBackButton() throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getClassLoader().getResource("fxml/AcceptTransferFXML.fxml"));
        AnchorPane pane = loader.load();
        AcceptProductController controller= loader.getController();
        controller.showAllTransfer();
        anchorPane.getChildren().setAll(pane);
    }

    public void handleMainMenuButton() throws IOException {
        FXMLLoader loader= new FXMLLoader(getClass().getClassLoader().getResource("fxml/HomePageFXML.fxml"));
        AnchorPane pane = loader.load();
        HomePageController controller= loader.getController();
        controller.checkStaff();
        anchorPane.getChildren().setAll(pane);
    }
}
