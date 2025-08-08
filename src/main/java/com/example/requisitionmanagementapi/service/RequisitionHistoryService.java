package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.RequisitionDAO;
import com.example.requisitionmanagementapi.dao.RequisitionHistoryDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.RequisitionHistoryDTO;
import com.example.requisitionmanagementapi.entity.Requisition;
import com.example.requisitionmanagementapi.entity.RequisitionHistory;
import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.mapper.RequisitionHistoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class RequisitionHistoryService {

    private final RequisitionHistoryDAO dao;
    private final RequisitionHistoryMapper mapper;
    private final UserDAO userDAO;
    private final RequisitionDAO requisitionDAO;

    public List<RequisitionHistoryDTO> getAll() {
        return mapper.toDTOList(dao.findAllByOrderByActionDateDesc());
    }

    /**
     * Récupérer l'historique d'une réquisition donnée
     */
    @Transactional(readOnly = true)
    public List<RequisitionHistoryDTO> getByRequisition(Long requisitionId) {
        List<RequisitionHistory> histories = dao.findByRequisitionIdOrderByActionDateDesc(requisitionId);
        return mapper.toDTOList(histories);
    }

    /**
     * Ajouter manuellement un historique (utile pour test ou action annexe)
     */
    public RequisitionHistoryDTO addManualHistory(Long requisitionId,
                                                  RequisitionStatus from,
                                                  RequisitionStatus to,
                                                  String comment,
                                                  Principal principal) {

        Requisition requisition = requisitionDAO.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Réquisition introuvable"));

        User actionBy = userDAO.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        RequisitionHistory history = new RequisitionHistory();
        history.setRequisition(requisition);
        history.setStatusBefore(from);
        history.setStatusAfter(to);
        history.setComment(comment);
        history.setActionDate(LocalDateTime.now());
        history.setActionBy(actionBy);
        return mapper.toDTO(dao.save(history));
    }
}
