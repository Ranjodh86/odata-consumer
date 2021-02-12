package com.odata.odataconsumer.service;

import com.odata.odataconsumer.model.Customer;
import com.odata.odataconsumer.model.Order;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.*;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.uri.FilterFactoryImpl;
import org.apache.olingo.client.core.uri.HasFilter;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.format.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ODataService {

    private static final String ROOT_URL = "https://services.odata.org/Northwind/Northwind.svc";
    private static final String CUSTOMER_SET_NAME = "Customers";
    private static final String CUSTOMER_ID = "CustomerID";
    private static final String ORDER_SET_NAME = "Orders";
    private static final Logger logger = LoggerFactory.getLogger(ODataService.class);

    public List<Customer> getCustomers() {

        ODataClient client = ODataClientFactory.getClient();
        URI absoluteUri = client.newURIBuilder(ROOT_URL).appendEntitySetSegment(CUSTOMER_SET_NAME).addQueryOption("top", "2", true).orderBy(CUSTOMER_ID).build();

        //https://services.odata.org/Northwind/Northwind.svc/Customers
        //https://services.odata.org/Northwind/Northwind.svc/Customers()
        //https://services.odata.org/Northwind/Northwind.svc/Customers?%24top=2
        //https://services.odata.org/Northwind/Northwind.svc/Customers?%24top=2&%24orderby=CustomerID

        logger.info("URL: " + absoluteUri);

        List<Customer> customers = new ArrayList<>();

        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request = client.getRetrieveRequestFactory().getEntitySetIteratorRequest(absoluteUri);
        request.setFormat(ContentType.APPLICATION_JSON);
        ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute();


        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator = response.getBody();

        while (iterator.hasNext()) {
            ClientEntity ce = iterator.next();

            Customer customer = populateCustomer(ce);
            logger.info("Customer detail -  {}", customer);

            customers.add(customer);

        }
        return customers;
    }

    public Customer getCustomer(String id) {

        ODataClient client = ODataClientFactory.getClient();
        URI absoluteUri = client.newURIBuilder(ROOT_URL).appendEntitySetSegment(CUSTOMER_SET_NAME).appendKeySegment(id).build();
        //https://services.odata.org/Northwind/Northwind.svc/Customers('ANKL')
        logger.info("URL: " + absoluteUri);

        ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory().getEntityRequest(absoluteUri);
        request.setFormat(ContentType.APPLICATION_JSON);
        ODataRetrieveResponse<ClientEntity> response = request.execute();

        ClientEntity ce = response.getBody();

        Customer customer = populateCustomer(ce);
        logger.info("Customer detail -  {}", customer);

        return customer;

    }

    public List<Order> getCustomerOrders(String customerId) {

        ODataClient client = ODataClientFactory.getClient();
        URI absoluteUri = client.newURIBuilder(ROOT_URL).appendEntitySetSegment(CUSTOMER_SET_NAME).appendKeySegment(customerId).appendActionCallSegment(ORDER_SET_NAME).build();
        //https://services.odata.org/Northwind/Northwind.svc/Customers('ANKL')/Orders
        logger.info("URL: " + absoluteUri);

        List<Order> orders = new ArrayList<>();

        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request = client.getRetrieveRequestFactory().getEntitySetIteratorRequest(absoluteUri);
        request.setFormat(ContentType.APPLICATION_JSON);
        ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute();

        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator = response.getBody();

        while (iterator.hasNext()) {
            ClientEntity ce = iterator.next();

            Order order = populateOrder(ce);
            logger.info("Order detail -  {}", order);

            orders.add(order);

        }
        return orders;
    }

    public List<Customer> getCustomersWithPrefix(String prefix) {

        ODataClient client = ODataClientFactory.getClient();

        URI absoluteUri = client.newURIBuilder(ROOT_URL).appendEntitySetSegment(CUSTOMER_SET_NAME).filter("startswith(CustomerID,'" + prefix + "')").build();
        //https://services.odata.org/Northwind/Northwind.svc/Customers?$filter=startswith(CustomerID,'ANATR')
        logger.info("URL: " + absoluteUri);

        List<Customer> customers = new ArrayList<>();

        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request = client.getRetrieveRequestFactory().getEntitySetIteratorRequest(absoluteUri);
        request.setFormat(ContentType.APPLICATION_JSON);
        ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute();

        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator = response.getBody();

        while (iterator.hasNext()) {
            ClientEntity ce = iterator.next();

            Customer customer = populateCustomer(ce);
            logger.info("Customer detail -  {}", customer);

            customers.add(customer);

        }
        return customers;
    }

    public Customer populateCustomer(ClientEntity ce) {

        Customer customer = new Customer();
        customer.setCustomerID(ce.getProperty("CustomerID").getValue().toString());
        customer.setAddress(ce.getProperty("Address").getValue().toString());
        customer.setCity(ce.getProperty("City").getValue().toString());
        customer.setCompanyName(ce.getProperty("CompanyName").getValue().toString());
        customer.setContactName(ce.getProperty("ContactName").getValue().toString());
        customer.setContactTitle(ce.getProperty("ContactTitle").getValue().toString());
        customer.setCountry(ce.getProperty("Country").getValue().toString());
        customer.setFax(ce.getProperty("Fax").getValue().toString());
        customer.setPhone(ce.getProperty("Phone").getValue().toString());
        customer.setPostalCode(ce.getProperty("PostalCode").getValue().toString());
        customer.setRegion(ce.getProperty("Region").getValue().toString());

        return customer;
    }

    public Order populateOrder(ClientEntity ce) {

        Order order = new Order();

        order.setCustomerID(ce.getProperty("CustomerID").getValue().toString());
        order.setEmployeeID(Integer.parseInt(ce.getProperty("EmployeeID").getValue().toString()));
        order.setFreight(Float.parseFloat(ce.getProperty("Freight").getValue().toString()));
        order.setOrderID(Integer.parseInt(ce.getProperty("OrderID").getValue().toString()));
        order.setShipAddress(ce.getProperty("ShipAddress").getValue().toString());
        order.setShipCity(ce.getProperty("ShipCity").getValue().toString());
        order.setShipName(ce.getProperty("ShipName").getValue().toString());
        order.setShipCountry(ce.getProperty("ShipCountry").getValue().toString());
        order.setShipPostalCode(ce.getProperty("ShipPostalCode").getValue().toString());
        order.setShipRegion(ce.getProperty("ShipRegion").getValue().toString());
        order.setShipVia(Integer.parseInt(ce.getProperty("ShipPostalCode").getValue().toString()));

        try {
            order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ce.getProperty("OrderDate").getValue().toString()));
            order.setShippedDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ce.getProperty("ShippedDate").getValue().toString()));
            order.setRequiredDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(ce.getProperty("RequiredDate").getValue().toString()));
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }

        return order;
    }
}
