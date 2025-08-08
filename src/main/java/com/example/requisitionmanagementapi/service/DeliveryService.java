package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dao.DeliveryDAO;
import com.example.requisitionmanagementapi.dao.RequisitionDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.*;
import com.example.requisitionmanagementapi.entity.*;
import com.example.requisitionmanagementapi.enums.DeliveryStatus;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.mapper.DeliveryMapper;
import com.example.requisitionmanagementapi.mapper.RequisitionMapper;
import com.example.requisitionmanagementapi.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final DeliveryDAO deliveryDAO;
    private final RequisitionMapper requisitionMapper;
    private final UserMapper userMapper;

    /**
            * Créer une livraison pour une réquisition validée
     */
    public DeliveryDTO createDelivery(Long requisitionId, DeliveryDTO dto, Principal principal) {
        Requisition requisition = requisitionDAO.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Réquisition introuvable"));
        User deliveredBy = getCurrentUser(principal);
        Delivery delivery = mapper.toEntity(dto);
        delivery.setRequisition(requisition);
        delivery.setDeliveryStatus(DeliveryStatus.PREPARATION);
        delivery.setDeliveredBy(deliveredBy);
        deliveryDAO.save(delivery);
        requisitionService.startProcessing(requisitionId, principal);

        return mapper.toDTO(dao.save(delivery));
    }

     public DeliveryDTO transitDelivery(Long deliveryId, Principal principal) {
        Delivery delivery = dao.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        User user = getCurrentUser(principal);
        delivery.setDeliveryStatus(DeliveryStatus.IN_TRANSIT);
        deliveryDAO.save(delivery);
        return mapper.toDTO(delivery);
    }

    /**
     * Modifier le statut d'une livraison (DAL ou réception)
     */
    public DeliveryDTO validateDelivery(Long deliveryId, Principal principal) {
        Delivery delivery = dao.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        User user = getCurrentUser(principal);

        if (delivery.getDeliveryStatus() != DeliveryStatus.RECEIVED) {
            delivery.setRecipient(user);
            Requisition requisition = delivery.getRequisition();
            if (requisition.getStatus() != RequisitionStatus.DELIVERED) {
                for (RequisitionArticle ra : requisition.getArticles()) {
                    Article article = ra.getArticle();
                    int newStock = article.getStockQuantity() - ra.getQuantity();
                    article.setStockQuantity(newStock);
                    articleDAO.save(article);
                }
                delivery.setDeliveryStatus(DeliveryStatus.RECEIVED);
                requisitionService.deliveryRequisition(requisition.getId(), principal);
                requisitionDAO.save(requisition);
            }
        }
        deliveryDAO.save(delivery);
        return mapper.toDTO(delivery);
    }

    /**
     * Récupérer toutes les livraisons
     */
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getAll() {
        List<Delivery> deliveries = dao.findAllByOrderByDeliveryDateDesc(); // tri ici !
        List<DeliveryResponse> result = new ArrayList<>();
        for (Delivery delivery : deliveries) {
            DeliveryResponse dto = new DeliveryResponse();
            dto.setId(delivery.getId());
            dto.setDeliveryStatus(delivery.getDeliveryStatus());
            dto.setDeliveryDate(delivery.getDeliveryDate());
            UserDTO recipient = userMapper.toDTO(delivery.getRecipient());
            dto.setRecipient(recipient);
            if (delivery.getRequisition() != null) {
                dto.setRequisition(requisitionMapper.toDTOResponse(delivery.getRequisition()));
            }
            result.add(dto);
        }
        return result;
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

    public DeliveryCount getDeliveryStatusCounts() {
        return new DeliveryCount(
                dao.countByDeliveryStatus(DeliveryStatus.PREPARATION),
                dao.countByDeliveryStatus(DeliveryStatus.IN_TRANSIT),
                dao.countByDeliveryStatus(DeliveryStatus.RECEIVED)
        );
    }
}
