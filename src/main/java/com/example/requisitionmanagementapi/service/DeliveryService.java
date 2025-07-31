package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dao.DeliveryDAO;
import com.example.requisitionmanagementapi.dao.RequisitionDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.DeliveryDTO;
import com.example.requisitionmanagementapi.entity.*;
import com.example.requisitionmanagementapi.enums.DeliveryStatus;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.mapper.DeliveryMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class DeliveryService {

    private final DeliveryDAO dao;
    private final DeliveryMapper mapper;
    private final RequisitionDAO requisitionDAO;
    private final UserDAO userDAO;
    private final RequisitionService requisitionService;
    private final ArticleDAO articleDAO;

    /**
            * Créer une livraison pour une réquisition validée
     */
    public DeliveryDTO createDelivery(Long requisitionId, DeliveryDTO dto, Principal principal) {
        Requisition requisition = requisitionDAO.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Réquisition introuvable"));

        if (requisition.getStatus() != RequisitionStatus.VALIDATED) {
            throw new IllegalStateException("La réquisition n'est pas validée");
        }

        User deliveredBy = getCurrentUser(principal);

        Delivery delivery = mapper.toEntity(dto);
        delivery.setRequisition(requisition);
        delivery.setDeliveryDate(LocalDateTime.now());
        delivery.setDeliveryStatus(DeliveryStatus.PREPARATION);
        delivery.setDeliveredBy(deliveredBy);

        return mapper.toDTO(dao.save(delivery));
    }

    /**
     * Modifier le statut d'une livraison (DAL ou réception)
     */
    public DeliveryDTO updateStatus(Long deliveryId, DeliveryStatus status, Principal principal) {
        Delivery delivery = dao.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        User user = getCurrentUser(principal);
        delivery.setDeliveryStatus(status);

        if (status == DeliveryStatus.RECEIVED) {
            delivery.setRecipient(user);
            Requisition requisition = delivery.getRequisition();
            if (requisition.getStatus() != RequisitionStatus.DELIVERED) {
                for (RequisitionArticle ra : requisition.getArticles()) {
                    Article article = ra.getArticle();
                    int newStock = article.getStockQuantity() - ra.getQuantity();
                    if (newStock < 0) {
                        throw new IllegalArgumentException(
                                "Stock insuffisant pour l'article : " + article.getName() +
                                        " (disponible : " + article.getStockQuantity() + ", demandé : " + ra.getQuantity() + ")"
                        );
                    }
                    article.setStockQuantity(newStock);
                    articleDAO.save(article);
                }
                requisition.setStatus(RequisitionStatus.DELIVERED);
              requisitionService.addHistory(requisition, requisition.getStatus(), RequisitionStatus.DELIVERED, "Réquisition livrée", principal);
                requisitionDAO.save(requisition);
            }
        }
        return mapper.toDTO(delivery);
    }

    /**
     * Récupérer toutes les livraisons
     */
    @Transactional(readOnly = true)
    public List<DeliveryDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    /**
     * Récupérer les livraisons liées à une réquisition
     */
    @Transactional(readOnly = true)
    public List<DeliveryDTO> getByRequisition(Long requisitionId) {
        return mapper.toDTOList(dao.findByRequisitionId(requisitionId));
    }



    /**
     * Utilitaire pour récupérer l'utilisateur courant
     */
    private User getCurrentUser(Principal principal) {
        return userDAO.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
    }

    public DeliveryDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}
