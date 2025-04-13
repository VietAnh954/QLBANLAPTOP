package vn.hoidanit.laptopshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    // @Autowired
    // private OrderService orderService;
    // @Autowired
    // private ProductService productService;
    // @Autowired
    // private UserService userService;
    // @Autowired
    // private OrderDetailService orderDetailService;
    @GetMapping("/admin")
    public String getDashboard() {
        return "admin/dashboard/show";
    }

}