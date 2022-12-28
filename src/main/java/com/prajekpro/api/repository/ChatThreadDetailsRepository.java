package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ChatThreadDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatThreadDetailsRepository extends JpaRepository<ChatThreadDetails, Long> {

    @Query("select cd from ChatThreadDetails cd where cd.sender.userId = :userId OR cd.receiver.userId = :userId")
    Page<ChatThreadDetails> findAllByReceiverOrSender(@Param("userId") String userId, Pageable pageable);

    @Query("select cd from ChatThreadDetails cd " +
            " where (cd.sender.userId = :sender AND cd.receiver.userId = :receiver )" +
            " OR (cd.receiver.userId = :sender AND cd.sender.userId = :receiver ) ")
    ChatThreadDetails findBySenderAndReceiver(@Param("sender") String sender, @Param("receiver") String receiver);
}
