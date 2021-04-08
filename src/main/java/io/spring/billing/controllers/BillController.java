package io.spring.billing.controllers;

import io.spring.billing.entities.Bill;
import io.spring.billing.entities.Client;
import io.spring.billing.entities.Line;
import io.spring.billing.entities.Product;
import io.spring.billing.manager.BillManager;
import io.spring.billing.manager.ClientManager;
import io.spring.billing.manager.ProductManager;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Log
@Controller
@SessionAttributes("bill")
public class BillController {

    @Autowired
    private BillManager billManager;

    @Autowired
    private ClientManager clientManager;

    @Autowired
    private ProductManager productManager;

    @RequestMapping(value = "/bill-details/{id}", method = RequestMethod.GET)
    public String details(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

        Bill bill = billManager.fetchByIdWithClientWithLinesWithProduct(id);
        if (bill == null) {
            flash.addFlashAttribute("error", "Bill not found!!!");
            return "redirect:/clients";
        }

        model.addAttribute("bill", bill);
        model.addAttribute("title", "Bill: ".concat(bill.getDescription()));
        return "bill-details";
    }

    @RequestMapping(value = "/bill-delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

        Bill bill = billManager.get(id);

        if (bill != null) {
            billManager.delete(bill);
            flash.addFlashAttribute("success", "Bill removed with success!");
            return "redirect:/client-details/" + bill.getClient().getId();
        }
        flash.addFlashAttribute("error", "Bill does not exist, it could not be removed!");

        return "redirect:/clients";
    }

    @RequestMapping(value = "/bill-new/{clientId}", method = RequestMethod.GET)
    public String create(@PathVariable(value = "clientId") Long clientId, Map<String, Object> model, RedirectAttributes flash) {

        Client client = clientManager.get(clientId);

        if (client == null) {
            flash.addFlashAttribute("error", "Client not found!!!");
            return "redirect:/clients";
        }

        Bill bill = new Bill();
        bill.setClient(client);

        model.put("bill", bill);
        model.put("title", "New bill");

        return "bill-form";
    }

    @RequestMapping(value = "/bill-new", method = RequestMethod.POST)
    public String save(@Valid Bill bill, BindingResult result, Model model,
                          @RequestParam(name = "item_id[]", required = false) Long[] itemId,
                          @RequestParam(name = "quantity[]", required = false) Integer[] quantity, RedirectAttributes flash,
                          SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Create bill");
            return "bill-form";
        }

        if (itemId == null || itemId.length == 0) {
            model.addAttribute("titulo", "Create bill");
            model.addAttribute("error", "Error: bill must have lines!");
            return "bill-form";
        }

        for (int i = 0; i < itemId.length; i++) {
            Product product = productManager.get(itemId[i]);

            Line line = new Line();
            line.setQuantity(quantity[i]);
            line.setProduct(product);
            bill.addLine(line);

            log.info("ID: " + itemId[i].toString() + ", quantity: " + quantity[i].toString());
        }

        billManager.save(bill);
        status.setComplete();

        flash.addFlashAttribute("success", "Bill created with success!");

        return "redirect:/client-details/" + bill.getClient().getId();
    }

}
