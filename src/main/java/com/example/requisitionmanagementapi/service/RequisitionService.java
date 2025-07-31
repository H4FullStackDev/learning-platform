package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dao.RequisitionDAO;
import com.example.requisitionmanagementapi.dao.RequisitionHistoryDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.RequisitionDTO;
import com.example.requisitionmanagementapi.entity.*;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.mapper.RequisitionMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class RequisitionService {

    private final RequisitionDAO dao;
    private final RequisitionMapper mapper;
    private final UserDAO userDAO;
    private final RequisitionDAO requisitionDAO;
    private final RequisitionHistoryDAO requisitionHistoryDAO;



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
        entity.setStatus(RequisitionStatus.DRAFT);
        entity.setCreatedAt(LocalDateTime.now());
        String username = principal.getName();

        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        entity.setCreatedBy(user);

        // Lien bidirectionnel requisition <-> articles
        entity.getArticles().forEach(a -> a.setRequisition(entity));

        return mapper.toDTO(requisitionDAO.save(entity));
    }

    /**
     * Soumettre une réquisition pour validation
     */
    public RequisitionDTO submitRequisition(Long id, Principal principal) {
        Requisition requisition = requisitionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        if (requisition.getStatus() != RequisitionStatus.DRAFT) {
            throw new IllegalStateException("Seules les réquisitions en brouillon peuvent être soumises");
        }

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.SUBMITTED, "Soumission de la réquisition", principal);
        requisition.setStatus(RequisitionStatus.SUBMITTED);

        return mapper.toDTO(requisition);
    }

    /**
     * Valider une réquisition (OPJ ou commissaire)
     */
    public RequisitionDTO validateRequisition(Long id, Principal principal) {
        Requisition requisition = requisitionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        if (requisition.getStatus() != RequisitionStatus.SUBMITTED) {
            throw new IllegalStateException("Seules les réquisitions soumises peuvent être validées");
        }

        String username = principal.getName();
        User validator = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        // Décrémenter les stocks
        addHistory(requisition, requisition.getStatus(), RequisitionStatus.VALIDATED, "Réquisition validée", principal);

        requisition.setValidatedBy(validator);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.VALIDATED);

        return mapper.toDTO(requisition);
    }

    /**
     * Rejeter une réquisition
     */
    public RequisitionDTO rejectRequisition(Long id,  String comment, Principal principal) {
        Requisition requisition = requisitionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        if (requisition.getStatus() != RequisitionStatus.SUBMITTED) {
            throw new IllegalStateException("Seules les réquisitions soumises peuvent être rejetées");
        }

        String username = principal.getName();
        User validator = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.REJECTED, comment, principal);

        requisition.setValidatedBy(validator);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.REJECTED);

        return mapper.toDTO(requisition);
    }

    public RequisitionDTO startProcessing(Long id, Principal principal) {
        Requisition requisition = requisitionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));
        if (requisition.getStatus() != RequisitionStatus.VALIDATED) {
            throw new IllegalStateException("Seules les réquisitions validées peuvent être traitées.");
        }
        requisition.setStatus(RequisitionStatus.IN_PROCESS);
        addHistory(requisition, RequisitionStatus.VALIDATED, RequisitionStatus.IN_PROCESS, "Prise en charge par la DAL", principal);
        return mapper.toDTO(requisition);
    }

    public RequisitionDTO cancelRequisition(Long id, Principal principal, String comment) {
        Requisition requisition = requisitionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        // On peut adapter les statuts selon tes besoins
        if (requisition.getStatus() == RequisitionStatus.IN_PROCESS ||
                requisition.getStatus() == RequisitionStatus.DELIVERED ||
                requisition.getStatus() == RequisitionStatus.CANCELLED) {
            throw new IllegalStateException("Cette réquisition ne peut plus être annulée.");
        }

        requisition.setStatus(RequisitionStatus.CANCELLED);
        addHistory(requisition, requisition.getStatus(), RequisitionStatus.CANCELLED, comment != null ? comment : "Réquisition annulée", principal);

        return mapper.toDTO(requisition);
    }



    /**
     * Récupérer toutes les réquisitions (filtrable plus tard)
     */
    @Transactional(readOnly = true)
    public List<RequisitionDTO> getAll() {
        return mapper.toDTOList(requisitionDAO.findAll());
    }

    /**
     * Récupérer les détails d’une réquisition par son ID
     */
    @Transactional(readOnly = true)
    public RequisitionDTO getById(Long id) {
        return mapper.toDTO(
                requisitionDAO.findById(id)
                        .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"))
        );
    }

    /**
     * Ajouter un enregistrement d'historique à la réquisition
     */
    public void addHistory(Requisition requisition, RequisitionStatus from, RequisitionStatus to, String comment, Principal principal) {
        RequisitionHistory history = new RequisitionHistory();
        history.setRequisition(requisition);
        history.setStatusBefore(from);
        history.setStatusAfter(to);
        history.setComment(comment);
        history.setActionDate(LocalDateTime.now());
        // TODO : récupérer utilisateur courant via SecurityContext
        String username = principal.getName();

        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        history.setActionBy(user);

        requisition.getHistories().add(history);
        requisitionHistoryDAO.save(history);
    }
}

