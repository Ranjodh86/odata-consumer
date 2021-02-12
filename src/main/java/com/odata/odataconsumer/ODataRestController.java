package com.odata.odataconsumer;

import com.odata.odataconsumer.model.Customer;
import com.odata.odataconsumer.model.Order;
import com.odata.odataconsumer.service.ODataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ODataRestController {

    private static Logger logger = LoggerFactory.getLogger(ODataRestController.class);

    @Autowired
    ODataService oDataService;

    @GetMapping("/customers")
    private List<Customer> getCustomers() {

        logger.info("get getCustomers");
        return oDataService.getCustomers();

    }

    @GetMapping("/customersStartsWith/{prefix}")
    private List<Customer> getCustomersWithPrefix(@PathVariable String prefix) {

        logger.info("get getCustomers");
        return oDataService.getCustomersWithPrefix(prefix);

    }

    @GetMapping("/customer/{customerId}")
    private Customer getCustomer(@PathVariable String customerId) {

        logger.info("get specific customer");
        return oDataService.getCustomer(customerId);

    }

    @GetMapping("/customerOrders/{customerId}")
    private List<Order> getCustomerOrder(@PathVariable String customerId) {

        logger.info("get specific customer's orders");
        return oDataService.getCustomerOrders(customerId);

    }
}
