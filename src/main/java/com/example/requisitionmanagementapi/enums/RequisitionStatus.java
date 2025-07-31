package com.example.requisitionmanagementapi.enums;

public enum RequisitionStatus {
    DRAFT,             // Brouillon
    SUBMITTED,         // Envoyée pour validation
    VALIDATED,         // Validée par OPJ ou commissaire
    REJECTED,          // Rejetée
    IN_PROCESS,        // Traitée par la DAL
    DELIVERED,         // Livraison complète effectuée
    CANCELLED          // Annulée
}
