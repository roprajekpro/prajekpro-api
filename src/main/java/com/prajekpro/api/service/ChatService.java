package com.prajekpro.api.service;

import com.prajekpro.api.dto.ChatMessageDetailsDTO;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.DownloadImageDTO;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

public interface ChatService {
    BaseWrapper getThreadDetailsList(Pageable pageable) throws ServicesException;

    BaseWrapper getMessageDetailsList(Long chatThreadId, Pageable pageable) throws ServicesException;

    BaseWrapper sendMessage(Long chatThreadId, ChatMessageDetailsDTO chatMessageDetails) throws ServicesException;

    BaseWrapper updateIsRead(Set<Long> messageIds) throws ServicesException;

    BaseWrapper createNewThread(String userId) throws ServicesException;

    BaseWrapper markAllMessageRead(Long chatThreadId) throws ServicesException;

    BaseWrapper sendDocument(Long chatThreadId, MultipartFile file, String chatMessageDetails) throws ServicesException, IOException;

    DownloadImageDTO getMessageAttachmentInfo(String id) throws IOException;
}
