package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProDocuments;
import com.safalyatech.common.repository.IBaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProDetailDocumentRepository extends JpaRepository<ProDocuments, Long>, IBaseRepository<ProDocuments> {
}
