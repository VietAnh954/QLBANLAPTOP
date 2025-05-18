package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository,
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository,
            UserService userService) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
    }

    // Tìm tất cả sản phẩm
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    // Tìm sản phẩm theo id
    public Optional<Product> fetchProductById(long id) {
        return this.productRepository.findById(id);
    }

    // Lưu sản phẩm
    public Product saveProduct(Product product) {
        return this.productRepository.save(product);
    }

    // Xóa sản phẩm theo id
    public void deleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session) {

        Optional<User> user = this.userService.getUserByEmail(email);
        if (user != null) {
            // Check xem người dùng đã có giỏ hàng chưa? neeus chuaw -> tạo mới
            Cart cart = this.cartRepository.findByUser(user.get());

            if (cart == null) {
                // Tạo mới giỏ hàng
                Cart otherCart = new Cart();
                otherCart.setUser(user.get());
                otherCart.setSum(0);

                cart = this.cartRepository.save(otherCart);
            }
            // Nếu có giỏ hàng rồi thì thêm sản phẩm vào giỏ hàng

            Optional<Product> productOptional = this.productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product realProduct = productOptional.get();

                // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
                CartDetail oldDetail = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);

                if (oldDetail == null) {
                    CartDetail cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setProduct(realProduct);
                    cartDetail.setPrice(realProduct.getPrice());
                    cartDetail.setQuantity(1);
                    this.cartDetailRepository.save(cartDetail);

                    // update cart(sum)
                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                } else {
                    oldDetail.setQuantity(oldDetail.getQuantity() + 1);
                    this.cartDetailRepository.save(oldDetail);
                }

            }
        }
    }
}
