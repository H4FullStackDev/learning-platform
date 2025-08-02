package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.DeliveryCount;
import com.example.requisitionmanagementapi.dto.DeliveryDTO;
import com.example.requisitionmanagementapi.dto.DeliveryResponse;
import com.example.requisitionmanagementapi.dto.RequisitionStatusCountDTO;
import com.example.requisitionmanagementapi.entity.Delivery;
import com.example.requisitionmanagementapi.enums.DeliveryStatus;
import com.example.requisitionmanagementapi.service.DeliveryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService service;

    /**
     * Créer une livraison pour une réquisition validée
     */
    @PostMapping("/requisition/{requisitionId}")
    public DeliveryDTO createDelivery(@PathVariable Long requisitionId,
                                      @RequestBody DeliveryDTO dto,
                                      Principal principal) {
        return service.createDelivery(requisitionId, dto, principal);
    }

    /**
     * Mettre à jour le statut d'une livraison (PREPARATION, IN_TRANSIT, RECEIVED)
     */
    @PutMapping("/{id}/validate")
    public DeliveryDTO validateDelivery(@PathVariable Long id,
                                    Principal principal) {
        return service.validateDelivery(id, principal);
    }

    @PutMapping("/{id}/transit")
    public DeliveryDTO transitDelivery(@PathVariable Long id,
                                    Principal principal) {
        return service.transitDelivery(id, principal);
    }

    /**
     * Obtenir toutes les livraisons
     */
    @GetMapping
    public List<DeliveryResponse> getAll() {
        return service.getAll();
    }

    /**
     * Obtenir les livraisons liées à une réquisition spécifique
     */
    @GetMapping("/requisition/{requisitionId}")
    public List<DeliveryDTO> getByRequisition(@PathVariable Long requisitionId) {
        return service.getByRequisition(requisitionId);
    }

    @GetMapping("/{id}")
    public DeliveryDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
    

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/status-counts")
    public DeliveryCount getRequisitionStatusCounts() {
        return service.getDeliveryStatusCounts();
    }
}
