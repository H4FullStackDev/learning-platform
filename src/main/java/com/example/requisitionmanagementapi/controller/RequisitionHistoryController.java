package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.RequisitionHistoryDTO;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.service.RequisitionHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/requisition-histories")
public class RequisitionHistoryController {

    private final RequisitionHistoryService service;

    @GetMapping
    public List<RequisitionHistoryDTO> getAll() {
        return service.getAll();
    }

    /**
     * Récupérer l'historique complet d'une réquisition
     */
    @GetMapping("/{requisitionId}")
    public List<RequisitionHistoryDTO> getByRequisition(@PathVariable Long requisitionId) {
        return service.getByRequisition(requisitionId);
    }

    /**
     * Ajouter un historique manuellement (action spécifique ou test)
     */
    @PostMapping("/{requisitionId}/add")
    public RequisitionHistoryDTO addManualHistory(@PathVariable Long requisitionId,
                                                  @RequestParam RequisitionStatus from,
                                                  @RequestParam RequisitionStatus to,
                                                  @RequestParam(required = false) String comment,
                                                  Principal principal) {
        return service.addManualHistory(requisitionId, from, to, comment, principal);
    }
}
