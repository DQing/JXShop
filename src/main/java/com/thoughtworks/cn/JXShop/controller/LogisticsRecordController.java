package com.thoughtworks.cn.JXShop.controller;

import com.thoughtworks.cn.JXShop.entity.LogisticsRecord;
import com.thoughtworks.cn.JXShop.entity.Order;
import com.thoughtworks.cn.JXShop.repository.InventoryRepository;
import com.thoughtworks.cn.JXShop.repository.LogisticsRecordRepository;
import com.thoughtworks.cn.JXShop.repository.OrderRepository;
import com.thoughtworks.cn.JXShop.utils.LogisticsStatus;
import com.thoughtworks.cn.JXShop.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping("/logisticsRecords")
public class LogisticsRecordController {

    @Autowired
    private LogisticsRecordRepository logisticsRecordRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderController orderController;

    public LogisticsRecordController() {
    }

    @GetMapping(value = "/{orderId}")
    public ResponseEntity getRecordRepository(@PathVariable(name = "orderId") Long orderId) throws Exception {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();
        if (order.getStatus() == OrderStatus.unPaid || order.getStatus() == OrderStatus.withdrawn) {
            return ResponseEntity.notFound().build();
        }

        LogisticsRecord logisticsRecord = order.getLogisticsRecord();
        return ResponseEntity.ok(logisticsRecord);
    }

    @PutMapping(value = "/{id}/orders/{orderId}")
    public ResponseEntity updateLogisticsRecordStatus(@PathVariable("id") Long logisticsRecordId, @PathVariable("orderId") Long orderId, @RequestParam LogisticsStatus logisticsStatus) throws Exception {
        Optional<LogisticsRecord> logisticsRecordOptional = logisticsRecordRepository.findById(logisticsRecordId);
        if (!logisticsRecordOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        LogisticsRecord logisticsRecord = logisticsRecordOptional.get();

        if (logisticsStatus == LogisticsStatus.shipping) {
            return shipLogistics(logisticsRecord);
        }

        if (logisticsStatus == LogisticsStatus.signed) {
            return signLogisticsRecord(logisticsRecord,orderId);
        }

        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity signLogisticsRecord(LogisticsRecord logisticsRecord,Long orderId) {
        if (logisticsRecord.getLogisticsStatus() != LogisticsStatus.shipping) {
            return ResponseEntity.badRequest().build();
        }

        logisticsRecord.setLogisticsStatus(LogisticsStatus.signed);
        logisticsRecord.setSignedTime(new Timestamp(System.currentTimeMillis()));
        logisticsRecordRepository.save(logisticsRecord);
        orderController.finishOrder(orderRepository.findById(orderId).get());

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity shipLogistics(LogisticsRecord logisticsRecord) {
        if (logisticsRecord.getLogisticsStatus() != LogisticsStatus.readyToShip) {
            return ResponseEntity.badRequest().build();
        }

        logisticsRecord.setLogisticsStatus(LogisticsStatus.shipping);
        logisticsRecord.setOutboundTime(new Timestamp(System.currentTimeMillis()));
        logisticsRecordRepository.save(logisticsRecord);

        return ResponseEntity.noContent().build();
    }

    public LogisticsRecord createLogisticsRecord() {
        LogisticsRecord logisticsRecord = new LogisticsRecord();
        logisticsRecord.setLogisticsStatus(LogisticsStatus.readyToShip);
        logisticsRecord.setDeliveryMan("李师傅");

        return logisticsRecordRepository.save(logisticsRecord);
    }
}
