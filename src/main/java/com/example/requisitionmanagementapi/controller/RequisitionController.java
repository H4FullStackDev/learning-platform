package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.RequisitionDTO;
import com.example.requisitionmanagementapi.dto.RequisitionStatusCountDTO;
import com.example.requisitionmanagementapi.dto.RequisitionStockRecapDTO;
import com.example.requisitionmanagementapi.dto.UserDTO;
import com.example.requisitionmanagementapi.service.RequisitionService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/requisitions")
public class RequisitionController {
    private final RequisitionService service;

    public RequisitionController(RequisitionService service) {
        this.service = service;
    }

    /**
     * Créer une nouvelle réquisition (état : DRAFT)
     */
    @PostMapping
    public RequisitionDTO create(@RequestBody RequisitionDTO dto, Principal principal) {
        return service.createRequisition(dto, principal);
    }

    /**
     * Soumettre une réquisition pour validation
     */
    @PutMapping("/{id}/submit")
    public RequisitionDTO submit(@PathVariable Long id, Principal principal) {
        return service.submitRequisition(id, principal);
    }

    @PutMapping("/{id}/draft")
    public RequisitionDTO draft(@PathVariable Long id, Principal principal) {
        return service.draftRequisition(id, principal);
    }

    /**
     * Valider une réquisition (OPJ ou Commissaire)
     */
    @PutMapping("/{id}/validate")
    public RequisitionDTO validate(@PathVariable Long id, Principal principal) {
        return service.validateRequisition(id, principal);
    }

    /**
     * Rejeter une réquisition (avec commentaire)
     */
    @PutMapping("/{id}/reject")
    public RequisitionDTO reject(@PathVariable Long id, @RequestParam String comment, Principal principal) {
        return service.rejectRequisition(id, comment, principal);
    }

    @PutMapping("/{id}/cancel")
    public RequisitionDTO cancel(@PathVariable Long id, @RequestParam(required = true) String comment, Principal principal) {
        return service.cancelRequisition(id, principal, comment);
    }

    /**
     * Récupérer toutes les réquisitions
     */
    @GetMapping
    public List<RequisitionDTO> getAll(Principal principal) {
        return service.getAll(principal);
    }

    @GetMapping("/status-counts")
    public RequisitionStatusCountDTO getRequisitionStatusCounts(Principal principal) {
        return service.getRequisitionStatusCounts(principal);
    }

    @GetMapping("/{id}/delivery-recap")
    public List<RequisitionStockRecapDTO> getDeliveryRecap(@PathVariable Long id) {
        return service.getStockRecapForRequisition(id);
    }


    /**
     * Récupérer une réquisition par son ID
     */
    @GetMapping("/{id}")
    public RequisitionDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
