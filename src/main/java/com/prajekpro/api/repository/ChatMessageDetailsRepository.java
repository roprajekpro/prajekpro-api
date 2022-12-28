package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ChatMessageDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatMessageDetailsRepository extends JpaRepository<ChatMessageDetails, Long> {

    Page<ChatMessageDetails> findByChatThreadDetails_Id(Long chatThreadId, Pageable pageable);

    @Query(nativeQuery = true, value = "select cm1.* from chat_message_details cm1 " +
            " where cm1.CHAT_THREAD_DETAILS_ID IN (:threadIds) " +
            " and cm1.MODIFIED_TS IN (select max(cm2.MODIFIED_TS) " +
            " from chat_message_details cm2 where cm2.CHAT_THREAD_DETAILS_ID IN (:threadIds) " +
            " group by cm2.CHAT_THREAD_DETAILS_ID) order by cm1.MODIFIED_TS DESC ")
    List<ChatMessageDetails> findByChatThreadDetails_IdAndModifiedTs(@PathVariable("threadIds") List<Long> threadIds);

    @Query(nativeQuery = true, value = " select count(cm.ID) from chat_message_details cm " +
            " where cm.CHAT_THREAD_DETAILS_ID = ?1 and cm.IS_READ = 0 and FK_TO = ?2")
    Integer countUnreadMessage(Long threadIds, String userId);

    @Modifying
    @Query("UPDATE ChatMessageDetails SET  isRead = 1 WHERE id IN (?1) and receiver.userId = ?2")
    int updateIsRead(Set<Long> messageIds, String userId);

    @Query(nativeQuery = true, value = "select count(*) as unReadMessageCount , CHAT_THREAD_DETAILS_ID " +
            "from chat_message_details " +
            " where CHAT_THREAD_DETAILS_ID IN (:threadIds)" +
            " group by CHAT_THREAD_DETAILS_ID ;")
    List<ChatMessageDetails> findByIsRead(@Param("threadIds") List<Long> threadIds);

    @Modifying
    @Query("UPDATE ChatMessageDetails SET  isRead = 1 WHERE chatThreadDetails.id = ?1")
    int markAllRead(Long chatThreadId);

    @Query("SELECT cmd from ChatMessageDetails cmd where cmd.id=:id")
    ChatMessageDetails fetchById(@Param("id") long id);
}
