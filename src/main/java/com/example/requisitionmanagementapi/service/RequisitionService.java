package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.RequisitionDAO;
import com.example.requisitionmanagementapi.dao.RequisitionHistoryDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.RequisitionDTO;
import com.example.requisitionmanagementapi.dto.RequisitionStatusCountDTO;
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

    private final RequisitionMapper mapper;
    private final UserDAO userDAO;
    private final RequisitionDAO dao;
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

        return mapper.toDTO(dao.save(entity));
    }

    public RequisitionDTO draftRequisition(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

//        if (requisition.getStatus() != RequisitionStatus.REJECTED) {
//            throw new IllegalStateException("Seules les réquisitions rejeté peuvent être soumises");
//        }

        String username = principal.getName();
        User validator = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        // Décrémenter les stocks
        addHistory(requisition, requisition.getStatus(), RequisitionStatus.DRAFT, "Réquisition mis en brouillons", principal);

        requisition.setValidatedBy(validator);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.DRAFT);
        requisition.setAction("Brouiller");
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

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.SUBMITTED, "Soumission de la réquisition", principal);
        requisition.setStatus(RequisitionStatus.SUBMITTED);
        requisition.setAction("Soumis");
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

        // Décrémenter les stocks
        addHistory(requisition, requisition.getStatus(), RequisitionStatus.VALIDATED, "Réquisition validée", principal);

        requisition.setValidatedBy(validator);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.VALIDATED);
        requisition.setAction("Valider");
        dao.save(requisition);

        return mapper.toDTO(requisition);
    }

    /**
     * Rejeter une réquisition
     */
    public RequisitionDTO rejectRequisition(Long id,  String comment, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        if (requisition.getStatus() != RequisitionStatus.SUBMITTED) {
            throw new IllegalStateException("Seules les réquisitions soumises peuvent être rejetées");
        }

        String username = principal.getName();
        User validator = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.REJECTED, comment != null ? comment : "Réquisition rejetée", principal);

        requisition.setValidatedBy(validator);
        requisition.setComment(comment);
        requisition.setValidationDate(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.REJECTED);
        requisition.setAction("Rejeter");
        dao.save(requisition);

        return mapper.toDTO(requisition);
    }

    public void startProcessing(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));
        addHistory(requisition, RequisitionStatus.VALIDATED, RequisitionStatus.IN_PROCESS, "Planification de la livraison", principal);
        requisition.setStatus(RequisitionStatus.IN_PROCESS);
        requisition.setAction("En cours");
        mapper.toDTO(requisition);
    }

    public RequisitionDTO cancelRequisition(Long id, Principal principal, String comment) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        // On peut adapter les statuts selon tes besoins
        if (requisition.getStatus() == RequisitionStatus.IN_PROCESS ||
                requisition.getStatus() == RequisitionStatus.DELIVERED ||
                requisition.getStatus() == RequisitionStatus.CANCELLED) {
            throw new IllegalStateException("Cette réquisition ne peut plus être annulée.");
        }


        addHistory(requisition, requisition.getStatus(), RequisitionStatus.CANCELLED, comment, principal);
        requisition.setStatus(RequisitionStatus.CANCELLED);
        requisition.setComment(comment);
        requisition.setAction("Annuler");
        dao.save(requisition);

        return mapper.toDTO(requisition);
    }

    public void deliveryRequisition(Long id, Principal principal) {
        Requisition requisition = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("Réquisition non trouvée"));

        // On peut adapter les statuts selon tes besoins
        if (requisition.getStatus() != RequisitionStatus.IN_PROCESS ) {
            throw new IllegalStateException("Cette réquisition ne peut plus être valider.");
        }

        addHistory(requisition, requisition.getStatus(), RequisitionStatus.CANCELLED, "Réquisition Livré", principal);
        requisition.setStatus(RequisitionStatus.DELIVERED);
        requisition.setAction("Livrer");
        dao.save(requisition);

        mapper.toDTO(requisition);
    }



    /**
     * Récupérer toutes les réquisitions (filtrable plus tard)
     */
    @Transactional(readOnly = true)
    public List<RequisitionDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
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

    public RequisitionStatusCountDTO getRequisitionStatusCounts() {
        return new RequisitionStatusCountDTO(
                dao.countByStatus(RequisitionStatus.DRAFT),
                dao.countByStatus(RequisitionStatus.SUBMITTED),
                dao.countByStatus(RequisitionStatus.VALIDATED),
                dao.countByStatus(RequisitionStatus.REJECTED),
                dao.countByStatus(RequisitionStatus.IN_PROCESS),
                dao.countByStatus(RequisitionStatus.DELIVERED),
                dao.countByStatus(RequisitionStatus.CANCELLED)
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

