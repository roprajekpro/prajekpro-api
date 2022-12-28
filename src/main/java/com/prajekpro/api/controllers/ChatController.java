package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.ChatMessageDetailsDTO;
import com.prajekpro.api.service.ChatService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping(value = {RestUrlConstants.PP_CHAT})
@Api(value = "API related to Chat management")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @ApiOperation(value = "API to get thread list")
    @GetMapping()
    public BaseWrapper getThreadDetailsList(Pageable pageable) throws ServicesException {
        return chatService.getThreadDetailsList(pageable);
    }

    @ApiOperation(value = "API to get message list")
    @GetMapping(value = {RestUrlConstants.PP_CHAT_MESSAGE})
    public BaseWrapper getMessageDetailsList(@PathVariable("chatThreadId") Long chatThreadId, Pageable pageable) throws ServicesException {
        return chatService.getMessageDetailsList(chatThreadId, pageable);
    }

    @ApiOperation(value = "API to send message ")
    @PostMapping(value = {RestUrlConstants.PP_CHAT_MESSAGE})
    public BaseWrapper sendMessage(@PathVariable("chatThreadId") Long chatThreadId, @RequestBody ChatMessageDetailsDTO chatMessageDetails) throws ServicesException {
        return chatService.sendMessage(chatThreadId, chatMessageDetails);
    }

    @ApiOperation(value = "update IsRead for chat message ")
    @PutMapping()
    public BaseWrapper updateIsRead(@RequestBody Set<Long> messageIds) throws ServicesException {
        return chatService.updateIsRead(messageIds);
    }

    @ApiOperation(value = "create new thread for chat ")
    @GetMapping(value = {RestUrlConstants.PP_CHAT_THREAD})
    public BaseWrapper createChatThread(@PathVariable("userId") String userId) throws ServicesException {
        return chatService.createNewThread(userId);
    }

    @ApiOperation(value = "API to mark All messages Read")
    @PutMapping(value = {RestUrlConstants.PP_CHAT_MESSAGE})
    public BaseWrapper markAllRead(@PathVariable("chatThreadId") Long chatThreadId) throws ServicesException {
        return chatService.markAllMessageRead(chatThreadId);
    }

    @ApiOperation(value = "Api to send Document")
    @PostMapping(value = {RestUrlConstants.PP_CHAT_MESSAGE_DOCUMENT})
    public BaseWrapper sendDocument(@PathVariable("chatThreadId") Long chatThreadId,
                                    @RequestParam("file") MultipartFile file, @RequestParam("receiverId") String receiverId) throws ServicesException, IOException {
        return chatService.sendDocument(chatThreadId, file, receiverId);
    }

}
