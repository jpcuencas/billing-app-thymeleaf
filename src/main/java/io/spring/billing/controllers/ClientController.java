package io.spring.billing.controllers;

import io.spring.billing.entities.Client;
import io.spring.billing.manager.ClientManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("client")
public class ClientController {

    @Autowired
    private ClientManager clientManager;

    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public String clients(Model model) {

        final List<Client> clients = clientManager.findAll();

        model.addAttribute("title", "Clients");
        model.addAttribute("clients", clients);

        return "clients";
    }

    @RequestMapping(value = "/client-details/{id}", method = RequestMethod.GET)
    public String details(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

        Client client = clientManager.fetchByIdWithBills(id);
        if (client == null) {
            flash.addFlashAttribute("error", "Client not found!!!");
            return "redirect:/clients";
        }

        model.put("title", "Client details");
        model.put("client", client);

        return "client-details";
    }

    @RequestMapping(value = "/client-delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

        Client client = clientManager.get(id);

        if (client != null) {
            clientManager.delete(client);
            flash.addFlashAttribute("success", "Client removed with success!");
            return "redirect:/clients";
        }
        flash.addFlashAttribute("error", "Client does not exist, it could not be removed!");

        return "redirect:/clients";
    }

    @RequestMapping(value = "/client-form", method = RequestMethod.GET)
    public String create(Map<String, Object> model) {
        Client client = new Client();
        model.put("client", client);
        model.put("title", "Create client");
        return "client-form";
    }

    @RequestMapping(value = "/client-new", method = RequestMethod.POST)
    public String save(@Valid Client client, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Create client");
            return "client-form";
        }

        clientManager.save(client);
        status.setComplete();
        flash.addFlashAttribute("success", "Client create with success!!!");
        return "redirect:clients";
    }

}
