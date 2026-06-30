package com.ems.portal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {

    List<NotificationMessage> findByRecipientCodeAndDeletedAtIsNullOrderByCreatedAtDesc(String recipientCode);

    List<NotificationMessage> findBySenderCodeOrderByCreatedAtDesc(String senderCode);

    long countByRecipientCodeAndDeletedAtIsNull(String recipientCode);

    long countBySenderCode(String senderCode);

    List<NotificationMessage> findByRecipientCodeAndReadFalseAndDeletedAtIsNull(String recipientCode);
}
