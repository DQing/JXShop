package com.thoughtworks.cn.JXShop.controller;

import com.thoughtworks.cn.JXShop.entity.*;
import com.thoughtworks.cn.JXShop.repository.*;
import com.thoughtworks.cn.JXShop.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PurchaseItemRepository purchaseItemRepository;

    @Autowired
    private LogisticsRecordRepository logisticsRecordRepository;

    @Autowired
    private LogisticsRecordController logisticsRecordController;

    public OrderController() {
    }

    @PostMapping(value = "")
    public ResponseEntity createOrder(@RequestBody List<HashMap> orderInfoArray) throws Exception {
        Order order = new Order();
        orderRepository.save(order);
        List<PurchaseItem> purchaseItemList = new ArrayList<>();

        for (HashMap orderInfo : orderInfoArray) {
            Integer productId = (Integer) orderInfo.get("productId");
            Integer purchaseCount = (Integer) orderInfo.get("purchaseCount");

            Optional<Product> productOptional = productRepository.findById(Long.valueOf(productId));
            Inventory inventory = productOptional.get().getInventory();

            if (!productOptional.isPresent() || inventory.getLockedCount() < purchaseCount) {
                 return ResponseEntity.notFound().build();
            }

            inventory.setLockedCount(inventory.getLockedCount() - purchaseCount);
            inventoryRepository.save(inventory);
            purchaseItemList.add(createPurchaseItem(productId, purchaseCount, order.getId()));
        }

        int totalPrice = getTotalPrice(purchaseItemList);

        order.setTotalPrice(totalPrice);
        order.setUserId(1);
        order.setStatus(OrderStatus.unPaid);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public PurchaseItem createPurchaseItem(Integer productId, Integer purchaseCount, Long orderId) {
        Product product = productRepository.findOne(Long.valueOf(productId));
        return purchaseItemRepository.save(new PurchaseItem(product.getId(), orderId, product.getName(), product.getDescription(), product.getPrice(), purchaseCount));
    }

    public int getTotalPrice(List<PurchaseItem> purchaseItemList) {
        return purchaseItemList.stream().map(purchaseItem -> purchaseItem.getPurchaseCount() * purchaseItem.getPurchasePrice()).reduce(0, (a, b) -> a + b);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus orderStatus) throws Exception {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();

        if (orderStatus == OrderStatus.paid) {
            return payForOrder(order);
        }

        if (orderStatus == OrderStatus.withdrawn) {
            return withdrawOrder(order);
        }

        if (orderStatus == OrderStatus.finished) {
            return finishOrder(order);
        }

        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity finishOrder(Order order) {
        if(order.getStatus() != OrderStatus.paid){
            return ResponseEntity.badRequest().build();
        }

        order.setStatus(OrderStatus.finished);
        order.setFinishTime(new Timestamp(System.currentTimeMillis()));

        for(PurchaseItem purchaseItem :order.getPurchaseItemList()){
            Long productId = purchaseItem.getProductId();
            int purchaseCount = purchaseItem.getPurchaseCount();
            
            Optional<Product> productOptional = productRepository.findById(productId);
            if(!productOptional.isPresent()){
                return ResponseEntity.badRequest().build();
            }

            Inventory inventory = productOptional.get().getInventory();
            inventory.setCount(inventory.getCount() - purchaseCount);
            inventoryRepository.save(inventory);
        }

        orderRepository.save(order);

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity withdrawOrder(Order order) {
        if(order.getStatus() != OrderStatus.unPaid){
            return ResponseEntity.badRequest().build();
        }

        order.setStatus(OrderStatus.withdrawn);
        order.setWithdrawnTime(new Timestamp(System.currentTimeMillis()));

        List<PurchaseItem> purchaseItems = purchaseItemRepository.findByOrderId(order.getId());
        for (PurchaseItem purchaseItem:purchaseItems){
            Inventory inventory = productRepository.findById(purchaseItem.getProductId()).get().getInventory();
            inventory.setLockedCount(inventory.getLockedCount() + purchaseItem.getPurchaseCount());

            inventoryRepository.save(inventory);
        }

        orderRepository.save(order);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity payForOrder(Order order) {
        if(order.getStatus() != OrderStatus.unPaid){
            return ResponseEntity.badRequest().build();
        }

        order.setStatus(OrderStatus.paid);
        order.setPaidTime(new Timestamp(System.currentTimeMillis()));
        LogisticsRecord logisticsRecord = logisticsRecordController.createLogisticsRecord();
        order.setLogisticsRecord(logisticsRecord);
        orderRepository.save(order);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "")
    public ResponseEntity findOrdersByUserId(@RequestParam(name = "userId") int userId) throws Exception {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity findAllInfoByOrderId(@PathVariable Long id) throws Exception {
        Optional<Order> orderOptional = orderRepository.findById(id);
        return orderOptional.<ResponseEntity>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
