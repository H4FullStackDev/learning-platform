package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.RequisitionArticleDAO;
import com.example.requisitionmanagementapi.dao.RequisitionDAO;
import com.example.requisitionmanagementapi.dao.RequisitionHistoryDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.RequisitionDTO;
import com.example.requisitionmanagementapi.dto.RequisitionStatusCountDTO;
import com.example.requisitionmanagementapi.dto.RequisitionStockRecapDTO;
import com.example.requisitionmanagementapi.entity.*;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.event.RequisitionEvent;
import com.example.requisitionmanagementapi.mapper.RequisitionMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class RequisitionService {

    private final RequisitionMapper mapper;
    private final UserDAO userDAO;
    private final RequisitionDAO dao;
    private final RequisitionArticleDAO requisitionArticleDAO;
    private final RequisitionHistoryDAO requisitionHistoryDAO;
    private final ApplicationEventPublisher events;



    public RequisitionDTO save(RequisitionDTO dto) {
        Requisition entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }

    /**
     * Créer une nouvelle réquisition à l'état BROUILLON
     */
    public RequisitionDTO createRequisition(RequisitionDTO dto, Principal principal) {
        Requisition entity = mapper.toEntity(dto);
        if (dto.getId()!=null) {
            Requisition requisition = dao.findById(dto.getId()).get();
            entity.setStatus(requisition.getStatus());
        }else{
            entity.setStatus(RequisitionStatus.DRAFT);
        }
        entity.setCreatedAt(LocalDateTime.now());
        String username = principal.getName();

        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        entity.setCreatedBy(user);

        // Lien bidirectionnel requisition <-> articles
        entity.getArticles().forEach(a -> a.setRequisition(entity));

        return mapper.toDTO(dao.save(entity));
    }

    public RequisitionDTO draftRequisition(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        String username = principal.getName();
        User validator = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.DRAFT,"Brouiller", "Réquisition mis en brouillons", principal);
        requisition.setValidatedBy(validator);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.DRAFT);
        dao.save(requisition);

        return mapper.toDTO(requisition);
    }

        /**
         * Soumettre une réquisition pour validation
         */
    public RequisitionDTO submitRequisition(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        if (requisition.getStatus() != RequisitionStatus.DRAFT && requisition.getStatus() != RequisitionStatus.REJECTED) {
            throw new IllegalStateException("Seules les réquisitions en brouillon ou rejetées peuvent être soumises");
        }
        addHistory(requisition, requisition.getStatus(), RequisitionStatus.SUBMITTED,"Soumis", "Soumission de la réquisition", principal);
        requisition.setStatus(RequisitionStatus.SUBMITTED);
        dao.save(requisition);
        return mapper.toDTO(requisition);
    }

    /**
     * Valider une réquisition (OPJ ou commissaire)
     */
    public RequisitionDTO validateRequisition(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        if (requisition.getStatus() != RequisitionStatus.SUBMITTED) {
            throw new IllegalStateException("Seules les réquisitions soumises peuvent être validées");
        }
        String username = principal.getName();
        User validator = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.VALIDATED,"Valider", "Réquisition validée", principal);
        requisition.setValidatedBy(validator);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.VALIDATED);
        dao.save(requisition);
        return mapper.toDTO(requisition);
    }

    /**
     * Rejeter une réquisition
     */
    @Transactional
    public RequisitionDTO rejectRequisition(Long id,  String comment, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        if (requisition.getStatus() != RequisitionStatus.SUBMITTED) {
            throw new IllegalStateException("Seules les réquisitions soumises peuvent être rejetées");
        }
        String username = principal.getName();
        User validator = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.REJECTED,  "Rejeter", comment , principal);
        requisition.setValidatedBy(validator);
        requisition.setComment(comment);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.REJECTED);
        dao.save(requisition);
        var actorId = validator.getId();
        var recipients = java.util.List.of(requisition.getCreatedBy().getId()); // ajoute d’autres destinataires si besoin
        var meta = java.util.Map.<String,Object>of("reason", comment);

        events.publishEvent(new RequisitionEvent(
                this, requisition.getTitle(), RequisitionEvent.Type.CANCELED, actorId, recipients, meta
        ));
        return mapper.toDTO(requisition);


    }


    public void startProcessing(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));
        addHistory(requisition, RequisitionStatus.VALIDATED, RequisitionStatus.IN_PROCESS, "En cours", "Planification de la livraison", principal);
        requisition.setStatus(RequisitionStatus.IN_PROCESS);
        mapper.toDTO(requisition);
    }

    public RequisitionDTO cancelRequisition(Long id, Principal principal, String comment) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));
        if (requisition.getStatus() == RequisitionStatus.IN_PROCESS ||
                requisition.getStatus() == RequisitionStatus.DELIVERED ||
                requisition.getStatus() == RequisitionStatus.CANCELLED) {
            throw new IllegalStateException("Cette réquisition ne peut plus être annulée.");
        }


        addHistory(requisition, requisition.getStatus(), RequisitionStatus.CANCELLED, "Annuler", comment, principal);
        requisition.setStatus(RequisitionStatus.CANCELLED);
        requisition.setComment(comment);
        dao.save(requisition);
        return mapper.toDTO(requisition);
    }

    public void deliveryRequisition(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));
        if (requisition.getStatus() != RequisitionStatus.IN_PROCESS ) {
            throw new IllegalStateException("Cette réquisition ne peut plus être valider.");
        }
        addHistory(requisition, requisition.getStatus(), RequisitionStatus.DELIVERED, "Livrer", "Réquisition Livré", principal);
        requisition.setStatus(RequisitionStatus.DELIVERED);
        dao.save(requisition);

        mapper.toDTO(requisition);
    }



    /**
     * Récupérer toutes les réquisitions (filtrable plus tard)
     */
    @Transactional(readOnly = true)
    public List<RequisitionDTO> getAll(Principal principal) {
        Optional<User> currentUser = userDAO.findByUsername(principal.getName());
        Set<Department> userDepartments = currentUser.get().getDepartments();
        List<Requisition> requisitions;
        if (userDepartments == null || userDepartments.isEmpty()) {
            requisitions = dao.findAll();
        } else {
            requisitions = dao.findByCreatedBy_DepartmentsIn(userDepartments);
        }

        return mapper.toDTOList(requisitions);
    }



    /**
     * Récupérer les détails d’une réquisition par son ID
     */
    @Transactional(readOnly = true)
    public RequisitionDTO getById(Long id) {
        return mapper.toDTO(
                dao.findById(id)
                        .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"))
        );
    }
    public RequisitionStatusCountDTO getRequisitionStatusCounts(Principal principal) {
        User user = getCurrentUser(principal);
        Set<Department> userDepartments = user.getDepartments();
        if (user.getDepartments() == null || user.getDepartments().isEmpty()) {
        return new RequisitionStatusCountDTO(
                dao.countByStatus(RequisitionStatus.DRAFT),
                dao.countByStatus(RequisitionStatus.SUBMITTED),
                dao.countByStatus(RequisitionStatus.VALIDATED),
                dao.countByStatus(RequisitionStatus.REJECTED),
                dao.countByStatus(RequisitionStatus.IN_PROCESS),
                dao.countByStatus(RequisitionStatus.DELIVERED),
                dao.countByStatus(RequisitionStatus.CANCELLED)
        );

    } else {
        return new RequisitionStatusCountDTO(
                dao.countByDepartmentAndStatus(userDepartments, RequisitionStatus.DRAFT),
                dao.countByDepartmentAndStatus(userDepartments, RequisitionStatus.SUBMITTED),
                dao.countByDepartmentAndStatus(userDepartments, RequisitionStatus.VALIDATED),
                dao.countByDepartmentAndStatus(userDepartments, RequisitionStatus.REJECTED),
                dao.countByDepartmentAndStatus(userDepartments, RequisitionStatus.IN_PROCESS),
                dao.countByDepartmentAndStatus(userDepartments, RequisitionStatus.DELIVERED),
                dao.countByDepartmentAndStatus(userDepartments, RequisitionStatus.CANCELLED)
        );
    }
    }

    private User getCurrentUser(Principal principal) {
    return userDAO.findByUsername(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
    }

    public List<RequisitionStockRecapDTO> getStockRecapForRequisition(Long requisitionId) {
        Requisition requisition = dao.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Réquisition introuvable"));

        List<RequisitionArticle> requisitionArticles = requisitionArticleDAO.findByRequisition(requisition);
        List<RequisitionStockRecapDTO> recapList = new ArrayList<>();

        for (RequisitionArticle reqArt : requisitionArticles) {
            Article article = reqArt.getArticle();
            int stock = article.getStockQuantity();
            int quantity = reqArt.getQuantity();
            int remaining = stock - quantity;
            RequisitionStockRecapDTO recap = new RequisitionStockRecapDTO();
            recap.setArticleId(article.getId());
            recap.setArticleName(article.getName());
            recap.setCurrentStock(stock);
            recap.setQuantityDemanded(quantity);
            recap.setRemainingStock(remaining);

            recapList.add(recap);
        }

        return recapList;
    }


    /**
     * Ajouter un enregistrement d'historique à la réquisition
     */
    public void addHistory(Requisition requisition, RequisitionStatus from, RequisitionStatus to,String action, String comment, Principal principal) {
        RequisitionHistory history = new RequisitionHistory();
        history.setRequisition(requisition);
        history.setStatusBefore(from);
        history.setStatusAfter(to);
        history.setComment(comment);
        history.setActionDate(LocalDateTime.now());
        String username = principal.getName();
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        history.setActionBy(user);
        history.setAction(action);
        requisition.getHistories().add(history);
        requisitionHistoryDAO.save(history);
    }
}

