package io.spring.billing.controllers;

import io.spring.billing.entities.Product;
import io.spring.billing.manager.ProductManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductManager productManager;

    @GetMapping(value = "/charge-products/{term}", produces = { "application/json" })
    public @ResponseBody
    List<Product> chargeProducts(@PathVariable String term) {
        return productManager.findByName(term);
    }

}
