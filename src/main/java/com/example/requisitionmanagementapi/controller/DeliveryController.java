package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.DeliveryDTO;
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
    @PutMapping("/{id}/status")
    public DeliveryDTO updateStatus(@PathVariable Long id,
                                    @RequestParam DeliveryStatus status,
                                    Principal principal) {
        return service.updateStatus(id, status, principal);
    }

    /**
     * Obtenir toutes les livraisons
     */
    @GetMapping
    public List<DeliveryDTO> getAll() {
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
}
