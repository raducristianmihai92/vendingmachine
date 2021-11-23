package net.mvpmatch.vendingmachine.service;

import net.mvpmatch.vendingmachine.data.entity.Product;
import net.mvpmatch.vendingmachine.data.respository.ProductRepository;
import net.mvpmatch.vendingmachine.web.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    public List<ProductDTO> convertToDto(List<Product> products) {
        return products.stream().map(item -> modelMapper.map(item,ProductDTO.class)).collect(Collectors.toList());
    }

    public ProductDTO convertToDto(Product product) {
        return modelMapper.map(product,ProductDTO.class);
    }

}
