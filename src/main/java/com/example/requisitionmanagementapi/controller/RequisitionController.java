package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.RequisitionDTO;
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

    @PutMapping("/{id}/start")
    public RequisitionDTO cancel(@PathVariable Long id,  Principal principal) {
        return service.startProcessing(id, principal);
    }

    @PutMapping("/{id}/cancel")
    public RequisitionDTO cancel(@PathVariable Long id, @RequestParam(required = false) String comment, Principal principal) {
        return service.cancelRequisition(id, principal, comment);
    }

    /**
     * Récupérer toutes les réquisitions
     */
    @GetMapping
    public List<RequisitionDTO> getAll() {
        return service.getAll();
    }

    /**
     * Récupérer une réquisition par son ID
     */
    @GetMapping("/{id}")
    public RequisitionDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
