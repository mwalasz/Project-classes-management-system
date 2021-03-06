package com.bd2.backend.service;

import com.bd2.backend.entities.Attachment;
import com.bd2.backend.entities.Section;
import com.bd2.backend.entities.Student;
import com.bd2.backend.repository.AttachmentRepository;
import com.bd2.backend.response.AttachmentsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AttachmentService {

    private AttachmentRepository attachmentRepository;
    private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Attachment saveAttachment(MultipartFile file, String description, Section section, Student student) {
        try {
            Attachment attachment = new Attachment(
                    StringUtils.cleanPath(file.getOriginalFilename()),
                    file.getContentType(), student, section, description, file.getBytes());

            return this.attachmentRepository.save(attachment);
        } catch (IOException e) {
            logger.error("Failed to save file into database, reason: " + e.getMessage());
        }
        return null;
    }

    public Attachment getFile(Long fileId) throws Exception {
        return this.attachmentRepository.findById(fileId)
                .orElseThrow(() -> new Exception("Attachment with id " + fileId + " not found!"));
    }

    public AttachmentsResponse getAllAttachmentsForSection(Long sectionId){
        List<Attachment> attachments = this.attachmentRepository.findAllBySectionId(sectionId);
        if(attachments.isEmpty()) {
            return null;
        }
        AttachmentsResponse attachmentsResponse = new AttachmentsResponse();
        attachments.forEach(attachment -> {
            attachmentsResponse.setSection(attachment.getSection());
            attachmentsResponse.addStudentWithAttachments(
                    attachment.getStudent(),
                    attachment.getId(),
                    attachment.getFileName(),
                    attachment.getFileType(),
                    attachment.getInsertDate(),
                    attachment.getDescription()
            );
        });

        return attachmentsResponse;
    }
}
