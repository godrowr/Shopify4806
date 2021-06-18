package myapp;
import myapp.product.Product;
import myapp.shop.Shop;
import myapp.shop.ShopRepository;
import myapp.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class FrontController {
    @Autowired
    private ShopRepository shopRepository;

    @RequestMapping("")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("login");
    }

    @GetMapping("login")
    public String login(Model model){
        return "login";
    }

    @GetMapping("browseShops")
    public String shops(Model model){
        return "shops";
    }

    @GetMapping("newshop")
    public String newShopForm(Model model){
        model.addAttribute("newShop", new Shop());
        return "newShopForm";
    }

    @GetMapping("newproduct")
    public String newProductForm(Model model){
        model.addAttribute("newProduct", new Product());
        return "newProductForm";
    }

    @GetMapping("error-403")
    public String error(Model model){
        return "error-403";
    }

    @GetMapping("viewShop")
    public String viewShop(@RequestParam(name="shopId") Long shopId, Model model) {
        Optional<Shop> shop = shopRepository.findById(shopId);
        if (shop.isPresent()) {
            model.addAttribute("shopId", shopId);
            model.addAttribute("shop", shop.get());
            return "viewShop";
        } else {
            return "error-001";
        }
    }

    @GetMapping("shoppingcart")
    public String shoppingcart(Model model){
        return "shoppingcart";
    }
}
