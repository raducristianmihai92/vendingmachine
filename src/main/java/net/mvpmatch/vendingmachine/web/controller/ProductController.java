package net.mvpmatch.vendingmachine.web.controller;

import lombok.RequiredArgsConstructor;
import net.mvpmatch.vendingmachine.data.entity.Product;
import net.mvpmatch.vendingmachine.data.entity.User;
import net.mvpmatch.vendingmachine.service.ProductService;
import net.mvpmatch.vendingmachine.service.UserService;
import net.mvpmatch.vendingmachine.web.dto.BuyDTO;
import net.mvpmatch.vendingmachine.web.dto.BuyResponseDTO;
import net.mvpmatch.vendingmachine.web.dto.ProductDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping(value = "/products", produces = "application/json")
    public List<ProductDTO> getAllProducts() {
        return productService.convertToDto(productService.findAll());
    }

    @GetMapping(value = "/products/id/{id}", produces = "application/json")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Optional<Product> product =  productService.findById(id);
        if(product.isPresent()){
            return new ResponseEntity<ProductDTO>(productService.convertToDto(product.get()), HttpStatus.OK);
        }

        return new ResponseEntity<String>("Product not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/products", consumes = "application/json")
    @PreAuthorize("hasAuthority('SELLER') and (@productController.canCreateOrUpdateProduct(principal, #product))")
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO product) {

        String message = "";
        if (StringUtils.isEmpty(product.getProductName())  || product.getCost() == null
            || product.getSeller() == null) {
            message = "The product name,  cost or seller is missing !";
            return new ResponseEntity<String>(message, HttpStatus.PARTIAL_CONTENT);
        }

        Product newProduct = new Product();
        newProduct.setProductName(product.getProductName());
        newProduct.setCost(product.getCost());
        newProduct.setAmountAvailable(product.getAmountAvailable());

        Optional<User> seller = userService.findById(product.getSeller().getId());

        if(seller.isPresent()) {
            newProduct.setSeller(seller.get());
        } else {
            message = "The seller user could not be found !";
            return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
        }

        try {
            productService.save(newProduct);
        } catch(Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        message = "Product was created with success!";
        return new ResponseEntity<String>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/products", produces = "application/json", consumes = "application/json")
    @PreAuthorize("hasAuthority('SELLER') and (@productController.canCreateOrUpdateProduct(principal, #product))")
    public ResponseEntity<?> update(@RequestBody ProductDTO product) {

        String errorMessage = "";
        if (product.getId() == null) {
            errorMessage = "The product can not be updated because id is missing !";
            return new ResponseEntity<String>(errorMessage, HttpStatus.PARTIAL_CONTENT);
        }

        Optional<Product> productFound = productService.findById(product.getId());
        if (!productFound.isPresent()) {
            errorMessage = "The product is not found! ";
            return new ResponseEntity<String>(errorMessage, HttpStatus.NOT_FOUND);
        }

        productFound.get().setId(product.getId());
        if (product.getProductName() != null) {
            productFound.get().setProductName(product.getProductName());
        }

        if (product.getCost() != null) {
            productFound.get().setCost(product.getCost());
        }

        if (product.getAmountAvailable() != null) {
            productFound.get().setAmountAvailable(product.getAmountAvailable());
        }

        if(product.getSeller() != null) {
            Optional<User> seller = userService.findById(product.getSeller().getId());

            if(seller.isPresent()) {
                product.setSeller(userService.convertToDto(seller.get()));
            } else {
                errorMessage = "The seller user could not be found !";
                return new ResponseEntity<String>(errorMessage, HttpStatus.NOT_FOUND);
            }
        }

        productService.save(productFound.get());

        Optional<Product> productUpdated = productService.findById(product.getId());

        if (productUpdated.isPresent()) {
            return new ResponseEntity<>(productService.convertToDto(productUpdated.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping(value = "/products/id/{id}")
    @PreAuthorize("hasAuthority('SELLER') and (@productController.canDeleteProduct(principal, #id))")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {

        String message = "";
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            productService.deleteById(id);

            message = "The specified product was deleted with success !";
            return new ResponseEntity<String>(message, HttpStatus.OK);
        }

        message = "The specified product was not deleted. Reason: could not be found.";
        return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/buy")
    @PreAuthorize("hasAuthority('BUYER')")
    public ResponseEntity<?> buy(@RequestBody BuyDTO buyDTO, Principal principal) {

        String message = "";
        Optional<Product> foundProduct = productService.findById(buyDTO.getProductId());
        if (!foundProduct.isPresent()) {
            message = "The specified product could not be found.";
            return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
        }

        Product product = foundProduct.get();

        if (product.getAmountAvailable() < buyDTO.getAmountOfProducts() ){
            message = "The amount of requested products is not available !";
            return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
        }

        Integer totalSpent = buyDTO.getAmountOfProducts() * product.getCost();

        Optional<User> foundUser = userService.findByUserName(principal.getName());
        User loggedUser = foundUser.get();

        if(totalSpent > loggedUser.getDeposit()){
            message = "There are not enough money into your deposit !";
            return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
        }

        product.setAmountAvailable( product.getAmountAvailable() - buyDTO.getAmountOfProducts());
        loggedUser.setDeposit(loggedUser.getDeposit() - totalSpent);

        BuyResponseDTO buyResponseDTO = new BuyResponseDTO();
        buyResponseDTO.setTotalSpent(totalSpent);
        buyResponseDTO.setDeposit(loggedUser.getDeposit());
        buyResponseDTO.setProductName(product.getProductName());
        buyResponseDTO.setProductQuantity(buyDTO.getAmountOfProducts());

        return new ResponseEntity<BuyResponseDTO>(buyResponseDTO, HttpStatus.OK);
    }

    public boolean canCreateOrUpdateProduct(String name, ProductDTO product) {
        Optional<User> user = userService.findByUserName(name);
        if (!user.isPresent()) {
            return false;
        }

        if (product != null && !product.getSeller().getId().equals(user.get().getId())) {
            return false;
        }

        return true;
    }

    public boolean canDeleteProduct(String name, Integer productId) {
        Optional<User> user = userService.findByUserName(name);
        if (!user.isPresent()) {
            return false;
        }

        Optional<Product> dbProduct = productService.findById(productId);

        if(dbProduct.isPresent() && !dbProduct.get().getSeller().getId().equals(user.get().getId())) {
            return false;
        }

        return true;
    }

}
